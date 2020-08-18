package io.github.jyllandsposten.playprometheusfilters.filters

import akka.stream.Materializer
import io.github.jyllandsposten.playprometheusfilters.metrics.DefaultPlayUnmatchedDefaults
import io.github.jyllandsposten.playprometheusfilters.metrics.LatencyRequestMetrics.RouteLatencyRequestMetricsBuilder
import io.prometheus.client.CollectorRegistry
import javax.inject.{Inject, Singleton}
import play.api.Configuration

import scala.concurrent.ExecutionContext

/**
  * A simple [[MetricsFilter]] using a counter metric to count requests.
  * Only adds a 'route' label.
  */
@Singleton
class RouteLatencyFilter @Inject()(registry: CollectorRegistry, configuration: Configuration)(implicit mat: Materializer, ec: ExecutionContext) extends MetricsFilter(configuration) {

  override val metrics = List(
    RouteLatencyRequestMetricsBuilder.build(registry, DefaultPlayUnmatchedDefaults)
  )
}
