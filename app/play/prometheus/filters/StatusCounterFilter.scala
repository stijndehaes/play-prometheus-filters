package play.prometheus.filters

import javax.inject.Inject

import akka.stream.Materializer
import com.google.inject.Singleton
import io.prometheus.client.{CollectorRegistry, Counter}
import play.api.mvc.{Filter, RequestHeader, Result}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StatusCounterFilter @Inject()(registry: CollectorRegistry) (implicit val mat: Materializer, ec: ExecutionContext) extends Filter {

  private[filters] val requestCounter = Counter.build()
    .name("http_requests_total")
    .help("Total amount of requests")
    .labelNames("status")
    .register(registry)

  def apply(nextFilter: RequestHeader => Future[Result])
    (requestHeader: RequestHeader): Future[Result] = {

    nextFilter(requestHeader).map { result =>
      requestCounter.labels(result.header.status.toString).inc()
      result
    }
  }
}