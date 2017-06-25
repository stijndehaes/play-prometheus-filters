package com.github.stijndehaes.playprometheusfilters

import com.google.inject.AbstractModule
import io.prometheus.client.CollectorRegistry

class PrometheusModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[CollectorRegistry]).toInstance(CollectorRegistry.defaultRegistry)
  }
}
