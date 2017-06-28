# Play prometheus filters

[![Build Status](https://travis-ci.org/stijndehaes/play-prometheus-filters.svg?branch=master)](https://travis-ci.org/stijndehaes/play-prometheus-filters)
[![Coverage Status](https://coveralls.io/repos/github/stijndehaes/play-prometheus-filters/badge.svg?branch=master)](https://coveralls.io/github/stijndehaes/play-prometheus-filters?branch=master)
This play library provides three types of filters that collect prometheus metrics.
A simple hello world application using these filters can be found in the following repo:
https://github.com/stijndehaes/play-prometheus-filters-example-app

To use the library add the following to you build.sbt

```scala
libraryDependencies += "com.github.stijndehaes" %% "play-prometheus-filters" % "0.1.0"

```

## The filters

### Request counter
This filter counts all the requests in your application and adds a label for the status

### Latency filter
This filter collects the latency of all requests

### Route Action Method Latency Filter
This filter collects the latency for all requests and adds a label call RouteActionMethod.
This action method is the method name of the method you provided your routes file.
This filter makes it possible to measure the latency for all your routes.

Example:

```
GET         /metrics          play.prometheus.controllers.PrometheusController.getMetrics
```

The RouteActionMethod for the above example would be getMetrics

## How to enable the filters
See the [documentation of play](https://www.playframework.com/documentation/2.5.x/ScalaHttpFilters#Using-filters)

You should make a filters class:

```scala
import javax.inject.Inject
import play.api.http.DefaultHttpFilters
import play.prometheus.filters.LatencyFilter
import play.prometheus.filters.StatusCounterFilter

class MyFilters @Inject() (
  latencyFilter: LatencyFilter,
  statusCounterFilter: StatusCounterFilter
) extends DefaultHttpFilters(latencyFilter, statusCounterFilter)
```

And the enable this filter in the application.conf

```$xslt
play.http.filters=com.example.MyFilters
```

## Prometheus controller
The project also provides a prometheus controller with a get metric method. If you add the following to your routes file:

```
GET         /metrics          play.prometheus.controllers.PrometheusController.getMetrics
```

You should be able to immediately get the metrics

## Example