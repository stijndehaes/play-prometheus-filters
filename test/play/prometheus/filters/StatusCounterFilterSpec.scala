package play.prometheus.filters

import io.prometheus.client.CollectorRegistry
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.verify
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{Action, Results}
import play.api.test.{DefaultAwaitTimeout, FakeRequest, FutureAwaits}

class StatusCounterFilterSpec extends WordSpec with MustMatchers with MockitoSugar with Results with DefaultAwaitTimeout with FutureAwaits with GuiceOneAppPerSuite {

  private implicit val mat = app.materializer

  "Filter constructor" should {
    "Add a counter to the prometheus registry" in {
      val collectorRegistry = mock[CollectorRegistry]
      new StatusCounterFilter(collectorRegistry)
      verify(collectorRegistry).register(any())
    }
  }

  "Apply method" should {
    "Count the requests with status" in {
      val filter = new StatusCounterFilter(mock[CollectorRegistry])
      val rh = FakeRequest()
      val action = Action(Ok("success"))

      await(filter(action)(rh).run())

      val metrics = filter.requestCounter.collect()
      metrics must have size 1
      val samples = metrics.get(0).samples
      samples.get(0).value mustBe 1.0
      samples.get(0).labelValues must have size 1
      samples.get(0).labelValues.get(0) mustBe "200"
    }
  }

}
