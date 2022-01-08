package com.dellemc.flink.hackthaon.pro;

import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChipMetadataAnalysisJob {
    private static final Logger log = LoggerFactory.getLogger(ChipMetadataAnalysisJob.class);
    private final ParameterTool parameters;

    public static void main(String[] args) throws Exception {
        new ChipMetadataAnalysisJob(ParameterTool.fromArgs(args)).run();
    }

    private ChipMetadataAnalysisJob(ParameterTool parameters) {
        this.parameters = parameters;
    }

    private void run() throws Exception {
        log.info("metrics job is stating.");
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(500);

        //chipMetadata process
        ChipMetadataPravegaReadTask chipMetadataPravegaReadTask = ChipMetadataPravegaReadTask.getInstance();
        chipMetadataPravegaReadTask.run(env, parameters, "chipdetect", "chipresults");

        //sink chip metadata to ES in cloud.
        ChipMetadataSinkToESTask chipMetadataSinkToESTask = ChipMetadataSinkToESTask.getInstance();
        chipMetadataSinkToESTask.run(env, parameters, "chipdetect", "chipresults");

        env.execute(ChipMetadataAnalysisJob.class.getSimpleName());
        log.info("all the tasks is summit!");
    }
}
