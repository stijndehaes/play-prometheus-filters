package com.github.stijndehaes.playprometheusfilters.filters

import akka.stream.Materializer
import com.github.stijndehaes.playprometheusfilters.metrics.DefaultUnmatchedDefaults
import com.github.stijndehaes.playprometheusfilters.metrics.LatencyRequestMetrics.RouteLatencyRequestMetricsBuilder
import com.google.inject.{Inject, Singleton}
import io.prometheus.client.CollectorRegistry

import scala.concurrent.ExecutionContext

@Singleton
class RouteLatencyFilter @Inject()(registry: CollectorRegistry)(implicit mat: Materializer, ec: ExecutionContext) extends MetricsFilter {

  override val metrics = List(
    RouteLatencyRequestMetricsBuilder.build(registry, DefaultUnmatchedDefaults)
  )
}
