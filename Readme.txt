The course github repository of Stephane is at below
https://github.com/simplesteph/grpc-java-course


> grpc is more efficient
    > less size compared to json
    > less cpu intensive compared to json. json is cpu intensive
    > binary protocol - faster and efficient communication

>  uses protocol buffer and language interoperability
> HTTP2 is multiplexing -> both server and client can push messages in parallel over same TCP connection
    > supports header compression
> HTTPs is binary 

> HTTP2 is secure by default

> 4 types of APIs in grpc
    > unary -> request/response
    > server streaming -> client initiates request and server can keep sending data as and when available
    > client streaming -> client opens a streaming connection and keeps sending messages. server sends response when it can
    > bi-directional streaming

> In bi-directional streaming, the server is free to respond whenever it wants to (not necessarily to each message it 
    receives from client) and how many times it wants to respond



> Some of the real world grpc services
    - Google spanner (proto file below)
    https://github.com/googleapis/googleapis/blob/master/google/spanner/v1/spanner.proto
    - Google pubsub
    https://github.com/googleapis/googleapis/blob/master/google/pubsub/v1/pubsub.proto

> Take a look at gRPC gateway project for exposing grpc as REST

> Take a look at gRPC error codes. There are very few error codes unlike REST
    in the grpc website, take a look at error handling section
    http://avi.im/grpc-errors

> Timeouts are implemented usign deadlines. It is recommended to set deadlines for all client rpc calls.
    grpc.io/blog/deadlines 
> Take a look at grpc reflections and cli.. Reflection allows to interrogate the grpc server to know what services exist,
    what methods, message types etc. 
    https://github.com/grpc/grpc-java/blob/master/documentation/server-reflection-tutorial.md

    Basically we need to enable server reflection in server code..

    Use Evans CLI to interrogate teh grpc server..

    