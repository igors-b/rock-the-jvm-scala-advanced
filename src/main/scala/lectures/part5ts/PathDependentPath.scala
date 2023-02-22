package lectures.part5ts

object PathDependentPath extends  App {

  class Outer {
    class Inner
    object InnerObject
    type InnerType

    def print(i: Inner) = println(i)

    def printGeneral(i: Outer#Inner) = println(i)
  }

  def aMethod: Int = {
    class HelperClass
    // type NewType not allowed
    type NewType = String // type aliases are allowed to be created in terms of methods
    2
  }

  // per-instance
  val outer = new Outer
  // val inner = new Inner         N/A
  // val inner1 = new Outer.Inner  N/A
  val inner = new outer.Inner //  outer.Inner is a TYPE which belongs only to this particular {outer = new Outer} object

  val outer2 = new Outer
  // val inner3: outer.Inner = new outer2.Inner   TYPES outer.Inner AND outer2.Inner are completely different types

  outer.print(inner)
  // outer2.print(inner) outer2.print accepts parameter of outer2.Inner type

  // path-dependent types

  // All the Inner types have a common super type Outer#Inner
  outer.printGeneral(inner)
  outer2.printGeneral(inner) // because type of inner is outer.Inner what is subtype of Outer#Inner

  /*
    Exercise:
    DB keyed by Int or String, but maybe others
   */

  /*
    use path-dependent types
    abstract type members and/or type aliases
   */

  trait ItemLike {
    type Key
  }

  trait Item[K] extends ItemLike {
    type Key = K
  }
  trait IntItem extends Item[Int]
  trait StringItem extends Item[String]

  def get[ItemType <: ItemLike](key: ItemType#Key): ItemLike = ???

  get[IntItem](42) // ok
  get[StringItem]("home") // ok

  // get[IntItem]("scala") // not ok
}
