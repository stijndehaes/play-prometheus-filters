package com.github.stijndehaes.playprometheusfilters

import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment}
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.hotspot._

object PrometheusModule {
  val defaultExportsKey = "play-prometheus-filters.register-default-hotspot-collectors"
}

class PrometheusModule extends Module {
  import PrometheusModule._

  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    CollectorRegistry.defaultRegistry.clear()

    configuration.getOptional[Boolean](defaultExportsKey).foreach { enabled =>
      if (enabled) {
        DefaultExports.initialize()
      }
    }

    Seq(
      bind[CollectorRegistry].to(CollectorRegistry.defaultRegistry)
    )
  }
}
