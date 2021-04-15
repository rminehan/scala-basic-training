---
author: Rohan
title: Session 5 - Higher Order Functions
date: 2021-04-29
---

```
 _   _ _       _
| | | (_) __ _| |__   ___ _ __
| |_| | |/ _` | '_ \ / _ \ '__|
|  _  | | (_| | | | |  __/ |
|_| |_|_|\__, |_| |_|\___|_|
         |___/
  ___          _
 / _ \ _ __ __| | ___ _ __
| | | | '__/ _` |/ _ \ '__|
| |_| | | | (_| |  __/ |
 \___/|_|  \__,_|\___|_|

 _____                 _   _
|  ___|   _ _ __   ___| |_(_) ___  _ __  ___
| |_ | | | | '_ \ / __| __| |/ _ \| '_ \/ __|
|  _|| |_| | | | | (__| |_| | (_) | | | \__ \
|_|   \__,_|_| |_|\___|\__|_|\___/|_| |_|___/

```

---

# Context

Functions are "first class" in scala.

Makes FP easier.

---

# First class?

You can do the same things with functions that you can with regular data.

## Regular Data

```scala
// Create a value
val i = 1

// Define a function that takes it as input
def takeInt(int: Int): Unit = {
  ...
}

// Pass it in
takeInt(i)
```

## Functions

```scala
// Create a value
val f: String => Int = _.length

// Define a function that takes it as input
def takeFunc(func: String => Int): Unit = {
  ...
}

// Pass it in
takeFunc(f)
```

Don't worry about the `_.toLength` syntax (will explain syntax later).

Just notice the parallels.

---

# Terminology

What is a higher order function?

Simple definition: A function that that takes another function as one of its inputs.

```scala
def takeFunc(func: String => Int): Unit = {
  ...
}
```

---

# Agenda

- motivate why higher order functions are useful


- look at scala's syntax in detail

---

# Motivation

Why are they useful?

We'll see they allow for very powerful code reuse.

The DRY principle - don't repeat yourself.

---

# Example 1

Write a function that converts a list of Strings to the equivalent list of lengths.

```
List(                       List(
  "Boban",                    5,
  "Enxhell",      ---->       7,
  "Rohan",                    5,
  "Zij"                       3
)                           )
```

(To the repl!)

---

# Example solution

Something like this:

```scala
def lengths(names: List[String]): List[Int] = names match {
  case Nil => Nil
  case head :: tail => head.length :: lengths(tail)
}
```

(Not tail recursive but that's a separate issue - we're keeping it simple)

---

# Example 2

Write a function that parses strings into integers:

```
List(                       List(
  "19238",                    19238,
  "1193843",      ---->       1193843,
  "23429",                    23429,
  "1139"                      1139
)                           )
```

(To the repl!)

---

# Example solution

```scala
def parse(digitStrings: List[String]): List[Int] = digitStrings match {
  case Nil => Nil
  case head :: tail => head.toInt :: parse(tail)
}
```

---

# The solutions feel the same

```scala
def lengths(names: List[String]): List[Int] = names match {
  case Nil => Nil
  case head :: tail => head.length :: lengths(tail)
}

def parse(digitStrings: List[String]): List[Int] = digitStrings match {
  case Nil => Nil
  case head :: tail => head.toInt :: parse(tail)
}
```

Both have this structure:

```scala
def myFunc(input: List[String]): List[Int] = input match {
  case Nil => Nil
  case head :: tail => process(head) :: myFunc(tail)
}

def process(s: String): Int = ...
```

The outer scaffolding is always the same.

Just the `process` logic is different.

---

# How to make this DRY?

Pass the processing logic in as a function.

To the repl!

---

# Example solution

```scala
def mapStringsToInts(inputs: List[String], process: String => Int): List[Int] = inputs match {
  case Nil => Nil
  case head :: tail => process(head) :: mapStringsToInts(tail, process)
}

mapStringsToInts(List("Boban", "Zij"), _.length)
// List(5, 3)

mapStringsToInts(List("123", "-123"), _.toInt)
// List(123, -123)
```

---

# What we did?

Abstracted/generalized our solutions into a more general one.

The mechanism for that is a higher order function.

```scala
def mapStringsToInts(inputs: List[String], process: String => Int): List[Int] = inputs match {
  case Nil => Nil
  case head :: tail => process(head) :: mapStringsToInts(tail, process)
}
```

We can abstract this even more.

---

# Example 3

Produce a list of Booleans for whether a name starts with 'E'.

```
List(                       List(
  "Boban",                    false,
  "Enxhell",      ---->       true,
  "Rohan",                    false,
  "Elaine"                    true
)                           )
```

And produce a list of Booleans for whether a name has length greater than 5.

```
List(                       List(
  "Boban",                    false,
  "Enxhell",      ---->       true,
  "Rohan",                    false,
  "Elaine"                    true
)                           )
```

We'll make a `mapStringsToBooleans` so that we can do:

```scala
mapStringsToBooleans(names, _.startsWith("E"))

mapStringsToBooleans(names, _.length > 5)
```

To the repl!

---

# Comparing them


```scala
def mapStringsToInts(inputs: List[String], process: String => Int): List[Int] = inputs match {
  case Nil => Nil
  case head :: tail => process(head) :: mapStringsToInts(tail, process)
}

def mapStringsToBooleans(inputs: List[String], process: String => Boolean): List[Boolean] = inputs match {
  case Nil => Nil
  case head :: tail => process(head) :: mapStringsToBooleans(tail, process)
})
```

Just did find-replace: `Int` -> `Boolean`

---

# Abstract with a type parameter

```scala
def mapStringsToInts(inputs: List[String], process: String => Int): List[Int] = inputs match {
  case Nil => Nil
  case head :: tail => process(head) :: mapStringsToInts(tail, process)
}

def mapStringsToBooleans(inputs: List[String], process: String => Boolean): List[Boolean] = inputs match {
  case Nil => Nil
  case head :: tail => process(head) :: mapStringsToBooleans(tail, process)
}

// More abstract
def mapStrings[T](inputs: List[String], process: String => T): List[T] = inputs match {
  case Nil => Nil
  case head :: tail => process(head) :: mapStrings(tail, process)
}
```

Example usages:

```scala
// T=Int
mapStrings(names, _.toInt)

// T=Boolean
mapStrings(names, _.length > 5)
```

---

# Type inference

We didn't tell the compiler what `T` was for each example:

```scala
// T=Int
mapStrings(names, _.toInt)

// T=Boolean
mapStrings(names, _.length > 5)
```

The compiler was able to figure it out by looking at the function you passed.

e.g. `_.toInt`. It knows this is a function from `String => Something`.

Calling `toInt` on a `String` yields an `Int`, so it infers that `T=Int`.

---

# Going further

Why limit ourselves to just operating on lists of strings?

There's nothing particularly stringy about this logic.

For example convert some floats to ints by calling `toInt` on them:

```
List(              List(
  1.4f,     --->     1,
  3.2f,              3,
  -1.3f              -1
)                  )
```

---

# Abstracting this

We'll use `A` for the input type (e.g. String, Float).

And `B` for the output type (e.g. Int, Boolean).

```scala
def map[A, B](inputs: List[A], process: A => B): List[B] = inputs match {
  case Nil => Nil
  case head :: tail => process(head) :: map(tail, process)
}

map[Float, Int](List(1.4f, 3.2f, -1.3f), _.toInt)
// List(1, 3, -1)
```

Note above explicit type parameters (compiler couldn't infer them).

---

# Recap so far

Our journey of abstraction:

```
          lengths                 (Specific List[Int] => List[String] functions)
           parse

            |

        mapStringsToInts          (Abstract out the inner logic)
        mapStringsToBooleans

            |

         mapStrings[T]            (Abstract the ouput type parameter)

            |

         map[A, B]                (Abstract the input parameter)
```

---

# Welcome to FP!

We _could_ abstract even further.

But that will get us off topic.

---

# Aside 1

Aside: This kind of abstraction is a very typical FP mindset.

As you get more abstract, you can solve more problems.

But the tools you work with become increasingly abstract and can be hard to understand.

`map` is a sweet spot: still quite easy to understand but very very applicable

---

# Aside 2

`map` relates to the FP concept of "Functor".

---

# Motivation 2 - filtering

Example: Remove strings from our list that have length less than 6.

```
List(                       List(
  "Boban",
  "Enxhell",      ---->       "Enxhell",
  "Rohan",
  "Zij",
  "Cynthia"                   "Cynthia"
)                           )
```

To the repl!

---

# Example solution

```scala
def longNames(names: List[String]): List[String] = names match {
  case Nil => Nil
  case head :: tail =>
    if (head.length >= 6)
      head :: longNames(tail)
    else
      longNames(tail)
}
```

(Again not tail recursive but that's unrelated to today's lesson)

---

# Abstracting the logic

Allow any condition in the form `String => Boolean`


```scala
// Old
def longNames(names: List[String]): List[String] = names match {
  case Nil => Nil
  case head :: tail =>
    if (head.length >= 6)
      head :: longNames(tail)
    else
      longNames(tail)
}

longNames(names)

// New
def filterStrings(inputs: List[String], predicate: String => Boolean): List[String] = inputs match {
  case Nil => Nil
  case head :: tail =>
    if (predicate(head))
      head :: filterStrings(tail, predicate)
    else
      filterStrings(tail, predicate)
}

filterStrings(names, _.length >= 6)
```

---

# Abstracting the type

Replace `String` with `A`:

```scala
// Old
def filterStrings(inputs: List[String], predicate: String => Boolean): List[String] = inputs match {
  case Nil => Nil
  case head :: tail =>
    if (predicate(head))
      head :: filterStrings(tail, predicate)
    else
      filterStrings(tail, predicate)
}

filterStrings(names, _.length >= 6)

// New
def filter[A](inputs: List[A], predicate: A => Boolean): List[A] = inputs match {
  case Nil => Nil
  case head :: tail =>
    if (predicate(head))
      head :: filter(tail, predicate)
    else
      filter(tail, predicate)
}

filter[String](names, _.length >= 6)
```

Note again the explicit type parameter as the compiler can't infer it.

---

```
 ____              _             
/ ___| _   _ _ __ | |_ __ ___  __
\___ \| | | | '_ \| __/ _` \ \/ /
 ___) | |_| | | | | || (_| |>  < 
|____/ \__, |_| |_|\__\__,_/_/\_\
       |___/                     
```

Hopefully the motivation is clear now.

Let's look at the finer points of syntax.

---

# Many ways to express functions

Typical scala - many ways to do something

We'll use different syntax with our trusty map function from earlier:

```scala
val names = List("Boban", "Rohan", "Zij")

def map[A, B](inputs: List[A], process: A => B): List[B] = inputs match {
  case Nil => Nil
  case head :: tail => process(head) :: map(tail, process)
}
```

---

# Underscore notation

```scala
map[String, Int](names, _.length)
```

Here underscore is the placeholder for the input.

Works well for expressions where you use the input once.

---

# Lambda expression

`_.length` is called a "lambda expression".

It's a little "on the fly" function.

---

# Overuse

Overuse can make code hard to read in nested situations.

```scala
thing.map(_.map(_.filter(_.length > 4)))
```

---

# Named input

More general form of a lambda expression (similar to python).

```scala
map[String, Int](names, str => str.length)
```

`str` is playing the same role as the underscore.

---

# Multiple references to the place holder

Allows multiple references to it:

```scala
map[String, Int](names, str => str.toInt * str.toInt)
```

---

# Readability

A variable name can make your code more readable at the trade off of more characters.

```scala
// Underscore style
result.map(_.map(_.nameOpt.flatMap(validate).filter(_.length > 4)))

// Named input style
result.map(people =>
  people.map(person =>
    person.nameOpt.flatMap(validate).filter(name => name.length > 4)
  )
)
```

Has more characters but is easier to read.

Programming isn't a game of trying to reduce characters just for the sake of it.

Removing noise/boilerplate is good, but not signal.

---

# Passing a method

Useful if:

- your logic is already in a method


- your logic is big and you want to put it in a method

```scala
def calculateAge(person: Person): Int = {
  ...
}

val people: List[Person] = List(...)

map[Person, Int](people, calculateAge)

// Equivalent to
map[Person, Int](people, person => calculateAge(person))
```

---

```
 __  __       _ _   _       _      
|  \/  |_   _| | |_(_)_ __ | | ___ 
| |\/| | | | | | __| | '_ \| |/ _ \
| |  | | |_| | | |_| | |_) | |  __/
|_|  |_|\__,_|_|\__|_| .__/|_|\___|
                     |_|           
 ____                                _            
|  _ \ __ _ _ __ __ _ _ __ ___   ___| |_ ___ _ __ 
| |_) / _` | '__/ _` | '_ ` _ \ / _ \ __/ _ \ '__|
|  __/ (_| | | | (_| | | | | | |  __/ ||  __/ |   
|_|   \__,_|_|  \__,_|_| |_| |_|\___|\__\___|_|   
                                                  
  ____                           
 / ___|_ __ ___  _   _ _ __  ___ 
| |  _| '__/ _ \| | | | '_ \/ __|
| |_| | | | (_) | |_| | |_) \__ \
 \____|_|  \___/ \__,_| .__/|___/
                      |_|        
```

---

# Multiple Parameter Groups

Scala lets you break your parameters up into groups.

```scala
def func(a: Int, b: Int)(c: Int, d: Int, e: Int)(f: Int, g: Int): Unit = ...
//       --------------  ----------------------  --------------
//           group 1              group 2            group 3


// Calling it
func(0, 1)(2, 3, 4)(5, 6)
```

---

# Why do this?

It enables syntactic tricks like "block" syntax (and implicits).

We'll change `map` to leverage block syntax.

---

# Changing map

```scala
// Old
def map[A, B](inputs: List[A], process: A => B): List[B] = ...
//            --------------------------------
//                   just one group

// New
def map[A, B](inputs: List[A])(process: A => B): List[B] = ...
//            ---------------  ---------------
//                group 1           group 2
```

Everything else about the function is the same except the recursive call:

```scala
// Old
map(inputs, process)

// New
map(inputs)(process)
```

---

# Calling it

```scala
def map[A, B](inputs: List[A])(process: A => B): List[B] = inputs match {
  case Nil => Nil
  case head :: tail => process(head) :: map(tail)(process)
}

map(names)(_.length) 
```

(Aside: Notice how we don't need the type parameters anymore!)

---

# Calling it using block syntax

When you have a parameter group with a single function parameter, you can use `{}` instead of `()`:

```scala
val names = List("Boban", "Enxhell", "Rohan", "Zij")

map(names) { name =>
  name.toUpperCase.take(3)
}

// List("BOB", "ENX", "ROH", "ZIJ")
```

---

# Why is block syntax cool?

Allows pattern matching syntax.

```scala
case class Person(name: String, age: Int) 

val people = List(Person("Enxhell", 16), Person("Thilo", 28), Person("Zij", 26)) 

map(people) {
  case Person(name, age) => Person(name.toUpperCase, age + 1)
} 
// List(Person("ENXHELL", 17), Person("THILO", 29), Person("ZIJ", 27))
```

Above we're destructuring the input.

---

# Aside

List already has map and filter built in.

```scala
names.map(_.toUpperCase) 

names.filter(_ == "Enxhell") 
```

We reimplemented a non-tail recursive implementation of `map`.

---

# Multi-parameter inputs

```scala
def demo(combine: (Int, Int) => Int): Unit = {
  println(s"1 combine 2 = ${combine(1, 2)}")
  println(s"2 combine 3 = ${combine(2, 3)}")
  println(s"5 combine 7 = ${combine(5, 7)}")
} 
```

---

# Calling it

```scala
demo((a, b) => a + b) 
//1 combine 2 = 3
//2 combine 3 = 5
//5 combine 7 = 12


demo((a, b) => a * b) 
//1 combine 2 = 2
//2 combine 3 = 6
//5 combine 7 = 35
```

---

# Nifty underscore syntax

Identical to previous slide

```scala
demo(_ + _) 
//1 combine 2 = 3
//2 combine 3 = 5
//5 combine 7 = 12


demo(_ * _) 
//1 combine 2 = 2
//2 combine 3 = 6
//5 combine 7 = 35
```

The first underscore represents the first parameter.

The second underscore represents the second parameter.

---

# And of course block syntax with destructuring

```scala
demo {
  case (a, b) => a * a + b * b
} 
//1 combine 2 = 5
//2 combine 3 = 13
//5 combine 7 = 74
```

---

# Again

Use the underscore syntax prudently.

Can make code hard to read.

Makes sense when the context makes it very clear how to interpret the underscores.

---

```
 ____                                             
/ ___| _   _ _ __ ___  _ __ ___   __ _ _ __ _   _ 
\___ \| | | | '_ ` _ \| '_ ` _ \ / _` | '__| | | |
 ___) | |_| | | | | | | | | | | | (_| | |  | |_| |
|____/ \__,_|_| |_| |_|_| |_| |_|\__,_|_|   \__, |
                                            |___/ 
```

That's it!

---

# Summary

- higher order functions take functions as inputs


- very useful for abstraction and code reuse


- many different ways to pass functions


- multiple parameter groups allow you to leverage "block" syntax


- try to keep your code readable

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
