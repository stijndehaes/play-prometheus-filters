package com.github.stijndehaes.playprometheusfilters.filters

import akka.stream.Materializer
import com.google.inject.{Inject, Singleton}
import io.prometheus.client.{Collector, CollectorRegistry, Histogram}
import play.api.mvc.{Filter, RequestHeader, Result}
import play.api.routing.Router

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ControllerStatusAndRouteLatencyFilter @Inject()(registry: CollectorRegistry)(implicit val mat: Materializer, ec: ExecutionContext) extends Filter {

  private[filters] val requestLatency = Histogram.build
    .name("requests_latency_seconds")
    .help("Request latency in seconds.")
    .labelNames("RouteActionMethod", "Status", "Controller")
    .register(registry)

  def apply(nextFilter: RequestHeader => Future[Result])
    (requestHeader: RequestHeader): Future[Result] = {

    val startTime = System.nanoTime

    nextFilter(requestHeader).map { result =>
      val endTime = System.nanoTime
      val requestTime = (endTime - startTime) / Collector.NANOSECONDS_PER_SECOND
      val routeLabel = requestHeader.attrs
        .get(Router.Attrs.HandlerDef)
        .map(_.method)
        .getOrElse(ControllerStatusAndRouteLatencyFilter.unmatchedRoute)
      val statusLabel = result.header.status.toString
      val controllerLabel = requestHeader.attrs
        .get(Router.Attrs.HandlerDef)
        .map(_.controller)
        .getOrElse(ControllerStatusAndRouteLatencyFilter.unmatchedController)
      requestLatency.labels(routeLabel, statusLabel, controllerLabel).observe(requestTime)
      result
    }
  }

}

object ControllerStatusAndRouteLatencyFilter {
  val unmatchedRoute: String = "unmatchedRoute"
  val unmatchedController: String = "unmatchedController"
}



