package com.github.stijndehaes.playprometheusfilters.controllers

import org.apache.pekko.util.ByteString
import com.github.stijndehaes.playprometheusfilters.utils.WriterAdapter
import javax.inject._
import play.api.mvc._
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.common.TextFormat
import org.slf4j.LoggerFactory
import play.api.http.HttpEntity

/**
  * A Play controller implementation to return collected metrics.
  * Use this controller to create an endpoint which can be scraped by Prometheus.
  *
  * Add to your `routes.conf`:
  * {{{
  *   # Prometheus metrics
  *   GET  /metrics  io.github.stijndehaes.playprometheusfilters.controllers.PrometheusController.getMetrics
  * }}}
  */
class PrometheusController @Inject()(registry: CollectorRegistry, cc: ControllerComponents) extends AbstractController(cc) {

  private val logger = LoggerFactory.getLogger(classOf[PrometheusController])

  def getMetrics = Action {
    logger.trace("Metrics call received")
    val samples = new StringBuilder()
    val writer = new WriterAdapter(samples)
    TextFormat.write004(writer, registry.metricFamilySamples())
    writer.close()

    Result(
      header = ResponseHeader(200, Map.empty),
      body = HttpEntity.Strict(ByteString(samples.toString), Some(TextFormat.CONTENT_TYPE_004))
    )
  }

}