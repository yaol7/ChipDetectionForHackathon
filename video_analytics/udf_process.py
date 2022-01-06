from logging import Logger
from common.video.udfs.python.pcb.pcb_classifier import Udf
import cv2
import json
import logging
import pravega_client
import numpy
from datetime import datetime
import os

os.chdir(os.path.dirname(os.path.abspath(__file__)))

scope_name = "chipdetect"
stream_name = "chipresults"
stream_manager = pravega_client.StreamManager("tcp://172.17.0.1:9090")
scope_result = stream_manager.create_scope(scope_name)
print(scope_result)
stream_result = stream_manager.create_stream(scope_name, stream_name, 1) # initially stream contains 1 segment
print(stream_result)
writer = stream_manager.create_writer(scope_name, stream_name)

video_raw = "intel.avi"
video_processed = "intel_processed.avi"

factory = "shanghai"
line = 1
factories = {"shanghai": [121.25150018654568, 31.360173390813358],
            "chengdu": [104.23791193434835, 30.575431742628915],
            "shenzhen": [113.90143419461378, 22.58097453057117] }


def save_pravega(meta, frame_number): 
    meta["encoding_level"] = 95
    meta["version"] = "1"
    meta["img_handle"] = "0f757a7813"
    meta["stream"] = stream_name
    meta["width"] = 1920
    meta["height"] = 1200
    meta["encoding_type"] = "jpeg"
    meta["defectsLen"] = len(meta["defects"])

    meta["frame_number"] = frame_number 

    meta["timestamp"] = datetime.utcnow().isoformat()[:-3]+'Z'

    meta["factory"] = factory
    meta["production_line"] = line
    meta["location"] = factories[factory]
    data = json.dumps(meta)
    print(meta)
    writer.write_event(data)


logger = logging.getLogger("image_analyzer")
bad_color=(0, 0, 255)
udf = Udf("common/video/udfs/python/pcb/ref/ref.png","common/video/udfs/python/pcb/ref/roi_2.json","common/video/udfs/python/pcb/ref/model_2.xml","common/video/udfs/python/pcb/ref/model_2.bin","CPU")
cap = cv2.VideoCapture(video_raw)
print(cap.get(cv2.CAP_PROP_POS_MSEC))
print(cap.get(cv2.CAP_PROP_POS_FRAMES))
print(cap.get(cv2.CAP_PROP_POS_AVI_RATIO))
print(cap.get(cv2.CAP_PROP_FOURCC))
print(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
print(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))
print(cap.get(cv2.CAP_PROP_FPS))

appsink2file = "appsrc ! autovideoconvert ! omxh265enc ! matroskamux ! filesink location=/test001.mkv "

pravegasink = "appsrc \
                ! decodebin  \
                ! videoconvert \
                ! queue \
                ! x264enc key-int-max=150 tune=zerolatency speed-preset=medium bitrate=500 \
                ! queue \
                ! mpegtsmux alignment=-1 \
                ! pravegasink stream=chipdetect/video-processed seal=false sync=false timestamp-mode=realtime-clock"

pravegasink = "appsrc \
                ! decodebin  \
                ! videoconvert \
                ! queue \
                ! x264enc key-int-max=150 tune=zerolatency speed-preset=medium bitrate=500 \
                ! pravegasink stream=chipdetect/video-processed controller=172.17.0.1:9090 seal=false sync=false timestamp-mode=realtime-clock"

autovideosink = "appsrc \
                ! decodebin \
                ! autovideoconvert \
                ! autovideosink sync=false "

fourcc = cv2.VideoWriter_fourcc('X', 'V', 'I', 'D')       
#out = cv2.VideoWriter(video_processed, fourcc, cap.get(cv2.CAP_PROP_FPS), (1920,1200), True)
out2 = cv2.VideoWriter(pravegasink, 0, cap.get(cv2.CAP_PROP_FPS), (1920,1200), True)
if not out2.isOpened():
    print("Failed to open output")
    exit()

flag = True
frame_num = 1
while(cap.isOpened() and flag):
    flag, frame = cap.read()
    frame_num = frame_num + 1
    cv2.namedWindow("output", cv2.WINDOW_NORMAL)
    if flag == False:
        cap.set(cv2.CAP_PROP_POS_FRAMES, 0)
        flag, frame = cap.read()

    if flag == True:
        frame_raw = frame.copy()
        metadata = dict()
        udf.process(frame,metadata)
        save_pravega(metadata, frame_num)
        # vis.draw_defect(metadata, frame)
        if 'defects' in metadata:
            for defect in metadata['defects']:
                # Get tuples for top-left and bottom-right coordinates
                top_left = tuple(defect['tl'])
                bottom_right = tuple(defect['br'])
                 
                # Draw bounding box
                cv2.rectangle(frame, top_left, bottom_right, bad_color, 2)
                    # Create window with freedom of dimensions
        imghstack = numpy.hstack((frame_raw, frame))
        cv2.resizeWindow("output", 960, 300)    
        cv2.imshow("output", numpy.array(imghstack, dtype = numpy.uint8))
        #out.write(frame)
        out2.write(frame)
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break
        
        print(metadata)

cap.release()
#out.release()
out2.release()
cv2.destroyAllWindows()
