package com.github.stijndehaes.playprometheusfilters.filters

import akka.stream.Materializer
import com.github.stijndehaes.playprometheusfilters.metrics.CounterRequestMetrics.CounterRequestMetricBuilder
import com.github.stijndehaes.playprometheusfilters.metrics.DefaultUnmatchedDefaults
import com.github.stijndehaes.playprometheusfilters.metrics.LatencyRequestMetrics.LatencyRequestMetricsBuilder
import com.google.inject.{Inject, Singleton}
import io.prometheus.client._

import scala.concurrent.ExecutionContext

@Singleton
class StatusAndRouteLatencyAndCounterFilter @Inject()(registry: CollectorRegistry)(implicit mat: Materializer, ec: ExecutionContext) extends MetricsFilter {

  override val metrics = List(
    LatencyRequestMetricsBuilder.build(registry, DefaultUnmatchedDefaults),
    CounterRequestMetricBuilder.build(registry, DefaultUnmatchedDefaults)
  )
}
