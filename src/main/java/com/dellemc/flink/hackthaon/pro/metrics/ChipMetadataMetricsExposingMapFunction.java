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
    private transient Histogram defectesHistogram;
    private transient int defectsLen;

    @Override
    public void open(Configuration parameters) {
        eventCounter = getRuntimeContext().getMetricGroup().counter("events");
        defectesHistogram =
                getRuntimeContext()
                        .getMetricGroup()
                        .histogram("defects_len", new DescriptiveStatisticsHistogram(10_000));
        getRuntimeContext()
                .getMetricGroup()
                .gauge("defects_len_2", (Gauge<Integer>) () -> defectsLen);
    }

    @Override
    public Integer map(ChipMetadata chipMetadata) {
        eventCounter.inc();
        defectesHistogram.update(chipMetadata.getDefectsLen());
        this.defectsLen = chipMetadata.getDefectsLen();

        return chipMetadata.getDefectsLen();
    }
}
