package lectures.part5ts

import lectures.part5ts

object TypeMembers extends App {

  class Animal
  class Dog extends Animal
  class Puppy extends Dog
  class Cat extends Animal

  class AnimalCollection {
    // type members
    type AnimalType // abstract type member
    type BoundedAnimal <: Animal
    type SuperBoundedAnimal >: Dog <: Animal // super bound of dog (lower bounded in dog) and upper bounded in Animal
    // type aliases
    type AnimalC = Cat
  }
    val ac = new AnimalCollection
    val dog: ac.AnimalType = ??? // we are not allowed to create instance because we have no constructor for it
    // val cat: ac.BoundedAnimal = new Cat

    val pup: ac.SuperBoundedAnimal = new Dog
    val puppy: ac.SuperBoundedAnimal = new Puppy
    val cat: ac.AnimalC = new Cat

  type CatAlias = Cat
  val anotherCat: CatAlias = new Cat

  // alternative to generics
  trait MyList {
    type T
    def add(element: T): MyList
  }

  class NonEmptyList(value: Int) extends MyList {
    type T = Int
    def add(element: Int): MyList = ???
  }

  // .type
  type CatsType = cat.type
  val newCat: CatsType = cat
  // val newCat2: CatsType = new Cat

  /*
    Exercise - enforce a type to bee applicable to SOME TYPES only
   */
  // LOCKED
  trait MList {
    type A
    def head: A
    def tail : MList
  }

  trait ApplicableToNumbers {
    type A <: Number
  }


  // NOT OK
//  class CustomList(hd: String, tl: CustomList) extends MList with ApplicableToNumbers {
//    type A = String
//    def head = hd
//    def tail = tl
//  }

  // OK
  class IntList(hd: Int, tl: IntList) extends MList {
    type A = Int
    def head = hd
    def tail = tl
  }


}
