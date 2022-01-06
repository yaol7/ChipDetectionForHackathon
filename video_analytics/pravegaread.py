import pravega_client
import asyncio

class PravegaClient:
    # assuming Pravega controller is listening at 127.0.0.1:9090
    def write():
        stream_manager = pravega_client.StreamManager("tcp://172.17.0.1:9090")
        scope_result = stream_manager.create_scope("chipdetect")
        print(scope_result)
        stream_result = stream_manager.create_stream("chipdetect", "chipresults", 1) # initially stream contains 1 segment
        print(stream_result)
        writer = stream_manager.create_writer("chipdetect","chipresults")
        writer.write_event("hello world 2")
        writer.flush()
        print("write complete")


    async def read():
        stream_manager = pravega_client.StreamManager("tcp://172.17.0.1:9090")
        reader_group = stream_manager.create_reader_group("rg1", "chipdetect", "chipresults")
        reader = reader_group.create_reader("reader1")
        # acquire a segment slice to read
        slice = await reader.get_segment_slice_async()
        print(slice)
        for event in slice:
            print(event.data())
            
        # after calling release segment, data in this segment slice will not be read again by
        # readers in the same reader group.
        reader.release_segment(slice)

        # remember to mark the finished reader as offline.
        reader.reader_offline()

PravegaClient.write()
asyncio.run(PravegaClient.read())