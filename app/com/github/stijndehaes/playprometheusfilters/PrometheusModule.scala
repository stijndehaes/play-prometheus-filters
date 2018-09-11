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

  /**
    * Registering default exporters. See [[DefaultExports.initialize()]]
    *
    * Not using DefaultExports.initialize since that makes it impossible to test.
    * Guice guarantees this is only run once for an application.
    */
  def registerDefaultExporters(registry: CollectorRegistry): Unit = {
    registry.register(new StandardExports)
    registry.register(new MemoryPoolsExports)
    registry.register(new BufferPoolsExports)
    registry.register(new GarbageCollectorExports)
    registry.register(new ThreadExports)
    registry.register(new ClassLoadingExports)
    registry.register(new VersionInfoExports)
  }

  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    val registry = new CollectorRegistry(true)

    configuration.getOptional[Boolean](defaultExportsKey).foreach { enabled =>
      if (enabled) {
        registerDefaultExporters(registry)
      }
    }

    Seq(
      bind[CollectorRegistry].to(registry)
    )
  }
}
