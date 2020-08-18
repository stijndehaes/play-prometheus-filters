package io.github.jyllandsposten.playprometheusfilters.metrics

/**
  * Defines default value what to return in case a certain property cannot be retrieved.
  * This allows for customization per metric what to return in case a value does not exist.
  *
  * E.g. instead of returning a 'unmatchedVerbe' string, could get the actual verbe from a [[play.api.mvc.RequestHeader]].
  *
  * {{{
  * case object MyDefaults extends UnmatchedDefaults {
  *   // return actual http method from request
  *   override  val unmatchedVerb: RequestHeader => String = rh => rh.method
  * }
  * }}}
  *
  * @tparam T Request object type for web framework. E.g. for play it's the [[play.api.mvc.RequestHeader]].
  */
trait UnmatchedDefaults[T] {

  val UnmatchedRouteString      = "unmatchedRoute"
  val UnmatchedControllerString = "unmatchedController"
  val UnmatchedPathString       = "unmatchedPath"
  val UnmatchedVerbString       = "unmatchedVerb"

  val unmatchedRoute: T => String      = _ => UnmatchedRouteString
  val unmatchedController: T => String = _ => UnmatchedControllerString
  val unmatchedPath: T => String       = _ => UnmatchedPathString
  val unmatchedVerb: T => String       = _ => UnmatchedVerbString
}

/**
  * Default implementation of [[UnmatchedDefaults]] which returns a fixed strings.
  */
case object DefaultPlayUnmatchedDefaults extends UnmatchedDefaults[play.api.mvc.RequestHeader]
