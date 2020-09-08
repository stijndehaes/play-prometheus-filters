package com.github.stijndehaes.playprometheusfilters.helpers

import scala.collection.compat._
import immutable.LazyList

/**
  * Source: https://gist.github.com/jorgeortiz85/908035
  *
  * Usage:
  *   p(instance)('privateMethod)(arg1, arg2, arg3)
  *
  * @param x
  * @param methodName
  */
class PrivateMethodCaller(x: AnyRef, methodName: String) {
  def apply(_args: Any*): Any = {
    val args = _args.map(_.asInstanceOf[AnyRef])
    def _parents: LazyList[Class[_]] =
      LazyList(x.getClass) #::: _parents.map(_.getSuperclass)
    val parents = _parents.takeWhile(_ != null).toList
    val methods = parents.flatMap(_.getDeclaredMethods)
    val method = methods
      .find(_.getName == methodName)
      .getOrElse(
        throw new IllegalArgumentException(
          "Method " + methodName + " not found"
        )
      )
    method.setAccessible(true)
    method.invoke(x, args: _*)
  }
}

case class PrivateMethodExposer(x: AnyRef) {
  def apply(method: scala.Symbol): PrivateMethodCaller =
    new PrivateMethodCaller(x, method.name)
}

//def p(x: AnyRef): PrivateMethodExposer = new PrivateMethodExposer(x)
