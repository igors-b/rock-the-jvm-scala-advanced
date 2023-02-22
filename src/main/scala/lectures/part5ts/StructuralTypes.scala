package lectures.part5ts

import scala.language.reflectiveCalls

object StructuralTypes extends App {

  //structural types

  type JavaClosable = java.io.Closeable

  class HipsterClosable {
    def close(): Unit = println("yeah yeah I'm closing!")
    def closeSilently(): Unit = println("not making a sound!")
  }

  // def closeQuietly(closable: JavaClosable OR HipsterClosable) // ?!

  type UnifiedCloseable = {
    def close(): Unit
  } // STRUCTURAL TYPE

  def closeQuietly(unifiedCloseable: UnifiedCloseable): Unit = unifiedCloseable.close()

  closeQuietly(new JavaClosable {
    override def close(): Unit = println("I am closing")
  })

  closeQuietly(new HipsterClosable)


  // TYPE REFINEMENTS


  type AdvancedCloseable = JavaClosable {
    def closeSilently(): Unit
  }

  class AdvancedJavaCloseable extends JavaClosable {
    def close(): Unit = println("Java closes")
    def closeSilently(): Unit = println("Java closes silently")
  }

  def closeShh(advancedCloseable: AdvancedCloseable): Unit = advancedCloseable.closeSilently()

  closeShh(new AdvancedJavaCloseable)
  // closeShh(new HipsterClosable)

  // using structural types as standalone types
  def altClose(closeable: { def close(): Unit }): Unit = closeable.close()

  // type-checking => duck typing

  type SoundMaker = {
    def makeSound(): Unit
  }

  class Dog {
    def makeSound(): Unit = println("bark!")
    def great(): Unit = println("great")
  }

  class Car {
    def makeSound(): Unit = println("vroom!")
  }

  val dog: SoundMaker = new Dog
  val car: SoundMaker = new Car

  // static duck typing

  // CAVEAT: based on reflection

  /*
    Exercises
   */
   // 1.
  trait CBL[+T] {
    def head: T
    def tail: CBL[T]
  }

  class Human extends {
    def head: Brain = new Brain
  }

  class Brain {
    override def toString: String = "BRAINZ!"
  }

  def f[T](somethingWithAHead: { def head: T}): Unit = println(somethingWithAHead.head)

  /*
    f is compatible with a CBL and with a Human? Yes
   */

  case object CBNil extends CBL[Nothing] {
    def head: Nothing = ???
    def tail: Nothing = ???
  }

  case class CBCons[T](override val head: T, override val tail: CBL[T]) extends CBL[T]
  val cbl = CBCons(2, CBNil)
  f(cbl)

  val human = new Human
  f(human)

  // 2.
  object HeadEqualizer {
    type Headable[T] = { def head: T}
    def ===[T](a: Headable[T], b: Headable[T]): Boolean = a.head == b.head
  }

  /*
    is compatible with a CBL and with Human? Yes
   */

  val brainzList = CBCons(new Brain, CBNil)
  val stringList = CBCons("Brainz", CBNil)
  HeadEqualizer.===(brainzList, new Human)

  // problem
  HeadEqualizer.===(new Human, stringList) // not type safe
}
