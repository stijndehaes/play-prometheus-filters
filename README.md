# Play prometheus filters

[![Build Status](https://travis-ci.com/Jyllands-Posten/play-prometheus-filters.svg?branch=master)](https://travis-ci.org/Jyllands-Posten/play-prometheus-filters)
[![Coverage Status](https://coveralls.io/repos/github/Jyllands-Posten/play-prometheus-filters/badge.svg?branch=master)](https://coveralls.io/github/Jyllands-Posten/play-prometheus-filters?branch=master)

This play library provides four types of filters that collect prometheus metrics.

Two of these filters are also compatible with the [lagom framework](https://github.com/lagom/lagom).

A simple hello world application using these filters can be found in the following repo:
https://github.com/Jyllands-Posten/play-prometheus-filters-example-app

To use the library add the following to you build.sbt:

```scala
libraryDependencies += "io.github.jyllandsposten" %% "play-prometheus-filters" % "0.6.0"

```
This latest version supports Play 2.8.
For more info on play version compatibility see the releases matrix.


## Releases

| Release     | Play version |
| :---------- | :----------- |
| 0.1.x       | 2.5.x        |
| 0.2.x       | 2.5.x        |
| 0.3.x       | 2.6.x        |
| 0.4.x       | 2.6.x        |
| 0.5.x       | 2.7.x        |
| 0.6.x       | 2.8.x        |

## The filters

### Request counter
This filter counts all the requests in your application and adds a label for the status.
This filter is compatible with the lagom framework.

### Latency filter
This filter collects the latency of all requests.
This filter is compatible with the lagom framework.

### Route Latency Filter
This filter collects the latency for all requests and adds a label called RouteActionMethod.
This action method is the method name of the method you provided your routes file.
This filter makes it possible to measure the latency for all your routes.
This filter is not compatible with the lagom framework, since it does not provide the RouteActionMethod.

### Status and Route Latency Filter
This filter collects the latency for all requests, adds the following labels:
* RouteActionMethod - Function method name in the controller for the request
* Status - Response code of request
* Controller - Controller that serviced the request
* Path - Path of request
* Verb - Verb of request (GET, PUT, etc.)
This filter makes it possible to measure the latency for all your routes and the status of the response for this route.
It thus combines all the above filters into one.
This filter is not compatible with the lagom framework, since it does not provide the RouteActionMethod.

### Status and Route Counter Filter
This filter collects the counts of all requests, adds the following labels:
* method - Function method name in the controller for the request
* status - Response code of request
* controller - Controller that serviced the request
* path - Path of request
* verb - Verb of request (GET, PUT, etc.)
This filter makes it possible to measure the counts for all your routes and the status of the response for this route.
It thus combines all the above filters into one.
This filter is not compatible with the lagom framework, since it does not provide the RouteActionMethod.

### Status and Route Latency and Counter Filter
Combines the StatusAndRoute Latency and Count filters.

Example:

```
GET         /metrics          play.prometheus.controllers.PrometheusController.getMetrics
```

The RouteActionMethod for the above example would be getMetrics

## How to enable the filters
See the [documentation of play](https://www.playframework.com/documentation/2.6.x/ScalaHttpFilters#Using-filters)

You should make a filters class:

```scala
import javax.inject.Inject
import play.api.http.DefaultHttpFilters
import io.github.jyllandsposten.playprometheusfilters.filters.LatencyFilter
import io.github.jyllandsposten.playprometheusfilters.filters.StatusCounterFilter

class MyFilters @Inject() (
  latencyFilter: LatencyFilter,
  statusCounterFilter: StatusCounterFilter
) extends DefaultHttpFilters(latencyFilter, statusCounterFilter)
```

And the enable this filter in the application.conf

```$xslt
play.http.filters=com.example.MyFilters
```

## Customizing metrics or building your own filters

The setup of a filter is very simple: extend the `MetricFilter` class and define a `metrics` property with a list of metrics
you want your filter to use.

E.g. the `LatencyFilter` looks like this:
```scala
@Singleton
class LatencyFilter @Inject()(registry: CollectorRegistry, configuration: Configuration)(implicit mat: Materializer, ec: ExecutionContext) extends MetricsFilter(configuration) {

  override val metrics = List(
    LatencyOnlyRequestMetricsBuilder.build(registry, DefaultUnmatchedDefaults)
  )
}
``` 

The `@Singleton` annotation ensures the application creates only a single instance of it.
Via the `@Inject` annotation, Play will automatically pass available instances into the contructor (using [Guice DI](https://github.com/google/guice/wiki/Motivation)).
The `CollectorRegistry` is needed to register a metric. The `Configuration` may contain information about paths to exclude for metrics.
This is handled by the `MetricsFilter` class.

You can customize a filter in different ways

- Define one or more metrics in the `metrics` list (other than in the filters already provided).
See `CounterRequestMetrics` and `LatencyRequestMetrics` for provided metric types.

- You can define you own metric. E.g. currently only `Counter` and `Histogram` (Latency) metrics are provided.
If you'd like to use Prometheus's `Summary` or `Gauge` metric, you can implement your own metric by creating
a `RequestMetric` implementation and a corresponding `RequestMetricBuilder`.  
See `CounterRequestMetrics` and `LatencyRequestMetrics` for how to implement your own builder and metric.  
The builder, `builds` sets up the metrics instance with a name, help, labels, etc and registers the metric.
Then it returns a `RequestMetric` instance which uses the metric. Implement the `mark` function to pass labels 
to the metric using data from either the request or response, then call the 'metric'-function like `observe` or `inc`
to apply the metrics.

- Customize the handling of defaults in case a certain label cannot be found.
This can be done by providing a custom `UnmachedDefaults` implementation. The default implementation
always returns a fixed string like `unmatchedPath` if the `path` property cannot be determined.

  E.g. a custom implementation could use the 'uri' property from the request, which is always available, instead of just a fixed string.
Using this on a Counter metric would give you insight into which non-existing urls are being used. A country creates a single metric bucket per unique uri.   
_You probably would not want to use dynamic default properties on a Latency or Summary metric since that would give many metric buckets per unique url`_

  ```scala
  case object DynamicUnmatchedDefaults extends UnmatchedDefaults {
    val unmatchedPath: RequestHeader => String = _.uri
  }
  ```
 
## Excluding paths from metrics
A path can be excluded from metrics by adding it to the `play-prometheus-filters.exclude.paths` property in the `application.conf`.
E.g. when using the `PrometheusController` you might want to exclude the path on which you configured the controller in the `routes` file.

By default, the `/metrics` is excluded.

```
play-prometheus-filters {
  # exclude /metrics endpoint assuming PrometheusController is routed to this uri
  exclude.paths = ["/metrics"]
}
```

## Prometheus controller
The project also provides a prometheus controller with a get metric method. If you add the following to your routes file:

```
GET         /metrics          io.github.jyllandsposten.playprometheusfilters.controllers.PrometheusController.getMetrics
```

You should be able to immediately get the metrics

## Default Hotspot metrics

The [Prometheus Hotspot library](https://github.com/prometheus/client_java#included-collectors) provides some default collectors
for garbage collection, memory pool, etc.
Default these collectors are _not_ registered. This can be changed by setting the configuration property to `true`.

```
play-prometheus-filters.register-default-hotspot-collectors = true
```

## Credits

Special Thanks to [`@stijndehaes`](https://github.com/stijndehaes), whos work this project was forked from

You can find the original project here [play-prometheus-filters](https://github.com/stijndehaes/play-prometheus-filters)

