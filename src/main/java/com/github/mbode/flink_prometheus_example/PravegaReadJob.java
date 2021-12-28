package com.github.mbode.flink_prometheus_example;

import io.pravega.connectors.flink.FlinkPravegaReader;
import io.pravega.connectors.flink.PravegaConfig;
import io.pravega.shaded.com.google.gson.Gson;
import io.pravega.shaded.com.google.gson.GsonBuilder;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.DataStreamUtils;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.DiscardingSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Iterator;
import java.util.Objects;

public class PravegaReadJob {
    private static final Logger log = LoggerFactory.getLogger(PravegaReadJob.class);
    private static final Gson GSON = new GsonBuilder().serializeSpecialFloatingPointValues().create();

    private PravegaReadJob() {
    }

    private static class Singleton {
        private static PravegaReadJob INSTANCE = new PravegaReadJob();
    }

    public static PravegaReadJob getInstance() {
        return Singleton.INSTANCE;
    }

    public void readStream(StreamExecutionEnvironment env, ParameterTool params, final String scope, final String streamName) throws Exception {
        log.info("start receiving data from scope: {}, stream: {}", scope, streamName);
        PravegaConfig pravegaConfig = PravegaConfig
                .fromParams(params)
                .withControllerURI(new URI("tcp://localhost:9090"))
                .withDefaultScope(scope);
        FlinkPravegaReader<String> source = FlinkPravegaReader.<String>builder()
                .withPravegaConfig(pravegaConfig)
                .forStream(streamName)
                .withDeserializationSchema(new SimpleStringSchema())
                .build();
        DataStream<String> dataStream = env.addSource(source)
                .name(FlinkPravegaReader.class.getSimpleName())
                .filter(Objects::nonNull)
                .name("filter not null");
        //dataStream.print();
        dataStream.writeAsText("file:///tmp/out", FileSystem.WriteMode.OVERWRITE);
        log.info("!!!!!!!!!!!!!!! pravega stream is done!!!!!");
        env.execute(PrometheusExampleJob.class.getSimpleName());
    }
}
