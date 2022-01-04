[![Actions Status](https://github.com/mbode/flink-prometheus-example/workflows/Gradle/badge.svg)](https://github.com/mbode/flink-prometheus-example/actions)
[![Docker Hub](https://img.shields.io/docker/cloud/build/maximilianbode/flink-prometheus-example.svg)](https://hub.docker.com/r/maximilianbode/flink-prometheus-example)
[![codecov](https://codecov.io/gh/mbode/flink-prometheus-example/branch/master/graph/badge.svg)](https://codecov.io/gh/mbode/flink-prometheus-example)
[![Flink v1.14.2](https://img.shields.io/badge/flink-v1.14.2-blue.svg)](https://github.com/apache/flink/releases/tag/release-1.14.2)
[![Prometheus v2.31.1](https://img.shields.io/badge/prometheus-v2.31.1-blue.svg)](https://github.com/prometheus/prometheus/releases/tag/v2.31.1)

This repository is used for the user guide of Flink Forward Asia Hackathon 2021.
issue link: https://github.com/flink-china/flink-forward-asia-hackathon-2021/issues/21
## Getting Started

### Startup Pravega
```
git clone -b r0.10 https://github.com/yaol7/pravega.git
./gradlew startStandalone
```

### Startup ElasticSearch-7.16.2
```
git clone https://eos2git.cec.lab.emc.com/yaol7/flink-prometheus-example.git
cd docker/ElasticSearch
~/docker/ElasticSearch/# sudo docker-compose up -d
```
### Startup Video Detection
```
sudo docker run -itd  -e DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix devops-repo.isus.emc.com:8116/liuj84/python_ia:1.1 bash
sudo docker exec -it <docker-id> bash
cd /video_analytics
python udf_process.py
```

### Startup Flink Process in Edge/Core
```
cd ~/flink-prometheus-example
sudo docker-compose up -d
```

## Reference

- [Apache Flink](https://flink.apache.org)
- [Pravega](https://cncf.pravega.io/)
- [GStreamer](https://gstreamer.freedesktop.org/)
- [Prometheus](https://prometheus.io)
- [Grafana](https://grafana.com)
