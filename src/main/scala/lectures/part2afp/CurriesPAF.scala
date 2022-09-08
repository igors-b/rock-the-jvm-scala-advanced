package lectures.part2afp

object CurriesPAF extends App {

  // curried functions
  val superAdder: Int => Int => Int =
    x => y => x + y

  val add3 = superAdder(3) // Int => Int = y => 3 + y
  println(add3(5))
  println(superAdder(3)(5))

  // METHOD!
  def curriedAdder(x: Int)(y: Int): Int = x + y // curried method

  // FUNCTION
  val add4: Int => Int = curriedAdder(4) // method converted into function value
  // transforming method to function is called:
  // lifting = ETA-EXPANSION

  // FUNCTIONS != METHODS (JVM limitation)

  def inc(x: Int) = x + 1
  List(1,2,3).map(inc) // compiler does ETA-EXPANSION for us and turns inc method into a function it rewrites it to:
  List(1,2,3).map(x => inc(x))

  // Partial function applications
  val add5 = curriedAdder(5) _ // do ETA-EXPANSION and transform curriedAdder(5) into a Int => Int function after applying
  // argument 5 to curriedAdder method as it's first curried parameter

  // EXERCISE
  val simpleAddFunction = (x: Int, y: Int) => x + y
  def simpleAddMethod(x: Int, y: Int) =  x + y
  def curriedAddMethod(x: Int)(y: Int) = x + y

  // add7: Int => Int = y => y + 7
  // as many different implementations using the above
  // be creative

  val add7_1 = simpleAddFunction(7, _)
  println(add7_1(3))

  val add7_2 = (x: Int) => simpleAddFunction(7, x) // simplest solution
  println(add7_2(3))

  val add7_3 = simpleAddFunction.curried(7)
  println(add7_3(3))

  val add7_10 = simpleAddFunction(7, _: Int) // alternative syntax for turning methods into function values

  val add7_4: Int => Int = simpleAddMethod(7, _)
  println(add7_4(3))

  val add7_5 = (x: Int) => simpleAddMethod(7, x)
  println(add7_5(3))

  val add7_7 = (x: Int) => curriedAddMethod(7)(x)
  println(add7_7(3))

  val add7_6 = curriedAddMethod(7) _ // PAF
  println(add7_6(3))

  val add7_8 = curriedAddMethod(7)(_) // PAF - alternative syntax
  println(add7_8(3))

  val add7_9 = simpleAddMethod(7, _: Int) // alternative syntax for turning methods into function values
                // y => simpleAddMethod(7, y)
  println(add7_9(3))

  // underscores are powerful
  def concatenator(a: String, b: String, c: String) = a + b + c
  val insertName = concatenator("Hello, I'm ", _: String, "! How are you?") // x => concatenator("Hello, I'm ", x, "! How are you?")
  println(insertName("Igors"))

  val fillInTheBlanks = concatenator("Hello, I'm ", _: String, _: String) // (x, y)  => concatenator("Hello, I'm ", x, y)
  println(fillInTheBlanks("Daniel! ", "Scala is awesome!"))

  // EXERCISES
  /*
    1. Process a list of numbers and return their string representations with different formats
    Use the %4.2f, %8.6f and %4.12f with curried formatter function
   */
  println("%4.2f".format(Math.PI))
  def doubleToString(formatter: String)(number: Double): String = formatter.format(number)
  val formatter_1 = doubleToString("%4.2f") _ // lift method into function
  val formatter_2 = doubleToString("%8.6f") _
  val formatter_3 = doubleToString("%4.12f") _
  val listOfDoubles = List.fill(3)(Math.PI)
  println(listOfDoubles.map(formatter_1))
  println(listOfDoubles.map(formatter_2))
  println(listOfDoubles.map(formatter_3))

  println(listOfDoubles.map(formatter_1)) // compiler does sweet eta-expansion for us
  /*
    2. difference between
    - functions vs methods
    - parameter: by name vs 0-lambda
   */
  def byName(n: => Int) = n + 1
  def byFunction(f: () => Int) = f() + 1

  def method: Int = 42
  def parenmethod(): Int = 42

  /*
    calling byName and byFunction
     - int
     - method
     - parenmethod
     - lambda
     - PAF
   */
  println(byName(42)) // ok
  println(byName(method)) // ok
  println(byName(parenmethod())) // ok
  println(byName(parenmethod)) // ok but beware ==> byName(parenmethod())
//  byName(() => 42) //does not work because byName requires value of type Int NOT function
  println(byName((() => 42)())) // cal lambda == call function by adding to it () returns value which is required for byName as parameter (() => 42)() == 42
//  byName(parenmethod _) // not ok because parenmethod _ returns function value but byName requires val of type Int

//  byFunction(42) // not ok//  byFunction(method) // not ok!!!!  compiler does not do ETA-expansion!!!
  byFunction(parenmethod) // ok compiler does ETA-expansion
  //  byFunction(parenmethod()) // not ok
    byFunction(() => 42) // ok
    byFunction(parenmethod _) // ok
}
