package com.github.stijndehaes.playprometheusfilters.filters

import akka.stream.Materializer
import com.google.inject.{Inject, Singleton}
import io.prometheus.client.{Collector, CollectorRegistry, Histogram}
import play.api.mvc.{Filter, RequestHeader, Result}
import play.api.routing.Router.Tags

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StatusAndRouteLatencyFilter @Inject()(registry: CollectorRegistry) (implicit val mat: Materializer, ec: ExecutionContext) extends Filter {

  private[filters] val requestLatency = Histogram.build
    .name("requests_latency_seconds")
    .help("Request latency in seconds.")
    .labelNames("RouteActionMethod", "Status")
    .register(registry)

  def apply(nextFilter: RequestHeader => Future[Result])
    (requestHeader: RequestHeader): Future[Result] = {

    val startTime = System.nanoTime

    nextFilter(requestHeader).map { result =>
      val endTime = System.nanoTime
      val requestTime = endTime - startTime / Collector.NANOSECONDS_PER_SECOND
      val routeLabel = requestHeader.tags.getOrElse(Tags.RouteActionMethod, RouteLatencyFilter.unmatchedRoute)
      val statusLabel = result.header.status.toString
      requestLatency.labels(routeLabel, statusLabel).observe(requestTime)
      result
    }
  }

}

object StatusAndRouteLatencyFilter {
  val unmatchedRoute: String = "unmatchedRoute"
}