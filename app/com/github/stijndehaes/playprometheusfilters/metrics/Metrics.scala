package com.github.stijndehaes.playprometheusfilters.metrics
import io.prometheus.client.{CollectorRegistry, Counter, Histogram, SimpleCollector}
import play.api.mvc.{RequestHeader, Result}
import play.api.routing.Router

/**
  * Generic request metric.
  * Provides convenience methods for getting labels from the request or response.
  *
  * @tparam M Type of metric. E.g. Counter or Histogram.
  */
trait RequestMetric[M <: SimpleCollector[_]] {
  val metric: M
  val unmatchedDefaults: UnmatchedDefaults

  def mark(requestTime: Double)(implicit requestHeader: RequestHeader, result: Result): Unit

  def pathLabel(implicit requestHeader: RequestHeader): String = requestHeader.attrs
    .get(Router.Attrs.HandlerDef)
    .map(_.path)
    .getOrElse(unmatchedDefaults.unmatchedPath(requestHeader))

  def methodLabel(implicit requestHeader: RequestHeader): String = requestHeader.attrs
    .get(Router.Attrs.HandlerDef)
    .map(_.method)
    .getOrElse(unmatchedDefaults.unmatchedRoute(requestHeader))

  def statusLabel(implicit result: Result) = result.header.status.toString

  def controllerLabel(implicit requestHeader: RequestHeader): String = requestHeader.attrs
    .get(Router.Attrs.HandlerDef)
    .map(_.controller)
    .getOrElse(unmatchedDefaults.unmatchedController(requestHeader))

  def verbLabel(implicit requestHeader: RequestHeader): String = requestHeader.attrs
    .get(Router.Attrs.HandlerDef)
    .map(_.verb)
    .getOrElse(unmatchedDefaults.unmatchedVerb(requestHeader))

  def routeLabel(implicit requestHeader: RequestHeader): String = requestHeader.attrs
    .get(Router.Attrs.HandlerDef)
    .map(_.method)
    .getOrElse(unmatchedDefaults.unmatchedRoute(requestHeader))
}

/**
  * Counter metric implementation.
  */
abstract class CounterRequestMetric(val metric: Counter, val unmatchedDefaults: UnmatchedDefaults) extends RequestMetric[Counter]

/**
  * Latency metric implementation using a histogram.
  */
abstract class LatencyRequestMetric(val metric: Histogram, val unmatchedDefaults: UnmatchedDefaults) extends RequestMetric[Histogram]

/**
  * A generic request metric builder.
  * @tparam RM Type of request metric.
  */
trait RequestMetricBuilder[RM <: RequestMetric[_]] {
  def build(registry: CollectorRegistry, unmatchedDefaults: UnmatchedDefaults): RM
}

/**
  * Provides some builders counter request metrics.
  *
  *  - StatusCounterRequestMetricBuilder: count with response status code label
  *  - CounterRequestMetricBuilder: count requests with labels method, status, controller, path and verb
  */
object CounterRequestMetrics {

  /**
    * Count requests with status label
    */
  object StatusCounterRequestMetricBuilder extends RequestMetricBuilder[CounterRequestMetric] {
    override def build(registry: CollectorRegistry, unmatchedDefaults: UnmatchedDefaults): CounterRequestMetric = {
      val counter = Counter.build()
        .name("http_requests_total")
        .help("Total amount of requests")
        .labelNames("status")
        .register(registry)
      return new CounterRequestMetric(counter, unmatchedDefaults) {
        override def mark(requestTime: Double)(implicit requestHeader: RequestHeader, result: Result): Unit = {
          counter.labels(statusLabel).inc()
        }
      }
    }
  }


  /**
    * Count requests with labels method, status, controller, path and verb
    */
  object CounterRequestMetricBuilder extends RequestMetricBuilder[CounterRequestMetric] {
    override def build(registry: CollectorRegistry, unmatchedDefaults: UnmatchedDefaults): CounterRequestMetric = {
      val counter = Counter.build()
        .name("http_requests_total")
        .help("Total amount of requests")
        .labelNames("method", "status", "controller", "path", "verb")
        .register(registry)
      return new CounterRequestMetric(counter, unmatchedDefaults) {
        override def mark(requestTime: Double)(implicit requestHeader: RequestHeader, result: Result): Unit = {
          counter.labels(methodLabel, statusLabel, controllerLabel, pathLabel, verbLabel).inc()
        }
      }
    }
  }
}

/**
  * Provides some builders latency request metrics.
  *
  *  - LatencyRequestMetricsBuilder: observe latency with route, status, controller, path and verb labels.
  *  - LatencyOnlyRequestMetricsBuilder: only observe latency. No labels.
  *  - RouteLatencyRequestMetricsBuilder: observe latency with only route label.
  */
object LatencyRequestMetrics {
  /**
    * Observe latency with route, status, controller, path and verb labels.
    */
  object LatencyRequestMetricsBuilder extends RequestMetricBuilder[LatencyRequestMetric] {
    override def build(registry: CollectorRegistry, unmatchedDefaults: UnmatchedDefaults) = {
      val metric = Histogram.build
        .name("requests_latency_seconds")
        .help("Request latency in seconds.")
        .labelNames("RouteActionMethod", "Status", "Controller", "Path", "Verb")
        .register(registry)

      new LatencyRequestMetric(metric, unmatchedDefaults) {
        override def mark(requestTime: Double)(implicit requestHeader: RequestHeader, result: Result): Unit = {
          metric.labels(routeLabel, statusLabel, controllerLabel, pathLabel, verbLabel).observe(requestTime)
        }
      }
    }
  }

  /**
    * Only observe latency. No labels.
    */
  object LatencyOnlyRequestMetricsBuilder extends RequestMetricBuilder[LatencyRequestMetric] {
    override def build(registry: CollectorRegistry, unmatchedDefaults: UnmatchedDefaults) = {
      val metric = Histogram.build
        .name("requests_latency_seconds")
        .help("Request latency in seconds.")
        .register(registry)
      new LatencyRequestMetric(metric, unmatchedDefaults) {
        override def mark(requestTime: Double)(implicit requestHeader: RequestHeader, result: Result): Unit = {
          metric.observe(requestTime)
        }
      }
    }
  }

  /**
    * Observe latency with only route label.
    */
  object RouteLatencyRequestMetricsBuilder extends RequestMetricBuilder[LatencyRequestMetric] {
    override def build(registry: CollectorRegistry, unmatchedDefaults: UnmatchedDefaults) = {
      val metric = Histogram.build
        .name("requests_latency_seconds")
        .help("Request latency in seconds.")
        .labelNames("RouteActionMethod")
        .register(registry)

      new LatencyRequestMetric(metric, unmatchedDefaults) {
        override def mark(requestTime: Double)(implicit requestHeader: RequestHeader, result: Result): Unit = {
          metric.labels(routeLabel).observe(requestTime)
        }
      }
    }
  }
}