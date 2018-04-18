package com.github.stijndehaes.playprometheusfilters.filters

import akka.stream.Materializer
import com.google.inject.{Inject, Singleton}
import io.prometheus.client.{CollectorRegistry, Counter}
import play.api.mvc.{Filter, RequestHeader, Result}
import play.api.routing.Router

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StatusAndRouteCounterFilter @Inject()(registry: CollectorRegistry)(implicit val mat: Materializer, ec: ExecutionContext) extends Filter {

  private[filters] val requestCounter = Counter.build()
    .name("http_requests_total")
    .help("Total amount of requests")
    .labelNames("method", "status", "controller", "path", "verb")
    .register(registry)

  def apply(nextFilter: RequestHeader => Future[Result])
    (requestHeader: RequestHeader): Future[Result] = {

    nextFilter(requestHeader).map { result =>
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
      requestCounter.labels(methodLabel, statusLabel, controllerLabel, pathLabel, verbLabel).inc()
      result
    }
  }

}

object StatusAndRouteCounterFilter {
  val unmatchedRoute: String = "unmatchedRoute"
  val unmatchedController: String = "unmatchedController"
  val unmatchedPath: String = "unmatchedPath"
  val unmatchedVerb: String = "unmatchedVerb"
}



