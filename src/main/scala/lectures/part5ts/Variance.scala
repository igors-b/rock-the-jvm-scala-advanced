package lectures.part5ts

object Variance extends App {

  trait Animal
  class Dog extends Animal
  class Cat extends Animal
  class Crocodile extends Animal

  // What is variance???
  // "inheritance" - type substitution of generics

  class Cage[T]
  // 1. yes - covariance
  class CCage[+T]
  val ccage: CCage[Animal] = new CCage[Cat]

  // 2. no - invariance
  class ICage[T]
  // val icage: ICage[Animal] = new ICage[Cat] - wrong

  // 3. hell no - opposite = contravariance
  class XCage[-T]
  val XCage: XCage[Cat] = new XCage[Animal]

  class InvariantCage[T](val animal: T) // invariant

  // covariant positions
  class CovariantCage[+T](val animal: T) // COVARIANT POSITION

  //class ContravariantCage[-T](val animal: T)
  /*
    val catCage: XCage[Cat] = new XCage[Animal](new Crocodile)
   */

  // class CovariantVariableCage[+T](var animal: T) // types of vars are in CONTRAVARIANT POSITION
  /*
    val ccage: CCage[Animal] = new CCage[Cat](new Cat)
    ccage.animal = new Crocodile
   */

  // class ContravariantVariableCage[-T](var animal: T) // also in COVARIANT POSITION
  /*
  val catCage: XCage[Cat] = new CCage[Animal](new crocodile)
   */

  class InvariantVariableCage[T](var animal: T) // ok

//  trait AnotherCovariantCage[+T] {
//    def addAnimal(animal: T) // method argument is in CONTRAVARIANT POSITION
//  }

  /*
    val ccage: CCage[Animal] = new CCage[Dog]
    ccage.add(new Cat)
   */

  class AnotherContravariantCage[-T] {
    def addAnimal(animal: T) = true
  }
  val acc: AnotherContravariantCage[Cat] = new AnotherContravariantCage[Animal]
  // acc.addAnimal(new Dog) WRONG
  acc.addAnimal(new Cat)
  class Kitty extends Cat
  acc.addAnimal(new Kitty)

  class MyList[+A] {
    def add[B >: A](element: B): MyList[B] = new MyList[B] // widening the type
  }

  val emptyList = new MyList[Kitty]
  val animals = emptyList.add(new Kitty)
  val moreAnimals = animals.add(new Cat)
  val evenMoreAnimals = moreAnimals.add(new Dog)

  // METHOD ARGUMENTS ARE IN CONTRAVARIANT POSITION

  // return types
  class PetShop[-T] {
//    def get(isItAPuppy: Boolean): T // METHOD RETURN TYPES ARE IN COVARIANT POSITION
    /*
      val catShop = new PetShop[Animal] {
        def get(isItAPuppy: Boolean): Animal = new Cat
      }

      val dogShop: PetShop[Dog] = catShop
      dogShop.get(true) // EVIL CAT!!!
     */
    def get[S <: T](isItAPuppy: Boolean, defaultAnimal: S): S = defaultAnimal
  }

  val shop: PetShop[Dog] = new PetShop[Animal]
  // val evilCat = shop.get(true, new Cat) - WRONG
  class TerraNova extends Dog
  val bigFurry = shop.get(true, new TerraNova)

  /*
    Big rule
    - method arguments are in CONTRAVARIANT POSITION
    - return types are in COVARIANT POSITION
   */

  /**
   * 1. Invariant, covariant, contravariant
   *   Parking[T](things: List[T]) {
   *     def park(vehicle: T)
   *     def impound(vehicles: List[T])
   *     def checkVehicles(conditions: String): List[T]
   *   }
   *
   * 2. Used someone else's API: IList[T]
   * 3. Parking = monad!
   *    - flatMap
   */

  class Vehicle
  class Bike extends Vehicle
  class Car extends Vehicle

  class IList[T]

  // 1.1. Invariant
  class ParkingInvariant[T](things: List[T]) {
    def park(vehicle: T): ParkingInvariant[T] = new ParkingInvariant[T](things :+ vehicle)
    def impound(vehicles: List[T]) = new ParkingInvariant[T](things.filter(vehicles.contains))
    def checkVehicles(conditions: String): List[T] = things
    def flatMap[S](f: T => ParkingInvariant[S]): ParkingInvariant[S] = ???
  }

  class ParkingCovariant[+T](things: List[T]) {
    def park[B >: T](vehicle: B): ParkingCovariant[B] = new ParkingCovariant[B](things :+ vehicle)
    def impound[B >: T](vehicles: List[B]) = new ParkingCovariant[B](things.filter(vehicles.contains))
    def checkVehicles(conditions: String): List[T] = things
    def flatMap[S](f: T => ParkingCovariant[S]): ParkingCovariant[S] = ???
  }

  class ParkingContravariant[-T](things: List[T]) {
    def park(vehicle: T): ParkingContravariant[T] = new ParkingContravariant[T](things :+ vehicle)
    def impound(vehicles: List[T]) = new ParkingContravariant[T](things.filter(vehicles.contains))
    def checkVehicles[B <: T](conditions: String): List[B] = List.empty
    def flatMap[R <: T, S](f: R => ParkingContravariant[S]): ParkingContravariant[S] = ???
  }

  /*
    Rule of thumb
      - use covariance = COLLECTION OF THINGS
      - use contravariance = GROUP OF ACTIONS
   */

  class ParkingCovariant2[+T](things: IList[T]) {
    def park[B >: T](vehicle: B): ParkingCovariant2[B] = ???
    def impound[B >: T](vehicles: IList[B]): ParkingCovariant2[B] = ???
    def checkVehicles[B >: T](conditions: String): IList[B] = ???
  }

  class ParkingContravariant2[-T](things: IList[T]) {
    def park(vehicle: T): ParkingContravariant2[T] = ???
    def impound[B <: T](vehicles: IList[B]): ParkingContravariant2[B] = ???
    def checkVehicles[B <: T](conditions: String): IList[B] = ???
  }

  // flatMap

}
