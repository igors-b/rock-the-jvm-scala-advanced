package lectures.part4implicits

import lectures.part4implicits.JSONSerialization.JSONNumber

import java.util.Date

object JSONSerialization extends App {

  /*
    Users, posts, feeds
    Serialize those to JSON
   */

  case class User(name: String, age: Int, email: String)

  case class Post(content: String, createdAt: Date)

  case class Feed(user: User, posts: List[Post])

  /*
    1 - intermediate data types: Int, String, List, Date
    2 - type classes for conversion to intermediate data types
    3 - serialize to JSON
   */

  sealed trait JSONValue { // intermediate data type}
    def stringify: String
  }

  final case class JSONString(value: String) extends JSONValue {
    def stringify: String =
      s"\"$value\""
  }

  final case class JSONNumber(value: Int) extends JSONValue {
    def stringify: String = value.toString
  }

  final case class JSONArray(value: List[JSONValue]) extends JSONValue {
    def stringify: String = value.map(_.stringify).mkString("[", ",", "]")
  }

  final case class JSONDate(value: Date) extends JSONValue {
    def stringify: String = value.toString
  }

  final case class JSONObject(values: Map[String, JSONValue]) extends JSONValue {
    /*
      {
        name: "John"
        age: 22
        email: john@rockthejvm.com
        friends: [ ... ]
        latestPost: {
          content: "Scala Rocks"
          date: ...
        }
      }
     */

    def stringify: String = values.map {
      case (key, value) => "\"" + key + "\":" + value.stringify
    }
      .mkString("{", ",", "}")
  }

  val data = JSONObject(Map(
    "user" -> JSONString("Daniel"),
    "posts" -> JSONArray(List(
      JSONString("Scala Rocks!"),
      JSONNumber(42)
    ))
  ))

  println(data.stringify)

  // type class to convert User, Post and Feed to some implementation of JSONValue
  /*
     1 - type class itself
     2 - type class instances (implicit)
     3 - pimp library to use type class instances
   */

  // call stringify on result

  // 2.1
  trait JSONConverter[T] {
    def convert(value: T): JSONValue
  }



  // 2.2 type class instances (implicit)

  // existing data types
  implicit object StringConverter extends JSONConverter[String] {
    override def convert(value: String): JSONValue = JSONString(value)
  }

  implicit object NumberConverter extends JSONConverter[Int] {
    override def convert(value: Int): JSONValue = JSONNumber(value)
  }

  // custom data typed
  implicit object UserConverter extends JSONConverter[User] {
    override def convert(user: User): JSONValue =
      JSONObject(
        Map(
          "name" -> JSONString(user.name),
          "age" -> JSONNumber(user.age),
          "name" -> JSONString(user.email)
        )
      )
  }

  implicit object PostConverter extends JSONConverter[Post] {
    override def convert(post: Post): JSONValue =
      JSONObject(
        Map(
          "content" -> JSONString(post.content),
          "createdAt" -> JSONDate(post.createdAt)
        )
      )
  }

  implicit object FeedConverter extends JSONConverter[Feed] {
    override def convert(feed: Feed): JSONValue =
      JSONObject(
        Map(
          "user" -> feed.user.toJSON,
          "posts" -> JSONArray(feed.posts.map(_.toJSON))
        )
      )
  }

  // 2.3 conversion
  implicit class JSONOps[T](value: T) {
    def toJSON(implicit converter: JSONConverter[T]): JSONValue =
      converter.convert(value)
  }

  // call stringify on result
  val now = new Date(System.currentTimeMillis())
  val john = User("John", 34, "john@rockthejvm.com")
  val feed = Feed(john, List(
    Post("hello", now),
    Post("look at this cute puppy", now)
  ))

  println(feed.toJSON.stringify)

}
