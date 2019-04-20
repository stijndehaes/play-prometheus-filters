package com.github.stijndehaes.playprometheusfilters

import io.prometheus.client.CollectorRegistry
import org.scalatest.{BeforeAndAfter, MustMatchers, WordSpec}
import org.slf4j.LoggerFactory
import play.api.inject.guice.GuiceApplicationBuilder

class PrometheusModuleSpec extends WordSpec with MustMatchers with BeforeAndAfter {

  before {
    // clearing registry before each test
    CollectorRegistry.defaultRegistry.clear()
  }

  private val logger = LoggerFactory.getLogger(classOf[PrometheusModuleSpec])

  "PrometheusModule" should {
    "register default exporters when enabled" in {
      // default enabled
      val app = new GuiceApplicationBuilder()
        .configure(PrometheusModule.defaultExportsKey -> true)
        .build()
      val collector = app.injector.instanceOf[CollectorRegistry]
      logger.info(s"More elements: ${collector.metricFamilySamples.hasMoreElements}")
      collector.metricFamilySamples.hasMoreElements mustBe true
    }

    "not register default exporters when disabled" in {
      // disable default exporters
      val app = new GuiceApplicationBuilder()
        .configure(PrometheusModule.defaultExportsKey -> false)
        .build()

      val collector = app.injector.instanceOf[CollectorRegistry]
      collector.metricFamilySamples.hasMoreElements mustBe false
    }
  }

}
