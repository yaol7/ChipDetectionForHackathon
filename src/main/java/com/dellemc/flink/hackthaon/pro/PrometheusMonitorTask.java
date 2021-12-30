package com.dellemc.flink.hackthaon.pro;

import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.DiscardingSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrometheusMonitorTask {
    private static Logger log = LoggerFactory.getLogger(PrometheusMonitorTask.class);

    private PrometheusMonitorTask() {}

    private static class Singleton {
        private static PrometheusMonitorTask INSTANCE = new PrometheusMonitorTask();
    }

    public static PrometheusMonitorTask getInstance() {
        return Singleton.INSTANCE;
    }


    public void run(StreamExecutionEnvironment env, ParameterTool parameters) {
        log.info("process the prometheus monitor task");
        env.addSource(new RandomSourceFunction(parameters.getInt("elements", Integer.MAX_VALUE)))
                .name(RandomSourceFunction.class.getSimpleName())
                .map(new FlinkMetricsExposingMapFunction())
                .name(FlinkMetricsExposingMapFunction.class.getSimpleName())
                .addSink(new DiscardingSink<>())
                .name(DiscardingSink.class.getSimpleName());
    }
}
