package controllers

import spray.routing._
import spray.http._
import spray.http.StatusCodes.Forbidden


// See https://developer.mozilla.org/en-US/docs/HTTP/Access_control_CORS

case class Origin(origin: String) extends HttpHeader {
  def name = "Origin"
  def lowercaseName = "origin"
  def value = origin
}

case class `Access-Control-Allow-Origin`(origin: String) extends HttpHeader {
  def name = "Access-Control-Allow-Origin"
  def lowercaseName = "access-control-allow-origin"
  def value = origin
}

case class `Access-Control-Allow-Credentials`(allowed: Boolean) extends HttpHeader {
  def name = "Access-Control-Allow-Credentials"
  def lowercaseName = "access-control-allow-credentials"
  def value = if(allowed) "true" else "false"
}

trait CORSDirectives { this: HttpService =>
  def respondWithCORSHeaders(origin: String) =
    respondWithHeaders(
      `Access-Control-Allow-Origin`(origin),
      `Access-Control-Allow-Credentials`(true))

  def corsFilter(origin: String)(route: Route) =
    if(origin == "*")
      respondWithCORSHeaders("*")(route)
    else
      optionalHeaderValueByName("Origin") {
        case None => route
        case Some(clientOrigin) =>
          if(origin == clientOrigin)
            respondWithCORSHeaders(origin)(route)
          else
            complete(Forbidden, Nil, "Invalid origin")  // Maybe, a Rejection will fit better
      }
}

