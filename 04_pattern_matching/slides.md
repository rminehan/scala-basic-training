---
author: Rohan
title: Session 4 - Pattern Matching
date: 2021-04-27
---

```
 ____       _   _
|  _ \ __ _| |_| |_ ___ _ __ _ __
| |_) / _` | __| __/ _ \ '__| '_ \
|  __/ (_| | |_| ||  __/ |  | | | |
|_|   \__,_|\__|\__\___|_|  |_| |_|

 __  __       _       _     _
|  \/  | __ _| |_ ___| |__ (_)_ __   __ _
| |\/| |/ _` | __/ __| '_ \| | '_ \ / _` |
| |  | | (_| | || (__| | | | | | | | (_| |
|_|  |_|\__,_|\__\___|_| |_|_|_| |_|\__, |
                                    |___/
```

(and destructuring)

---

# This kind of thing

```scala
x match {
  case 1 => ...
  case "foo" => ...
  case List(1, 2, 3) => ...
  case Array(x, _, z) if x > z => ...
}
```

---

# Today

We've seen bits and pieces of pattern matching already.

Today will be a bit more rigorous and fill some gaps.

---

# Remember

Just yell out if you have a question!

---

# Default value

Without a default value, you run the risk of a match exception.

```scala
3 match {
  case 4 => println("Got 4!")
  case 5 => println("Got 5!")
}
```

Generates

```
scala.MatchError: 3 (of class java.lang.Integer)

```

---

# Fixing it

```scala
3 match {
  case 4 => println("Got 4!")
  case 5 => println("Got 5!")
  case _ => println("Unknown input")
}
```

---

# Q: Is underscore a special "default" syntax? (like `default` in java)

---

# A: Not really

When you understand pattern matching more deeply,

you'll see that this is just a particular use of `_`

---

# Guards

An `if` after your pattern.

Good for complex value logic.

```scala
case class Person(name: String, age: Int)

person match {
  case Person(name, age) if name.startsWith("B") => println("Name starts with 'B'")
  case _ => println("Default case")
}
```

Note: no brackets needed around the condition.

---

# Alternatives (Or-ing)

```scala
def age(name: String): Int = name match {
  case "enxhell" | "boban" | "billy" => 16
  case _ => 25
}
```

---

# Restrictions for or-ing

This doesn't make sense:

```scala
person match {
  case Person(name, 13) | Person("bob", age) => println(s"Got person $name with age: $age")
  case _ => println("Default case")
}
```

`name` is only defined in the first alternative

`age` is only defined in the second alternative

The RHS doesn't make sense in either case.

---

# Compiler errors:

```scala
person match {
  case Person(name, 13) | Person("bob", age) => println(s"Got person $name with age: $age")
  case _ => println("Default case")
}
```

yields:

```
cmd3.sc:2: illegal variable in pattern alternative
  case Person(name, 13) | Person("bob", age) => println("Got person with age")
              ^
cmd3.sc:2: illegal variable in pattern alternative
  case Person(name, 13) | Person("bob", age) => println("Got person with age")
                                        ^
Compilation Failed
```

---

# What if the variables are the same?

Here `name` is used in the same way in both alternatives:

```scala
person match {
  case Person(name, 13) | Person(name, 15) => println("Got 13 or 15 year old")
  case _ => println("Default case")
}
```

Compiler says no:

```
cmd2.sc:2: illegal variable in pattern alternative
  case Person(name, 13) | Person(name, 15) => println("Got 13 or 15 year old")
              ^
cmd2.sc:2: illegal variable in pattern alternative
  case Person(name, 13) | Person(name, 15) => println("Got 13 or 15 year old")
                                 ^
```

---

# Underscore

Means "anything goes".

```scala
person match {
  case Person("Boban", _)  => ...
  case _ => ...
}
```

Case 1: `age` can be anything.

Case 2: `Person` can be anything.

---

# Recall default case

```scala
person match {
  case Person("Boban", _)  => ...
  case _ => ...
}
```

Both underscores are doing the same thing.

The second one just happens to be at the "top level".

---

# Spot the difference

```scala
person match {
  case Person("Boban", _)   => println(s"Got name: $name")
  case _ => ...
}

person match {
  case Person("Boban", age) => println(s"Got name: $name")
  case unmatched => ...
}
```

`age` is the same as `_`

They both "match" anything.

The difference is that `_` doesn't bind a name to it.

---

# Alternative default cases

```scala
person match {
  ...
  case unmatched => println(s"Default case: $unmatched")
}

person match {
  ...
  case Person(name, age) => println(s"Default case: $name and $age")
}
```

Both of them will match any person because they have no requirements.

Nothing will get past them.

---

# Collections

```scala
def processList(list: List[Int]): Unit = list match {
  case List(1, 2, 3) => println("Got exact List(1, 2, 3)")

  case List(1, _, 3) => println("Got list length 3 starting with 1 and ending with 3")

  case List(1, _*) => println("Got a list starting with 1, length 1 or more")

  case List(x, y, z) if x <= y && y <= z => println("Got an ordered list length 3")

  case List(_, _, _) => println("Got a list length 3")

  case List(_, _, _*) => println("Got a list length 2 or more")

  case _ => println("Default case")
}

processList(List(1, 2, 3, 4, 5))
processList(List(1, 2, 3))
processList(List(1))
processList(List(1, 4, 3, 4, 5))
processList(List(2, 3, 4))
processList(List(2, 3, 4, 5))
```

---

# Further reading for collections

Read about `unapplySeq`

---

# Destructuring in variable declarations

You can do this:

```scala
def processPair(pair: (Int, Int)): Unit = {
  val (x, y) = pair
  println(x)
  println(y)
}

processPair((1, 2))
```

---

# These are the same kind of thing

Both are using the same pattern matching machinery

```scala
val (x, y) = pair

pair match {
  case (x, y) => ...
}
```

---

# List example

```scala
val list = List(1, 2, 3, 4)

...

val List(a, b, c, d) = list

println(a)
println(b)
println(c)
println(d)
```

----

# List-tuple example

```scala
val list = List(
  ("zij", 1),
  ("clement", 2),
  ("willy", 3)
)

...

val List((name1, pos1), (name2, pos2), (name3, pos3)) = list
```

---

# Recapping that

Destructuring syntax can be used in declarations.

---

# What if it doesn't match?

```scala
val list = List(
  ("zij", 1),
  ("clement", 2),
  ("willy", 3)
)

// Assumes two pairs in the list
val List((name1, pos1), (name2, pos2)) = list
```

---

# Get a match exception

```scala
val list = List(
  ("zij", 1),
  ("clement", 2),
  ("willy", 3)
)

// Assumes two pairs in the list
val List((name1, pos1), (name2, pos2)) = list
```

```
scala.MatchError: List((zij,1), (clement,2), (willy,3))
```

So be careful with your patterns.

---

# Special case

Simple variable names are just "trivial patterns".

```scala
val list = List(1, 2, 3)
```

Analogous to:

```scala
List(1, 2, 3) match {
  case list => ...
}
```

The pattern is simple with no conditions so it always matches.

---

# Matching inside and out

The `@` operator.

---

# Example

We want to bind the person itself as well as its age to variables:

```scala
person match {
  case p @ Person(_, age) => println("Person $p matched with age: $age")
}
```

---

# Can use in subpatterns

```scala
people match {
  case list @ List(person1, person2 @ Person("Boban", _), person3) =>
    println(s"Got 3 people, person2: $person2 is a Boban")

  case _ => println("Default case")
}
```

---

# Pattern matching lists recursively

Understanding head and tail and the `::` syntax.

---

# Terminology

Head - the first element in the list

Tail - the sublist formed from the next element onwards.

Example: `List(0, 1, 2, 3)`

- head: `0`


- tail: `List(1, 2, 3)`

---

# Lists are either:

- empty (called `Nil`)


- non-empty

If it's non-empty, it has a head.

It will also have a tail (possibly empty).

---

# Examples:

`Nil` - empty

`List(1, 2, 3)` - non-empty with head `1` and tail `List(2, 3)`

`List(1)` - non-empty with head `1` and tail `Nil`

---

# Recursion

Lists are often used in recursive algorithms.

## Base case

List is empty

Return something

## Recursive case

List is non-empty

Process the head

Recurse on the tail and combine the results

---

# Example

Add all the numbers in a list.

e.g. `List(1, 2, 3) => 6`

---

# Recursive approach

## Base case

List is empty

Return 0 (the sum of an empty list is zero)

## Recursive case

List is non-empty with a head and tail.

Recursively sum the tail.

Add that to the head.

---

# Implementing it

```scala
def sum(list: List[Int]): Int = list match {
  case Nil => 0
  case head :: tail => head + sum(tail)
}
```

The odd `::` syntax is explained in the video on operators and List.

---

# Issue

Q: What's the issue with this approach?

```scala
def sum(list: List[Int]): Int = list match {
  case Nil => 0
  case head :: tail => head + sum(tail)
}
```

---

# Issue

A: Not stack safe, O(n) space.

Solution here would be tail recursion.

We have training videos for that topic.

I'll put a tail recursive version in the appendix for keen beans.

(Or be adventurous and try it yourself)

---

# Matching by type

```scala
def getInt(any: Any): Int = any match {
  case i: Int => i
  case s: String => s.length
  case f: Float => f.toInt
  case (x: Int, y: String) => getInt(x) + getInt(y)
  case _: Double => 3
  case _ => 0
}
```

---

# Matching by type

What about something like this:

```scala
def getInt(any: Any): Int = any match {
  case _: List[Int] => 0
  case _: List[String] => 1
  case _: List[Double] => 2
  case _ => 3
}

println(getInt(List(0, 1, 2)))
println(getInt(List("Abc", "Def")))
println(getInt(List(1.0, 2.0, 3.0)))
```

What output do you expect?

---

# Q: What output do you expect?

```scala
def getInt(any: Any): Int = any match {
  case _: List[Int] => 0
  case _: List[String] => 1
  case _: List[Double] => 2
  case _ => 3
}

println(getInt(List(0, 1, 2)))
println(getInt(List("Abc", "Def")))
println(getInt(List(1.0, 2.0, 3.0)))
```

A: Probably expecting:

```
0
1
2
```

But you'll get:

```
0
0
0
```

Yikes!

---

# What's going on!

```scala
def getInt(any: Any): Int = any match {
  case _: List[Int] => 0
  case _: List[String] => 1
  case _: List[Double] => 2
  case _ => 3
}

println(getInt(List(0, 1, 2)))
println(getInt(List("Abc", "Def")))
println(getInt(List(1.0, 2.0, 3.0)))
```

Output:

```
0
0
0
```

Q: Has scala forgotten the difference between an Int, String and Double?

---

# Hmmm...

Q: Has scala forgotten the difference between an Int, String and Double?

A: Yes it has.

---

# Type erasure

We're getting stung by something called "type erasure".

---

# Type erasure in a nutshell

```
<nutshell>
```

At runtime, `List[Int]`, `List[String]` and `List[Double]` are all just `List`.

The type parameter was erased when the code was compiled.

```scala
def getInt(any: Any): Int = any match {
  case _: List[Int] => 0
  case _: List[String] => 1
  case _: List[Double] => 2
  case _ => 3
}
```

The above are _runtime_ type checks (not compile time).

```
</nutshell>
```

---

# For more info

This is covered in the COMPAT course (expected for 2027)

---

# Warnings

Q: Why isn't the compiler warning us of this?

A: It actually is, but ammonite is suppressing the warnings.

---

# If you want to see the warnings

In this folder there's a scala script you can run with `scala type_erasure.scala`

(requires you to install scala cli).

```scala
def getInt(any: Any): Int = any match {
  case _: List[Int] => 0
  case _: List[String] => 1
  case _: List[Double] => 2
  case _ => 3
}
```

```
type_erasure.scala:10: warning: non-variable type argument Int
  in type pattern List[Int] (the underlying of List[Int]) is unchecked since it is eliminated by erasure
    case _: List[Int] => 0
            ^
type_erasure.scala:11: warning: non-variable type argument String
  in type pattern List[String] (the underlying of List[String]) is unchecked since it is eliminated by erasure
    case _: List[String] => 1
            ^
type_erasure.scala:12: warning: non-variable type argument Double
  in type pattern List[Double] (the underlying of List[Double]) is unchecked since it is eliminated by erasure
    case _: List[Double] => 2
            ^
type_erasure.scala:11: warning: unreachable code
    case _: List[String] => 1
```

---

# Unreachable code

This is what your code is effectively doing:

```scala
def getInt(any: Any): Int = any match {
  case _: List => 0
  case _: List => 1
  case _: List => 2
  case _ => 3
}
```

```
type_erasure.scala:11: warning: unreachable code
    case _: List[String] => 1
```

Cases should go from narrow -> broad, otherwise lower cases will never get matched.

---

# Should I be worried about type erasure?

If you write idiomatic scala code it's very unlikely you'll hit this.

Ask the Uncles how often they've hit it (or if they even knew it was a problem).

---

# How does pattern matching work?

> This is cool, but how does it work?
>
> Does the scala language have lots of built in rules for these types?

---

# Rules built into the language?

No, it's all being defined through the standard library.

Scala = Scalable Language

---

# How does pattern matching work?

The mechanism here is "extractors".

Types like `List` and tuples are providing extractors.

They integrate with the pattern matching syntax.

Case classes do this for you.

You can do this with your own types.

There is a talk on this.

---

# Summary

- "condition-free" cases like `_` and `person`


- guards - `if`


- alternatives


- collections


- `@` for labelling


- list's `Nil` and `head :: tail`


- matching by type and type erasure


- extensible through defining your own extractors

---

# Official docs

Quick intro: https://docs.scala-lang.org/tour/pattern-matching.html

More details: https://docs.scala-lang.org/overviews/scala-book/match-expressions.html

Look into partial functions too.

---

```
  ____          _        _   _            _
 / ___|___   __| | ___  | | | |_ __   ___| | ___  ___
| |   / _ \ / _` |/ _ \ | | | | '_ \ / __| |/ _ \/ __|
| |__| (_) | (_| |  __/ | |_| | | | | (__| |  __/\__ \ ?
 \____\___/ \__,_|\___|  \___/|_| |_|\___|_|\___||___/
```

Comments/reflections from Code Uncles?

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __|
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/

  ____                                     _
 / ___|___  _ __ ___  _ __ ___   ___ _ __ | |_ ___
| |   / _ \| '_ ` _ \| '_ ` _ \ / _ \ '_ \| __/ __|
| |__| (_) | | | | | | | | | | |  __/ | | | |_\__ \
 \____\___/|_| |_| |_|_| |_| |_|\___|_| |_|\__|___/

```

---

# Appendix

Tail recursive `sum`:

```scala
import scala.annotation.tailrec

def sum(list: List[Int]): Int = {

  @tailrec
  def sumTailRec(acc: Int, remaining: List[Int]): Int = remaining match {
    case Nil => acc
    case head :: tail => sumTailRec(acc + head, tail)
  }

  sumTailRec(0, list)
}
```
