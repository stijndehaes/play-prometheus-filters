package com.github.stijndehaes.playprometheusfilters.controllers

import akka.util.ByteString
import com.github.stijndehaes.playprometheusfilters.utils.WriterAdapter
import com.google.inject.Inject
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.common.TextFormat
import org.slf4j.LoggerFactory
import play.api.http.HttpEntity
import play.api.mvc._

class PrometheusController @Inject()(registry: CollectorRegistry) extends Controller {

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