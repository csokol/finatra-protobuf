package io.sokol.finatra.protobuf.integration.app

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import io.sokol.finatra.protobuf.messages.Protos.Health
import io.sokol.finatra.protobuf.messages.Protos.Health.Status

class HealthController extends Controller {

  get("/marshal") { request: Request =>
    Health.newBuilder()
      .setStatus(Status.OK)
      .build()
  }

  post("/unmarshal") { payload: Health =>
    payload
  }

}
