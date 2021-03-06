plugins {
    java
    jacoco

    id("org.unbroken-dome.test-sets") version "4.0.0"
    id("com.github.johnrengelman.shadow") version "7.1.1"
    id("com.diffplug.spotless") version "6.0.5"
    id("com.github.spotbugs") version "5.0.3"
    id("com.avast.gradle.docker-compose") version "0.14.11"
    id("com.github.ben-manes.versions") version "0.39.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories { mavenCentral() }

testSets { create("integrationTest") }

dependencies {
    val flinkVersion = "1.11.6"
    compileOnly("org.apache.flink:flink-java:$flinkVersion")
    compileOnly("org.apache.flink:flink-streaming-java_2.11:$flinkVersion")
    implementation("org.projectlombok:lombok:1.18.22")
    implementation("io.pravega:pravega-connectors-flink-1.11_2.12:0.10.1")
    implementation("org.apache.flink:flink-connector-elasticsearch7_2.12:1.14.2")

    val junitVersion = "5.8.2"
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.apache.flink:flink-test-utils_2.11:$flinkVersion")
    testImplementation("org.assertj:assertj-core:3.21.0")
    testImplementation("org.mockito:mockito-junit-jupiter:4.2.0")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testRuntimeOnly("org.slf4j:slf4j-simple:1.7.32")

    val integrationTestImplementation by configurations
    integrationTestImplementation("com.mashape.unirest:unirest-java:1.4.9")
    integrationTestImplementation("org.awaitility:awaitility:4.1.1")
    integrationTestImplementation("com.github.docker-java:docker-java:3.2.12")
}

tasks {
    withType(Test::class).configureEach { useJUnitPlatform() }
    "jacocoTestReport"(JacocoReport::class) {
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }
    "check" { dependsOn("jacocoTestReport") }
}

dockerCompose {
    isRequiredBy(tasks["integrationTest"])
    projectName = null
}

spotless {
    java { googleJavaFormat() }
    kotlinGradle { ktlint() }
}
