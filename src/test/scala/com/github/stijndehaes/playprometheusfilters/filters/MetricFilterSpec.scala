package com.github.stijndehaes.playprometheusfilters.filters

import com.github.stijndehaes.playprometheusfilters.metrics.CounterRequestMetrics.CounterRequestMetricBuilder
import com.github.stijndehaes.playprometheusfilters.metrics.{CounterRequestMetric, DefaultPlayUnmatchedDefaults, RequestMetric}
import com.github.stijndehaes.playprometheusfilters.mocks.MockController
import com.typesafe.config.ConfigFactory
import io.prometheus.client.CollectorRegistry
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.Materializer
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration
import scala.jdk.CollectionConverters._
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test.{DefaultAwaitTimeout, FakeRequest, FutureAwaits}

import scala.concurrent.ExecutionContext.Implicits.global

class MetricFilterSpec extends PlaySpec with MockitoSugar with Results with DefaultAwaitTimeout with FutureAwaits with GuiceOneAppPerSuite {

  implicit val actorSystem: ActorSystem = ActorSystem("test")

  val configuration: Configuration = Configuration(ConfigFactory.parseString(
    """play-prometheus-filters.exclude.paths = ["/test"]"""
  ))

  "Filter constructor" should {
    "Get exclude paths from configuration" in {
      implicit val mat: Materializer = app.materializer
      val filter = new MetricsFilter(configuration) {
        override val metrics = List.empty[RequestMetric[_, RequestHeader, Result]]
      }

      filter.excludePaths must have size 1 // only check size since cannot compare Regex's
    }
  }

  "Apply method" should {
    "skip metrics for excluded paths" in {
      implicit val mat: Materializer = app.materializer
      val collectorRegistry = mock[CollectorRegistry]
      val filter = new MetricsFilter(configuration) {
        override val metrics: List[CounterRequestMetric] = List(
          CounterRequestMetricBuilder.build(collectorRegistry, DefaultPlayUnmatchedDefaults)
        )
      }

      val rh = FakeRequest("GET", "/test")
      val action = new MockController(stubControllerComponents()).ok

      await(filter(action)(rh).run())

      val metrics = filter.metrics.head.metric.collect().asScala
      metrics must have size 1
      val samples = metrics.head.samples
      samples.size() mustBe 0 // expect no metrics
    }
  }
}
