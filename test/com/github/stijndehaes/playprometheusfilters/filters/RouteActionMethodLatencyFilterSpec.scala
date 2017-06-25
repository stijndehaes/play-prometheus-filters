package com.github.stijndehaes.playprometheusfilters.filters

import akka.stream.Materializer
import io.prometheus.client.CollectorRegistry
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.verify
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.Action
import play.api.test.{DefaultAwaitTimeout, FakeRequest, FutureAwaits}
import play.api.mvc._
import play.api.routing.Router.Tags

class RouteActionMethodLatencyFilterSpec extends WordSpec with MustMatchers with MockitoSugar with Results with DefaultAwaitTimeout with FutureAwaits with GuiceOneAppPerSuite  {

  private implicit val mat = app.materializer

  "Filter constructor" should {
    "Add a histogram to the prometheus registry" in {
      val collectorRegistry = mock[CollectorRegistry]
      new RouteActionMethodLatencyFilter(collectorRegistry)
      verify(collectorRegistry).register(any())
    }
  }

  "Apply method" should {
    "Measure the latency" in {
      val filter = new RouteActionMethodLatencyFilter(mock[CollectorRegistry])
      val rh = FakeRequest().withTag(Tags.RouteActionMethod, "test")
      val action = Action(Ok("success"))

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
  }

}
