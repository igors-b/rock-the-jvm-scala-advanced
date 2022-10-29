package lectures.part3concurrency

import java.util.concurrent.Executors
import scala.annotation.tailrec

object Intro extends App {

  // JVM threads
  /*
    interface Runnable {
      public void run()
    }
   */
  val runnable = new Runnable {
    override def run(): Unit = println("Running in parallel")
  }
  val aThread = new Thread(runnable)

  aThread.start() // gives the signal to the JVM to start a JVM thread
  // creates a thread which runs on top of OS thread
  runnable.run() // calls  run method of the Thread instance, does not do anything in parallel
  aThread.join() // blocks until aThread finishes running

  val threadHello = new Thread(() => (1 to 5).foreach(_ => println("Hello")))
  val threadGoodbye = new Thread(() => (1 to 5).foreach(_ => println("Goodbye")))

  threadHello.start()
  threadGoodbye.start()
  // different runs produce different results!

  // executors
  val pool = Executors.newFixedThreadPool(10)
//  pool.execute(() => println("something in the thread pool"))
//
//  pool.execute(() => {
//    Thread.sleep(1000)
//    println("Done after 1 second")
//  })
//
//  pool.execute(() => {
//    Thread.sleep(1000)
//    println("Almost done")
//    Thread.sleep(1000)
//    println("Done after 2 seconds")
//  })

  pool.shutdown()
//  pool.execute(() => println("should not appear")) // throws an exception in the calling thread
//  pool.shutdownNow()
  println(pool.isShutdown) // true

  def runInParallel = {
    var x = 0
     val thread1 = new Thread(() => {
       x = 1
     })

    val thread2 = new Thread(() => {
      x = 2
    })

    thread1.start()
    thread2.start()
    println(x)
  }

//  (1 to 100).foreach(_ => runInParallel)
  // race condition

  class BankAccount(@volatile var amount: Int) {
    override def toString: String = "" + amount
  }

  def buy(account: BankAccount, thing: String, price: Int) = {
    account.amount -= price
//    println("I've bought " + thing)
//    println("my account is now " + account)
  }

  for (_ <- 1 to 1000) {
    val account = new BankAccount(50000)
    val thread1 = new Thread(() => buy(account, "shoes", 3000))
    val thread2 = new Thread(() => buy(account, "iphone12", 4000))

    thread1.start()
    thread2.start()
    Thread.sleep(10)
    if (account.amount != 43000) println("AHA: " + account)
//    println()
  }

  /*
    thread1 (shoes): 50000
      - account = 50000 - 3000 = 47000
    thread2 (iphone): 50000
      - account = 50000 - 4000 = 46000 overwrites the memory of account.amount
   */

  // option #1: use synchronized()
  def buySafe(account: BankAccount, thing: String, price: Int) =
    account.synchronized {
      // no two threads can evaluate this at the same time
      account.amount -= price
      println("I've bought " + thing)
      println("my account is now " + account)
    }

  // option #2: use @volatile

  /**
   * Exercises
   *
   * 1) Construct 50 "inception" threads (inception threads are treads which constructs another threads)
   *      Thread1 -> thread2 -> thread3 -> ....
   *      println("hello from thread #3")
   *    in REVERSE ORDER
   */

  def inceptionThreads(amount: Int): Unit = {
    if (amount <= 0) ()
    else {
      new Thread(() => {
        println(s"Hello from thread #$amount")
        inceptionThreads(amount-1)
      }).start()
    }
  }
  inceptionThreads(10)

  def inceptionThreadsRTJVM(maxThreads: Int, i: Int = 1): Thread = new Thread(() => {
    if (i < maxThreads) {
      val newThread = inceptionThreadsRTJVM(maxThreads, i + 1)
      newThread.start()
      newThread.join()
    }
    println(s"Hello RTJVM from thread #$i")
  })
  inceptionThreadsRTJVM(50).start()

  /**
   * 2)
   */
  var x = 0
  val threads = (1 to 100).map(_ => new Thread(() => x += 1))
  threads.foreach(_.start())
  /*
    1) what is the biggest value possible for x? 100
    2) what is the smallest value possible for x? 1
   */
  threads.foreach(_.join())
  println(x)

  /**
   * 3) sleep fallacy
   */
  var message = ""
  val awesomeThread = new Thread(() => {
    Thread.sleep(1000)
    message = "Scala is awesome"
  })

  message = "Scala sucks"
  awesomeThread.start()
  Thread.sleep(900)
  awesomeThread.join() //todo fix the problem of exercise #3
  println(message)
  /*
    What is the value of message TODO almost always "Scala is awesome"
    Is it guaranteed? TODO NO!
    Why?/ Why not?

    (main thread)
      message = "Scala sucks"
      awesomeThread.start()(
        sleep() - relieves execution
    (awesome thread)
      sleep() - relieves execution
    (OS gives the CPU to some important thread which takes the CPU for more than 2 seconds)
    (OS is free to choose which thread to run and OS could give the CPU to the MAIN thread)
      println("Scala sucks")
    (OS gives the CPU to awesomeThread)
      message = "Scala is awesome"
   */
}
