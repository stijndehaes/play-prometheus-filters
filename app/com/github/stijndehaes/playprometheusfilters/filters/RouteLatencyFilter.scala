package com.github.stijndehaes.playprometheusfilters.filters

import akka.stream.Materializer
import com.google.inject.{Inject, Singleton}
import io.prometheus.client.{CollectorRegistry, Histogram}
import play.api.mvc.{Filter, RequestHeader, Result}
import play.api.routing.Router

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RouteLatencyFilter @Inject()(registry: CollectorRegistry) (implicit val mat: Materializer, ec: ExecutionContext) extends Filter {

  private[filters] val requestLatency = Histogram.build
    .name("requests_latency_seconds")
    .help("Request latency in seconds.")
    .labelNames("RouteActionMethod")
    .register(registry)

  def apply(nextFilter: RequestHeader => Future[Result])
    (requestHeader: RequestHeader): Future[Result] = {
    val routeLabel = requestHeader.attrs
      .get(Router.Attrs.HandlerDef)
      .map(_.method)
      .getOrElse(RouteLatencyFilter.unmatchedRoute)
    val requestTimer = requestLatency.labels(routeLabel).startTimer
    nextFilter(requestHeader).map { result =>
      requestTimer.observeDuration()
      result
    }
  }

}

object RouteLatencyFilter {
  val unmatchedRoute: String = "unmatchedRoute"
}