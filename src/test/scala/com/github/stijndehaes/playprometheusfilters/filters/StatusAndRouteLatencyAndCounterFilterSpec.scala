package com.github.stijndehaes.playprometheusfilters.filters

import com.github.stijndehaes.playprometheusfilters.filters.StatusAndRouteLatencyAndCounterFilter
import com.github.stijndehaes.playprometheusfilters.metrics.DefaultPlayUnmatchedDefaults
import com.github.stijndehaes.playprometheusfilters.mocks.MockController
import io.prometheus.client.CollectorRegistry
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.verify
import org.mockito.Mockito.times
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration
import play.api.libs.typedmap.TypedMap
import play.api.mvc.Results
import play.api.routing.{HandlerDef, Router}
import play.api.test.Helpers.stubControllerComponents
import play.api.test.{DefaultAwaitTimeout, FakeRequest, FutureAwaits}

import scala.concurrent.ExecutionContext.Implicits.global

class StatusAndRouteLatencyAndCounterFilterSpec extends AnyWordSpec with Matchers with MockitoSugar with Results with DefaultAwaitTimeout with FutureAwaits with GuiceOneAppPerSuite  {

  private implicit val mat = app.materializer
  private val configuration = mock[Configuration]

  "Filter constructor" should {
    "Add a histogram and count to the prometheus registry" in {
      val collectorRegistry = mock[CollectorRegistry]
      new StatusAndRouteLatencyAndCounterFilter(collectorRegistry, configuration)
      verify(collectorRegistry, times(2)).register(any())
    }
  }

  "Apply method" should {
    "Measure the latency and count" in {
      val filter = new StatusAndRouteLatencyAndCounterFilter(mock[CollectorRegistry], configuration)
      val rh = FakeRequest().withAttrs( TypedMap(
        Router.Attrs.HandlerDef -> HandlerDef(null, null, "testController", "test", null, "GET", "/path", null ,null)
      ))
      val action = new MockController(stubControllerComponents()).ok

      await(filter(action)(rh).run())

      val latencyMetrics = filter.metrics(0).metric.collect()
      latencyMetrics must have size 1
      val latencySamples = latencyMetrics.get(0).samples
      val latencySample = latencySamples.get(latencySamples.size() - 2)
      latencySample.value mustBe 1.0
      latencySample.labelValues must have size 5
      latencySample.labelValues.get(0) mustBe "test"
      latencySample.labelValues.get(1) mustBe "200"
      latencySample.labelValues.get(2) mustBe "testController"
      latencySample.labelValues.get(3) mustBe "/path"
      latencySample.labelValues.get(4) mustBe "GET"

      val countMetrics = filter.metrics(1).metric.collect()
      countMetrics must have size 1
      val countSamples = countMetrics.get(0).samples
      val countSample = countSamples.get(0)
      countSample.value mustBe 1.0
      countSample.labelValues must have size 5
      countSample.labelValues.get(0) mustBe "test"
      countSample.labelValues.get(1) mustBe "200"
      countSample.labelValues.get(2) mustBe "testController"
      countSample.labelValues.get(3) mustBe "/path"
      countSample.labelValues.get(4) mustBe "GET"
    }

    "Measure the latency and count for an unmatched route" in {
      val filter = new StatusAndRouteLatencyAndCounterFilter(mock[CollectorRegistry], configuration)
      val rh = FakeRequest()
      val action = new MockController(stubControllerComponents()).error

      await(filter(action)(rh).run())

      val latencyMetrics = filter.metrics(0).metric.collect()
      latencyMetrics must have size 1
      val latencySamples = latencyMetrics.get(0).samples
      val latencySample = latencySamples.get(latencySamples.size() - 2)

      latencySample.value mustBe 1.0
      latencySample.labelValues must have size 5
      latencySample.labelValues.get(0) mustBe DefaultPlayUnmatchedDefaults.UnmatchedRouteString
      latencySample.labelValues.get(1) mustBe "404"
      latencySample.labelValues.get(2) mustBe DefaultPlayUnmatchedDefaults.UnmatchedControllerString
      latencySample.labelValues.get(3) mustBe DefaultPlayUnmatchedDefaults.UnmatchedPathString
      latencySample.labelValues.get(4) mustBe DefaultPlayUnmatchedDefaults.UnmatchedVerbString

      val countMetrics = filter.metrics(1).metric.collect()
      countMetrics must have size 1
      val countSamples = countMetrics.get(0).samples
      val countSample = countSamples.get(0)
      countSample.value mustBe 1.0
      countSample.labelValues must have size 5
      countSample.labelValues.get(0) mustBe DefaultPlayUnmatchedDefaults.UnmatchedRouteString
      countSample.labelValues.get(1) mustBe "404"
      countSample.labelValues.get(2) mustBe DefaultPlayUnmatchedDefaults.UnmatchedControllerString
      countSample.labelValues.get(3) mustBe DefaultPlayUnmatchedDefaults.UnmatchedPathString
      countSample.labelValues.get(4) mustBe DefaultPlayUnmatchedDefaults.UnmatchedVerbString
    }
  }
}
