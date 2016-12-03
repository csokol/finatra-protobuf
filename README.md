#finatra-protobuf

Provides [Protocol Buffers](https://developers.google.com/protocol-buffers/) integration
for [Finatra](http://twitter.github.io/finatra/) framework. 

Also supports JSON encoded messages using [JsonFormat class] (https://developers.google.com/protocol-buffers/docs/reference/java/com/google/protobuf/util/JsonFormat).

##Installation

The library is not available on any Maven repository, yet. To install, clone the project and build it:

````bash
git clone git@github.com:csokol/finatra-protobuf.git
cd finatra-protobuf
sbt publishLocal
````

##Usage

Simply override the default `messageBodyModule` at your server configuration:

```scala
class AppServer extends HttpServer {

  //...

  override def messageBodyModule: Module = ProtobufMessageModule

  //...
}
```

See https://github.com/csokol/finatra-protobuf/tree/master/src/test/scala/io/sokol/finatra/protobuf/integration/app/ 
for a sample application.
