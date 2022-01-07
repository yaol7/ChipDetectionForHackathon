package com.dellemc.flink.hackthaon.pro;

import io.pravega.connectors.flink.FlinkPravegaReader;
import io.pravega.connectors.flink.FlinkPravegaWriter;
import io.pravega.connectors.flink.PravegaConfig;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class ChipVideoRawSinkToCloudTask {

    private ChipVideoRawSinkToCloudTask() {}

    public static class Singleton {
        private static final ChipVideoRawSinkToCloudTask INSTANCE = new ChipVideoRawSinkToCloudTask();
    }

    public static ChipVideoRawSinkToCloudTask getInstance() {
        return Singleton.INSTANCE;
    }

    public void run(StreamExecutionEnvironment env, ParameterTool params, final String scope, final String streamName,
                    final String destStreamName) throws URISyntaxException {
        PravegaConfig pravegaConfig = PravegaConfig
                .fromParams(params)
                .withControllerURI(new URI("tcp://172.17.0.1:9090"))
                .withDefaultScope(scope);
        FlinkPravegaReader<String> source = FlinkPravegaReader.<String>builder()
                .withPravegaConfig(pravegaConfig)
                .withReaderGroupName("rawdatacloudrg")
                .forStream(streamName)
                .withDeserializationSchema(new SimpleStringSchema())
                .build();

        FlinkPravegaWriter<String> sinkWriter = FlinkPravegaWriter.<String>builder()
                .withPravegaConfig(pravegaConfig)
                .forStream(destStreamName)
                .withSerializationSchema(new SimpleStringSchema())
                .build();
        env.addSource(source)
                .name(FlinkPravegaReader.class.getSimpleName())
                .filter(Objects::nonNull)
                .addSink(sinkWriter)
                .name(FlinkPravegaWriter.class.getSimpleName());
    }
}
