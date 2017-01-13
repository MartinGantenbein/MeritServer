package meritserver.http.routes

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import meritserver.models.{CreateUser, User}
import meritserver.services.UsersService
import spray.json.DefaultJsonProtocol

import scala.util.Success

trait UsersServiceRoute extends UsersService with BaseServiceRoute with SprayJsonSupport with DefaultJsonProtocol {

  implicit val userFormat = jsonFormat4(User)
  implicit val createUserFormat = jsonFormat2(CreateUser)

  val usersRoute: Route = pathPrefix("users") {
    pathEndOrSingleSlash {
      get {
        complete(getUsers)
      } ~
      post {
        entity(as[CreateUser]) { user =>
          complete(createUser(user))
        }
      } ~
        put {
          entity(as[List[CreateUser]]) { users =>
            complete(createUsers(users))
          }
        } ~
        delete {
            deleteUsers
            complete(StatusCodes.NoContent)
          }
    } ~
      path(Segment) { id: String =>
        get {
          getUserById(id) match {
            case Success(Some(user)) => complete(user)
            case Success(None) => complete(StatusCodes.NotFound)
            case _ => complete(StatusCodes.InternalServerError)
          }
        }
      }
  }
}
