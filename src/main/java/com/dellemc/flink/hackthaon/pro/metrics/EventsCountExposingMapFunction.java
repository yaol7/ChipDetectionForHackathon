package com.dellemc.flink.hackthaon.pro.metrics;

import com.dellemc.flink.hackthaon.pro.ChipMetadata;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.Gauge;

public class EventsCountExposingMapFunction extends RichMapFunction<ChipMetadata, ChipMetadata> {
    private static final long serialVersionUID = 1L;

    private transient Counter events_count_1;
    private transient Counter events_count_2;
    private transient Counter events_count_3;
    private transient Counter events_count_4;
    private transient Counter events_count_5;

    private transient Counter defect_chip_count_1;
    private transient Counter defect_chip_count_2;
    private transient Counter defect_chip_count_3;
    private transient Counter defect_chip_count_4;
    private transient Counter defect_chip_count_5;

    private transient float defect_rate_1;
    private transient float defect_rate_2;
    private transient float defect_rate_3;
    private transient float defect_rate_4;
    private transient float defect_rate_5;

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

        defect_chip_count_1 = getRuntimeContext()
                .getMetricGroup()
                .counter("defect_chip_count_1");
        defect_chip_count_2 = getRuntimeContext()
                .getMetricGroup()
                .counter("defect_chip_count_2");
        defect_chip_count_3 = getRuntimeContext()
                .getMetricGroup()
                .counter("defect_chip_count_3");
        defect_chip_count_4 = getRuntimeContext()
                .getMetricGroup()
                .counter("defect_chip_count_4");
        defect_chip_count_5 = getRuntimeContext()
                .getMetricGroup()
                .counter("defect_chip_count_5");

        getRuntimeContext()
                .getMetricGroup()
                .gauge("defect_rate_1", (Gauge<Float>) () -> defect_rate_1);
        getRuntimeContext()
                .getMetricGroup()
                .gauge("defect_rate_2", (Gauge<Float>) () -> defect_rate_2);
        getRuntimeContext()
                .getMetricGroup()
                .gauge("defect_rate_3", (Gauge<Float>) () -> defect_rate_3);
        getRuntimeContext()
                .getMetricGroup()
                .gauge("defect_rate_4", (Gauge<Float>) () -> defect_rate_4);
        getRuntimeContext()
                .getMetricGroup()
                .gauge("defect_rate_5", (Gauge<Float>) () -> defect_rate_5);

    }

    @Override
    public ChipMetadata map(ChipMetadata chipMetadata) {
        switch (chipMetadata.getProduction_line()) {
            case "1":
                events_count_1.inc();
                if (chipMetadata.getDefectsLen() > 0) {
                    defect_chip_count_1.inc();
                }
                if (events_count_1.getCount() > 0) {
                    defect_rate_1 = (float) (defect_chip_count_1.getCount() / (events_count_1.getCount() * 1.0));
                }
                break;
            case "2":
                events_count_2.inc();
                if (chipMetadata.getDefectsLen() > 0) {
                    defect_chip_count_2.inc();
                }
                if (events_count_2.getCount() > 0) {
                    defect_rate_2 = (float) (defect_chip_count_2.getCount() / (events_count_2.getCount() * 1.0));
                }
                break;
            case "3":
                events_count_3.inc();
                if (chipMetadata.getDefectsLen() > 0) {
                    defect_chip_count_3.inc();
                }
                if (events_count_3.getCount() > 0) {
                    defect_rate_3 = (float) (defect_chip_count_3.getCount() / (events_count_3.getCount() * 1.0));
                }
                break;
            case "4":
                events_count_4.inc();
                if (chipMetadata.getDefectsLen() > 0) {
                    defect_chip_count_4.inc();
                }
                if (events_count_4.getCount() > 0) {
                    defect_rate_4 = (float) (defect_chip_count_4.getCount() / (events_count_4.getCount() * 1.0));
                }
                break;
            case "5":
                events_count_5.inc();
                if (chipMetadata.getDefectsLen() > 0) {
                    defect_chip_count_5.inc();
                }
                if (events_count_5.getCount() > 0) {
                    defect_rate_5 = (float) (defect_chip_count_5.getCount() / (events_count_5.getCount() * 1.0));
                }
                break;
            default:
        }
        return chipMetadata;
    }
}
