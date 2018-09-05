package com.github.stijndehaes.playprometheusfilters

import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment}
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.hotspot.DefaultExports

class PrometheusModule extends Module {
  private val defaultExportsKey = "play-prometheus-filters.default-exports"

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
