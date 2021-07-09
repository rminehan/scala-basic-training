import $ivy.`org.typelevel::cats-core:2.1.1`

case class Person(name: String, age: Int)

val people = List(
  Person("Boban", 28),
  Person("Lulu", 28),
  Person("Zij", 27),
  Person("Clement", 27),
  Person("James", 45),
)
