package meritserver.models

import java.time.{LocalDate, LocalDateTime}
import java.util.UUID

import spray.json.DefaultJsonProtocol

import scala.util.Try

case class User(id: String = UUID.randomUUID().toString,
                familyName: String,
                firstName: String,
                balance: Int = 0)
case class CreateUser(id: Option[String],
                      familyName: String,
                      firstName: String) {
  require(id match {
    case Some(pId) => !pId.isEmpty
    case None      => true
  }, "id must not be empty")
  require(!familyName.isEmpty, "familyName must not be empty")
  require(!firstName.isEmpty, "firstName must not be empty")
}

case class Transaction(id: String = UUID.randomUUID().toString,
                       from: String,
                       to: String,
                       amount: Int,
                       reason: String,
                       date: LocalDateTime = LocalDateTime.now,
                       booked: Boolean = false)
case class CreateTransaction(from: String,
                             to: String,
                             amount: Int,
                             reason: String) {
  require(!from.isEmpty, "From must not be empty!")
  require(!to.isEmpty, "To must not be empty!")
  require(amount > 0, "Amount must be positive!")
  require(!reason.isEmpty, "A reason would be nice!")
}

case class Merit(userId: String, name: String, received: Int, sent: Int, available: Int)

trait Model2Json extends DefaultJsonProtocol {
  import spray.json.{DeserializationException, JsString, JsValue, RootJsonFormat}

  implicit object localDateFormat extends RootJsonFormat[LocalDateTime] {
    override def read(json: JsValue): LocalDateTime = json match {
      case JsString(s) =>
        Try { LocalDateTime.parse(s.toString) }
          .getOrElse(LocalDate.parse(s.toString).atStartOfDay())
      case _ =>
        throw DeserializationException(s"Not a proper LocalDate: [$json]")
    }

    override def write(date: LocalDateTime): JsValue = JsString(date.toString)
  }

  implicit val userFormat: RootJsonFormat[User] = jsonFormat4(User)
  implicit val createUserFormat: RootJsonFormat[CreateUser] = jsonFormat3(
    CreateUser)
  implicit val transactionFormat: RootJsonFormat[Transaction] = jsonFormat7(
    Transaction)
  implicit val createTransactionFormat: RootJsonFormat[CreateTransaction] =
    jsonFormat4(CreateTransaction)
  implicit val meritFormat: RootJsonFormat[Merit] = jsonFormat5(Merit)
}
