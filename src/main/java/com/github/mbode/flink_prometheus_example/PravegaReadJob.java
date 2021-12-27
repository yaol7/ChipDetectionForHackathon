package com.github.mbode.flink_prometheus_example;

import io.pravega.connectors.flink.FlinkPravegaReader;
import io.pravega.connectors.flink.PravegaConfig;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PravegaReadJob {
    private static final Logger log = LoggerFactory.getLogger(PravegaReadJob.class);

    private PravegaReadJob() {
    }

    private static class Singleton {
        private static PravegaReadJob INSTANCE = new PravegaReadJob();
    }

    public static PravegaReadJob getInstance() {
        return Singleton.INSTANCE;
    }

    public void readStream(StreamExecutionEnvironment env, ParameterTool params, final String scope, final String streamName) throws Exception {
        /*log.info("start receiving data from scope: {}, stream: {}", scope, streamName);
        PravegaConfig pravegaConfig = PravegaConfig
                .fromParams(params)
                .withDefaultScope(scope);
        FlinkPravegaReader<String> source = FlinkPravegaReader.<String>builder()
                .withPravegaConfig(pravegaConfig)
                .forStream(streamName)
                .withDeserializationSchema(new SimpleStringSchema())
                .build();
        DataStream<String> dataStream = env.addSource(source).name("Pravega Stream Read Job");
        dataStream.print();

        env.execute("Pravega Stream Read Job");*/
        log.info("!!!!!!!!!!!!! my person test case...");
        DataStream<Person> flintstones = env.fromElements(
                new Person("Fred", 35),
                new Person("Wilma", 35),
                new Person("Pebbles", 2));

        DataStream<Person> adults = flintstones.filter((FilterFunction<Person>) person -> person.age >= 18);
        adults.print();

        env.execute();

    }

    public static class Person {
        public String name;
        public Integer age;
        public Person() {}

        public Person(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        public String toString() {
            return this.name.toString() + ": $$$$$$$$$$$$$$$$$$ age " + this.age.toString();
        }
    }
}
