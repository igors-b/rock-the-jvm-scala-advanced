package lectures.part4implicits

import scala.annotation.tailrec
import scala.language.implicitConversions

object PimpMyLibrary extends App {

  // 2.isEven

  implicit class RichInt(value: Int){
    def isEven: Boolean = value % 2 == 0
    def sqrt: Double = Math.sqrt(value)
    def *[T](list: List[T]): List[T] = (1 to value).toList.flatMap(_ => list)
    def **[T](list: List[T]): List[T] = {
      def concatenate(n: Int): List[T] =
        if (n <= 0) List.empty
        else list ++ concatenate(n-1)
        concatenate(value)
    }
    def times(function: () => Unit): Unit = (1 to value).foreach(_ => function())
    def times2(function: () => Unit): Unit = {
      @tailrec
      def timesAux(n: Int): Unit =
        if (n <= 0) ()
        else {
          function()
          timesAux(n - 1)
        }
      timesAux(value)
    }
  }

  new RichInt(42).sqrt
  42.isEven

  // type enrichment = pimping
  1 to 10

  import scala.concurrent.duration._
  2.seconds

  /*
    Enrich the String class
      - asInt
      - encrypt
        "John" -> "Lnjp"

    Keep enriching the Int class
    - times(function)
      3.times(() => ...)
    - * which will take List as argument
      3 * List(1, 2) => List(1, 2, 1, 2, 1, 2)
   */

  implicit class RichString(str: String) {
    def asInt: Int = str.toIntOption.getOrElse(str.length)
    def encrypt(cypherDistance: Int): String = str.map(ch => (ch.toInt + cypherDistance).asInstanceOf[Char])
  }

  println(List("5".asInt, "Uelosipedka".asInt))
  println("John".encrypt(2))
  println(3 * List(1,2,3))
  val printFunc: () => Unit = () => println("Hello implicits")
  3.times(printFunc)
  println(3 ** List(1,2,3))
  3.times2(printFunc)

  // "3" / 4
  implicit def stringToInt(str: String): Int = Integer.valueOf(str)
  println("12" / 4)

  // equivalent: implicit class RichInt(value: Int)
  class RichAltInt(value: Int)
  implicit def enrich(value: Int): RichAltInt = new RichAltInt(value)

  // danger zone
  implicit def intToBoolean(i: Int): Boolean = i == 1

  /*
    if (n) do something
    else do something else
   */

  val aConditionedValue = if (3) "OK" else "Something wrong"
  println(aConditionedValue)
}
