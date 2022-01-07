package com.dellemc.flink.hackthaon.pro;

import io.pravega.connectors.flink.FlinkPravegaReader;
import io.pravega.connectors.flink.PravegaConfig;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.UUID;

public class ModelTraingForVideoRaw {
    private ModelTraingForVideoRaw() {}

    public static class Singleton {
        private static final ModelTraingForVideoRaw INSTANCE = new ModelTraingForVideoRaw();
    }

    public static ModelTraingForVideoRaw getInstance() {
        return Singleton.INSTANCE;
    }

    public void run(StreamExecutionEnvironment env, ParameterTool params, final String scope, final String streamName) throws URISyntaxException {
        PravegaConfig pravegaConfig = PravegaConfig
                .fromParams(params)
                .withControllerURI(new URI("tcp://localhost:9090"))
                .withDefaultScope(scope);
        FlinkPravegaReader<String> source = FlinkPravegaReader.<String>builder()
                .withPravegaConfig(pravegaConfig)
                .withReaderGroupName("rawdatargnew")
                .forStream(streamName)
                .withDeserializationSchema(new SimpleStringSchema())
                .build();

        env.addSource(source)
                .filter(Objects::nonNull)
                .writeAsText("file:///tmp/out", FileSystem.WriteMode.OVERWRITE);
                //.print("--------------myoutput>>");
    }
}
