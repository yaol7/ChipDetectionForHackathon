[![Actions Status](https://github.com/mbode/flink-prometheus-example/workflows/Gradle/badge.svg)](https://github.com/mbode/flink-prometheus-example/actions)
[![Docker Hub](https://img.shields.io/docker/cloud/build/maximilianbode/flink-prometheus-example.svg)](https://hub.docker.com/r/maximilianbode/flink-prometheus-example)
[![codecov](https://codecov.io/gh/mbode/flink-prometheus-example/branch/master/graph/badge.svg)](https://codecov.io/gh/mbode/flink-prometheus-example)
[![Flink v1.14.2](https://img.shields.io/badge/flink-v1.14.2-blue.svg)](https://github.com/apache/flink/releases/tag/release-1.14.2)
[![Prometheus v2.31.1](https://img.shields.io/badge/prometheus-v2.31.1-blue.svg)](https://github.com/prometheus/prometheus/releases/tag/v2.31.1)

This repository is used for the user guide of Flink Forward Asia Hackathon 2021.
issue link: https://github.com/flink-china/flink-forward-asia-hackathon-2021/issues/21
## Getting Started
```
Clone the repository
git clone https://eos2git.cec.lab.emc.com/yaol7/flink-prometheus-example.git
```

### Startup Pravega
```
cd ~/flink-prometheus-example/docker/pravega-docker
./up.sh
```

### Startup ElasticSearch-7.16.2
```
cd ~/flink-prometheus-example/docker/ElasticSearch
docker-compose up -d
```

### Startup Video Process
```
cd ~/flink-prometheus-example/docker/video-process
xhost +
docker-compose up
```

### Startup Flink Process
```
cd ~/flink-prometheus-example
sudo docker-compose up -d
```

## Play Processed Video from Pravega
Follow this guide [gstreamer-pravega](https://github.com/pravega/gstreamer-pravega) to install gstreamer-pravega
```
gst-launch-1.0 -v   pravegasrc   stream=chipdetect/video-processed controller=172.17.0.1:9090   start-mode=earliest ! decodebin ! videoconvert ! autovideosink sync=false
```

## Reference

- [Apache Flink](https://flink.apache.org)
- [Pravega](https://cncf.pravega.io/)
- [GStreamer](https://gstreamer.freedesktop.org/)
- [Prometheus](https://prometheus.io)
- [Grafana](https://grafana.com)
