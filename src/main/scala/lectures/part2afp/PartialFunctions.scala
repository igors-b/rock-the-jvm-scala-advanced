package lectures.part2afp

object PartialFunctions extends App {

  val aFunction = (x: Int) => x + 1 // Function1[Int, Int] === Int => Int

  val aFussyFunction = (x: Int) =>
    if (x == 1) 42
    else if (x == 2) 56
    else if (x == 5) 999
    else throw new FunctionNotApplicableException

  class FunctionNotApplicableException extends RuntimeException

  val aNicerFussyFunction = (x: Int) => x match {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  }
  // {1, 2, 5} => Int

  val aPartialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  } // a partial function value

  println(aPartialFunction(2))
  // println(aPartialFunction(3)) throws MatchError

  // PF utilities

  println(aPartialFunction.isDefinedAt(45))

  // lift
  val lifted = aPartialFunction.lift // Int => Option[Int]
  println(lifted(2))
  println(lifted(3))

  val pfChain = aPartialFunction.orElse[Int, Int] {
    case 45 => 67
  }

  println(pfChain(2))
  println(pfChain(45))

  // PF extend normal functions
  val aTotalFunction: Int => Int = {
    case 1 => 99
  }

  // HOFs accept partial functions as well
  val mappedList = List(1, 2, 3).map {
    case 1 => 34
    case 2 => 234
    case 3 => 900
  }
  println(mappedList)

  /*
    Note: PFs can only have ONE parameter  type
   */

  /**
   * Exercises
   *
   * 1 - construct partial function  yourself (anonymous class)
   * 2 - dumb chatbot as a PF
   */

  // 1
  val myPartialFunction = new PartialFunction[String, String] {
    override def isDefinedAt(x: String): Boolean = x == "hello" || x == "What is your name?" || x == "Good buy"

    override def apply(v1: String): String = v1 match {
      case "hello" => "Hi there!"
      case "What is your name?" => "My name is Chat-Bot"
      case "Good buy" => "I'll see you later"
    }
  }

  scala.io.Source.stdin.getLines().map(myPartialFunction).foreach(println)
}
