package com.github.stijndehaes.playprometheusfilters.filters
import akka.stream.Materializer
import com.github.stijndehaes.playprometheusfilters.metrics.RequestMetric
import io.prometheus.client.Collector
import play.api.mvc.{Filter, RequestHeader, Result}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Generic filter implementation to add metrics for a request.
  * Subclasses only have to define the `metrics` property to apply metrics.
  *
  * ```
  * @Singleton
  * class MyFilter @Inject()(registry: CollectorRegistry)(implicit mat: Materializer, ec: ExecutionContext) extends MetricsFilter {
  *
  *   override val metrics = List(
  *     LatencyOnlyRequestMetricsBuilder.build(registry, DefaultUnmatchedDefaults)
  *   )
  * }
  * ```
  *
  * Metrics can be created by using a [[com.github.stijndehaes.playprometheusfilters.metrics.RequestMetricBuilder]].
  * The builder creates and configures the metrics for the class instance.
  *
  * See [[com.github.stijndehaes.playprometheusfilters.metrics.CounterRequestMetrics]] and
  * [[com.github.stijndehaes.playprometheusfilters.metrics.LatencyRequestMetrics]] for some provided
  * builders.
  *
  * @param mat
  * @param ec
  */
abstract class MetricsFilter(implicit val mat: Materializer, ec: ExecutionContext) extends Filter {

  val metrics: List[RequestMetric[_]]

  def apply(nextFilter: RequestHeader => Future[Result])
           (requestHeader: RequestHeader): Future[Result] = {

    val startTime = System.nanoTime

    nextFilter(requestHeader).map { implicit result =>
      implicit val rh = requestHeader
      val endTime = System.nanoTime
      val requestTime = (endTime - startTime) / Collector.NANOSECONDS_PER_SECOND

      metrics.foreach(_.mark(requestTime))

      result
    }
  }
}
