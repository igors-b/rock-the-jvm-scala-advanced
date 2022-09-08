package exercises

import scala.annotation.tailrec

trait MySet[A] extends (A => Boolean) {
  /*
    EXERCISE #1 - implement a functional set
   */

  def apply(elem: A): Boolean = contains(elem)
  def contains(elem: A): Boolean
  def +(elem: A): MySet[A]
  def ++(anotherSet: MySet[A]): MySet[A] // union

  def map[B](f: A => B): MySet[B]
  def flatMap[B](f: A => MySet[B]): MySet[B]
  def filter(predicate: A => Boolean): MySet[A]
  def foreach(f: A => Unit): Unit

  /*
    Exercise #2
      - removing an element
      - intersection with another set
      - difference with another set
   */

  def -(elem: A): MySet[A]
  def --(anotherSet: MySet[A]): MySet[A] // difference
  def &(anotherSet: MySet[A]): MySet[A]

  // def isEmpty: Boolean

  /*
  Exercise #3 - implement a unary_! = NEGATION of a set
 */
  def unary_! : MySet[A]
}

class EmptySet[A] extends MySet[A] {

  def contains(elem: A): Boolean = false
  def +(elem: A): MySet[A] = new NonEmptySet[A](head = elem, tail = this)
  def ++(anotherSet: MySet[A]): MySet[A] = anotherSet
  def map[B](f: A => B): MySet[B] = new EmptySet[B]
  def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B]
  def filter(predicate: A => Boolean): MySet[A] = this
  def foreach(f: A => Unit): Unit = ()

  def isEmpty: Boolean = true

  //part 2
  def -(elem: A): MySet[A] = this
  def --(anotherSet: MySet[A]): MySet[A] = this
  def &(set: MySet[A]): MySet[A] = this

  def unary_! : MySet[A] = new PropertyBasedSet[A](_ => true)
}

//class AllInclusiveSet[A] extends MySet[A] {
//
//  def contains(elem: A): Boolean = true
//  def +(elem: A): MySet[A] = this
//  def ++(anotherSet: MySet[A]): MySet[A] = this
//
//  def map[B](f: A => B): MySet[B] = ???
//  def flatMap[B](f: A => MySet[B]): MySet[B] = ???
//  def foreach(f: A => Unit): Unit = ???
//
//  def filter(predicate: A => Boolean): MySet[A] = ??? // property-based set
//  def -(elem: A): MySet[A] = ???
//  def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)
//  def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet)
//
//  override def isEmpty: Boolean = false
//
//  override def unary_! : MySet[A] = new EmptySet[A]
//}
// all elements of type A which satisfy a property
// { x in A | property(x)} mathematical definition
class PropertyBasedSet[A](property: A => Boolean) extends MySet[A] {
  def contains(elem: A): Boolean = property(elem)

  // { x in A | property(x) } + elememt = { x in A | property(x) || x == elem }
  def +(elem: A): MySet[A] =
    new PropertyBasedSet[A](x => property(x) || x == elem)

  // { x in A | property(x) } ++ anotherSet => { x in A | property(x) || anotherSet contains x }
  def ++(anotherSet: MySet[A]): MySet[A] =
    new PropertyBasedSet[A](x => property(x) || anotherSet(x))
  def map[B](f: A => B): MySet[B] = politelyFail
  def flatMap[B](f: A => MySet[B]): MySet[B] = politelyFail
  def foreach(f: A => Unit): Unit = politelyFail

  def filter(predicate: A => Boolean): MySet[A] = new PropertyBasedSet[A](x => property(x) && predicate(x))
  def -(elem: A): MySet[A] = filter(x => x != elem)
  def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)
  def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet)
  //def isEmpty: Boolean = false
  def unary_! : MySet[A] = new PropertyBasedSet[A](x => !property(x))

  def politelyFail = throw new IllegalArgumentException("Really deep rabbit hole!")
}

class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A] {

  def h: A = head
  def t: MySet[A] = tail

  def contains(elem: A): Boolean =
    head == elem || tail.contains(elem)

  def +(elem: A): MySet[A] = {
    if (this contains elem) this
    else new NonEmptySet[A](elem, this)
  }

  def ++(anotherSet: MySet[A]): MySet[A] =
    tail ++ anotherSet + head

  def map[B](f: A => B): MySet[B] =
    tail.map(f) + f(head)

  def flatMap[B](f: A => MySet[B]): MySet[B] =
    f(head) ++ tail.flatMap(f)

  def filter(predicate: A => Boolean): MySet[A] = {
    val filteredTail = tail.filter(predicate)
    if (predicate(head)) filteredTail + head
    else filteredTail
  }

  def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }

  def -(elem: A): MySet[A] =
    if (head == elem) tail
    else tail.-(elem) + head

  def isEmpty: Boolean = false

  def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet) // intersection = filter

  def --(anotherSet: MySet[A]): MySet[A] = {
//    @tailrec
//    def buildSet(set: MySet[A], acc: MySet[A]): MySet[A] = {
//      if (set.isEmpty) acc
//      else if (anotherSet.contains(set.h)) buildSet(set.t, acc - set.h)
//      else buildSet(set.t, acc)
//
//    }
//    if (anotherSet.isEmpty) this
//    else buildSet(this, this)
    filter(!anotherSet)
  }

  // new operator
  def unary_! : MySet[A] = new PropertyBasedSet[A](x => !this.contains(x))

}

object MySet {
  def apply[A](values: A*): MySet[A] = {
    @tailrec
    def buildSet(valSeq: Seq[A], acc: MySet[A]): MySet[A] =
      if (valSeq.isEmpty) acc
      else buildSet(valSeq.tail, acc + valSeq.head)
    buildSet(values.to(Seq), new EmptySet[A])
  }
}

object MySetPlayground extends App {
  val s = MySet(1,2,3,4)

  s + 5 ++ MySet(-1, -2) + 3 flatMap (x => MySet(x, 10 * x)) filter (_ % 2 == 0) foreach println

  println("Part 2")
  s -- MySet(2,4,5) foreach println

  val negative = !s // s.unary_! = all the naturals not equal to 1,2,3,4
  println(negative(2))
  println(negative(5))

  val negativeEven = negative.filter(_ % 2 == 0)
  println(negativeEven(6))
  println(negativeEven(7))

  val negativeEvenFive = negativeEven + 5
  println(negativeEvenFive(5)) // all the even numbers > 4 plus 5

}
