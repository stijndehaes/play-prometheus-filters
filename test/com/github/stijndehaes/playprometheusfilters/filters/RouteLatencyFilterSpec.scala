package com.github.stijndehaes.playprometheusfilters.filters

import com.github.stijndehaes.playprometheusfilters.mocks.MockController
import io.prometheus.client.CollectorRegistry
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.verify
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.typedmap.TypedMap
import play.api.mvc._
import play.api.routing.{HandlerDef, Router}
import play.api.test.Helpers.stubControllerComponents
import play.api.test.{DefaultAwaitTimeout, FakeRequest, FutureAwaits}

import scala.concurrent.ExecutionContext.Implicits.global

class RouteLatencyFilterSpec extends WordSpec with MustMatchers with MockitoSugar with Results with DefaultAwaitTimeout with FutureAwaits with GuiceOneAppPerSuite  {

  private implicit val mat = app.materializer

  "Filter constructor" should {
    "Add a histogram to the prometheus registry" in {
      val collectorRegistry = mock[CollectorRegistry]
      new RouteLatencyFilter(collectorRegistry)
      verify(collectorRegistry).register(any())
    }
  }

  "Apply method" should {
    "Measure the latency" in {
      val filter = new RouteLatencyFilter(mock[CollectorRegistry])
      val rh = FakeRequest().withAttrs( TypedMap(
        Router.Attrs.HandlerDef -> HandlerDef(null, null, null, "test", null, null ,null ,null ,null)
      ))
      val action = new MockController(stubControllerComponents()).ok

      await(filter(action)(rh).run())

      val metrics = filter.requestLatency.collect()
      metrics must have size 1
      val samples = metrics.get(0).samples
      //this is the count sample
      val countSample = samples.get(samples.size() - 2)
      countSample.value mustBe 1.0
      countSample.labelValues must have size 1
      countSample.labelValues.get(0) mustBe "test"
    }

    "Measure the latency for an unmatched route" in {
      val filter = new RouteLatencyFilter(mock[CollectorRegistry])
      val rh = FakeRequest()
      val action = new MockController(stubControllerComponents()).error

      await(filter(action)(rh).run())

      val metrics = filter.requestLatency.collect()
      metrics must have size 1
      val samples = metrics.get(0).samples
      //this is the count sample
      val countSample = samples.get(samples.size() - 2)
      countSample.value mustBe 1.0
      countSample.labelValues must have size 1
      countSample.labelValues.get(0) mustBe RouteLatencyFilter.unmatchedRoute
    }
  }

}
