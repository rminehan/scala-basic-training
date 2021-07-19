---
author: Rohan
date: 2021-07-15
title: Validation Hands On (part 2)
---

```
__     __    _ _     _       _   _             
\ \   / /_ _| (_) __| | __ _| |_(_) ___  _ __  
 \ \ / / _` | | |/ _` |/ _` | __| |/ _ \| '_ \ 
  \ V / (_| | | | (_| | (_| | |_| | (_) | | | |
   \_/ \__,_|_|_|\__,_|\__,_|\__|_|\___/|_| |_|
                                               
 _   _                 _     
| | | | __ _ _ __   __| |___ 
| |_| |/ _` | '_ \ / _` / __|
|  _  | (_| | | | | (_| \__ \
|_| |_|\__,_|_| |_|\__,_|___/
                             
  ___        
 / _ \ _ __  
| | | | '_ \ 
| |_| | | | |
 \___/|_| |_|
             
```

Part 2

---

# Recap

Several demos:

- `Double` - introduced us to `cats.data.Validated`


- `AddEither` - used `Either` in a monadic way


- `AddValidatedList` - modelled errors with a `List` and didn't short circuit


- `AddValidatedNel` - tighted error model to `NonEmptyList/Chain`

---

# Today

Continue on:

- applicative concepts (mapN and parMapN)


- foldable vs reducible


- notes on modelling inputs

---

# Today

Again might not be able to keep under time...

Will split if necessary

:scroll:

---

```
                       _   _ 
 _ __ ___   __ _ _ __ | \ | |
| '_ ` _ \ / _` | '_ \|  \| |
| | | | | | (_| | |_) | |\  |
|_| |_| |_|\__,_| .__/|_| \_|
                |_|          
```

---

# Three inputs

What if our script had to validate three inputs?

We'd get 2^3 = 8 cases to deal with

```
             Arg
      1       2       3
-----------------------------
 0  valid   valid   valid
 1  valid   valid   invalid
 2  valid   invalid valid
 3  valid   invalid invalid
 4  invalid valid   valid
 5  invalid valid   invalid
 6  invalid invalid valid
 7  invalid invalid invalid
```

What if we had 4!? Or 5!?

Becomes unmanageable pretty quickly.

---

# What rescues us?

Code Uncles: What tool comes to the rescue here?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# What rescues us?

> Code Uncles: What tool comes to the rescue here?

mapN

Powered under the hood by applicative

Applicative: monad's plucky little brother/sister

:party-parrot:

---

# Monad vs Applicative

## Monad

Dependent computations

```scala
for {
  boban <- findByName("Boban")
  friend <- boban.friendOpt
  friendOfFriend <- friend.friendOpt
} yield friendOfFriend
```

Can't reorder them

## Applicative

Independent computations

```scala
(parse("1"), parse("2")) match {
  ...
}
```

---

# mapN demo

```scala
import cats.syntax.all._ 
```

Notes:

- use 2.13 to get "partial unification"


- make sure to be using a newish version of cats to be in sync with the docs

To the repl!

---

# Summary

```scala
@ import cats.syntax.all._ 

@ import cats.data.ValidatedNel 

@ type ParseError = String 

@ def parse(digits: String): ValidatedNel[ParseError, Int] = {
    if (digits.forall(_.isDigit)) Valid(digits.toInt)
    else Invalid(NonEmptyList.of(s"Input invalid. '$digits' Non-digits found in input string"))
  } 

@ (parse("123"), parse("345"), parse("678")).mapN {
    (left, middle, right) => left + middle + right
  } 
// Valid(1146)

@ (parse("abc"), parse("345"), parse("def")).mapN {
    (left, middle, right) => left + middle + right
  } 
//   "Input invalid. 'abc' Non-digits found in input string",
//   "Input invalid. 'def' Non-digits found in input string"
```

---

# Changes to `parse`

To use `mapN` smoothly, I've changed `parse` to return a `ValidatedNel`:

```diff
-def parse(digits: String): Validated[ParseError, Int] = {
+def parse(digits: String): ValidatedNel[ParseError, Int] = {
   if (digits.forall(_.isDigit)) Valid(digits.toInt)
-  else Invalid(s"Non-digits found in input '$digits'")
+  else Invalid(NonEmptyList.of(s"Non-digits found in input '$digits'"))
 } 
```

Too hard to explain at this point

Just know that `mapN` doesn't work on simple `Validated`'s

---

# Using that in our script

We'll make a script that adds 3 numbers and our `mapN` trick

To the vim!

`AddMapN.scala`

---

# Applicative

Advanced FP concept, but very useful!

Lets us generalize `map` over many inputs

---

```
 ____            _   _       _ 
|  _ \ __ _ _ __| |_(_) __ _| |
| |_) / _` | '__| __| |/ _` | |
|  __/ (_| | |  | |_| | (_| | |
|_|   \__,_|_|   \__|_|\__,_|_|
                               
 _   _       _  __ _           _   _             
| | | |_ __ (_)/ _(_) ___ __ _| |_(_) ___  _ __  
| | | | '_ \| | |_| |/ __/ _` | __| |/ _ \| '_ \ 
| |_| | | | | |  _| | (_| (_| | |_| | (_) | | | |
 \___/|_| |_|_|_| |_|\___\__,_|\__|_|\___/|_| |_|
                                                 
```

You'll see this pop up here and there prior to scala 2.13

---

# Basic problem

Sometimes the compiler has trouble spotting "type constructors"

Taking a few steps back

---

# Type constructors

You can think of `List` as a type function:

> Give `List` a type `A`, and it produces a type `List[A]`

(called a type constructor)

---

# Conceptually

```scala
def List(type: Type): Type = List[Type]
```

(Not real code!)

---

# More examples!

> What's another example of a type constructor

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# More examples!

> What's another example of a type constructor

- `Option`


- `Vector`


- `Array`


- ...

Anything with a single generic parameter

---

# Clarifying terminology

`List` is a type constructor (bare)

`List[A]` is a type

---

# Holy List

`List` has a "type hole" in it which lets us put in a type

```
    List[_]
         ^
  put a type in here
```

The hole is what makes it feel like a function

---

# More complex example

```scala
Map[String, _]

// Like a function:
def Map[String, _](type: Type): Type = Map[String, Type]
```

Give it a type `A`, and it splits out `Map[String, A]`

The `_` is a hole that can be filled with a type

---

# Scala syntax

We can represent that we want a type constructor:

```scala
trait Functor[F[_]] {
//            ^^^^
  def map[A, B](fa: F[A])(f: A => B): F[B]
//                    ^                 ^   <--- doesn't make sense
//                                               without a type hole
}
```

> You can only pass `F` if it has a hole in it

---

# Demo

```scala
@ trait Functor[F[_]] {
    def map[A, B](fa: F[A])(f: A => B): F[B]
  } 

@ new Functor[Int] {
    def map[A, B](fa: Int[A])(f: A => B): Int[B] = ???
  } 
// Fail
// Int takes no type parameters, expected: 1
// val res1 = new Functor[Int] {
//                        ^

@ new Functor[Option] {
    def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa match {
      case Some(a) => Some(f(a))
      case None => None
    }
  } 
// Compiles - `Option` has a type hole
```

---

# Complex example

How to use it with this concept?

```scala
Map[String, _]

@ trait Functor[F[_]] {
    def map[A, B](fa: F[A])(f: A => B): F[B]
  } 

@ new Functor[Map[String, _]] {
    def map[A, B](fa: Map[String, A])(f: A => B): Map[String, B] = ...
  } 

// Doesn't work
// Map[String, _] takes no type parameters, expected: 1
// new Functor[Map[String, _]] {
//             ^
```

---

# Concepts vs Implementation

Conceptually we understand `Map[String, _]` as a type function

The compiler doesn't think in those abstract terms though

It just wants a simple type constructor, e.g. `Option[_]`

---

# Workaround

Rework it into a simple type constructor using an alias

```scala
@ type StringMap[A] = Map[String, A] 

@ new Functor[StringMap] {
    def map[A, B](fa: StringMap[A])(f: A => B): StringMap[B] = ...
  } 
// Compiles
```

---

# Partial unification

A plugin added to the compiler prior to 2.13

```scala
// * -> *
def List(type: Type): Type

// * -> *
def Map[String, _](type: Type): Type
```

It helps the compiler understand these types as functions such that:

```scala
Map[String, Int]
```

can be viewed as:

```scala
Map[String, _](Int)

// ie. Map[String, _] applied to Int
```

---

# Connection to cats

`Validated[E, A]` is a double banger type like `Map`

Partial unification helps the compiler see it as:

```
Validated[E, _] applied to A
```

Useful in contexts where a `* -> *` type function is needed

e.g. type classes, generic methods

---

# 2.13

The partial unification plugin was very successful

Became integrated into the language in 2.13

(no plugin needed)

---

# For 2.12

Add the file `project/plugins.sbt` (might already exist) and put this in it:

```sbt
addSbtPlugin("org.lyranthe.sbt" % "partial-unification" % "1.1.2") // Or a more up to date version
```

See [docs](https://index.scala-lang.org/fiadliel/sbt-partial-unification/partial-unification/1.1.2?target=_2.12_1.0)

---

# Further reading

[Good explanation](https://gist.github.com/djspiewak/7a81a395c461fd3a09a6941d4cd040f2)
of the issue by Daniel Spiewak

---

```
                  __  __             _   _ 
 _ __   __ _ _ __|  \/  | __ _ _ __ | \ | |
| '_ \ / _` | '__| |\/| |/ _` | '_ \|  \| |
| |_) | (_| | |  | |  | | (_| | |_) | |\  |
| .__/ \__,_|_|  |_|  |_|\__,_| .__/|_| \_|
|_|                           |_|          
```

For monicative validation

Older cousin twice removed of `mapN`

---

# Phases of validation

Recall our parser:

```scala
type ParseError = String

def parse2(digits: String): Validated[ParseError, Int] = {
  if (digits.matches("-?\\d+")) {
    val bigInt = BigInt(digits)
    if (bigInt.isValidInt)
      Valid(bigInt.toInt)
    else
      Invalid(s"Representation '$digits' is not in range for a 32 bit signed integer")
  }
  else Invalid(s"Input must be 1 or more digits with an optional minus sign")
} 
```

Some checks can't be performed until later

For example, you can't check for overflow unless your input is a meaningful numeric representation

---

# Concepts

> you can't check for overflow unless your input is a meaningful numeric representation

This isn't just an implementation detail, it's inherent to the specification

```scala
def parse2(digits: String): Validated[ParseError, Int] = {
  if (digits.matches("-?\\d+")) {
    val bigInt = BigInt(digits)
    if (bigInt.isValidInt)
      Valid(bigInt.toInt)
    else
      Invalid(s"Representation '$digits' is not in range for a 32 bit signed integer")
  }
  else Invalid(s"Input must be 1 or more digits with an optional minus sign")
} 
```

An error message like this doesn't make sense:

> "Representation 'boban jones' is not in range for a 32 bit signed integer"

(it presupposes that it can be parsed into an integral form)

---

# Compiler phases

Similar thing for a compiler

It compiles in phases and can't move to the next phase of validation until everything makes sense

```scala
object BadCode {
  [
  def foo: Inty = 1
}}
```

It can't do type analysis if it's unable to parse the data into a syntax tree

---

# Monicative structure

Validation

```
 --------        --------        --------
|        |      |        |      |        |
|        | ---> |        | ---> |        | ---> ...
|        |      |        |      |        |
 --------        --------        --------
```

Each outer stage depends on the previous one to complete (monad)

Within each stage though, things can be validated independently (applicative)

```scala
for {
  stage1Data <- (v1a, v1b, v1c).mapN(...)
  stage2Data <- (v2a, v2b).mapN(...)
  stage3Data <- (v3a, v3b, v3c, v4c).mapN(...)
  ...
} yield stageNData
```

---

# Common case

Relationships between data

> Cli program that receives two integers such that they sum to an odd integer

```
   Array       (int1, int2)    (int1, int2)
 --------        --------        --------
| two    |      | inputs |      |overflow|
| inputs | ---> | are    | ---> |        | ---> ...
|        |      |integral|      | odd    |
 --------        --------        --------
```

---

# Dependent across stages

> Cli program that receives two integers such that they sum to an odd integer

```
   Array       (int1, int2)     (int1, int2)
 --------        --------        --------
| two    |      | inputs |      |overflow|
| inputs | ---> | are    | ---> |        | ---> ...
|        |      |integral|      | odd    |
 --------        --------        --------
```

We can't answer questions like:

> is the sum odd

until we've validated they're in an integral form

---

# Applicative within stages

> Cli program that receives two integers such that they sum to an odd integer

```
   Array       (int1, int2)     (int1, int2)
 --------        --------        --------
| two    |      | inputs |      |overflow|
| inputs | ---> | are    | ---> |        | ---> ...
|        |      |integral|      | odd    |
 --------        --------        --------
```

Within each stage there's potentially multiple parallel errors

## Start stage

- invalid number of elements

## Middle stage

- first number isn't integral
- second number isn't integral

## Last stage

- sum overflows (not a proper int)
- sum isn't odd

You can test these independently by adding them as `BigInt`'s,

no need to short circuit out before the oddness check if the sum overflows

---

# Problem with Validated

What issue do we hit trying to represent each stage with a `Validated`?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Problem with Validated

> What issue do we hit trying to represent each stage with a `Validated`?

It's not a monad - no `flatMap`

Could use `Either`

---

# Either says:

> So you've come crawling back eh?
>
> Thought you were too good for me...
>
> Only Willy defended me...

---

# The problem with Either?

It's not applicative

Won't work within each stage with `mapN`

---

# High view

```scala
val oddSumPair = for {
  // Stage 1
  (str1, str2) <- checkArgs(args).toEither // ValidatedNel[String, (String, String)]

  // Stage 2
  (int1, int2) <- (parse(str1), parse(str2)).mapN(...).toEither

  // Stage 3
  _ <- checkSum(int1, int2).toEither // ValidatedNel[String, Unit]
  // or if we're encoding the odd sum-iness of it into a type
  oddSumPair <- checkSum(int1, int2).toEither // ValidatedNel[String, OddSumPair]

} yield (int1, int2) or oddSumPair
```

Final result is an `Either`

Our mini-validators are returning `Validated` and we're converting to `Either`

---

# Alternative

> Our mini-validators are returning `Validated` and we're converting to `Either`

Maybe your mini-validators are already returning `Either`

```scala
val oddSumPair = for {
  // Stage 1
  (str1, str2) <- checkArgs(args) // Either[String, (String, String)]

  // Stage 2
  (int1, int2) <- (parse(str1), parse(str2)).mapN(...)

  // Stage 3
  _ <- checkSum(int1, int2) // Either[String, Unit]
  // or if we're encoding the odd sum-iness of it into a type
  oddSumPair <- checkSum(int1, int2) // Either[String, OddSumPair]

} yield (int1, int2) or oddSumPair
```

Now we don't need the `toEither`, but our `mapN`'s break

---

# Solution?

> Now we don't need the `toEither`, but our `mapN`'s break

Use `parMapN` instead:

```diff
 val oddSumPair = for {
   // Stage 1
   (str1, str2) <- checkArgs(args) // Either[String, (String, String)]
 
   // Stage 2
-  (int1, int2) <- (parse(int1), parse(int2)).mapN(...)
+  (int1, int2) <- (parse(int1), parse(int2)).parMapN(...)
 
   // Stage 3
   _ <- checkSum(int1, int2) // ValidatedNel[String, Unit]
   // or if we're encoding the odd sum-iness of it into a type
   oddSumPair <- checkSum(int1, int2) // Either[String, OddSumPair]
 
 } yield (int1, int2) or oddSumPair
```

---

# parMapN - summary

Some validation naturally falls into dependent stages (monadic concept)

Within each stage we should still do parallel independent validation (applicative concept)

---

# parMapN - summary

Validated is not a monad

We can use Either to represent the final result for each stage

But Either is not an applicative from `mapN`'s perspective

---

# parMapN - summary

If our mini-validations are `Validated`, then just do `.toEither` after combining them with `mapN`

If our mini-validations are `Either`, then combine them directly with `parMapN`

---

# Further reading on parMapN

[Cats docs for Parallel](https://typelevel.org/cats/typeclasses/parallel.html)

There's some other nifty tricks in that article

---

```
 _____ _           _ _             
|  ___(_)_ __   __| (_)_ __   __ _ 
| |_  | | '_ \ / _` | | '_ \ / _` |
|  _| | | | | | (_| | | | | | (_| |
|_|   |_|_| |_|\__,_|_|_| |_|\__, |
                             |___/ 
   _            
  (_) __ _ _ __ 
  | |/ _` | '__|
  | | (_| | |   
 _/ |\__,_|_|   
|__/            
  __ _ _           
 / _(_) | ___  ___ 
| |_| | |/ _ \/ __|
|  _| | |  __/\__ \
|_| |_|_|\___||___/
                   
```

Quick demo on how I locate jar files in my system

In sbt, use `show fullClasspath`

```bash
cd path/to/sbt/project/using/cats
sbt "show fullClasspath" | grep cats
```

In my case they're usually downloaded by coursier and live in:

```
~/.cache/coursier/v1/https/repo1.maven.org/maven2/
```

---

```
 _____                 
|_   _|   _ _ __   ___ 
  | || | | | '_ \ / _ \
  | || |_| | |_) |  __/
  |_| \__, | .__/ \___|
      |___/|_|         
 ___        __                              
|_ _|_ __  / _| ___ _ __ ___ _ __   ___ ___ 
 | || '_ \| |_ / _ \ '__/ _ \ '_ \ / __/ _ \
 | || | | |  _|  __/ | |  __/ | | | (_|  __/
|___|_| |_|_|  \___|_|  \___|_| |_|\___\___|
                                            
 ____  _          __  __ 
/ ___|| |_ _   _ / _|/ _|
\___ \| __| | | | |_| |_ 
 ___) | |_| |_| |  _|  _|
|____/ \__|\__,_|_| |_|  
                         
```

---

# Two type parameters - Validated[E, A]

What is the compile time type of this?

```scala
Valid(3)
```

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Two type parameters - Validated[E, A]

> What is the compile time type of this?

```scala
@ Valid(1) 
// Valid[Int] = Valid(1)
```

Remember it's a child class of `Validated`

---

# Tricky situations

```scala
val v = Valid(1)

// is really
val v: Valid[Int] = Valid(1)

// and isn't say
val v: Validated[String, Int] = Valid(1)
```

Might cause issues here and there

---

# Nice factory methods

You'll probably need this:

```scala
@ import cats.implicits._ 
```

## Building invalids

```scala
@ "Oh dear!".invalid[Int]
// Validated[String, Int] = Invalid("Oh dear!")
```

Here the `Int` is telling it the _happy_ type

(there is no data or other context to infer it)

## Building valids

Mirror the above

```scala
@ 3.valid[String]
// Validated[String, Int] = Valid(3)
```

---

# Nel's

So often our little validators work much more conveniently if they return nel's/nec's 

```scala
// Returns plain validated
def parse(digits: String): Validated[String, Int] = ...

(parse("1").toValidatedNel, parse("2").toValidatedNel, parse("3").toValidatedNel).mapN(...)
```

Compare with:

```scala
// Returns plain validated
def parse(digits: String): ValidatedNel[String, Int] = ...

(parse("1"), parse("2"), parse("boban")).mapN(...)
```

---

# Inside the little validators

> our little validators work much more conveniently if they return nel's/nec's 

```scala
def parse(digits: String): ValidatedNec[ParseError, Int] = {
  if (digits.matches("-?\\d+")) {
    val bigInt = BigInt(digits)
    if (bigInt.isValidInt)
      Valid(bigInt.toInt)
    else
      Invalid(NonEmptyChain.one(s"Representation '$digits' is not in range for a 32 bit signed integer"))
  //          ^^^^^^^^^^^^^^^^^
  }
  else Invalid(NonEmptyChain.one(s"Input must be 1 or more digits with an optional minus sign"))
  //           ^^^^^^^^^^^^^^^^^
} 
```

Many code paths just yield a single error

Can lead to a lot of clutter wrapping single values up

---

# Nel functions to the rescue!

```scala
@ 3.validNel[String] 
// ValidatedNel[String, Int] = Valid(3)

@ "Crumbs".invalidNel[Int] 
// ValidatedNel[String, Int] = Invalid(NonEmptyList("Crumbs", List()))

@ "Crumbs".invalidNec[Int] 
// ValidatedNec[String, Int] = Invalid(Singleton("Crumbs"))
```

---

# Transforming our code

Old

```scala
def parse(digits: String): ValidatedNec[ParseError, Int] = {
  if (digits.matches("-?\\d+")) {
    val bigInt = BigInt(digits)
    if (bigInt.isValidInt)
      Valid(bigInt.toInt)
    else
      Invalid(NonEmptyChain.one(s"Representation '$digits' is not in range for a 32 bit signed integer"))
  }
  else Invalid(NonEmptyChain.one(s"Input must be 1 or more digits with an optional minus sign"))
} 
```

New

```scala
def parse(digits: String): ValidatedNec[ParseError, Int] = {
  if (digits.matches("-?\\d+")) {
    val bigInt = BigInt(digits)
    if (bigInt.isValidInt)
      bigInt.toInt.validNec
    else
      s"Representation '$digits' is not in range for a 32 bit signed integer".invalidNec
  }
  else s"Input must be 1 or more digits with an optional minus sign".invalidNec
}
```

Note: type inference lets us get away with not putting type parameters in
