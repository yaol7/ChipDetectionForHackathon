package com.dellemc.flink.hackthaon.pro.metrics;

import com.dellemc.flink.hackthaon.pro.ChipMetadata;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Counter;

public class EventsCountExposingMapFunction extends RichMapFunction<ChipMetadata, Integer> {
    private static final long serialVersionUID = 1L;

    private transient Counter events_count_1;
    private transient Counter events_count_2;
    private transient Counter events_count_3;
    private transient Counter events_count_4;
    private transient Counter events_count_5;

    @Override
    public void open(Configuration parameters) {
        events_count_1 = getRuntimeContext()
                .getMetricGroup()
                .counter("events_count_1");
        events_count_2 = getRuntimeContext()
                .getMetricGroup()
                .counter("events_count_2");
        events_count_3 = getRuntimeContext()
                .getMetricGroup()
                .counter("events_count_3");
        events_count_4 = getRuntimeContext()
                .getMetricGroup()
                .counter("events_count_4");
        events_count_5 = getRuntimeContext()
                .getMetricGroup()
                .counter("events_count_5");


    }

    @Override
    public Integer map(ChipMetadata chipMetadata) {
        switch (chipMetadata.getProduction_line()) {
            case "1":
                events_count_1.inc();
                break;
            case "2":
                events_count_2.inc();
                break;
            case "3":
                events_count_3.inc();
                break;
            case "4":
                events_count_4.inc();
                break;
            case "5":
                events_count_5.inc();
                break;
            default:
        }
        return chipMetadata.getDefectsLen();
    }
}
