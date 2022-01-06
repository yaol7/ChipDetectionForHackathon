import pravega_client
# assuming Pravega controller is listening at 127.0.0.1:9090
stream_manager = pravega_client.StreamManager("tcp://127.0.0.1:9090")

scope_result = stream_manager.create_scope("scope_foo")
print(scope_result)

stream_result = stream_manager.create_stream("scope_foo", "stream_bar", 1) # initially stream contains 1 segment
print(stream_result)

writer = stream_manager.create_writer("scope_foo","stream_bar")
writer.write_event("hello world")