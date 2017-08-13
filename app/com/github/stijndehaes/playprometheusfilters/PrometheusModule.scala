package com.github.stijndehaes.playprometheusfilters

import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment}
import io.prometheus.client.CollectorRegistry

class PrometheusModule extends Module {

  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    CollectorRegistry.defaultRegistry.clear()
    Seq(
      bind[CollectorRegistry].to(CollectorRegistry.defaultRegistry)
    )
  }
}
