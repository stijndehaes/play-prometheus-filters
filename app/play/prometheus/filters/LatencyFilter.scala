package play.prometheus.filters

import akka.stream.Materializer
import com.google.inject.{Inject, Singleton}
import io.prometheus.client.{CollectorRegistry, Histogram}
import play.api.mvc.{Filter, RequestHeader, Result}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class LatencyFilter @Inject()(registry: CollectorRegistry) (implicit val mat: Materializer, ec: ExecutionContext) extends Filter {

  private[filters] val requestLatency = Histogram.build
    .name("requests_latency_seconds")
    .help("Request latency in seconds.")
    .register(registry)

  def apply(nextFilter: RequestHeader => Future[Result])
    (requestHeader: RequestHeader): Future[Result] = {

    val requestTimer = requestLatency.startTimer
    nextFilter(requestHeader).map { result =>
      requestTimer.observeDuration()
      result
    }
  }

}