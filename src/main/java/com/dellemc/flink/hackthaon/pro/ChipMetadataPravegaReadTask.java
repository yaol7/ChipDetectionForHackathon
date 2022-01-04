package com.dellemc.flink.hackthaon.pro;

import com.dellemc.flink.hackthaon.pro.metrics.ChipMetadataMetricsExposingMapFunction;
import com.dellemc.flink.hackthaon.pro.metrics.EventsCountExposingMapFunction;
import io.pravega.connectors.flink.FlinkPravegaReader;
import io.pravega.connectors.flink.PravegaConfig;
import io.pravega.shaded.com.google.gson.Gson;
import io.pravega.shaded.com.google.gson.GsonBuilder;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.DiscardingSink;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.elasticsearch.common.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Objects;

public class ChipMetadataPravegaReadTask {
    private static final Logger log = LoggerFactory.getLogger(ChipMetadataPravegaReadTask.class);
    private static final Gson GSON = new GsonBuilder().serializeSpecialFloatingPointValues().create();

    private ChipMetadataPravegaReadTask() {
    }

    private static class Singleton {
        private static ChipMetadataPravegaReadTask INSTANCE = new ChipMetadataPravegaReadTask();
    }

    public static ChipMetadataPravegaReadTask getInstance() {
        return ChipMetadataPravegaReadTask.Singleton.INSTANCE;
    }

    public void run(StreamExecutionEnvironment env, ParameterTool params, final String scope, final String streamName) throws Exception {
        log.info("start receiving data from scope: {}, stream: {}", scope, streamName);
        PravegaConfig pravegaConfig = PravegaConfig
                .fromParams(params)
                .withControllerURI(new URI("tcp://172.17.0.1:9090"))
                .withDefaultScope(scope);
        FlinkPravegaReader<String> source = FlinkPravegaReader.<String>builder()
                .withPravegaConfig(pravegaConfig)
                .forStream(streamName)
                .withDeserializationSchema(new SimpleStringSchema())
                .build();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.addSource(source)
                .filter(jx -> !Strings.isNullOrEmpty(jx))
                .map(json -> GSON.fromJson(json.trim(), ChipMetadata.class))
                .filter(Objects::nonNull)
                .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<ChipMetadata>(Time.milliseconds(50)) {
                    @Override
                    public long extractTimestamp(ChipMetadata chipMetadata) {
                        return chipMetadata.getTimestamp().getTime();
                    }
                })
                .keyBy(obj -> obj.getProduction_line())
                .window(TumblingEventTimeWindows.of(Time.seconds(5)))
                .sum("defectsLen")
                .map(new ChipMetadataMetricsExposingMapFunction())
                .name(ChipMetadataMetricsExposingMapFunction.class.getSimpleName())
                .addSink(new DiscardingSink<>())
                .name(DiscardingSink.class.getSimpleName());

        //count total events for every production line
        env.addSource(source)
                .filter(jx -> !Strings.isNullOrEmpty(jx))
                .map(json -> GSON.fromJson(json.trim(), ChipMetadata.class))
                .filter(Objects::nonNull)
                .keyBy(obj -> obj.getProduction_line())
                .map(new EventsCountExposingMapFunction())
                .name(EventsCountExposingMapFunction.class.getSimpleName())
                .addSink(new DiscardingSink<>())
                .name(DiscardingSink.class.getSimpleName());
    }
}
