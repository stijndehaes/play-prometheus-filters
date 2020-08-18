package dk.jyllandsposten.playprometheusfilters.filters
import akka.stream.Materializer
import dk.jyllandsposten.playprometheusfilters.metrics.RequestMetric
import io.prometheus.client.Collector
import play.api.Configuration
import play.api.mvc.{Filter, RequestHeader, Result}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Generic filter implementation to add metrics for a request.
  * Subclasses only have to define the `metrics` property to apply metrics.
  *
  * {{{
  * @Singleton
  * class MyFilter @Inject()(registry: CollectorRegistry)(implicit mat: Materializer, ec: ExecutionContext) extends MetricsFilter {
  *
  *   override val metrics = List(
  *     LatencyOnlyRequestMetricsBuilder.build(registry, DefaultUnmatchedDefaults)
  *   )
  * }
  * }}}
  *
  * Metrics can be created by using a [[dk.jyllandsposten.playprometheusfilters.metrics.RequestMetricBuilder RequestMetricBuilder]].
  * The builder creates and configures the metrics for the class instance.
  *
  * See [[dk.jyllandsposten.playprometheusfilters.metrics.CounterRequestMetrics CounterRequestMetrics]] and
  * [[dk.jyllandsposten.playprometheusfilters.metrics.LatencyRequestMetrics LatencyRequestMetrics]] for some provided
  * builders.
  *
  * @param mat
  * @param ec
  */
abstract class MetricsFilter(configuration: Configuration)(implicit val mat: Materializer, ec: ExecutionContext) extends Filter {

  val metrics: List[RequestMetric[_, RequestHeader, Result]]

  val excludePaths = {
    import collection.JavaConverters._
    Option(configuration.underlying)
      .map(_.getStringList("play-prometheus-filters.exclude.paths"))
      .map(_.asScala.toSet)
      .map(_.map(_.r))
      .getOrElse(Set.empty)
  }

  def apply(nextFilter: RequestHeader => Future[Result])
           (requestHeader: RequestHeader): Future[Result] = {

    // check if current uri is excluded from metrics
    def urlIsExcluded = excludePaths.exists(_.findFirstMatchIn(requestHeader.uri).isDefined)

    val startTime = System.nanoTime

    nextFilter(requestHeader).map { implicit result =>
      implicit val rh = requestHeader

      if (!urlIsExcluded) {
        val endTime = System.nanoTime
        val requestTime = (endTime - startTime) / Collector.NANOSECONDS_PER_SECOND

        metrics.foreach(_.mark(requestTime))
      }

      result
    }
  }
}
