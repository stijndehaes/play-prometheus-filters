package dk.jyllandsposten.playprometheusfilters.filters

import akka.stream.Materializer
import dk.jyllandsposten.playprometheusfilters.metrics.DefaultPlayUnmatchedDefaults
import dk.jyllandsposten.playprometheusfilters.metrics.LatencyRequestMetrics.LatencyOnlyRequestMetricsBuilder
import io.prometheus.client.CollectorRegistry
import javax.inject.{Inject, Singleton}
import play.api.Configuration

import scala.concurrent.ExecutionContext

/**
  * A simple [[MetricsFilter]] using a histogram metric to record latency without any labels.
  */
@Singleton
class LatencyFilter @Inject()(registry: CollectorRegistry, configuration: Configuration)(implicit mat: Materializer, ec: ExecutionContext) extends MetricsFilter(configuration) {

  override val metrics = List(
    LatencyOnlyRequestMetricsBuilder.build(registry, DefaultPlayUnmatchedDefaults)
  )
}