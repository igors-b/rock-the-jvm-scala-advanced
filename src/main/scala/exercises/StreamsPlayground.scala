package exercises

import scala.annotation.tailrec

abstract class MyStream[+A] {
  def isEmpty: Boolean
  def head: A
  def tail: MyStream[A]

  def #::[B >: A](element: B): MyStream[B] // prepend operator
  def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] // concatenate two streams

  def foreach(f: A => Unit): Unit
  def map[B](f: A => B): MyStream[B]
  def flatMap[B](f: A => MyStream[B]): MyStream[B]
  def filter(predicate: A => Boolean): MyStream[A]

  def take(n: Int): MyStream[A] // takes first n elements of this stream
  def takeAsList(n: Int): List[A] = take(n).toList()

  /*
    [1, 2, 3].toList([]) =
    [2, 3].toList([1]) =
    [1].toList([2, 1])
    [].toList([[3, 2, 1]) = [3, 2, 1].reverse = [1, 2, 3]
   */
  @tailrec
  final def toList[B >: A](acc: List[B] = Nil): List[B] =
    if (isEmpty) acc.reverse
    else tail.toList(head :: acc)
}

object EmptyStream extends MyStream[Nothing] {
  def isEmpty: Boolean = true

  def head: Nothing = throw new NoSuchElementException

  def tail: MyStream[Nothing] = throw new NoSuchElementException

  def #::[B >: Nothing](element: B): MyStream[B] = new NonEmptyStream(hd = element, tl = this)

  def ++[B >: Nothing](anotherStream: => MyStream[B]): MyStream[B] = anotherStream

  def foreach(f: Nothing => Unit): Unit = ()

  def map[B](f: Nothing => B): MyStream[B] = this

  def flatMap[B](f: Nothing => MyStream[B]): MyStream[B] = this

  def filter(predicate: Nothing => Boolean): MyStream[Nothing] = this

  def take(n: Int): MyStream[Nothing] = this
}

class NonEmptyStream[+A](hd: A, tl: => MyStream[A]) extends MyStream[A] {
  def isEmpty: Boolean = false

  override val  head: A = hd

  override lazy val tail: MyStream[A] = tl // call by need

  def #::[B >: A](element: B): MyStream[B] = new NonEmptyStream(element, this)

  def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] = new NonEmptyStream(head, tail ++ anotherStream)

  def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }

  /*
    s = new NonEmptyStream(1, ?)
    mapped = s.map(_ + 1) = new Cons(2, s.tail.map(_ + 1)) - s.tail.map(_ + 1) will not be evaluated until => mapped.tail will be called
   */
  def map[B](f: A => B): MyStream[B] = new NonEmptyStream(f(head), tail.map(f)) // preserves lazy evaluation

  def flatMap[B](f: A => MyStream[B]): MyStream[B] = f(head) ++ tail.flatMap(f)

  def filter(predicate: A => Boolean): MyStream[A] = {
    if (predicate(head)) new NonEmptyStream(head, tail.filter(predicate)) // tail.filter(predicate) part will not be evaluated until it will be called
    else tail.filter(predicate) // will be evaluated only evaluation for the first head value which is holded for predicate and the rest  part will be lazy evaluated
  }

  def take(n: Int): MyStream[A] =
    if (n <=0 ) EmptyStream
    else if (n == 1) new NonEmptyStream(head, EmptyStream)
    else new NonEmptyStream(head, tail.take(n-1))
}

object MyStream {
  def from[A](start: A)(generator: A => A): MyStream[A] = // generator generates next value based on previous value of the stream
    new NonEmptyStream(start, MyStream.from(generator(start))(generator))
}
object StreamsPlayground extends App {

  val naturals = MyStream.from(1)(_ + 1)
  println(naturals.head)
  println(naturals.tail.head)
  println(naturals.tail.tail.head)

  val startFrom0 = 0 #:: naturals // naturals.#::(0)
  println(startFrom0.head)

  startFrom0.take(10000).foreach(println)

  // map, flatMap
  println(startFrom0.map(_ * 2).take(100).toList())
  println(startFrom0.flatMap(x => new NonEmptyStream(x, new NonEmptyStream(x + 1, EmptyStream))).take(10).toList())
  println(startFrom0.filter(_ < 10).take(10).take(20).toList())

  // Exercises on streams
  // 1 - stream of Fibonacci numbers
  // 2 - stream of prime numbers with Eratosthenes' sieve
  /*
    [2 3 4 ...]
    filter out all numbers divisible by 2
    [2 3 5 7 9 11 ...]
    filter out all numbers divisible by 3
    [2 3 5 7 11 13 15 17...]
    filter out all numbers divisible by 3
    [2 3 5 7 11 13 17 19 ...]
   */

  // [first, fibonacci(second, first + second)]
  def fibonacci(first: Int, second: Int): MyStream[Int] =
    new NonEmptyStream[Int](first, fibonacci(second, first + second))

  println(fibonacci(1,1).take(10).toList())

  /*
    [ 2 3 4 5 6 7 8 9 10 11 12 ...]
    [ 2 3 5 7 9 11 13...] devided by 2
    [ 2 eratosthenes applied to (numbers filtered by n % 2 != 0) ]
    [ 2 3 eratosthenes applied to [5 7 9 11 13 ...] filtered by n % 3 != 0 ]
   */
  // eratosthenes sieve
  def eratosthenes(numbers: MyStream[Int]): MyStream[Int] =
    if (numbers.isEmpty) numbers
    else new NonEmptyStream[Int](numbers.head, eratosthenes(numbers.tail.filter(_ % numbers.head != 0)))

  println(eratosthenes(MyStream.from(2)(_ + 1)).take(20).toList())

    @tailrec
    def fib(n: Int, prev: Int = 1, acc: Int = 0): Int =
    if (n <= 0) acc
    else fib(n-1, acc, acc + prev) // n = 4 fib(3, 1, 1) => fib(2, 1, 2) => fib(1, 2 , 3) => fib(0, 3, 5)


  println(fib(0))  // n = 0 == 0
  println(fib(1)) // n = 1  ==  1
  println(fib(2)) // n = 2  == 1 + 0 = 1
  println(fib(3)) // n = 3 == 1 + 1 + 0
  println(fib(4)) // n = 3 == 1 + 1 + 0
  println(fib(5)) // n = 3 == 1 + 1 + 0
  println(fib(6)) // n = 3 == 1 + 1 + 0
}
