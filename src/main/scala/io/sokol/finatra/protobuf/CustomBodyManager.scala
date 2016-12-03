package io.sokol.finatra.protobuf

import java.io.{InputStream, InputStreamReader}
import javax.inject.Singleton

import com.google.common.net.MediaType.{JSON_UTF_8, PROTOBUF}
import com.google.protobuf.GeneratedMessageV3.Builder
import com.google.protobuf.Message
import com.google.protobuf.util.JsonFormat
import com.google.protobuf.util.JsonFormat.Printer
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.marshalling.{DefaultMessageBodyReader, DefaultMessageBodyWriter, WriterResponse}
import com.twitter.inject.{InjectorModule, TwitterModule}

object ProtobufMessageModule extends TwitterModule {
  flag("http.response.charset.enabled", "true", "charset must be enabled")

  override val modules = Seq(InjectorModule)

  override def configure() {
    bindSingleton[DefaultMessageBodyReader].to[FinatraDefaultMessageBodyReader]
    bindSingleton[DefaultMessageBodyWriter].to[FinatraDefaultMessageBodyWriter]
  }

  @Singleton
  class FinatraDefaultMessageBodyReader extends DefaultMessageBodyReader {
    val parser = JsonFormat.parser()

    override def parse[T: Manifest](request: Request): T = {
      def messageClass(implicit manifest: Manifest[T]): Class[_] = manifest.runtimeClass
      val contentType = request.headerMap.getOrElse("Content-Type", JSON_UTF_8.toString)

      val parsed = contentType.split(";")(0) match {
        case "application/json" => jsonToProto(request, messageClass)
        case "application/protobuf" => unmarshalProto(request, messageClass)
      }

      parsed.asInstanceOf[T]
    }

    def jsonToProto(request: Request, messageClass: Class[_]) = {
      val builder = messageClass.getMethod("newBuilder")
        .invoke(messageClass).asInstanceOf[Builder[_]]
      parser.merge(new InputStreamReader(request.getInputStream()), builder)
      builder.build()
    }

    def unmarshalProto(request: Request, messageClass: Class[_]) = {
      val parseMethod = messageClass.getMethod("parseFrom", classOf[InputStream])
      parseMethod.invoke(null, request.getInputStream())
    }
  }

  class FinatraDefaultMessageBodyWriter extends DefaultMessageBodyWriter {
    val jsonPrinter: Printer = JsonFormat.printer()

    override def write(obj: Any): WriterResponse = {
      ??? //should never be called
    }

    override def write(request: Request, obj: Any): WriterResponse = {
      val accept = request.headerMap.getOrElse("Accept", JSON_UTF_8.toString)
      val message = obj.asInstanceOf[Message]
      accept match {
        case "application/protobuf" => marshalProto(message)
        case _ => protoAsJson(message)
      }
    }

    def protoAsJson(msg: Message) = {
      WriterResponse(JSON_UTF_8, jsonPrinter.print(msg))
    }

    def marshalProto(msg: Message) = {
      WriterResponse(PROTOBUF, msg.toByteArray)
    }
  }
}
