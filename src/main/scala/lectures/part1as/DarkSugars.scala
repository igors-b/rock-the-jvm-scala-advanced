package lectures.part1as

import scala.util.Try

object DarkSugars extends App {

  // syntax sugar #1: methods with single param
  def singleArgumentMethod(arg: Int): String = s"$arg little ducks"
  val description = singleArgumentMethod {
    //write some code
    println("Descriptions some code")
    42
  }
  println(description)

  // example
//  val aTryInstance = Try { // apply method of Try
//    throw RuntimeException
//  }

  List(1,2,4).map { x =>
    x + 1
  }

  // syntax sugar #2: single abstract method pattern
  trait Action {
    def act(x: Int): Int
  }

  val anInstance: Action = new Action {
    def act(x: Int): Int = x * 2
  }
  val aFunkyInstance: Action = (x: Int) => x + 2 // magic
  // example: Runnables
  val aThread = new Thread(new Runnable {
    def run(): Unit = println("hello, Scala")
  })
  val aSweeterThread: Thread = new Thread(() => println("sweet Scala"))

  abstract class AbstractType {
    def implemented: Int = 42
    def f(a : Int): Unit
  }

  val anAbstractInstance: AbstractType = (a: Int) => println("sweet")

  // syntax sugar #3: the :: and #:: methods are special
  val prependedList = 2 :: List(3,4)
  // 2.::(List(3,4)) - incorrect
  // List(3,4).::(2) - true
  // ?!

  // scala spec: the last char decides associativity of method
  1 :: 2 :: 3 :: List(4, 5)
  List(4, 5).::(3).::(2).::(1)

  class MyStream[T] {
    def -->:(value: T): MyStream[T] = this // actual implementation here
  }

  val myStream = 1 -->: 2 -->: 3 -->: new MyStream[Int]

  // syntax sugar #4 multi-word method naming
  class TeenGirl(name: String) {
    def `and than said`(gossip: String): Unit = println(s"$name said $gossip")
  }

  val lilly = new TeenGirl("Lilly")
  lilly `and than said` "Scala is so sweet"

  // syntax sugar #5: infix types
  class Composite[A, B]
  val composite:Composite[Int, String] = ???
  val infixComposite: Int Composite String = ???

  class -->[A, B]
  val towards: Int --> String = ???

  // syntax sugar #6: update() id very special, much like apply()
  val anArray = Array(1,2,3)
  anArray(2) = 7 // rewritten to anArray.update(2, 7)
  // used in mutable collections
  // remember apply() AND update()

  // syntax sugar #7: setters for mutable containers
  class Mutable {
    private var internalMember: Int = 0 // private for OO encapsulation
    def member: Int = internalMember // "getter"
    def member_=(value: Int): Unit =
      internalMember = value // "setter"
  }

  val aMutableContainer = new Mutable
  aMutableContainer.member = 42 // rewritten as aMutableContainer.member_=(42)



}


