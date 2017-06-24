package play.prometheus.filters

import akka.stream.Materializer
import io.prometheus.client.CollectorRegistry
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc._
import play.api.test.{DefaultAwaitTimeout, FakeRequest, FutureAwaits}

class LatencyFilterSpec extends WordSpec with MustMatchers with MockitoSugar with Results with DefaultAwaitTimeout with FutureAwaits with GuiceOneAppPerSuite {

  "Filter constructor" should {
    "Add a histogram to the prometheus registry" in {
      val collectorRegistry = mock[CollectorRegistry]
      new LatencyFilter(collectorRegistry)(mock[Materializer], defaultContext)
      verify(collectorRegistry).register(any())
    }
  }

  "Apply method" should {
    "Measure the latency" in {
      implicit val mat = app.materializer
      val filter = new LatencyFilter(mock[CollectorRegistry])
      val rh = FakeRequest()
      val action = Action(Ok("success"))

      await(filter(action)(rh).run())

      val metrics = filter.requestLatency.collect()
      metrics must have size 1
      val samples = metrics.get(0).samples
      //this is the count sample
      val countSample = samples.get(samples.size() - 2)
      countSample.value mustBe 1.0
      countSample.labelValues must have size 0
    }
  }

}
