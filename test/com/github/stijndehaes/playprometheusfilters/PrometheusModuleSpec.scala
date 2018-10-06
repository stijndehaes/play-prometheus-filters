package com.github.stijndehaes.playprometheusfilters

import io.prometheus.client.CollectorRegistry
import org.scalatest.{BeforeAndAfter, MustMatchers, WordSpec}
import play.api.inject.guice.GuiceApplicationBuilder

class PrometheusModuleSpec extends WordSpec with MustMatchers with BeforeAndAfter {

  before {
    // clearing registry before each test
    CollectorRegistry.defaultRegistry.clear()
  }

  "PrometheusModule" should {
    "register default exporters when enabled" in {
      // default enabled
      val app = new GuiceApplicationBuilder()
        .configure(PrometheusModule.defaultExportsKey -> true)
        .build()

      val collector = app.injector.instanceOf[CollectorRegistry]
      collector.getExporterNames.size must be > 0
    }

    "not register default exporters when disabled" in {
      // disable default exporters
      val app = new GuiceApplicationBuilder()
        .configure(PrometheusModule.defaultExportsKey -> false)
        .build()

      val collector = app.injector.instanceOf[CollectorRegistry]
      collector.getExporterNames.size must be (0)
    }
  }

  /**
    * Utility to expose exporter names for test on [[CollectorRegistry]].
    */
  implicit class CollectorRegistryExtention(val registry: CollectorRegistry) {
    /**
      * @return Registered exporter names.
      */
    def getExporterNames: Seq[String] = {
      val exportNames = collection.mutable.Buffer.empty[String]
      val mfs = registry.metricFamilySamples()
      while(mfs.hasMoreElements) {
        exportNames += mfs.nextElement().name
      }
      exportNames
    }
  }
}
