package io.github.jyllandsposten.playprometheusfilters

import io.prometheus.client.CollectorRegistry
import io.prometheus.client.hotspot._
import org.slf4j.LoggerFactory
import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment}

object PrometheusModule {
  val defaultExportsKey = "play-prometheus-filters.register-default-hotspot-collectors"
}

class PrometheusModule extends Module {
  import PrometheusModule._

  private val logger = LoggerFactory.getLogger(classOf[PrometheusModule])

  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    CollectorRegistry.defaultRegistry.clear()

    configuration.getOptional[Boolean](defaultExportsKey).foreach { enabled =>
      if (enabled) {
        logger.info("Default exports are enabled")
        DefaultExports.initialize()
        logger.info("Default exports registered")
      } else {
        logger.info("Default exports are disabled")
      }
    }

    Seq(
      bind[CollectorRegistry].to(CollectorRegistry.defaultRegistry)
    )
  }
}
