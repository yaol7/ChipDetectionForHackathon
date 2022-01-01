package com.dellemc.flink.hackthaon.pro;

import io.pravega.connectors.flink.FlinkPravegaReader;
import io.pravega.connectors.flink.PravegaConfig;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.elasticsearch.ActionRequestFailureHandler;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.streaming.connectors.elasticsearch7.ElasticsearchSink;
import org.apache.flink.streaming.connectors.elasticsearch7.RestClientFactory;
import org.apache.flink.util.ExceptionUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.util.concurrent.EsRejectedExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class ChipMetadataSinkToESTask {
    private static Logger log = LoggerFactory.getLogger(ChipMetadataSinkToESTask.class);

    private ChipMetadataSinkToESTask() {}

    private static class Singleton {
        private static ChipMetadataSinkToESTask INSTANCE = new ChipMetadataSinkToESTask();
    }

    public static ChipMetadataSinkToESTask getInstance() {
        return Singleton.INSTANCE;
    }

    public void run(StreamExecutionEnvironment env, ParameterTool params, final String scope, final String streamName) throws URISyntaxException {
        PravegaConfig pravegaConfig = PravegaConfig
                .fromParams(params)
                .withControllerURI(new URI("tcp://172.17.0.1:9090"))
                .withDefaultScope(scope);
        FlinkPravegaReader<String> source = FlinkPravegaReader.<String>builder()
                .withPravegaConfig(pravegaConfig)
                .forStream(streamName)
                .withDeserializationSchema(new SimpleStringSchema())
                .build();

        List<HttpHost> httpHosts = new ArrayList<>();
        httpHosts.add(new HttpHost("172.17.0.1", 9200, "http"));

        ElasticsearchSink.Builder<String> esSinkBuilder = new ElasticsearchSink.Builder<>(httpHosts,
                new MyElasticsearchSinkFunction("chipindex", "_doc"));
        //set batch process
        esSinkBuilder.setBulkFlushMaxActions(50);
        esSinkBuilder.setBulkFlushMaxSizeMb(10);
        esSinkBuilder.setFailureHandler((ActionRequestFailureHandler) (action, failure, restStatusCode, indexer) -> {
            if (ExceptionUtils.findThrowable(failure, EsRejectedExecutionException.class).isPresent()) {
                indexer.add(action);
            } else {
                log.error("Failed to sink data to ES, action: {}", action, failure);
            }
        });

        /*
       env.addSource(source)
                .filter(Objects::nonNull)
                .addSink(esSinkBuilder.build());*/
        List<String> list = new ArrayList<>();
        list.add("{\"name\": \"jack\", \"age\": 20}");
        list.add("{\"name\": \"rose\", \"age\": 21}");
        env.enableCheckpointing(5000);
        env.fromCollection(list)
                .filter(Objects::nonNull)
                //.writeAsText("file:///tmp/out", FileSystem.WriteMode.OVERWRITE);
                .addSink(esSinkBuilder.build())
                .name(MyElasticsearchSinkFunction.class.getName());

    }
}
