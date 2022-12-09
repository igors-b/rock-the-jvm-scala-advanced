package lectures.part3concurrency

import java.util.concurrent.atomic.AtomicReference
import scala.collection.parallel.CollectionConverters._
import scala.collection.parallel.immutable.ParVector

object ParallelUtils extends App {
  // 1 - parallel collections

  val parList = List(1,2,3).par

  val parVector = ParVector[Int](1,2,3)

  /*
    Seq
    Vector
    Array
    Map - Hash, Trie
    Set - Hash, Trie
   */

  def measure[T](operation: => T): Long = {
    val time = System.currentTimeMillis()
    operation
    System.currentTimeMillis() - time
  }

  val list = (1 to 100000000).toList
  val serialTime = measure {
    list.map(_ + 1)
  }
  val parallelTime = measure {
    list.par.map(_ + 1)
  }

  println("serialTime " + serialTime)
  println("parallelTime " + parallelTime)

  // 2 - atomic ops and references
  val atomic = new AtomicReference[Int](2)
  val currentValue = atomic.get() // thread-safe read
  atomic.set(4) // thread-safe write
  atomic.getAndSet(5) // thread-safe combo
  atomic.compareAndSet(38, 56) // if the value = 38, than set the value to 56
  // reference equality

  atomic.updateAndGet(_ + 1) // thread-safe function run
  atomic.getAndUpdate(_ + 1) // inverse order of previous method

  // accululation
  atomic.accumulateAndGet(12, _ + _) // thread-safe accumulation
  // 1. take the argument
  // 2. take the value of atomic
  // 3. calculate the value of function
  // 4. set the value inside the atomic
  // 5. return the value of atomic
  atomic.getAndAccumulate(12, _ + _)
}
