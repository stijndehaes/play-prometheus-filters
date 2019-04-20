package com.github.stijndehaes.playprometheusfilters.filters

import com.github.stijndehaes.playprometheusfilters.mocks.MockController
import io.prometheus.client.CollectorRegistry
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test.{DefaultAwaitTimeout, FakeRequest, FutureAwaits}

import scala.concurrent.ExecutionContext.Implicits.global

class LatencyFilterSpec extends PlaySpec with MockitoSugar with Results with DefaultAwaitTimeout with FutureAwaits with GuiceOneAppPerSuite {

  val configuration = mock[Configuration]

  "Filter constructor" should {
    "Add a histogram to the prometheus registry" in {
      implicit val mat = app.materializer
      val collectorRegistry = mock[CollectorRegistry]
      new LatencyFilter(collectorRegistry, configuration)
      verify(collectorRegistry).register(any())
    }
  }

  "Apply method" should {
    "Measure the latency" in {
      implicit val mat = app.materializer
      val filter = new LatencyFilter(mock[CollectorRegistry], configuration)
      val rh = FakeRequest()
      val action = new MockController(stubControllerComponents()).ok

      await(filter(action)(rh).run())

      val metrics = filter.metrics(0).metric.collect()
      metrics must have size 1
      val samples = metrics.get(0).samples
      //this is the count sample
      val countSample = samples.get(samples.size() - 2)
      countSample.value mustBe 1.0
      countSample.labelValues must have size 0
    }
  }

}
