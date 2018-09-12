package com.github.stijndehaes.playprometheusfilters.filters

import akka.stream.Materializer
import com.github.stijndehaes.playprometheusfilters.metrics.DefaultUnmatchedDefaults
import com.github.stijndehaes.playprometheusfilters.metrics.LatencyRequestMetrics.LatencyRequestMetricsBuilder
import javax.inject.{ Inject, Singleton }
import io.prometheus.client.CollectorRegistry
import play.api.Configuration

import scala.concurrent.ExecutionContext

@Singleton
class StatusAndRouteLatencyFilter @Inject()(registry: CollectorRegistry, configuration: Configuration)(implicit mat: Materializer, ec: ExecutionContext) extends MetricsFilter(configuration) {

  override val metrics = List(
    LatencyRequestMetricsBuilder.build(registry, DefaultUnmatchedDefaults)
  )
}
