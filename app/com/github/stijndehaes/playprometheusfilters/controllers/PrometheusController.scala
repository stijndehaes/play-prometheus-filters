package com.github.stijndehaes.playprometheusfilters.controllers

import akka.util.ByteString
import com.github.stijndehaes.playprometheusfilters.utils.WriterAdapter
import javax.inject._
import play.api.mvc._
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.common.TextFormat
import play.api.http.HttpEntity

class PrometheusController @Inject()(registry: CollectorRegistry, cc: ControllerComponents) extends AbstractController(cc) {

  def getMetrics = Action {
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