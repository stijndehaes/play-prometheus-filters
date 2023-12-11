package com.github.stijndehaes.playprometheusfilters.filters

import com.github.stijndehaes.playprometheusfilters.metrics.DefaultPlayUnmatchedDefaults
import com.github.stijndehaes.playprometheusfilters.metrics.LatencyRequestMetrics.LatencyRequestMetricsBuilder
import io.prometheus.client.CollectorRegistry
import org.apache.pekko.stream.Materializer
import play.api.Configuration

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

/**
  * A [[MetricsFilter]] using a histogram metric to record latency.
  * Latency metric adds 'RouteActionMethod', 'Status', 'Controller', 'Path' and 'Verb' labels.
  */
@Singleton
class StatusAndRouteLatencyFilter @Inject()(registry: CollectorRegistry, configuration: Configuration)(implicit mat: Materializer, ec: ExecutionContext) extends MetricsFilter(configuration) {

  override val metrics = List(
    LatencyRequestMetricsBuilder.build(registry, DefaultPlayUnmatchedDefaults)
  )
}
