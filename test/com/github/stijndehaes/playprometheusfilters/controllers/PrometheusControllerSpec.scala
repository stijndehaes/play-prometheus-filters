package com.github.stijndehaes.playprometheusfilters.controllers

import java.util.Collections

import io.prometheus.client.Collector.MetricFamilySamples
import io.prometheus.client.{Collector, CollectorRegistry}
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.mvc.Results
import play.api.test.FakeRequest
import play.api.test.Helpers._


class PrometheusControllerSpec extends PlaySpec with Results with MockitoSugar {

  "Get metrics method" should {
    "Return the prometheus metrics" in {
      val collectorRegistry = mock[CollectorRegistry]
      val metricsFamilySample = new MetricFamilySamples("test", Collector.Type.COUNTER, "help", Collections.emptyList())
      when(collectorRegistry.metricFamilySamples()).thenReturn(new java.util.Vector(Collections.singleton(metricsFamilySample)).elements)

      val client = new PrometheusController(collectorRegistry)

      val request = FakeRequest(GET, "/metrics")

      val result = client.getMetrics.apply(request)
      status(result) mustBe OK
      contentAsString(result) mustBe "# HELP test help\n# TYPE test counter\n"
    }
  }

}
