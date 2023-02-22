package lectures.part4implicits

import java.{util => ju}

object ScalaJavaConversions extends App {
  import collection.JavaConverters._
  val javaSet: ju.Set[Int] = new ju.HashSet[Int]()

  (1 to 5).foreach(javaSet.add)
  println(javaSet)


  val scalaSet = javaSet.asScala

  /*
    Iterator
    Iterable
    ju.List - scala.mutable.Buffer
    ju.Set - scala.mutable.Set
    ju.Map - scala.mutable.Map
   */

  import collection.mutable._
  val numbersBuffer = ArrayBuffer[Int](1, 2, 3)
  val juNumberBuffer = numbersBuffer.asJava

  println(juNumberBuffer.asScala eq numbersBuffer)

  val numbers = List(1,2,3)
  val juNumbers = numbers.asJava
  val backToScala = juNumbers.asScala
  println(backToScala eq numbers) // false
  println(backToScala == numbers) // true

  class ToScala[T](value: => T) {
    def asScala: T = value
  }

  implicit def asScalaOptional[T](o: ju.Optional[T]): ToScala[Option[T]] = new ToScala[Option[T]](
    if (o.isPresent) Some(o.get) else None
  )

  val juOptional: ju.Optional[Int] = ju.Optional.of(42)
  val scalaOption = juOptional.asScala
  println(scalaOption)
}
