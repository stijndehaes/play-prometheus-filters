package dk.jyllandsposten.playprometheusfilters.mocks

import javax.inject.Inject

import play.api.mvc.{AbstractController, ControllerComponents}

class MockController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def ok = Action {
    Ok("ok")
  }

  def error = Action {
    NotFound("error")
  }

}
