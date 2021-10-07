case class Person(name: String, parrot: String)

val people = List(
  Person("Zij", "sassy-parrot"),
  Person("Rohan", "old-timey-parrot"),
  Person("Boban", "troll-parrot"), // Note how this is a different entry to the one above
  Person("Willy", "honey-badger-parrot")
)

import scala.util.control.Breaks.{break, breakable}

breakable {
  people.foreach { person =>
    if (person.name == "Boban") break
    else println(person.parrot)
  }
}
