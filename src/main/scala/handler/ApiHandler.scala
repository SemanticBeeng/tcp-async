package handler

import akka.util.ByteString
import scala.concurrent.ExecutionContext.Implicits.global
import api.Api
import spray.http.HttpMethods._
import util.Conf
import akka.io.Tcp.Write
import akka.actor.{ Props, ActorRef }

object ApiHandlerProps extends HandlerProps {
  def props(connection: ActorRef) = Props(classOf[ApiHandler], connection)
}

class ApiHandler(connection: ActorRef) extends Handler(connection) {

  private val newLine = ByteString("\n")

  /**
   * Makes an api request to Google Elevation API with given location data and returns response to user.
   */
  def received(data: String) = {
    val uri = Conf.apiUrl + data
    Api.httpRequest(method = GET, uri = uri) map {
      response =>
        respond(response.entity.asString)
    }
  }

  /**
   * Sends given data over connection to client, appending a newline
   * @param response
   */
  def respond(response: String) {
    connection ! Write(ByteString(response) ++ newLine)
  }
}