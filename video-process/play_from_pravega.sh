gst-launch-1.0 -v   pravegasrc   stream=chipdetect/video-processed controller=172.17.0.1:9090   start-mode=earliest ! decodebin ! videoconvert ! autovideosink sync=false
