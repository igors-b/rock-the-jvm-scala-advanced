package lectures.part4implicits

object OrganizingImplicits extends  App {

  implicit def reverseOrdering(): Ordering[Int] = Ordering.fromLessThan(_ > _)
  //implicit val normalOrdering: Ordering[Int] = Ordering.fromLessThan(_ < _)
  println(List(1,4,2,3).sorted)
  // implicit ordering taken from scala.Predef ???? which imported by default

  /*
    Implicits (used as implicit parameters):
      - val/var
      - object
      - accessor methods = defs with no parentheses
   */

  // Exercise
  case class Person(name: String, age: Int)

  val persons = List(
    Person("Steve", 30),
    Person("Amy", 22),
    Person("John", 66),
  )

  object SomeObject {
    // it will not compile since implicit ordering is not in the implicit scope
    implicit val personAlphabeticOrdering: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)
  }

  object Person {
    // it will compile since implicit ordering is in the implicit scope for Person case class
    implicit val personAlphabeticOrdering: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)
  }

  // Implement implicit Ordering for Person which sort persons alphabetically by name
  implicit val personAlphabeticOrdering: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)

  println(persons.sorted)

  /*
    Implicit scope
      - normal scope = LOCAL SCOPE
      - imported scope
      - companions of all types involved in the method signature
        - List companion Onject
        - Ordering companion object
        - all the types involved = A or any supertype
   */
  // def sorted[B >: A](implicit ord: Ordering[B]): List[B]

  /*
    BEST PRACTICES
    When defining an implicit val:

    #1
     - if there is a single possible value for it
     - and you can edit the code for the type
     THEN DEFINE THE IMPLICIT IN THE COMPANION

    #2
     - if there are many possible values for it
     - but a single good one
     - and you can edit the code for the type
     THEN DEFINE THE GOOD ONE IMPLICIT IN THE COMPANION
   */

  /*
    EXERCISE

    - totalPrice = most used (50%)
    - by unit count = 25%
    - by unit price = 25%

    IMPLEMENT ORDERINGS AND PUT THEM IN THE RIGHT PLACE
   */

  case class Purchase(nUnits: Int, unitPrice: Double) {
    def totalPrice: Double = nUnits * unitPrice
  }

  object Purchase {
    implicit val totalPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan((a, b) => a.totalPrice < b.totalPrice)
  }

  object PurchaseOrderingByCount {
    implicit val countOrdering: Ordering[Purchase] = Ordering.fromLessThan(_.nUnits < _.nUnits)
  }

  object PurchaseOrderingByUnitPrice {
    implicit val countOrdering: Ordering[Purchase] = Ordering.fromLessThan(_.unitPrice < _.unitPrice)
  }

}
