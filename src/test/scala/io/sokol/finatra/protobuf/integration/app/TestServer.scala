package io.sokol.finatra.protobuf.integration.app

import com.google.inject.Module
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.CommonFilters
import com.twitter.finatra.http.routing.HttpRouter
import io.sokol.finatra.protobuf.ProtobufMessageModule

object TestServer$ extends TestServer

class TestServer extends HttpServer {

  override def defaultFinatraHttpPort = ":9999"

  override def messageBodyModule: Module = ProtobufMessageModule

  override def configureHttp(router: HttpRouter) {
    router
      .filter[CommonFilters]
      .add[HealthController]
  }

}
