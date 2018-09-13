package com.github.stijndehaes.playprometheusfilters.filters

import akka.stream.Materializer
import com.github.stijndehaes.playprometheusfilters.metrics.DefaultUnmatchedDefaults
import com.github.stijndehaes.playprometheusfilters.metrics.LatencyRequestMetrics.RouteLatencyRequestMetricsBuilder
import javax.inject.{ Inject, Singleton }
import io.prometheus.client.CollectorRegistry
import play.api.Configuration

import scala.concurrent.ExecutionContext

/**
  * A simple [[MetricsFilter]] using a counter metric to count requests.
  * Only adds a 'route' label.
  */
@Singleton
class RouteLatencyFilter @Inject()(registry: CollectorRegistry, configuration: Configuration)(implicit mat: Materializer, ec: ExecutionContext) extends MetricsFilter(configuration) {

  override val metrics = List(
    RouteLatencyRequestMetricsBuilder.build(registry, DefaultUnmatchedDefaults)
  )
}
