package io.sokol.finatra.protobuf.support

import com.google.common.net.MediaType
import com.google.protobuf.Message
import com.twitter.finagle.http.{Method, Request}

object RequestFactory {

  def post(path: String, message: Message): Request = {
    val request = Request(Method.Post, path)
    request.headerMap += ("Content-Type" -> MediaType.PROTOBUF.toString)
    request.headerMap += ("Accept" -> MediaType.PROTOBUF.toString)
    request.write(message.toByteArray)
    request
  }

}
