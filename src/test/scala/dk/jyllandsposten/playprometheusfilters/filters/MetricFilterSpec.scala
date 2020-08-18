package dk.jyllandsposten.playprometheusfilters.filters

import dk.jyllandsposten.playprometheusfilters.metrics.CounterRequestMetrics.CounterRequestMetricBuilder
import dk.jyllandsposten.playprometheusfilters.metrics.{DefaultPlayUnmatchedDefaults, RequestMetric}
import dk.jyllandsposten.playprometheusfilters.mocks.MockController
import com.typesafe.config.ConfigFactory
import io.prometheus.client.CollectorRegistry
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test.{DefaultAwaitTimeout, FakeRequest, FutureAwaits}

import scala.concurrent.ExecutionContext.Implicits.global

class MetricFilterSpec extends PlaySpec with MockitoSugar with Results with DefaultAwaitTimeout with FutureAwaits with GuiceOneAppPerSuite {

  val configuration = Configuration(ConfigFactory.parseString(
    """play-prometheus-filters.exclude.paths = ["/test"]"""
  ))

  "Filter constructor" should {
    "Get exclude paths from configuration" in {
      implicit val mat = app.materializer
      val filter = new MetricsFilter(configuration) {
        override val metrics = List.empty[RequestMetric[_, RequestHeader, Result]]
      }

      filter.excludePaths must have size 1 // only check size since cannot compare Regex's
    }
  }

  "Apply method" should {
    "skip metrics for excluded paths" in {
      implicit val mat = app.materializer
      val collectorRegistry = mock[CollectorRegistry]
      val filter = new MetricsFilter(configuration) {
        override val metrics = List(
          CounterRequestMetricBuilder.build(collectorRegistry, DefaultPlayUnmatchedDefaults)
        )
      }

      val rh = FakeRequest("GET", "/test")
      val action = new MockController(stubControllerComponents()).ok

      await(filter(action)(rh).run())

      val metrics = filter.metrics(0).metric.collect()
      metrics must have size 1
      val samples = metrics.get(0).samples
      samples.size() mustBe 0 // expect no metrics
    }
  }
}
