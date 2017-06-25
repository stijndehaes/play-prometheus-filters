package com.github.stijndehaes.playprometheusfilters.controllers

import akka.util.ByteString
import com.github.stijndehaes.playprometheusfilters.utils.WriterAdapter
import com.google.inject.Inject
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.common.TextFormat
import play.api.http.HttpEntity
import play.api.mvc._

class PrometheusController @Inject()(registry: CollectorRegistry) extends Controller {

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