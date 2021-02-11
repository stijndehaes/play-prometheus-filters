package com.github.stijndehaes.playprometheusfilters

import java.util
import com.github.stijndehaes.playprometheusfilters.helpers.PrivateMethodExposer
import io.prometheus.client.{Collector, CollectorRegistry}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.{BeforeAndAfter, PrivateMethodTester}
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.inject.guice.GuiceApplicationBuilder

class PrometheusModuleSpec extends AnyWordSpec with Matchers with BeforeAndAfter with PrivateMethodTester with GuiceOneAppPerTest {

  before {
    // clearing registry before each test
    CollectorRegistry.defaultRegistry.clear()
  }

  "PrometheusModule" should {
    "not register default exporters when disabled" in {
      // disable default exporters
      val app = new GuiceApplicationBuilder()
        .configure(PrometheusModule.defaultExportsKey -> false)
        .build()

      val collector = app.injector.instanceOf[CollectorRegistry]
      val collectors: util.HashSet[Collector] = PrivateMethodExposer(collector)(Symbol("collectors"))().asInstanceOf[java.util.HashSet[Collector]]//PrivateMethod[java.util.HashSet[Collector]](Symbol(collectors))
      collectors.size must be (0)
    }
  }

  /**
    * Utility to expose exporter names for test on [[CollectorRegistry]].
    */
  implicit class CollectorRegistryExtention(val registry: CollectorRegistry) {
    /**
      * @return Registered exporter names.
      */
    def getExporterNames: collection.Seq[String] = {
      val exportNames = collection.mutable.Buffer.empty[String]
      val mfs = registry.metricFamilySamples()
      while(mfs.hasMoreElements) {
        exportNames += mfs.nextElement().name
      }
      exportNames
    }
  }
}
