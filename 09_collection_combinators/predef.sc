// Start a repl and load this in using `import $file.predef, predef._`

val bigNames = List(
  "Boban", "Enxhell", "Zij",
  "Cynthia", "Zack", "Pinxi",
  "Willy", "Lulu", "Clement",
  "Rohan", "James", "Bobantha",
  "Thilo", "Bobanita", "Jon"
)

val names1 = Vector("Boban", "Enxhell", "Zij", "Cynthia")

val naughtyNames = Vector("Boban", "", "Zij", "", "Cynthia")

import scala.collection.mutable.ArraySeq
val names2 = ArraySeq("Boban", "Enxhell", "Bobanita", "Bobanta", "Willy")

val names3 = ArraySeq("Boban", "Enxhell", "Bobanita", "Bobanta", "Willy", "Zij", "McBoban", "Rohan")

val names4 = List("Boban", "Enxhell", "Zij", "Willy", "Rohan")

case class Person(name: String, age: Int)

// Input
val people = Array(
  Person("Boban", 26),
  Person("Enxhell", 16),
  Person("James", 45),
  Person("Clement", 26)
)

val names5 = List("Zack", "Boban", "Rohan")

val numbers = Vector(-10, -6, -4, -1, 0, 3, 10, 12)

val names6 = List("Boban", "Enxhell", "James", "Clement")
val ages = List(26, 16, 45, 26)

val arrays = List(
  Array(1, 2),
  Array(3, 4),
  Array(5, 6)
)

val moreArrays = List(
  Array(3, 4),
  Array(5, 6)
)

val names7 = Vector("Enxhell", "Zij", "Boban", "Cynthia", "Bobanita")

val names8 = LazyList("Boban", "Boban", "Enxhell", "James", "Zij", "Zack", "James")
