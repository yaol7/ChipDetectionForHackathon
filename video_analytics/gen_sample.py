import random
import pravega_client
from datetime import datetime
import json
import time
import argparse

class GenFactoryLineData:
    random_type = "0000011"
    random_len = "0000000112333456666"
    factories = {"shanghai": [121.25150018654568, 31.360173390813358],
                        "chengdu": [104.23791193434835, 30.575431742628915],
                        "shenzhen": [113.90143419461378, 22.58097453057117] }

    defects_prob = { "shanghai-1": ["0", "0000000002333456666"],
                            "shanghai-2": ["0", "0000000"],
                            "shanghai-3": ["0", "000000000011233"],
                            "shanghai-4": ["0", "0000000001122344"],
                            "shanghai-5": ["0", "000000000112333456666"],
                            "chengdu-1": ["000001", "00000001111111"],
                            "chengdu-2": ["000001", "1111233345666677"],
                            "chengdu-3": ["000001", "00000000112333456666"],
                            "chengdu-4": ["000001", "0000000011233345"],
                            "chengdu-5": ["000001", "000000002333456666"],
                            "shenzhen-1": ["00111111", "000000000000000"],
                            "shenzhen-2": ["00111111", "0000000000000112"],
                            "shenzhen-3": ["00111111", "00000000000000001"],
                            "shenzhen-4": ["111111", "00000000011112334466"],
                            "shenzhen-5": ["111111", "00000000011233345666677"] }
    
    def __init__(self, factory, line):
        self.frame_number = 0
        self.factory = factory
        self.line = line
        self.scope_name = "chipdetect"
        self.stream_name = "chipresults"
        self.stream_manager = pravega_client.StreamManager("tcp://172.17.0.1:9090")
        scope_result = self.stream_manager.create_scope(self.scope_name)
        print(scope_result)
        stream_result = self.stream_manager.create_stream(self.scope_name, self.stream_name, 1) # initially stream contains 1 segment
        print(stream_result)
        

    
    def save_pravega(self, metadata): 
        data = json.dumps(metadata)
        writer = self.stream_manager.create_writer(self.scope_name,self.stream_name)
        writer.write_event(data)

    def genOne(self):
        meta = dict()
        if self.factory == "all":
            factory = random.choice(list(self.factories.keys()))
        else:
            factory = self.factory
        location = self.factories[factory.lower()]
        if self.line == -1:
            line = random.randint(1, 5)
        else:
            line = self.line
        key = factory + "-" + str(line)
        defects = []
        len = int(random.choice(self.defects_prob[key][1]))
        for i in range(len):
            x = random.randint(0, 1850)
            y = random.randint(0, 1140)
            defects.append({'type': random.choice(self.defects_prob[key][0]), 'tl': [x, y], 'br': [x + random.randint(40,60), y + random.randint(30, 50)]})

        meta["encoding_level"] = 95
        meta["version"] = "1"
        meta["img_handle"] = "0f757a7812"
        meta["topic"] = self.stream_name
        meta["width"] = 1920
        meta["height"] = 1200
        meta["encoding_type"] = "jpeg"
        meta["defects"] = defects 
        meta["defectsLen"] = len

        meta["frame_number"] = self.frame_number 
        self.frame_number = self.frame_number + 1

        meta["timestamp"] = datetime.utcnow().isoformat()[:-3]+'Z'

        meta["factory"] = factory
        meta["production_line"] = line
        meta["location"] = location
        print(meta)
        return meta


if __name__ == "__main__":

    ap = argparse.ArgumentParser()
    ap.add_argument("-f", "--factory", required=False, help="shanghai chengdu shenzhen", action='store', type=str)
    ap.add_argument("-l", "--line", required=False, help="", action='store', type=str)
    ap.add_argument("-n", "--number", required=False, help="", action='store', type=str)

    factory = "all"
    line = -1
    number = 1
    args = vars(ap.parse_args())
    if args["factory"] is not None:
        factory = args["factory"]
    if args["line"] is not None:
        line = args["line"]
    if args["number"] is not None:
        number = args["number"]


    gen = GenFactoryLineData(factory, line)

    for i in range(int(number)):
        time.sleep(0.1)
        metadata = gen.genOne()
        gen.save_pravega(metadata)  


    
