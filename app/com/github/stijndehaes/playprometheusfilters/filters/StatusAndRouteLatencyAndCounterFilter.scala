package com.github.stijndehaes.playprometheusfilters.filters

import akka.stream.Materializer
import com.google.inject.{Inject, Singleton}
import io.prometheus.client.{Collector, CollectorRegistry, Counter, Histogram}
import play.api.mvc.{Filter, RequestHeader, Result}
import play.api.routing.Router

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StatusAndRouteLatencyAndCounterFilter @Inject()(registry: CollectorRegistry)(implicit val mat: Materializer, ec: ExecutionContext) extends Filter {

  private[filters] val requestCounter = Counter.build()
    .name("http_requests_total")
    .help("Total amount of requests")
    .labelNames("method", "status", "controller", "path", "verb")
    .register(registry)

  private[filters] val requestLatency = Histogram.build
    .name("requests_latency_seconds")
    .help("Request latency in seconds.")
    .labelNames("RouteActionMethod", "Status", "Controller", "Path", "Verb")
    .register(registry)

  def apply(nextFilter: RequestHeader => Future[Result])
    (requestHeader: RequestHeader): Future[Result] = {

    val startTime = System.nanoTime

    nextFilter(requestHeader).map { result =>
      val endTime = System.nanoTime
      val requestTime = (endTime - startTime) / Collector.NANOSECONDS_PER_SECOND
      val methodLabel = requestHeader.attrs
        .get(Router.Attrs.HandlerDef)
        .map(_.method)
        .getOrElse(StatusAndRouteLatencyFilter.unmatchedRoute)
      val statusLabel = result.header.status.toString
      val controllerLabel = requestHeader.attrs
        .get(Router.Attrs.HandlerDef)
        .map(_.controller)
        .getOrElse(StatusAndRouteLatencyFilter.unmatchedController)
      val pathLabel = requestHeader.attrs
        .get(Router.Attrs.HandlerDef)
        .map(_.path)
        .getOrElse(StatusAndRouteLatencyFilter.unmatchedPath)
      val verbLabel = requestHeader.attrs
        .get(Router.Attrs.HandlerDef)
        .map(_.verb)
        .getOrElse(StatusAndRouteLatencyFilter.unmatchedVerb)
      requestLatency.labels(methodLabel, statusLabel, controllerLabel, pathLabel, verbLabel).observe(requestTime)
      requestCounter.labels(methodLabel, statusLabel, controllerLabel, pathLabel, verbLabel).inc()
      result
    }
  }

}

object StatusAndRouteLatencyAndCounterFilter {
  val unmatchedRoute: String = "unmatchedRoute"
  val unmatchedController: String = "unmatchedController"
  val unmatchedPath: String = "unmatchedPath"
  val unmatchedVerb: String = "unmatchedVerb"
}






