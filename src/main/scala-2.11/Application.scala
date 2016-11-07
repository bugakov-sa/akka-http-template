import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.Logger
import scala.io.{Source, StdIn}

case class Configuration(host: String, port: Int)

object Configuration {
  def read: Configuration = {
    val src = Source.fromFile(Paths.get(
      System.getProperty("user.dir"),
      "..",
      "conf",
      "settings.txt"
    ).toFile)
    val map = src.getLines.map(line => (
      line.split("=")(0).trim,
      line.split("=")(1).trim)
    ).toMap
    src.close
    Configuration(map("host"), map("port") toInt)
  }
}

object Application extends App {

  private val log = Logger("Application")

  val conf = Configuration.read

  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()

  implicit val executionContext = system.dispatcher

  val route =
    get {
      path("p") {
        getFromFile(Paths.get(
          System.getProperty("user.dir"),
          "..",
          "web",
          "index.html"
        ).toFile, ContentTypes.`text/html(UTF-8)`)
      } ~ pathPrefix("p") {
        getFromDirectory(Paths.get(
          System.getProperty("user.dir"),
          "..",
          "web"
        ).toString)
      }
    }

  val bindingFuture = Http().bindAndHandle(route, conf.host, conf.port)

  log.info("Server online at http://{}:{}/", conf.host, conf.port.toString)
  log.info("Press ENTER to stop...")

  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}