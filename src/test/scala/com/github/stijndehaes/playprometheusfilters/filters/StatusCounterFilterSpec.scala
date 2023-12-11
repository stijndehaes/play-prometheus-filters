package com.github.stijndehaes.playprometheusfilters.filters

import com.github.stijndehaes.playprometheusfilters.mocks.MockController
import io.prometheus.client.CollectorRegistry
import org.apache.pekko.stream.Materializer
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.verify
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration
import play.api.mvc.Results
import play.api.test.Helpers.stubControllerComponents
import play.api.test.{DefaultAwaitTimeout, FakeRequest, FutureAwaits}

import scala.concurrent.ExecutionContext.Implicits.global

class StatusCounterFilterSpec extends AnyWordSpec with Matchers with MockitoSugar with Results with DefaultAwaitTimeout with FutureAwaits with GuiceOneAppPerSuite {

  private implicit val mat: Materializer = app.materializer
  private val configuration = mock[Configuration]

  "Filter constructor" should {
    "Add a counter to the prometheus registry" in {
      val collectorRegistry = mock[CollectorRegistry]
      new StatusCounterFilter(collectorRegistry, configuration)
      verify(collectorRegistry).register(any())
    }
  }

  "Apply method" should {
    "Count the requests with status" in {
      val filter = new StatusCounterFilter(mock[CollectorRegistry], configuration)
      val rh = FakeRequest()
      val action = new MockController(stubControllerComponents()).ok

      await(filter(action)(rh).run())

      val metrics = filter.metrics.head.metric.collect()
      metrics must have size 1
      val samples = metrics.get(0).samples
      samples.get(0).value mustBe 1.0
      samples.get(0).labelValues must have size 1
      samples.get(0).labelValues.get(0) mustBe "200"
    }
  }

}
