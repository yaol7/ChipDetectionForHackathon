package com.dellemc.flink.hackthaon.pro.metrics;

import com.dellemc.flink.hackthaon.pro.ChipMetadata;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.Gauge;

public class ChipMetadataMetricsExposingMapFunction extends RichMapFunction<ChipMetadata, ChipMetadata> {
    private Counter eventCounter;
    private Gauge defectsLenGauge;
    private ChipMetadata chipMetadata;

    @Override
    public void open(Configuration parameters) {
        eventCounter = getRuntimeContext().getMetricGroup().counter("receive_events");
        //defectsLenGauge = getRuntimeContext().getMetricGroup().gauge("defects_len", new DefectsLenGauge(chipMetadata));
    }

    @Override
    public ChipMetadata map(ChipMetadata chipMetadata) throws Exception {
        eventCounter.inc();
        this.chipMetadata = chipMetadata;

        return chipMetadata;
    }

    public static class DefectsLenGauge implements Gauge<Integer> {
        private final ChipMetadata chipMetadata;

        public DefectsLenGauge(ChipMetadata chipMetadata) {
            this.chipMetadata = chipMetadata;
        }

        @Override
        public Integer getValue() {
            return chipMetadata.getDefectsLen();
        }
    }
}
