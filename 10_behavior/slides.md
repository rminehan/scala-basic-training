---
author: Rohan
date: 2021-06-10
title: Behavior
---

```
 ____       _                 _            
| __ )  ___| |__   __ ___   _(_) ___  _ __ 
|  _ \ / _ \ '_ \ / _` \ \ / / |/ _ \| '__|
| |_) |  __/ | | | (_| |\ V /| | (_) | |   
|____/ \___|_| |_|\__,_| \_/ |_|\___/|_|   
                                           
```

---

# Concepts we're going to look at

- classes/interfaces


- inheritance


- type classes

---

# Behavior?

The unifying aspect of all these things is it's about expressing behavior.

---

# Take traits for example

```scala
// Defines the behavior/adjective of knowing how to eat cake.
// In this case using a trait designed to be implemented with `extends`
trait Eater {
  def eat(cake: Cake): Unit
}

// Expresses that BabyJon knows how to eat cake
// The mechanism is to `extend` the trait
object BabyJon extends Eater {
  def eat(cake: Cake): Unit = {
    println("Yummy cake!")
  }
}
```

---

# Other ways

We'll there are other ways to do this

---

# Today's learning objectives

- understand the mechanical aspects of syntax


- examine issues with the method we just showed


- look a little at type classes

---

```
 ____              _             
/ ___| _   _ _ __ | |_ __ ___  __
\___ \| | | | '_ \| __/ _` \ \/ /
 ___) | |_| | | | | || (_| |>  < 
|____/ \__, |_| |_|\__\__,_/_/\_\
       |___/                     
```

Before going on our Philosophical journey,

let's clarify what tools the language has for expressing concepts

---

# In the java realm

Things are pretty simple:

## Things

- class (abstract and concrete)


- interface

## What they do

- classes can implement interfaces


- classes can inherit from other classes


- interfaces can inherit from each other

---

# In the scala realm

More things!

- class (abstract and concrete)


- trait


- object


- case class/object


- package objects

Can be quite daunting when you have many ways to do something.

---

# Quick overview

This is a snapshot of how I typically use the syntax to achieve my goals:

```scala
// ADT - trait + case classes/objects
sealed trait JobStatus
case object Unemployed extends JobStatus
case object Searching extends JobStatus
case class Employed(title: String) extends JobStatus

// Data/Model - case class
case class Person(name: String, age: Int, jobStatus: JobStatus)

// "Static" utils - object (often a companion object)
object Person {
  def buildUnemployedPerson(name: String, age: Int): Person = ...
}

// Abstraction - trait
// This abstraction represents a database of people
trait PersonCollection {
  def findPerson(id: Id): Option[Person]
  def findAll(): List[Person]
}

// Concrete implementation - class/object
class MongoPersonCollection(connectionString: String) extends PersonCollection {
  def findPerson(id: Id): Option[Person] = {
    // implement with mongo database
    ...
  }

  def findAll(): List[Person] = {
    // implement with mongo database
    ...
  }
}

// Extension methods
// Useful for types we don't control
// Use prudently juniors!
implicit class PersonOps(person: Person) {
  def isBoban: Boolean = person.name.toLowerCase == "boban"
}
```

---

# This is just my style

Other developers might do things differently (particularly when things get more complex).

For the analytics code base we roughly follow this style.

---

# Syntactic things not in that list

- class inheritance


- `protected` keyword


- abstract classes


- enums


- package objects

---

```
 ____            _                        
/ ___|  ___ __ _| | __ _       _ __ _   _ 
\___ \ / __/ _` | |/ _` |_____| '__| | | |
 ___) | (_| (_| | | (_| |_____| |  | |_| |
|____/ \___\__,_|_|\__,_|     |_|   \__, |
                                    |___/ 
 _____ _     _                 
|_   _| |__ (_)_ __   __ _ ___ 
  | | | '_ \| | '_ \ / _` / __|
  | | | | | | | | | | (_| \__ \
  |_| |_| |_|_|_| |_|\__, |___/
                     |___/     
```

A quick look at some of the scala-ry syntactic tools we use

that are different to java

- trait


- case class


- ADT's

---

```
 _             _ _   
| |_ _ __ __ _(_) |_ 
| __| '__/ _` | | __|
| |_| | | (_| | | |_ 
 \__|_|  \__,_|_|\__|
                     
```

Q: What is a trait?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

Hmmm...

---

# What is a trait?

Your intuition is that it's like a java interface.

That's pretty much right.

---

# Clarification

This sentence is a bit muddled:

> A scala trait is the same as a java interface

Remember that java and scala are both targeting the JVM.

We're not converting scala code to java code.

---

# Clarification

A better sentence would be:

> A scala trait and java interface both compile to the same JVM thing

(In this case the "thing" is a JVM interface)

```
   SCALA           JAVA
   trait         interface
      \             /
scalac \           / javac
        \         /
         interface
```

---

# In the olden days (before java 8)...

JVM interface was completely abstract.

But scala traits could do this:

```scala
case class Cake(flavor: String, kilos: Int)

trait Eater {
  def eat(cake: Cake): Unit  // abstract

  def eatChocolateCake(kilos: Int): Unit = { // concrete - derived from eat
    eat(Cake("chocolate", kilos))
  }
}
```

---

# In the olden days

The JVM interface wasn't man enough to represent a scala trait.

The scala compiler would have had to work around that in some way.

```
SCALA                     JAVA
trait                     interface
     \                   /
      \ scalac          / javac
       \               /
        ???      interface
```

In java 8 though, it had concrete methods added to it (static ones too!) 

---

# Back to our question

> What is a trait?

## Java 8 onwards

A trait compiles to a JVM interface.

It's scala's equivalent to java's interface.

## Before Java 8

A trait probably compiled to a JVM interface and some extra hidden stuff.

---

# "Inheritance"

Term gets used loosely.

Technically interfaces are _implemented_ and classes are _inherited_.

```scala
trait Trait {
  def shout: String
}

class Base extends Trait { // Implements Trait
  def shout: String = "Base!"
}

class Child extends Base // Inherits Base
```

---

# Multiple inheritance

The JVM doesn't allow multiple inheritance of classes.

It does allow multiple interfaces to be implemented though.

---

# Scala syntax

Use `extends` for the first one, then `with` for subsequent ones:

```scala
class MyClass extends Thing1 with Thing2 with Thing3 ...
```

There can be at most 1 class in that list of things.

If there is one, put it first.

```scala
class MyClass extends Thing1 with Thing2 with Thing3 ...
                   // class       traits ------>
                   // or
                   // trait
```

---

# Abstract classes?

> Traits can have abstract and concrete methods

Isn't that just the same as an abstract class?

```scala
trait BiscuitEater {
  def eat(biscuit: Biscuit): Unit  // <------ abstract

  def eatTimTam(): Unit = {        // <------ concrete
    eat(new TimTam) 
  }
}
```

Couldn't we do:

```scala
abstract class BiscuitEater {
  def eat(biscuit: Biscuit): Unit  // <------ abstract

  def eatTimTam(): Unit = {        // <------ concrete
    eat(new TimTam) 
  }
}
```

Natural question:

Q: When would you use one over the other?

---

# Practically

> Q: When would you use one over the other?

Remember there's no multiple inheritance.

If you used abstract classes everywhere,

that will use up the "inheritance quota" people have.

```scala
abstract class BiscuitEater {
  def eat(biscuit: Biscuit): Unit

  ... // concrete stuff
}

class Albanian
class Boban extends Albanian with BiscuitEater { // Won't compile :(
  def eat(biscuit: Biscuit): Unit = {
    println("My biscuit!")
  }
}
```

---

# Philosophically

It's the subtle difference between:

> Child "is a" biscuit eater (class inheritance)

vs

> Child "knows how to" eat biscuits (interface implementation)

```scala
trait BiscuitEater {
  def eat(biscuit: Biscuit): Unit

  ... // concrete stuff
}

class Albanian
class Boban extends Albanian with BiscuitEater {
  def eat(biscuit: Biscuit): Unit = {
    println("My biscuit!")
  }
}
```

---

# Standard library

You will find abstract classes in the scala standard library though.

For example `List`!

```scala
sealed abstract class List[+A] ...
```

:scream-cat:

They probably have their reasons though.

Maybe they're trying to represent that `List` is a thing, not a behavior.

---

# Terminology: "mixin"

You'll sometimes hear scala developers talk about traits as "mixin"s.

As if you're cooking a stew and mixing in lots of ingredients.

```scala
class MyClass extends Trait1 with Trait2 with Trait3 ...
//  mix them all into the pot
```

Mindset: `MyClass` gets all the powers of the individual traits.

---

# Mixins

I don't like this style much.

It creates complex confusing hierarchies.

The standard library is full of this as are popular scala libraries.

They probably have their reasons though.

---

```
    _    ____ _____ 
   / \  |  _ \_   _|
  / _ \ | | | || |  
 / ___ \| |_| || |  
/_/   \_\____/ |_|  
                    
```

Algebraic data types.

ie. fancy enums

---

# In simple terms

An ADT is used when you have an abstraction with a finite number of implementations.

---

# Example

e.g. an email status one of:

- `Verified`


- `VerifiedLikely`


- `Invalid`


- `Unverified`

---

# The annoying "default" case

Suppose we implemented it like this:

```scala
trait EmailStatus

case object Verified extends EmailStatus
case object VerifiedLikely extends EmailStatus
case object Invalid extends EmailStatus
case object Unverified extends EmailStatus
```

then we wanted to convert each to a certainty `Int`:

```scala
// Made up example
def emailStatus2Certainty(emailStatus: EmailStatus): Int = emailStatus match {
  case Verified | Invalid => 5
  case VerifiedLikely => 4
}
```

---

# Bug

```scala
def emailStatus2Certainty(emailStatus: EmailStatus): Int = emailStatus match {
  case Verified | Invalid => 5
  case VerifiedLikely => 4
  // Unverified???
}
```

Forgot to handle this case and sadly the compiler doesn't pick this up (a bit annoying).

(See [related SO answer](https://stackoverflow.com/a/44579538/15607965))

---

# Other cases

Boban might do this:

```scala
import code.demo.EmailStatus

case object SneakyBoban extends EmailStatus
```

---

# Exhaustive logic

You can't write exhaustive logic if you don't know all the possible instances.

```scala
// Made up example
def emailStatus2Certainty(emailStatus: EmailStatus): Int = emailStatus match {
  case Verified | Invalid => 5
  case VerifiedLikely => 4
  case Unverified => 0
  // Endless other possible cases
}
```

Solution?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# seal it

:clap: :clap:

(Seal sound effects)

---

# ADT

```scala
sealed trait EmailStatus

case object Verified extends EmailStatus
case object VerifiedLikely extends EmailStatus
case object Invalid extends EmailStatus
case object Unverified extends EmailStatus

def emailStatus2Certainty(emailStatus: EmailStatus): Int = emailStatus match {
  case Verified | Invalid => 5
  case VerifiedLikely => 4
}
```

Now we get a compiler warning:

```
demo.scala:9: warning: match may not be exhaustive.
It would fail on the following input: Unverified
  def emailStatus2Certainty(emailStatus: EmailStatus): Int = emailStatus match {
                                                             ^
```

Hoorah!

---

# Objection!

> But Rohan,

you say

> in my OO class at school we learnt to use polymorphism for this:

```scala
sealed trait EmailStatus {
  def certainty: Int
}

case object Verified extends EmailStatus {
  def certainty: Int = 5
}

case object VerifiedLikely extends EmailStatus {
  def certainty: Int = 4
}

case object Invalid extends EmailStatus {
  def certainty: Int = 5
}

case object Unverified extends EmailStatus {
  def certainty: Int = 0
}
```

---

# Response

> in my OO class at school we learnt to use polymorphism for this:

Just because you caught a disease,

doesn't mean you have to spread it to everyone else

---

# The OO mindset

Shove all the behavior into your data.

Generally I don't like this approach.

It doesn't fit with the idea of separating:

- data


- functions that transform data

---

# Mess

It doesn't "scale" well.

A concept like `EmailStatus` would be used in many situations.

For example we need a stringy representation for reports, and parity.

Are you going to show more and more properties into it?

```scala
sealed trait EmailStatus {
  def certainty: Int
  def show: String
  def parity: Parity
  ...
}
```

All these unrelated concepts from unrelated scripts are piling up in our little data class.

---

# Responsibility

The responsibility of the ADT is to represent the allowed cases for an abstraction.

It's a very data-ry concept.

Logic/interpretations specific to different scripts should live in those scripts.

---

# "Baking"

We'll see that baking behavior into something like this is a weak pattern.

Coming up.

---

# Another Objection

> But Rohan

you say

> why not just use an enum? Scala and Java both have these.

---

# Another Objection

> why not just use an enum? Scala and Java both have these.

Our example was simple like that.

Usually ADT's aren't though and you want to use case classes.

```scala
sealed trait EmailStatus

case class Verified(confidence: Int, oracleOpt: Option[Source]) extends EmailStatus
case object VerifiedLikely extends EmailStatus
case class Invalid(impact: Int) extends EmailStatus
case object Unverified extends EmailStatus
```

Each instance can be quite customized this way.

---

# Scala 3

Will unify enums and ADT's a bit.

---

# Summing up ADT's

- use them to model data concepts where there's a finite number of possibilities


- use the `sealed` keyword to make it truly sealed and enhance the compiler


- use `case object` for simple "enum-like" instances


- use `case class` when you need to push more data in


- avoid shoving lots of behavior into them (unless it's really inherent to it)

---

```
  ____               
 / ___|__ _ ___  ___ 
| |   / _` / __|/ _ \
| |__| (_| \__ \  __/
 \____\__,_|___/\___|
                     
  ____ _                         
 / ___| | __ _ ___ ___  ___  ___ 
| |   | |/ _` / __/ __|/ _ \/ __|
| |___| | (_| \__ \__ \  __/\__ \
 \____|_|\__,_|___/___/\___||___/
                                 
```

---

# Most common question?

> What's the difference between a case class and a regular class?
>
> When to use one over the other?
>
> If case classes do everything regular ones do, wouldn't I just use them everywhere?

---

# Quick answer

> What's the difference between a case class and a regular class?
>
> When to use one over the other?

## Case class

A case class is for modelling data, e.g. a row in a database

I say "data" to capture the idea that it doesn't _do_ anything by itself.

It exists for some higher thing to process it.

## Class

Used in the typical OO sense.

Something that represents a behavior.

_Not_ to be used for data generally.

Will see more on this later.

---

# Case classes under the hood

It's a regular class that has been pimped up with extra functionality.

```scala
case class Person(name: String, age: Int)

// as if you'd written
class Person(val name: String, val age: Int)

object Person {
  def apply(name: String, age: Int): Person = new Person(name, age)
  def unapply(person: Person): Option[(String, Int)] = Some((person.name, person.age))
  // and others like copy, tupled, curried
  ...
}
```

Earlier lessons from the JVM series cover this more deeply

---

# Summing up case classes

Used to represent "dumb" data.

---

```
 ___                         
|_ _|___ ___ _   _  ___  ___ 
 | |/ __/ __| | | |/ _ \/ __|
 | |\__ \__ \ |_| |  __/\__ \
|___|___/___/\__,_|\___||___/
                             
          _ _   _     
__      _(_) |_| |__  
\ \ /\ / / | __| '_ \ 
 \ V  V /| | |_| | | |
  \_/\_/ |_|\__|_| |_|
                      
           _                 _     
  _____  _| |_ ___ _ __   __| |___ 
 / _ \ \/ / __/ _ \ '_ \ / _` / __|
|  __/>  <| ||  __/ | | | (_| \__ \
 \___/_/\_\\__\___|_| |_|\__,_|___/
                                   
```

ie. "Baking"

---

# Back to our opening example

```scala
trait Eater {
  def eat(cake: Cake): Unit
}

object BabyJon extends Eater {
  def eat(cake: Cake): Unit = {
    println("Yummy cake!")
  }
}
```

This is a syntactic way of expressing:

- the abstract concept of being able to eat cake (`trait`)


- BabyJon having a specific way to eat cake (`extends`)


---

# Clarifying terminology

I'll call this pattern "behavior baking" (just a term I made up).

```scala
trait PieEater {
  def eat(pie: Pie): Unit
}

trait VegemiteEater {
  def eat(vegemite: Vegemite): Unit
}

object BabyJon extends PieEater with VegemiteEater {

  def eat(cake: Pie): Unit = {
    println("Pie Pie Pie!")
  }

  def eat(vegemite: Vegemite): Unit = {
    println("Why did no one tell me about this!")
  }

}
```

The ability to eat pies and vegemite has been baked into `BabyJon`.

---

# Issues with behavior baking?

It's done at the source code level which creates some issues.

Any ideas team?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Scenario

Vegemite pies are invented (replace the meat with vegemite).

This trait is for those brave enough to eat them:

```scala
trait VegemitePieEater {
  def eat(vegemitePie: VegemitePie): Unit
}
```

---

# BabyJon can do it

The creator of this trait can conceptually see how to make BabyJon implement it

using BabyJon's existing vegemite and pie eating implementations:

```scala
object BabyJon extends PieEater with VegemiteEater with VegemitePieEater {
  ...

  def eat(vegemitePie: VegemitePie): Unit = {
    // Implement it using eat(Pie) and eat(Vegemite) somehow
  }
}
```

---

# Conceptually

A new kind of behavior has been defined (vegemite pie eating)

and conceptually we can see how to implement it on an existing thing (`BabyJon`).

But it was defined _after_ `BabyJon` was baked.

---

# Food analogy

Once you pull something out of oven, it's too late to change the fundamental ingredients.

e.g. Once you pull a cake out of the oven, it's too late to add the eggs and baking soda.

---

# Back to BabyJon

Need to rebake him:

```scala
object BabyJon extends PieEater with VegemiteEater with VegemitePieEater
//                                              rebake  ^^^^^^^^^^^^^^^^
```

---

# Rebake

_If_ you control the source code for `BabyJon`, you can rebake him.

Often we don't though.

And even if you could rebake it, that might cause issues

(e.g. binary compatibility, compiler issues)

---

# Fundamental issue

It is often the case that we come up with a new abstraction,

that we want to apply to an already baked type.

Examples we've already seen:

- `Array[A]` is a `Seq[A]`


- `String` is a `Seq[Char]`

---

# Another issue

What if our thing implements behavior in multiple ways?

---

# For example

BabyJon can eat pie with his hands,

or with cutlery in the company of a lady.

How do you represent that with our baking strategy?

---

# Double extends?

Extend it twice?

```scala
object BabyJon extends PieEater extends PieEater ... {

  def eat(pie: Pie): Unit = {
    println("No ladies around, using my hands, feeling good")
  }

  def eat(pie: Pie): Unit = {
    println("Lady sighted, using cutlery, feeling proper")
  }

}
```

Obviously this doesn't work - double definition.

Even if you somehow could, how would people know which one to use?

---

# Don't forget primitives

Baking with `extends` is a concept just for classes.

We want a way to associate behaviors to primitive types too!

---

# Static-ness

Some behaviors are "static" or "class level" and don't fit with `extends`.

Easiest to show with an example.

---

# Foldable

Recall our slides where we discovered the fold abstraction:

## Sum

```scala
var acc = 0

for (i <- 1 to 4) {
  acc = acc + i
}

acc
```

## Product

```scala
var acc = 1

for (i <- 1 to 4) {
  acc = acc * i
}

acc
```

## Differences?

- different seed


- different "combine" logic

Scaffolding is the same though.

Abstract it out into a higher order function!

---

# So we made fold 

```scala
def fold[A](seq: Seq[A], seed: A)(combine: (A, A) => A): A = {
  var acc = seed
  
  for (next <- seq) {
    acc = combine(acc, next)
  }
  
  acc
}
```

---

# Conceptually

Thinking more conceptually, what do you we need to provide to fold down a sequence of `A`'s?

- a seed `A` to get it started


- combine logic


- some nice properties also help
    - commutativity: a + b = b + a
    - associativity: a + (b + c) = (a + b) + c


- the seed is neutral or an identity element
    - seed + a = a + seed = a

---

# Foldable-ness

Let's say that a type is "foldable" if it has some concrete way to define these concepts

(Seed and combine logic)

Note how "foldable" is like an -able adjective. Like print-able, map-able etc...

---

# Is `Int` foldable?

Yes we've seen in two ways - addition and multiplication.

---

# Solving this with baking

Let's try to make an abstraction for this that we'd bake into our types:

```scala
trait Foldable {
  def seed: ???

  // Combine myself with something else
  def combine(other: ???): ???
}
```

Looks like we need a type parameter.

---

# With a type parameter

```scala
trait Foldable[T] {
  def seed: T

  def combine(other: T): T
}
```

---

# Now we need something to bake it into

Since learning ZIO,

Zij has been inspired to reinvent something and give it his own "Zij" branding.

---

# ZList

So Zij built a `ZList` (Z = Zij, not ZIO).

(Basically the same as regular `List`)

It's new code we control, so we can bake our trait in.

---

# How would you fold lists together?

ie. what seed and combine logic?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# ZList

```scala
sealed trait ZList[A] extends Foldable[ZList[A]] {

  def seed: ZList[A] = ZList.empty[A]

  def combine(other: ZList[A]): ZList[A] = this +z+ other

}

// Define `Z::` and `ZNil`
```

---

# Now we need something that uses this behavior

Redefine our fold method!

```scala
def fold[A <: Foldable[A]](seq: Seq[A]): A = ???
```

We don't need to pass the seed and combiner,

they're baked into `A`.

---

# Problem

```scala
def fold[A <: Foldable[A]](seq: Seq[A]): A = {
  var acc = // hmmm...

  for (next <- seq)
    acc = acc.combine(next)

  acc
}
```

How do you get the seed? You need an `A`:

```scala
trait Foldable[T] {
  def seed: T

  def combine(other: T): T
}
```

---

# Fundamental issue

Not asking:

> How do I get a seed for a value of A?

But:

> How do I get a seed for the type A?

Seed is a static concept (combine is really too).

You can't bake in static concepts.

---

# Summing up the issues with baking behavior in

- can't mixin new concepts to something that's already baked


- can only bake in one implementation


- doesn't fit well with static/class level logic

---

```
 _____                 
|_   _|   _ _ __   ___ 
  | || | | | '_ \ / _ \
  | || |_| | |_) |  __/
  |_| \__, | .__/ \___|
      |___/|_|         
  ____ _                         
 / ___| | __ _ ___ ___  ___  ___ 
| |   | |/ _` / __/ __|/ _ \/ __|
| |___| | (_| \__ \__ \  __/\__ \
 \____|_|\__,_|___/___/\___||___/
                                 
```

An alternative approach.

---

# Analytics code base

We don't use type classes much there.

This is optional extra material.

---

# Recall our fundamental problem

We want a mechanism to define an abstract "-able" behavior/adjective,

then be able to show how different types can implement it

---

# Foldable

Let's go back to what our first fold looked like:

```scala
def fold[A](seq: Seq[A], seed: A)(combine: (A, A) => A): A = ...
```

It needs a seed and a combine.

Let's capture those bits of information in a trait as they are here.

---

# Foldable with type classes

Define the behavior (called a "type class"):

```scala
// Represents what we need to know to fold some A
trait Foldable[A] {
  def seed: A
  
  def combine(x: A, y: A): A
}
```

This is not something we try and mix into `A`.

Our implementation will not live inside `A`,

but a separate thing called a "type class instance".

---

# Type class instances

Let's prove that `Int` and `String` are foldable.

Note these are defined totally separately to the classes themselves.

```scala
// Int's are foldable under addition
object IntAddition extends Foldable[Int] {
  def seed: Int = 0
  def combine(x: Int, y: Int): Int = x + y
}

// Int's are foldable under multiplication
object IntMultiplication extends Foldable[Int] {
  def seed: Int = 1
  def combine(x: Int, y: Int): Int = x * y
}

// String's are foldable under concatenation
object StringConcatenation extends Foldable[String] {
  def seed: String = ""
  def combine(x: String, y: String): String = x + y
}
```

Each of these really just captures the information we were passing to `fold`.

---

# Write something to use it

```scala
// Pass the method a sequence of A's and the logic for how to fold them
def fold[A](seq: Seq[A], foldable: Foldable[A]): A = {

  var acc = foldable.seed // yey it works!

  for (a <- seq)
    acc = foldable.combine(acc, a)

  acc
}
```

---

# Use it

```scala
// Pass the method a sequence of A's and the logic for how to fold them
def fold[A](seq: Seq[A], foldable: Foldable[A]): A = {

  var acc = foldable.seed // yey it works!

  for (a <- seq)
    acc = foldable.combine(acc, a)

  acc
}

// Add some numbers
fold(1 to 10, IntAddition)
// Multiply some numbers
fold(1 to 10, IntMultiplication)
// Concatenate some strings
fold(List("abc", "def", "ghi"), StringConcatenation)
```

---

# Separation

Note how we didn't try and shove everything into the type we were folding.

The information lives elsewhere in a type class instance.

(We need to undo years of conditioning related to OO programming)

---

# The beauty of type classes

The `IntAddition` and `IntMultiplication` shows we were able to:

- apply behavior to a primitive


- define two implementations simultaneously


- retro-actively apply a new abstraction to an old type we don't control


- represent "static" logic

---

# Recapping

A "type class" is a way to represent an abstract behavior (often an -able adjective).

```scala
// Represents what we need to know to fold some A
trait Foldable[A] {
  def seed: A
  
  def combine(x: A, y: A): A
}
```

A "type class instance" is an implementation of the type class for a particular type.

```scala
// Int's are foldable under addition
object IntAddition extends Foldable[Int] {
  def seed: Int = 0
  def combine(x: Int, y: Int): Int = x + y
}
```

By staying away from "baking", we avoid many pitfalls.

---

# "Proof" lingo

People often talk about type class instances as "proofs" or "evidence".

```scala
// "Proof that Int is Foldable under addition"
object IntAddition extends Foldable[Int] {
  def seed: Int = 0
  def combine(x: Int, y: Int): Int = x + y
}
```

---

# Back to Zij - a long day

Zij just finished a long day coding his new `ZList` (Z for Zij, not ZIO).

He clicks "publish" and v1 goes out to the internet.

Zij relaxes by re-reading his copy of "The Elements of Style" by Strunk and White.

---

# Scenario

His subconscious mind has a sudden jolt of realization:

> ZList is foldable! I can concatenate them! Oh I forgot about this!

```scala
// Folding this:
List(
  ZList(1, 2, 3),
  ZList(4, 5, 6, 7),
  ZList(8, 9, 10
)

// Yields:
ZList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
```

---

# Which universe

## Baking universe

If Zij lives in the universe where we have to bake things in,

then he'll have to update and recompile his library.

Potentially causing compatibility issues and other icky things.

## Type class universe

Tomorrow Zij can define this separately:

```scala
class ZListConcatenation[T] extends Foldable[ZList[T]] {
  def seed: ZList[T] = ZList.empty[T]
  def combine(a: ZList[T], b: ZList[T]): ZList[T] = a +z+ b
}
```

Relieved, Zij returns to his Strunk and White,

sipping coffee from a plastic bag through a plastic straw.

---

# Boilerplate

Type classes are often not "first class" citizens.

Implementing them requires a little more boilerplate.

Whereas inheritance just uses `extends`.

```scala
class MyFoldableClass extends Foldable {
  ...
}
```

It's easier syntactically, but weaker architecturally.

Unfortunately syntactic ease often influences lazy developers.

---

# Back to ADT's

Hopefully you can see why I don't like:

```scala
sealed trait EmailStatus {
  def certainty: Int
}
```

---

# So should we always use type classes?

---

# So should we always use type classes?

Nah

And we don't in the analytics code base.

Where do they make sense?

---

# Universal abstractions

Some abstractions are so universal that they pop up everywhere.

We meet a lot of these in FP:

- foldable


- functor (map-able)


- monad (flatMap-able)

etc...

---

# Universal abstractions

We often realize that new abstractions can be applied to types from libraries we don't control.

For example you might be using a matrix library and you need to double all the numbers in your matrix:

```
1  2   -->  2  4
4  3        8  6
```

---

# Click!

```
1  2   -->  2  4
4  3        8  6
```

It clicks:

> That's a structure preserving transformation.
>
> Oh matrix is a functor!
>
> Would be nice if I could do this:
>
```scala
matrix.map(_ * 2)
```

With type classes and a bit of implicit scala trickery we can achieve this.

No modifications to the matrix library required.

---

# So

Type classes make sense for these very universal abstractions.

---

# When they don't make sense

A very specific leadiq abstraction:

```scala
trait PersonCollection {
  def findPerson(id: Id): Option[Person]
  def findAll(): List[Person]
}
```

Will only have one implementation which we're writing.

You're not going to be looking through some open source library and realize:

> Hey that's a leadiq person collection!

---

# The cons of baking in traits

- can only do this with types we control


- can only implement the trait in one way


- can't apply to primitives


- can't represent static logic

```scala
trait PersonCollection {
  def findPerson(id: Id): Option[Person]
  def findAll(): List[Person]
}
```

Usually those criticisms don't make any sense here.

Baking the trait in is syntactically simpler with no real cons.

---

# Summing up today

---

# Syntax

Scala has many class/interface syntactic tools,

but I tend to just use it like this:

```scala
// ADT - trait + case classes/objects
sealed trait JobStatus
case object Unemployed extends JobStatus
case object Searching extends JobStatus
case class Employed(title: String) extends JobStatus

// Data/Model - case class
case class Person(name: String, age: Int, jobStatus: JobStatus)

// "Static" utils - object (often a companion object)
object Person {
  def buildUnemployedPerson(name: String, age: Int): Person = ...
}

// Abstraction - trait
// This abstraction represents a database of people
trait PersonCollection {
  def findPerson(id: Id): Option[Person]
  def findAll(): List[Person]
}

// Concrete implementation - class/object
class MongoPersonCollection(connectionString: String) extends PersonCollection {
  def findPerson(id: Id): Option[Person] = {
    // implement with mongo database
    ...
  }

  def findAll(): List[Person] = {
    // implement with mongo database
    ...
  }
}
```

---

# Baking

The OO way we've been conditioned to represent abstract behaviors and implementations:

```scala
trait Behavior

class Thingy extends Behavior
```

---

# Type classes

Define a "type class" (usually as a trait),

and then "type class instances" which prove that your type satisfies the type class.

---

# Comparison

## Baking

Syntactically slick (less boilerplate).

Architecturally quite limiting.

Makes sense for very specific abstractions we're controlling (like a database abstraction).

## Type classes

More boilerplate.

Architecturally much more flexible.

Makes sense for universal "-able" abstractions

(good fit for FP as it has a lot of those)

---

# What we didn't talk about

Using implicits to make type classes feel more slick.

Without that, it makes type classes just seem like:

> Oh you're just collecting all the parameters together and giving it a name?

---

# QnA?
