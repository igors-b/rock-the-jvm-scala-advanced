package lectures.part5ts

object SelfTypes extends App {

  // requiring a type to be mixed in

  trait Instrumentalist {
    def play(): Unit
  }

  trait Singer { self: Instrumentalist => // SELF TYPE whoever implements Singer must implement Instrumentalist

    // rest of the implementation or API
    def sing(): Unit
  }

  class LeadSinger extends Singer with Instrumentalist {
    override def play(): Unit = ???
    override def sing(): Unit = ???
  }

  //Illegal
//  class Vocalist extends Singer {
//    override def sing(): Unit = ???
//  }

  val jamesHetfield = new Singer with Instrumentalist {
    def play(): Unit = ???
    def sing(): Unit = ???
  }

  class Guitarist extends Instrumentalist {
    override def play(): Unit = println("(guitar solo)")
  }

  val ericClapton = new Guitarist with Singer {
    override def sing(): Unit = ???
  }

  // SELF-TYPES are usually compared with INHERITANCE
  class A
  class B extends A // B is an A

  trait T
  trait S { self: T => } // S requires T

  // CAKE PATTERN => "dependency injection" in JAVA

  // classical dependency injection
  class Component {
    // API
  }

  class ComponentA extends Component
  class ComponentB extends Component
  class DependentComponent(val component: Component)

  // CAKE PATTERN
  trait ScalaComponent {
    // API
    def action(x: Int): String
  }

  trait ScalaDependentComponent { self: ScalaComponent =>
    def dependentAction(x: Int): String = action(x) + " this rocks!"
  }

  trait ScalaApplication { self: ScalaDependentComponent => }

  // layer 1 - small components
  trait Picture extends ScalaComponent
  trait Stats extends ScalaComponent

  // layer 2 - compose
  trait Profile extends ScalaDependentComponent with Picture
  trait Analytics extends ScalaDependentComponent with Stats

  // layer 3 app
  trait AnalyticsApp extends ScalaApplication with Analytics

  /*
     The main difference between "dependency injection" and "CAKE PATTERN" is that when using
     "dependency injection" pattern framework or another piece of code takes care to verify or to inject our values at
     runtime! Under the "CAKE PATTERN" these dependencies are checked at compile time! FUNDAMENTAL DIFFERENCE!!!

   */

  // cyclical dependencies
  // compiler fails with such an error - illegal cyclic reference involving class X
//  class X extends Y
//  class Y extends X

  trait X { self: Y => }
  trait Y { self: X => }
}
