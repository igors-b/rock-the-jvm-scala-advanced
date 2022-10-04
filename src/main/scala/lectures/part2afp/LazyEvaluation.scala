package lectures.part2afp

import scala.collection.WithFilter

object LazyEvaluation extends App {

  // lazy DELAYS the evaluation of values
  // val x: Int = throw new RuntimeException - crashes the program with exception
  lazy val x: Int = throw new RuntimeException
//  println(x) // the call of lazy val will crash the program

  lazy val y: Int = {
    println("Hello")
    42
  }

  println(y)
  println(y)

  // examples of implications:
  // #1 side effects
  def sideEffectCondition: Boolean = {
    println("Boo")
    true
  }

  def simpleCondition: Boolean = false

  lazy val lazyCondition = sideEffectCondition
  println(if(simpleCondition && lazyCondition) "yes" else "no")

  // #2 in conjunction with call by name
  def byName(n: => Int): Int = n + n + n + 2
  def retrieveMagicValue = {
    //side effect or long computation
    println("Waiting")
    Thread.sleep(1000)
    42
  }

  println(byName(retrieveMagicValue))
  // use lazy vals
  // technics named USE BY NEED
  def byName2(n: => Int): Int = {
    lazy val t = n
    t + t + t + 2
  }
  println(byName2(retrieveMagicValue))

  // #3 filtering with lazy vals
  def lessThan30(i: Int): Boolean = {
    println(s"$i is less than 30?")
    i < 30
  }

  def greaterThan20(i: Int): Boolean = {
    println(s"$i is greater than 20?")
    i > 20
  }

  val numbers = List(1, 25, 40, 5, 23)
  val lt30 = numbers.filter(lessThan30) // List(1, 25, 5, 23)
  val gt20 = lt30.filter(greaterThan20)
  println(gt20)

  val lt30Lazy: WithFilter[Int, List] = numbers.withFilter(lessThan30) // lazy vals under the hood
  val gt20Lazy: WithFilter[Int, List] =  lt30Lazy.withFilter(greaterThan20)
  println
  println(gt20Lazy)
  gt20Lazy.foreach(println)

  // for-comprehensions use withFilter with guards
  for {
    a <- List(1,2,3) if a % 2 == 0 // use lazy vals!
  } yield a + 1
  // it translates to:
  List(1,2,3).withFilter(_ % 2 == 0).map(_ + 1)

  /*
    Exercise: implement a lazily evaluated, singly linked STREAM of elements.

    naturals = MyStream.from(1)(x => x + 1) = stream of natural numbers (potentially infinite!)
    naturals.take(100) // lazily evaluated stream of the first 100 naturals (finite stream)
    natural.take(100).foreach(println)
    natural.foreach(println) // will crush - infinite
    naturals.map(_ * 2) // stream of all even numbers (potentially infinite!)
   */
  abstract class MyStream[+A] {
    def isEmpty: Boolean
    def head: A
    def tail: MyStream[A]

    def #::[B >: A](element: B): MyStream[B] // prepend operator
    def ++[B >: A](anotherStream: MyStream[B]): MyStream[B] // concatenate two streams

    def foreach(f: A => Unit): Unit
    def map[B](f: A => B): MyStream[B]
    def flatMap[B](f: A => MyStream[B]): MyStream[B]
    def filter(predicate: A => Boolean): MyStream[A]

    def take(n: Int): MyStream[A] // takes first n elements of this stream
    def takeAsList(n: Int): List[A]
  }

  object MyStream {
    def from[A](start: A)(generator: A => A): MyStream[A] = ??? // generator generates next value based on previous value of the stream
  }
}
