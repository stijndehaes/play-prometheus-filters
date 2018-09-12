package com.github.stijndehaes.playprometheusfilters.metrics

import play.api.mvc.RequestHeader

/**
  * Defines default value what to return in case a certain property cannot be retrieved.
  * This allows for customization per metric what to return in case a value does not exist.
  *
  * E.g. instead of returning a 'unmatchedVerbe' string, could get the actual verbe from the [[RequestHeader]].
  * ```
  * case object MyDefaults extends UnmatchedDefaults {
  *   // return actual http method from request
  *   override  val unmatchedVerb: RequestHeader => String = rh => rh.method
  * }
  * ```
  */
trait UnmatchedDefaults {

  val UnmatchedRouteString      = "unmatchedRoute"
  val UnmatchedControllerString = "unmatchedController"
  val UnmatchedPathString       = "unmatchedPath"
  val UnmatchedVerbString       = "unmatchedVerb"

  val unmatchedRoute: RequestHeader => String      = _ => UnmatchedRouteString
  val unmatchedController: RequestHeader => String = _ => UnmatchedControllerString
  val unmatchedPath: RequestHeader => String       = _ => UnmatchedPathString
  val unmatchedVerb: RequestHeader => String       = _ => UnmatchedVerbString
}

/**
  * Default implementation of [[UnmatchedDefaults]] which returns a fixed strings.
  */
case object DefaultUnmatchedDefaults extends UnmatchedDefaults
