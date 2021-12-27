package com.github.mbode.flink_prometheus_example;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.DiscardingSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrometheusExampleJob {
  private static final Logger log = LoggerFactory.getLogger(PrometheusExampleJob.class);
  private final ParameterTool parameters;

  public static void main(String[] args) throws Exception {
    new PrometheusExampleJob(ParameterTool.fromArgs(args)).run();
  }

  private PrometheusExampleJob(ParameterTool parameters) {
    this.parameters = parameters;
  }

  private void run() throws Exception {
    log.info("metrics job is stating.");
    final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.enableCheckpointing(500);
    env.disableOperatorChaining();

    env.addSource(new RandomSourceFunction(parameters.getInt("elements", Integer.MAX_VALUE)))
        .name(RandomSourceFunction.class.getSimpleName())
        .map(new FlinkMetricsExposingMapFunction())
        .name(FlinkMetricsExposingMapFunction.class.getSimpleName())
        .addSink(new DiscardingSink<>())
        .name(DiscardingSink.class.getSimpleName());
    //env.execute(PrometheusExampleJob.class.getSimpleName());
    log.info("metrics job is summitted!!!");

    log.info("start read metadata from Pravega stream of flink job.");
    //final StreamExecutionEnvironment newEnv = StreamExecutionEnvironment.getExecutionEnvironment();
    PravegaReadJob job = PravegaReadJob.getInstance();
    //job.readStream(env, parameters, "dataScope", "metaStream");
    log.info("!!!!!!!!!!!!! my person test case...");
    DataStream<PravegaReadJob.Person> flintstones = env.fromElements(
            new PravegaReadJob.Person("Fred", 35),
            new PravegaReadJob.Person("Wilma", 35),
            new PravegaReadJob.Person("Pebbles", 2));

    DataStream<PravegaReadJob.Person> adults = flintstones.filter((FilterFunction<PravegaReadJob.Person>) person -> person.age >= 18);
    adults.print();

    env.execute(PrometheusExampleJob.class.getSimpleName());
    log.info("$$$$$$$$$$$$$ flink job is summited..");
  }
}
