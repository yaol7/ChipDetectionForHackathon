package com.dellemc.flink.hackthaon.pro.metrics;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.Histogram;
import org.apache.flink.runtime.metrics.DescriptiveStatisticsHistogram;

public class FlinkMetricsExposingMapFunction extends RichMapFunction<Integer, Integer> {
    private static final long serialVersionUID = 1L;

    private transient Counter eventCounter;
    private transient Histogram defectesHistogram;

    @Override
    public void open(Configuration parameters) {
        eventCounter = getRuntimeContext().getMetricGroup().counter("events");
        defectesHistogram =
                getRuntimeContext()
                        .getMetricGroup()
                        .histogram("defects_len", new DescriptiveStatisticsHistogram(10_000));
    }

    @Override
    public Integer map(Integer value) {
        eventCounter.inc();
        defectesHistogram.update(value);

        return value;
    }
}
