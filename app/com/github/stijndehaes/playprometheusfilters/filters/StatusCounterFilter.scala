package com.github.stijndehaes.playprometheusfilters.filters

import akka.stream.Materializer
import com.github.stijndehaes.playprometheusfilters.metrics.CounterRequestMetrics.StatusCounterRequestMetricBuilder
import com.github.stijndehaes.playprometheusfilters.metrics.DefaultUnmatchedDefaults
import com.google.inject.Singleton
import io.prometheus.client.CollectorRegistry
import javax.inject.Inject

import scala.concurrent.ExecutionContext

@Singleton
class StatusCounterFilter @Inject()(registry: CollectorRegistry)(implicit mat: Materializer, ec: ExecutionContext) extends MetricsFilter {

  override val metrics = List(
    StatusCounterRequestMetricBuilder.build(registry, DefaultUnmatchedDefaults)
  )
}