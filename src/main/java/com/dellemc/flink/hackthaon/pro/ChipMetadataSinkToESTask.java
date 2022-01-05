package com.dellemc.flink.hackthaon.pro;

import com.dellemc.flink.hackthaon.pro.metrics.EventsCountExposingMapFunction;
import io.pravega.connectors.flink.FlinkPravegaReader;
import io.pravega.connectors.flink.PravegaConfig;
import io.pravega.shaded.com.google.gson.Gson;
import io.pravega.shaded.com.google.gson.GsonBuilder;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.elasticsearch.ActionRequestFailureHandler;
import org.apache.flink.streaming.connectors.elasticsearch7.ElasticsearchSink;
import org.apache.flink.util.ExceptionUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.util.concurrent.EsRejectedExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ChipMetadataSinkToESTask {
    private static Logger log = LoggerFactory.getLogger(ChipMetadataSinkToESTask.class);
    private static final Gson GSON = new GsonBuilder().serializeSpecialFloatingPointValues().create();

    private ChipMetadataSinkToESTask() {
    }

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
                .withReaderGroupName("readergroup3")
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

        env.addSource(source)
                .filter(jx -> !Strings.isNullOrEmpty(jx))
                .map(json -> GSON.fromJson(json.trim(), ChipMetadata.class))
                .filter(Objects::nonNull)
                .keyBy(obj -> obj.getProduction_line())
                .map(new EventsCountExposingMapFunction())
                .name(EventsCountExposingMapFunction.class.getSimpleName())
                .map(obj -> GSON.toJson(obj))
                .addSink(esSinkBuilder.build())
                .name(MyElasticsearchSinkFunction.class.getName());
    }
}
