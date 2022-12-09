package lectures.part3concurrency

import scala.concurrent.duration._
import scala.concurrent.{Await, Future, Promise}
import scala.util.{Failure, Random, Success, Try}

object FuturesPromises extends  App {

  // implicit val ec: ExecutionContextExecutor = ExecutionContext.global TODO: that was my solution
  import scala.concurrent.ExecutionContext.Implicits.global

  def calculateMeaningOfLife: Int = {
    Thread.sleep(2000)
    42
  }

  val aFuture = Future {
    calculateMeaningOfLife // calculates the meaning of life ob ANOTHER thread
  } // (global) which is passed by the compiler implicitly

  println(aFuture.value) // Option[Try[Int]]

  println("Waiting on the future")
  aFuture.onComplete { // TODO: REMEMBER - the return type of onComplete method is Unit
    case Failure(e) => println(s"I''e failed with exception: $e")
    case Success(value) => println(s"The result of evaluation of a future is: $value")
  } //on SOME thread (we don't know on which thread this Partial function of onComplete method will be evaluated)

  Thread.sleep(5000)

  // mini social network

  case class Profile(id: String, name: String) {
    def poke(anotherProfile: Profile): Unit =
      println(s"${this.name} poking ${anotherProfile.name}")
  }

  object SocialNetwork {
    // "database"
    val names = Map(
      "fb.id.1-zuck" -> "Mark",
      "fb.id.2-bill" -> "Bill",
      "fb.id.0-dummy" -> "Dummy"
    )

    val friends = Map(
      "fb.id.1-zuck" -> "fb.id.2-bill"
    )

    val random = new Random()

    // API
    def fetchProfile(id: String): Future[Profile] = Future {
      Thread.sleep(random.nextInt(300))
      Profile(id, names(id))
    }

    def fetchBestFriend(profile: Profile): Future[Profile] = Future {
      Thread.sleep(random.nextInt(400))
      val bfId = friends(profile.id)
      Profile(bfId, names(bfId))
    }
  }

  // client: mark to poke bill
  val mark = SocialNetwork.fetchProfile("fb.id.1-zuck")
  mark.onComplete {
    case Success(markProfile) =>
      val bill = SocialNetwork.fetchBestFriend(markProfile)
      bill.onComplete {
        case Success(billProfile) => markProfile.poke(billProfile)
        case Failure(e) => e.printStackTrace()
      }
    case Failure(ex) => ex.printStackTrace()
  }

  Thread.sleep(1000)

  // functional composition of futures
  // map, flatMap, filter
  val nameOnTheWall = mark.map(profile => profile.name)

  val marksBestFriend = mark.flatMap(SocialNetwork.fetchBestFriend)

  val zucksBestFriendRestricted = marksBestFriend.filter(_.name.startsWith("Z"))

  // for-comprehensions
  for {
    mark <- SocialNetwork.fetchProfile("fb.id.1-zuck")
    bill <- SocialNetwork.fetchBestFriend(mark)
  } mark.poke(bill)
  Thread.sleep(1000)

  // fallbacks
  val aProfileNoMatterWhat = SocialNetwork.fetchProfile("unknown id").recover {
    case _: Throwable => Profile("fb.id.0-dummy", "Forever alone")
  }

  val aFetchedProfileNoMatterWhat = SocialNetwork.fetchProfile("unknown id").recoverWith {
    case _: Throwable => SocialNetwork.fetchProfile("fb.id.0-dummy")
  }

  val fallbackResult = SocialNetwork.fetchProfile("unknown id").fallbackTo(SocialNetwork.fetchProfile("fb.id.0-dummy"))

  // online banking app
  case class User(name: String)
  case class Transaction(sender: String, receiver: String, amount: Double, status: String)

  object BankingApp {
    val name = "Rock the JVM banking"

    def fetchUser(name: String): Future[User] = Future {
      //simulate fetching from the DB
      Thread.sleep(500)
      User(name)
    }

    def createTransaction(user: User, merchantName: String, amount: Double): Future[Transaction] = Future {
      //simulate some processes
      Thread.sleep(1000)
      Transaction(user.name, merchantName, amount, "SUCCESS")
    }

    def purchase(userName: String, item: String, merchantName: String, cost: Double): String = {
      // fetch the user from the DB
      // create a transaction
      // WAIT for the transaction to finish
      val transactionStatusFuture = for {
        user <- fetchUser(userName)
        transaction <- createTransaction(user, merchantName, cost)
      } yield transaction.status

      Await.result(transactionStatusFuture, 2.seconds) // implicit conversions -> pimp my library
    }
  }
  println(BankingApp.purchase("Daniel", "iphone 12", "Rock the JVM store", 3000))

  // promises

  val aPromise = Promise[Int]() // "controller" over a future
  val future = aPromise.future

  // thread 1 - "consumer"
  future.onComplete {
    case Success(r) => println("[consumer] I've received " + r)
  }

  // thread 2 - "producer"
  val producer = new Thread(() => {
    println("[producer] crunching numbers...")
    Thread.sleep(500)
    // "fulfilling" the promise
    aPromise.success(42)
    println("[producer] done")
  })

  producer.start()
  Thread.sleep(1000)

  /*
    1) fulfill a future IMMEDIATELY with a value
    2) run a function called inSequence which returns a future after this future finish running
      def inSequence(fa, fb) -> run fb after fa finishes
    3) return a future containing the earliest value returned by two futures
      def first(fa, fb) => new future containing the value of the fa or fb which finishes first
    4) def last(fa, fb) => new future with the last value
    5) run an action repeatedly until condition is met => return first value that satisfies the condition
      def retryUntil[T](action: () => Future[T], condition: T => Boolean): Future[T]
   */

  // 1) fulfill a future IMMEDIATELY with a value
  def fulfillImmediately[T](value: T): Future[T] = Future(value)

  // 2) run a function called inSequence which returns a future after this future finish running
  //    def inSequence(fa, fb) -> run fb after fa finishes
  def inSequence[A, B](first: Future[A], second: Future[B]): Future[B] =
    first.flatMap(a => second.map(b => {
      println(s"[first] finished with value $a")
      Thread.sleep(500)
      println(s"[second] finished with value $b")
      b
    }))

  inSequence(Future(3+4), Future(10))
  Thread.sleep(1000)

  //3) return a future containing the earliest value returned by two futures
  //      def first(fa, fb) => new future containing the value of the fa or fb which finishes first
  def first[T](fa: Future[T], fb: Future[T]): Future[T] = {
    val promise = Promise[T]()
    val future =promise.future
    fa.onComplete(promise.tryComplete)
    fb.onComplete(promise.tryComplete)
    future.onComplete{ case Success(value) => println(s"The first was $value")}
    future
  }

  val firstF: Future[String] = Future {
    Thread.sleep(500)
    "first"
  }
  val lastF: Future[String] = Future {
    Thread.sleep(2000)
    "last"
  }

  first(firstF, lastF)

  // 4) def last(fa, fb) => new future with the last value
  def last[T](fa: Future[T], fb: Future[T]): Future[T] = {
    val promiseFirst = Promise[T]()
    val promiseLast = Promise[T]()
    val futureLast = promiseLast.future
    fa.onComplete {
      case Success(value) => if (Try(promiseFirst.success(value)).isSuccess) () else Try(promiseLast.success(value))
    }
    fb.onComplete {
      case Success(value) => if (Try(promiseFirst.success(value)).isSuccess) () else Try(promiseLast.success(value))

    }
    futureLast.onComplete { case Success(value) => println(s"The last was $value") }
    futureLast
  }

  last(firstF, lastF)

  def last2[T](fa: Future[T], fb: Future[T]): Future[T] = {
    val promiseFirst = Promise[T]
    val promiseLast = Promise[T]

    val checkAndComplete = (result: Try[T]) =>
      if (!promiseFirst.tryComplete(result))
        promiseLast.complete(result)

    fa.onComplete(checkAndComplete)
    fb.onComplete(checkAndComplete)

    promiseLast.future
  }

  //5) run an action repeatedly until condition is met => return first value that satisfies the condition
  //   def retryUntil[T](action: () => Future[T], condition: T => Boolean): Future[T]
  def retryUntil[T](action: () => Future[T], condition: T => Boolean): Future[T] = {
    val promise = Promise[T]()
    def helper(): Unit = action().onComplete {
      case Success(value) => if (condition(value)) promise.success(value) else helper()
    }
    helper()
    promise.future.onComplete { case Success(value) => println(value)}
    promise.future
  }
  retryUntil(() => Future(5), (a: Int) =>  a > 4)

  def retryUntil2[T](action: () => Future[T], condition: T => Boolean): Future[T] = {
    action()
      .filter(condition)
      .recoverWith {
        case _ => retryUntil2(action, condition)
      }
  }

  val random = new Random()
  val action = () => Future {
    Thread.sleep(100)
    val nextValue = random.nextInt(100)
    println("generated " + nextValue)
    nextValue
  }

  retryUntil2(action, (x: Int) => x < 50).foreach(result => println("settled at " + result))
  Thread.sleep(10000)
}
