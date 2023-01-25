package exercises

import lectures.part4implicits.TypeClasses.User

object EqualityPlayground extends App {
  /**
   *  Equality
   */
  trait Equal[T] {
    def apply(comp1: T, comp2: T): Boolean
  }

  implicit object UserEqualityChecker extends Equal[User] {
    def apply(usr1: User, usr2: User): Boolean =
      (usr1.name.compare(usr2.name) == 0) && (usr1.age == usr2.age)
  }

  object UserNameEqualityChecker extends Equal[User] {
    def apply(usr1: User, usr2: User): Boolean = usr1.name.compare(usr2.name) == 0
  }
  val john = User("John", 32, "john@rockthejvm.com")
  val oldJohn = User("John", 70, "igors@rockthejvm.com")

  println(UserEqualityChecker(john, oldJohn))
  println(UserNameEqualityChecker(john, oldJohn))

  /*
  Exercise: implement the TC pattern for the Equality tc.
 */

  object Equal {
    def apply[T](a: T, b: T)(implicit equalityChecker: Equal[T]): Boolean = equalityChecker(a, b)
  }

  println(Equal(john, oldJohn))

  // AD-HOC polymorphism

  /*
    Exercise: improve the Equal type class with implicit conversion class
    ===(anotherValue: T)
    !==(anotherValue: T)
   */
  implicit class EqualEnrichment[T](value: T) {
    def ===(anotherValue: T)(implicit equalityChecker: Equal[T]): Boolean =
      equalityChecker(value, anotherValue)

    def !==(anotherValue: T)(implicit equalityChecker: Equal[T]): Boolean =
      !equalityChecker(value, anotherValue)
  }

  println("EqualEnrichment exercise")
  println(john === oldJohn)
  println(john !== oldJohn)
  /*
    john.===(oldJohn)
    new EqualEnrichment[User].===(oldJohn)
    new EqualEnrichment[User].===(oldJohn)(UserEqualityChecker)
   */
  /*
    TYPE SAFE
   */
}
