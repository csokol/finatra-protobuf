package io.sokol.finatra.protobuf.integration

import java.io.InputStream

import com.google.common.net.MediaType
import com.twitter.finagle.http.Status.Ok
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.EmbeddedHttpServer
import com.twitter.inject.server.FeatureTest
import com.twitter.util.Await
import io.sokol.finatra.protobuf.integration.app.TestServer
import io.sokol.finatra.protobuf.messages.Protos.Health
import io.sokol.finatra.protobuf.messages.Protos.Health.Status
import io.sokol.finatra.protobuf.support.RequestFactory
import org.apache.commons.io.IOUtils

class ProtobufMarshalingFeatureTest extends FeatureTest {

  override val server = new EmbeddedHttpServer(new TestServer,
    flags = Map(
      "http.response.charset.enabled" -> "true"
    ))

  "HealthController" should {
    "marshal proto as json" in {
      val response = server.httpGet(
        path = "/marshal",
        andExpect = Ok,
        headers = Map("Accept" -> "application/json")
      )
      val body: String = extractBody(response)
      val rawJson = body.replaceAll("\\s", "")
      assert(rawJson == """{"status":"OK"}""")
    }

    "unmarshal proto as json" in {
      server.httpPost(
        path = "/unmarshal",
        postBody = """{"status":"OK"}""",
        andExpect = Ok,
        headers = Map("Accept" -> "application/json")
      )
    }

    "marshal binary proto" in {
      val response = server.httpGet(
        path = "/marshal",
        andExpect = Ok,
        headers = Map("Accept" -> MediaType.PROTOBUF.toString)
      )
      val payload: Health = parse[Health](response)
      assert(payload.getStatus == Status.OK)
    }

    "marshal and unmarshal binary proto" in {
      val health = Health.newBuilder().setStatus(Status.OK).build()

      val request = RequestFactory.post("/unmarshal", health)
      val response = execute(request)

      assert(response.status.code == 200)
    }
  }

  def parse[T: Manifest](response: Response)(implicit manifest: Manifest[T]): T = {
    val messageClass = manifest.runtimeClass
    val parseMethod = messageClass.getMethod("parseFrom", classOf[InputStream])
    parseMethod.invoke(null, response.getInputStream()).asInstanceOf[T]
  }

  def execute(request: Request): Response = {
    Await.result(server.httpClient(request))
  }

  def extractBody(response: Response): String = {
    response.encodeString()
    val body = IOUtils.toString(response.getInputStream())
    body
  }

}
