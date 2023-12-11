package com.github.stijndehaes.playprometheusfilters.filters

import com.github.stijndehaes.playprometheusfilters.metrics.LatencyRequestMetrics.LatencyOnlyRequestMetricsBuilder
import com.github.stijndehaes.playprometheusfilters.metrics.{DefaultPlayUnmatchedDefaults, LatencyRequestMetric}
import io.prometheus.client.CollectorRegistry
import org.apache.pekko.actor.ActorSystem
import play.api.Configuration

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import org.apache.pekko.stream.Materializer

/**
  * A simple [[MetricsFilter]] using a histogram metric to record latency without any labels.
  */
@Singleton
class LatencyFilter @Inject()(registry: CollectorRegistry, configuration: Configuration)(implicit val actorSystem: ActorSystem, ec: ExecutionContext) extends MetricsFilter(configuration) {

  override implicit val mat: Materializer = Materializer(actorSystem)

  override val metrics: List[LatencyRequestMetric] = List(
    LatencyOnlyRequestMetricsBuilder.build(registry, DefaultPlayUnmatchedDefaults)
  )
}