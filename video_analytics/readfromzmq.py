import time
import zmq
context = zmq.Context()
socket = context.socket(zmq.SUB)
socket.connect("tcp://localhost:65013")
topicfilter = "camera1_stream_results"
socket.setsockopt_string(zmq.SUBSCRIBE, topicfilter)
string = socket.recv()  
print(string) 