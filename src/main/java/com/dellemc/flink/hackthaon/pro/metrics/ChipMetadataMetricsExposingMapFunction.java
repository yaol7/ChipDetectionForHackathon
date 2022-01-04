package com.dellemc.flink.hackthaon.pro.metrics;

import com.dellemc.flink.hackthaon.pro.ChipMetadata;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.Gauge;
import org.apache.flink.metrics.Histogram;
import org.apache.flink.runtime.metrics.DescriptiveStatisticsHistogram;

public class ChipMetadataMetricsExposingMapFunction extends RichMapFunction<ChipMetadata, Integer> {
    private static final long serialVersionUID = 1L;

    private transient Counter eventCounter;
    private transient int defectsLen_1;
    private transient int defectsLen_2;
    private transient int defectsLen_3;

    @Override
    public void open(Configuration parameters) {
        eventCounter = getRuntimeContext().getMetricGroup().counter("events");
        getRuntimeContext()
                .getMetricGroup()
                .gauge("defects_len_1", (Gauge<Integer>) () -> defectsLen_1);
        getRuntimeContext()
                .getMetricGroup()
                .gauge("defects_len_2", (Gauge<Integer>) () -> defectsLen_2);
        getRuntimeContext()
                .getMetricGroup()
                .gauge("defects_len_3", (Gauge<Integer>) () -> defectsLen_3);

    }

    @Override
    public Integer map(ChipMetadata chipMetadata) {
        eventCounter.inc();
        switch (chipMetadata.getProduction_line()) {
            case "1":
                defectsLen_1 = chipMetadata.getDefectsLen();
                break;
            case "2":
                defectsLen_2 = chipMetadata.getDefectsLen();
                break;
            case "3":
                defectsLen_3 = chipMetadata.getDefectsLen();
                break;
            default:
        }

        return chipMetadata.getDefectsLen();
    }
}
