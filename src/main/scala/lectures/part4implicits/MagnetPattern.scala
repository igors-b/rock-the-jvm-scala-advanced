package lectures.part4implicits

import scala.concurrent.{ExecutionContext, Future}

object MagnetPattern extends App {

  // method overloading
  class P2PRequest
  class P2PResponse
  class Serializer[T]

  trait Actor {
    def receive(statusCode: Int): Int
    def receive(request: P2PRequest): Int
    def receive(response: P2PResponse): Int
    def receive[T](message: T)(implicit serializer: Serializer[T]): Int
    // The same as previous but serializer declared implicitly using context bound:
    // def receive[T: Serializer](message: T): Int
    def receive[T: Serializer](message: T, statusCode: Int): Int
    def receive(future: Future[P2PRequest]): Int
    // def receive(future: Future[P2PResponse]): Int - IT'S NOT possible to have two methods with same parameter type Future
    // lots of overloads
  }

  /*
    1 - type erasure
    2 - lifting doesn't work for all overloads
        val  receiveFV = receive _ // ?!

    3 - code duplication
    4 - type inferrence and default args

       actor.receive(?!)
   */

  trait MessageMagnet[Result] {
    def apply(): Result
  }

  def receive[R](magnet: MessageMagnet[R]): R = magnet()

  implicit class FromP2PRequest(request: P2PRequest) extends MessageMagnet[Int] {
    def apply(): Int = {
      // logic for handling P2P request
      println("Handling P2P request")
      42
    }
  }

  implicit class FromP2PResponse(request: P2PResponse) extends MessageMagnet[Int] {
    def apply(): Int = {
      // logic for handling P2P response
      println("Handling P2P response")
      24
    }
  }

  receive(new P2PRequest)
  receive(new P2PResponse)

  // 1 - no more type erasure problems!
  implicit class FromRequestFuture(future: Future[P2PRequest]) extends MessageMagnet[Int] {
    def apply(): Int = 2
  }

  implicit class FromResponseFuture(future: Future[P2PResponse]) extends MessageMagnet[Int] {
    def apply(): Int = 3
  }

  implicit val ec = ExecutionContext.global
  println(receive(Future(new P2PRequest)))
  println(receive(Future(new P2PResponse)))

  // 2- lifting works
  trait MathLib {
    def add1(x: Int): Int = x + 1
    def add1(x: String): Int = x.toInt + 1
    // add1 overloads
  }

  // "magnetize"
  trait AddMagnet {
    def apply(): Int
  }

  def add1(magnet: AddMagnet): Int = magnet()

  implicit class AddInt(x: Int) extends AddMagnet {
    def apply(): Int = x + 1
  }

  implicit class AddString(x: String) extends AddMagnet {
    def apply(): Int = x.toInt + 1
  }

  val addFV = add1 _
  println(addFV(1))
  println(addFV("3"))

  val receiveFV = receive[Int] _
  receiveFV(new P2PRequest)
  receiveFV(new P2PResponse)

  /*
    DRAWBACKS

      1 - super verbose
      2 - harder to read
      3 - you can't name or place default arguments
      4 - call by name doesn't work correctly
      (exercise: prove it) (hint: side effects)
   */

  class Handler {
    def handle(s: => String) = {
      println(s)
      println(s)
    }
  }

  trait HandleMagnet {
    def apply(): Unit
  }

  def handle(magnet: HandleMagnet) = magnet()

  implicit class StringHandle(s: => String) extends HandleMagnet {
    def apply(): Unit = {
      println(s)
      println(s)
    }
  }

  def sideEffectMethod(): String = {
    println("Hello, Scala")
    "hahaha"
  }

  handle(sideEffectMethod())
}
