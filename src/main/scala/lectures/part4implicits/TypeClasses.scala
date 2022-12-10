package lectures.part4implicits

import java.util.Date

object TypeClasses extends App {

  trait HTMLWritable {
    def toHtml: String
  }

  case class User(name: String, age: Int, email: String) extends HTMLWritable {
    def toHtml: String = s"<div>$name ($age yo) <a href=$email/> </div>"
  }

  val john = User("John", 32, "john@rockthejvm.com")
  john.toHtml
  /*
    Disadvantages:
      1 - for the types WE write
      2 - ONE implementation out of quite a number
   */

  // option 2 - pattern matching
  object HTMLSerializerPM {
    def serializeToHtml(value: Any) = value match {
      case User(n, a, e) =>
      case _ =>
    }
  }

  /*
    Disadvantages:
      1 - lost type safety
      2 - need to modify the code every time
      3 - still ONE implementation
   */

  trait HTMLSerializer[T] {
    def serialize(value: T): String
  }

  implicit object UserSerializer extends HTMLSerializer[User] {
    def serialize(user: User): String = s"<div>${user.name} (${user.age} yo) <a href=${user.email}/> </div>"
  }

  println(UserSerializer.serialize(john))

  // 1 - we can define serializers for other types
  object DateSerializer extends HTMLSerializer[Date] {
    def serialize(date: Date): String = s"<div>${date.toString}</div>"
  }

  // 2 - we can define MULTIPLE serializers
  object PartialUserSerializer extends HTMLSerializer[User] {
    def serialize(user: User): String = s"<div>${user.name}</div>"
  }

  // part 2
  object HTMLSerializer {
    def serialize[T](value: T)(implicit  serializer: HTMLSerializer[T]): String =
      serializer.serialize(value)

    def apply[T](implicit serializer: HTMLSerializer[T]): HTMLSerializer[T] = serializer
  }

  implicit object IntSerializer extends HTMLSerializer[Int] {
    def serialize(value: Int): String = s"<div style: color=blue>$value</div>"
  }

  println(HTMLSerializer.serialize(42)) //because of companion HTMLSerializer has defined def serialize[T](value: T)(implicit  serializer: HTMLSerializer[T])
  println(HTMLSerializer.serialize(john)) //because of companion HTMLSerializer has defined def serialize[T](value: T)(implicit  serializer: HTMLSerializer[T])

  println(HTMLSerializer[Int].serialize(42)) //because of companion HTMLSerializer has defined def apply[T](implicit serializer: HTMLSerializer[T]): HTMLSerializer[T]
  println(HTMLSerializer[User].serialize(john)) //because of companion HTMLSerializer has defined def apply[T](implicit serializer: HTMLSerializer[T]): HTMLSerializer[T]
}
