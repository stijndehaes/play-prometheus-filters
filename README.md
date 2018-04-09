# Play prometheus filters

[![Build Status](https://travis-ci.org/stijndehaes/play-prometheus-filters.svg?branch=master)](https://travis-ci.org/stijndehaes/play-prometheus-filters)
[![Coverage Status](https://coveralls.io/repos/github/stijndehaes/play-prometheus-filters/badge.svg?branch=master)](https://coveralls.io/github/stijndehaes/play-prometheus-filters?branch=master)
This play library provides four types of filters that collect prometheus metrics.

Two of these filters are also compatible with the [lagom framework](https://github.com/lagom/lagom).

A simple hello world application using these filters can be found in the following repo:
https://github.com/stijndehaes/play-prometheus-filters-example-app

To use the library add the following to you build.sbt:

```scala
libraryDependencies += "com.github.stijndehaes" %% "play-prometheus-filters" % "0.3.0"

```
This version only supports play 2.6.
For more info on play version compatibility see the releases matrix.


## Releases

| Release     | Play version |
| :---------- | :----------- |
| 0.1.x       | 2.5.x        |
| 0.2.x       | 2.5.x        |
| 0.3.x       | 2.6.x        |


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

### Status and Route Counter Filter
Combines the StatusAndRoute Latency and Count filters.

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
import com.github.stijndehaes.playprometheusfilters.filters.LatencyFilter
import com.github.stijndehaes.playprometheusfilters.filters.StatusCounterFilter

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
GET         /metrics          com.github.stijndehaes.playprometheusfilters.controllers.PrometheusController.getMetrics
```

You should be able to immediately get the metrics

## Example
