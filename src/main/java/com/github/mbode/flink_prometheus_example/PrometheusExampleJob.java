package com.github.mbode.flink_prometheus_example;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamUtils;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.DiscardingSink;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSink;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PrometheusExampleJob {
    private static final Logger log = LoggerFactory.getLogger(PrometheusExampleJob.class);
    private final ParameterTool parameters;

    public static void main(String[] args) throws Exception {
        new PrometheusExampleJob(ParameterTool.fromArgs(args)).run();
    }

    private PrometheusExampleJob(ParameterTool parameters) {
        this.parameters = parameters;
    }

    private void run() throws Exception {
        log.info("metrics job is stating.");
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //env.enableCheckpointing(500);
        //env.disableOperatorChaining();
        //env.setParallelism(1);
    /*env.addSource(new RandomSourceFunction(parameters.getInt("elements", Integer.MAX_VALUE)))
        .name(RandomSourceFunction.class.getSimpleName())
        .map(new FlinkMetricsExposingMapFunction())
        .name(FlinkMetricsExposingMapFunction.class.getSimpleName())
        .addSink(new DiscardingSink<>())
        .name(DiscardingSink.class.getSimpleName());


    env.execute(PrometheusExampleJob.class.getSimpleName());*/
        //log.info("start read metadata from Pravega stream of flink job.");
        //PravegaReadJob job = PravegaReadJob.getInstance();
        //job.readStream(env, parameters, "dataScope", "metaStream");

        //log.info("$$$$$$$$$$$$$ flink job is summited..");

        final String json = "{\n" +
                "  \"name\": \"rose\",\n" +
                "  \"age\": 20\n" +
                "}";
        Map<String, String> config = new HashMap<>();
        config.put("cluster.name", "es-docker-cluster");
        config.put("bulk.flush.max.actions", "1");

        List<InetSocketAddress> transportAddresses = new ArrayList<>();
        transportAddresses.add(new InetSocketAddress(InetAddress.getByName("es02"), 9200));

        List<String> list = new ArrayList<>();
        list.add(json);
        env.fromCollection(list)
                .filter(Objects::nonNull)
                .addSink(new ElasticsearchSink<>(config, transportAddresses, new ElasticsearchSinkFunction<String>() {
                    public IndexRequest createIndexRequest(String element) {
                        Map<String, String> json = new HashMap<>();
                        json.put("data", element);
                        log.info("-------------- json data:" + element);
                        return Requests.indexRequest()
                                .index("my-index-chip")
                                .type("_doc")
                                .source(json);
                    }

                    @Override
                    public void process(String element, RuntimeContext ctx, RequestIndexer indexer) {
                        indexer.add(createIndexRequest(element));
                    }
                }));

        env.execute(PrometheusExampleJob.class.getSimpleName());
        log.info("!!!!!!!!!!!!!!!!!! flink job is summit!");
    }
}
