package io.github.jyllandsposten.playprometheusfilters.filters

import akka.stream.Materializer
import io.github.jyllandsposten.playprometheusfilters.metrics.CounterRequestMetrics.CounterRequestMetricBuilder
import io.github.jyllandsposten.playprometheusfilters.metrics.DefaultPlayUnmatchedDefaults
import io.prometheus.client.CollectorRegistry
import javax.inject.{Inject, Singleton}
import play.api.Configuration

import scala.concurrent.ExecutionContext

/**
  * A [[MetricsFilter]] using a counter metric to count requests.
  * Adds a 'method', 'status', 'controller', 'path' and 'verb' labels.
  */
@Singleton
class StatusAndRouteCounterFilter @Inject()(registry: CollectorRegistry, configuration: Configuration)(implicit mat: Materializer, ec: ExecutionContext) extends MetricsFilter(configuration) {

  override val metrics = List(
    CounterRequestMetricBuilder.build(registry, DefaultPlayUnmatchedDefaults)
  )
}
