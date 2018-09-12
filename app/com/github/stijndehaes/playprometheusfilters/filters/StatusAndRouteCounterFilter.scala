package com.github.stijndehaes.playprometheusfilters.filters

import akka.stream.Materializer
import com.github.stijndehaes.playprometheusfilters.metrics.CounterRequestMetrics.CounterRequestMetricBuilder
import com.github.stijndehaes.playprometheusfilters.metrics.DefaultUnmatchedDefaults
import com.google.inject.{Inject, Singleton}
import io.prometheus.client.CollectorRegistry

import scala.concurrent.ExecutionContext

@Singleton
class StatusAndRouteCounterFilter @Inject()(registry: CollectorRegistry)(implicit mat: Materializer, ec: ExecutionContext) extends MetricsFilter {

  override val metrics = List(
    CounterRequestMetricBuilder.build(registry, DefaultUnmatchedDefaults)
  )
}
