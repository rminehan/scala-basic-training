---
author: Rohan
date: 2021-07-14
title: Validation Hands On (part 1)
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

---

# Overview Recap

```
- List/NonEmptyList


- Chain/NonEmptyChain


- Validation Concepts


- Validation Hands on    <---- you are here
```

Bringing it all together!

---

# Append Recap

## List

List has O(n) append

```scala
val lists = List(
  List(1, 2),
  List(3, 4),
  List(5, 6),
  List(7, 8),
  List(9)
)

// n appends ---> O(n^2)
lists.foldLeft(List.empty[Int])(_ ++ _)
```

## Chain

Chain has O(1) append

```scala
val chains = Chain(
  Chain(1, 2),
  Chain(3, 4),
  Chain(5, 6),
  Chain(7, 8),
  Chain(9)
)

// n appends ---> O(n)
chains.foldLeft(Chain.empty[Int])(_ ++ _)
```

---

# Chain ADT

> Chain has O(1) append

Achieved by adding an `Append` to the ADT

```scala
sealed abstract class Chain[+A]

case object Empty extends Chain[Nothing]
case class Singleton[A](a: A) extends Chain[A]
case class Append[A](left: Chain[A], right: Chain[A]) extends Chain[A]
case class Wrap[A](seq: Seq[A]) extends Chain[A]
```

---

# Appending and validation?

How do they relate?

---

# Appending and validation?

We often get a large number of small clumps of errors that we want to concatenate:

```scala
Chain(
  // Name validation
  Chain("Invalid character in name"),
  // Password validation
  Chain("Password is too short", "Password must contain a digit"),
  // Email validation
  Chain("Email already in use"),
  ...
)

// flatten down to a single list of errors

Chain(
  "Invalid character in name",
  "Password is too short",
  "Password must contain a digit",
  "Email already in use",
  ...
)
```

---

# Validation Concepts Recap

> (1) A runtime check that your data meets your requirements
>
> (2) (If errors) Collecting as many errors as possible
>
> (3) (If valid) Encoding that knowledge such that it won't deteriorate

---

# Validation Concepts Recap

> (1) A runtime check that your data meets your requirements
>
> (2) (If errors) Collecting as many errors as possible
>
> (3) (If valid) Encoding that knowledge such that it won't deteriorate

3 prevents:

- revalidating the data over and over


- unnecessary effects


- units tests for all the invalid inputs

---

# Today

- the fortress metaphor


- cats.data.Validated


- non-empty validation concepts

---

# Two sessions

Probably too much to fit in one session

---

```
 ____                _           
| __ )  ___  _ __ __| | ___ _ __ 
|  _ \ / _ \| '__/ _` |/ _ \ '__|
| |_) | (_) | | | (_| |  __/ |   
|____/ \___/|_|  \__,_|\___|_|   
                                 
 ____            _            _   _             
|  _ \ _ __ ___ | |_ ___  ___| |_(_) ___  _ __  
| |_) | '__/ _ \| __/ _ \/ __| __| |/ _ \| '_ \ 
|  __/| | | (_) | ||  __/ (__| |_| | (_) | | | |
|_|   |_|  \___/ \__\___|\___|\__|_|\___/|_| |_|
                                                
```

Our app as a fortress

---

# Our app as a fortress

```
     ----      ----      ----
    |    |    |    |    |    |
    |    |    |    |    |    |     WILD COUNTRY
    |     ----      ----     |  UNCIVILIZED/LAWLESS
    |                        |
    |                        |   database data  (weak type systems)
    |    CIVILIZED      Door |    csv rows
    |    DRESS CODE      --  |    cli args
    |                   |  | |     json
    |                   |  | |   <-------
     ------------------------
```

Channel your inner Singaporean

---

# Type representations

```
     ----      ----      ----
    |    |    |    |    |    |
    |    |    |    |    |    |     WILD COUNTRY
    |     ----      ----     |  UNCIVILIZED/LAWLESS
    |                        |
    |                        |   database data  (weak type systems)
    |    CIVILIZED      Door |    csv rows
    |    DRESS CODE      --  |    cli args
    |                   |  | |     json
    |                   |  | |   <-------
     ------------------------
```

## Internal data representations

"Strong"

- case classes


- java.time representations


- ADT's for enum-y things

## Externally weak types used

"Weak"

- often stringy (unconstrained)


- very little information

---

# Validation

```
     ----      ----      ----
    |    |    |    |    |    |
    |    |    |    |    |    |     WILD COUNTRY
    |     ----      ----     |  UNCIVILIZED/LAWLESS
    |                        |
    |                        |   database data
    |     CIVILIZED     Door |    csv rows
    |     DRESS CODE     --  |    cli args
    |                   |  | |     json
    |                   |  | |   <-------
     ------------------------
```

Validation is the bouncer at the door

- rejects bad data


- lets good data in and captures the check (like a sticker)

---

# Example

Command line program that receives a collection of at least one number

## Uncivilized representation

All inputs arrive as `Array[String]`

(Very weak information-less type representation)

## Internally

Validate they're numbers

Encode that information with a type like `NonEmptyList[Int]`

---

# Conceptually

The bouncer/validator:

```
                    --------> (valid) NonEmptyList[Int]
                   /
Array[String] --->
                   \
                    --------> (invalid) Error(s)
```

---

```
 ____                                     _   _             
|  _ \ ___ _ __  _ __ ___  ___  ___ _ __ | |_(_)_ __   __ _ 
| |_) / _ \ '_ \| '__/ _ \/ __|/ _ \ '_ \| __| | '_ \ / _` |
|  _ <  __/ |_) | | |  __/\__ \  __/ | | | |_| | | | | (_| |
|_| \_\___| .__/|_|  \___||___/\___|_| |_|\__|_|_| |_|\__, |
          |_|                                         |___/ 
            _ _     _       _   _             
__   ____ _| (_) __| | __ _| |_(_) ___  _ __  
\ \ / / _` | | |/ _` |/ _` | __| |/ _ \| '_ \ 
 \ V / (_| | | | (_| | (_| | |_| | (_) | | | |
  \_/ \__,_|_|_|\__,_|\__,_|\__|_|\___/|_| |_|
                                              
```

---

# Option?

In our examples we've been using `Option`:

```scala
class Age private(inner: Short) {
  ...
}

object Age {
  // The only way to construct an Age
  def fromInt(age: Short): Option[Age] = {
    if (age < 18 || age > 150) None
    else Some(new Age(inner))
  }
}
```

```
                    --------> (valid) Some(Age)
                   /
Short ----------->
                   \
                    --------> (invalid) None
```

Problems with this?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Some(problem)

> Problems with this?

```
                    --------> (valid) Some(Age)
                   /
Short ----------->
                   \
                    --------> (invalid) None
```

No description of what went wrong

Nothing to send the user

---

# Improving it

The `Some` part is good,

we need a richer error representation type though

---

```
__     __    _ _     _       _           _ 
\ \   / /_ _| (_) __| | __ _| |_ ___  __| |
 \ \ / / _` | | |/ _` |/ _` | __/ _ \/ _` |
  \ V / (_| | | | (_| | (_| | ||  __/ (_| |
   \_/ \__,_|_|_|\__,_|\__,_|\__\___|\__,_|

cats.data.Validated
```

Representing the result of a validation

---

# Overview

`Validated` is an ADT that represents the two outcomes:

- `Valid(data)`


- `Invalid(error)`

```
                    --------> Valid(<Type encoding that knowledge>)
                   /
Input  ---------->
                   \
                    --------> Invalid(error)
```

---

# Example - type signature

Parsing a `String` into an `Int`

```scala
import cats.data.Validated

def parse(digits: String): Validated[Exception, Int] = ...
```

```
                    --------> Valid(Int)
                   /
String ---------->
                   \
                    --------> Invalid(error)
```

---

# Example - add implementation

```scala
import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}

// Parser for simple strings of digits
def parse(digits: String): Validated[Exception, Int] = {
  if (digits.forall(_.isDigit)) Valid(digits.toInt)
  else Invalid(new Exception(s"Non-digits found in input '$digits'"))
}
```

---

# Two type parameters

```scala
def parse(digits: String): Validated[Exception, Int] = ...

// E = Exception
// A = Int
```

We have a type parameter for the error and happy value

---

# Error

```scala
// Parser for simple strings of digits
def parse(digits: String): Validated[Exception, Int] = {
  if (digits.forall(_.isDigit)) Valid(digits.toInt)
  else Invalid(new Exception(s"Non-digits found in input '$digits'"))
} 
```

We're _returning_ an exception, not throwing it

So why are we using exceptions then?

---

# Error

> So why are we using exceptions then?

Just a habit - no reason we have to though, could just use a String

```scala
type ParseError = String

def parse(digits: String): Validated[ParseError, Int] = {
  if (digits.forall(_.isDigit)) Valid(digits.toInt)
  else Invalid(s"Non-digits found in input '$digits'")
  //             ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
  //                  Just a simple string now
} 
```

---

# Blind spots

```scala
type ParseError = String

def parse(digits: String): Validated[ParseError, Int] = {
  if (digits.forall(_.isDigit)) Valid(digits.toInt)
  else Invalid(s"Non-digits found in input '$digits'")
}
```

Aside: There are some blind spots in this logic. Can you see them?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Blind spots

```scala
type ParseError = String

def parse(digits: String): Validated[ParseError, Int] = {
  if (digits.forall(_.isDigit)) Valid(digits.toInt)
  else Invalid(s"Non-digits found in input '$digits'")
}
```

The `if` test isn't strict enough to guarantee that `digits.toInt` is safe:

```scala
@ parse("") 
// java.lang.NumberFormatException: For input string: ""

@ parse("123423042830492834234") 
// java.lang.NumberFormatException: For input string: "123423042830492834234"

@ parse("-1")
// Invalid("Non-digits found in input '-1'")
```

---

# Alternatives

```scala
type ParseError = String

def parse2(digits: String): Validated[ParseError, Int] = {
  if (digits.matches("-?\\d+")) {
    val bigInt = BigInt(digits)
    if (bigInt.isValidInt)
      Valid(bigInt.toInt)
    else
      Invalid(s"Representation is not in range for a 32 bit signed integer")
  }
  else Invalid(s"Input must be 1 or more digits with an optional minus sign")
} 
```

---

# Alternatives

```scala
import scala.util.Try

def parse3(digits: String): Validated[ParseError, Int] = {
  Try(digits.toInt).fold(
    err => Invalid(s"Error: $err"),
    data => Valid(data)
  )
}
```

ie. just wrap around the `toInt`

---

# Pro's and Con's

> just wrap around the `toInt`

```scala
  Try(digits.toInt).fold(
    err => Invalid(s"Error: $err"),
    data => Valid(data)
  )
```

## Pro's

- saves us having to reinvent the wheel

## Con's

- slow on bad inputs (throwing and catching exceptions)


- implementation driving the spec

(couples us to the implentation and hidden aspects of it)


- no control of error messages


- short circuiting means just one error message


- easy to misinterpret the root cause of an exception

---

# Sticking with the old one

I've already copy-pasted the bad code through all my scripts though so we'll just go with it

---

```
 ____                       
|  _ \  ___ _ __ ___   ___  
| | | |/ _ \ '_ ` _ \ / _ \ 
| |_| |  __/ | | | | | (_) |
|____/ \___|_| |_| |_|\___/ 
                            
 _____ _                
|_   _(_)_ __ ___   ___ 
  | | | | '_ ` _ \ / _ \
  | | | | | | | | |  __/
  |_| |_|_| |_| |_|\___|
                        
```

Enough chin wagging!

Let's get in the trenches and do some border security

We'll write a cli program that doubles a Int passed from the command line

`Double.scala`

To vim!

---

# Run with

```bash
$ scala -cp path/to/cats-core_2.12-2.6.1.jar Double.scala 5
10
```

(Assumes you're using scala 2.12)

Note I'm using a newer one here to keep up with the docs

```diff
-2.1.1
+2.6.1
```

---

# Validated combinators

Play around with `map`, `filter` etc...

To the repl!

---

# Repl Summary

```scala
@ import cats.data.Validated

@ import cats.data.Validated.{Valid, Invalid} 

@ val v: Validated[String, Int] = Valid(3) 

@ v. 
===                  ensure               forall               ...
andThen              ensureOr             foreach              ...
ap                   exists               getOrElse            ...
bimap                findValid            isInvalid            ...
canEqual             fold                 isValid              ...
combine              foldLeft             leftMap              ...
compare              foldRight            map                  ...

@ v.map(_ + 3) 
// Valid(6)

@ val v2: Validated[String, Int] = Invalid("Too Bobany") 

@ v2.map(_ + 3) 
// Invalid("Too Bobany")

@ v.fold(err => 5, i => i + 6) 
// 9

@ v.map(_ + 6).getOrElse(5) 
// 9

@ v.toOption 
// Some(3)

@ v.toEither 
// Right(3)

@ Validated.fromEither(v.toEither) 
// Valid(3)
```

---

```
 _____ _ _   _               
| ____(_) |_| |__   ___ _ __ 
|  _| | | __| '_ \ / _ \ '__|
| |___| | |_| | | |  __/ |    ?
|_____|_|\__|_| |_|\___|_|   
                             
```

Can't we just use `Either`?

---

# Either?

Either has a `Left` and `Right`. Can't we do:

```
                    --------> Right(<Type encoding that knowledge>)
                   /
Input  ---------->
                   \
                    --------> Left(error)
```

e.g.

```scala
def parse(digits: String): Either[ParseError, Int] = {
  if (digits.forall(_.isDigit)) Right(digits.toInt)
  else Left(s"Input invalid. '$digits' Non-digits found in input string")
} 
```

Any thoughts?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Validated - analysis

A little more explicit

Can add validation specific functionality to it

Doesn't require a left/right bias convention

Not a monad

---

# Either - analysis

No libraries, imports needed

Gives us a chance to make fun of Southpaws (e.g. Cynthia)

---

# Detour

Make the example more complex - we'll add two numbers

Get rid of `Validated` and use `Either`

`AddEither.scala`

To vim!

---

# Error handling

```scala
  case Array(str1, str2) =>
    for {
      int1 <- parse(str1)
      int2 <- parse(str2)
    } yield (int1, int2)
```

```bash
$ scala AddEither.scala foo bar
# Encountered error: Non-digits found in input 'foo'
```

Q: What's the issue with this error message?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Error handling

```scala
  case Array(str1, str2) =>
    for {
      int1 <- parse(str1)
      int2 <- parse(str2)
    } yield (int1, int2)
```

```bash
$ scala AddEither.scala foo bar
# Encountered error: Input invalid. 'foo' Non-digits found in input string
```

> Q: What's the issue with this error message?

No mention of "bar"

---

# What caused this issue?

```scala
  case Array(str1, str2) =>
    for {
      int1 <- parse(str1)
      int2 <- parse(str2)
    } yield (int1, int2)
```

Q: Why is it bailing out after one error?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Either is a monad

```scala
  case Array(str1, str2) =>
    for {
      int1 <- parse(str1)
      int2 <- parse(str2)
    } yield (int1, int2)
```

> Q: Why is it bailing out after one error?

We're using `Either.flatMap`

Above desugars to:

```scala
parse(str1).flatMap { int1 =>
  parse(str2).map { int2 =>
    (int1, int2)
  }
}
```

---

# Option/Either

They're implementation of `flatMap` is to short-circuit out on the unhappy path:

```scala
for {
  boban <- findByName("Boban")
  friend <- boban.friendOpt
  friendOfFriend <- friend.friendOpt
} yield friendOfFriend
```

"Unhappy":

- Option: None


- Either: Left (it's right biased)

---

# Monads

Represent dependent computation

```scala
for {
  boban <- findByName("Boban")
  friend <- boban.friendOpt
  friendOfFriend <- friend.friendOpt
} yield friendOfFriend
```

Validation is not "dependent"

ie. we can validate different inputs independently

---

# Good UI design

Give them all the errors up front

Not one by one

Don't bail out of the first error

(Analogy: scala compiler vs python interpreter)

---

# Bad Modelling

```scala
val inputV: Either[String, (Int, Int)] = args match {
//          ^^^^^^^^^^^^^^^^^^^^^^^^^^
  case Array(str1, str2) =>
    for {
      int1 <- parse(str1)
      int2 <- parse(str2)
    } yield (int1, int2)

  case _ => Left("Expected exactly two arguments composed of digits")
}
```

Our error type is forcing us into this too

It only allows one error string

---

# Modelling errors

We could have potentially many errors

Some code paths might have 0, some might have 2, some might have 1

What should we use for the error type?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Modelling errors

```scala
val inputV: Either[List[String], (Int, Int)] = ...
//                 ^^^^^^^^^^^^
```

That's a bit better

---

# Remodelling

We'll switch back to `Validated` as it will suit this better

`AddValidatedList.scala`

To vim!

---

# Recap

Comparing first and second implementations

## Short circuiting

Got rid of it

Parsed both args independently and then examined the pair of results

## Better modelling

Modelled our errors as a list as many things can go wrong

(don't under-estimate the fail-iness of your users)

---

```
 _   _               
| | | | _____      __
| |_| |/ _ \ \ /\ / /
|  _  | (_) \ V  V / 
|_| |_|\___/ \_/\_/  
                     
                             
 _ __ ___   __ _ _ __  _   _ 
| '_ ` _ \ / _` | '_ \| | | |
| | | | | | (_| | | | | |_| |
|_| |_| |_|\__,_|_| |_|\__, |
                       |___/ 
                             ___ 
  ___ _ __ _ __ ___  _ __ __|__ \
 / _ \ '__| '__/ _ \| '__/ __|/ /
|  __/ |  | | | (_) | |  \__ \_| 
 \___|_|  |_|  \___/|_|  |___(_) 
                                 
```

We're still being optimistic about the fail-iness of our users

---

# Errors

```scala
inputV match {
  case Valid((int1, int2)) => println(int1 + int2)
                                                                                                  
  case Invalid(errors) => errors.foreach(println)
  //                      ^^^^^^ empty?
}
```

Does it make sense for our logic to go into the error case,

but have an empty list of errors? 

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Errors

```scala
inputV match {
  case Valid((int1, int2)) => println(int1 + int2)
                                                                                                  
  case Invalid(errors) => errors.foreach(println)
  //                      ^^^^^^ empty?
}
```

> Does it make sense for our logic to go into the error case,
> 
> but have an empty list of errors? 

Not really

If you went into the error case, then something must have gone wrong 

You'd expect at least one error for that

e.g.

```scala
val inputV: Validated[List[String], (Int, Int)] = args match {
  case Array(str1, str2) => ...

  // It's wrong, but I won't tell you why - :sassy-parrot:
  case _ => Invalid(List.empty[String])
}
```

---

# Improving error modelling

Current validation model:

```scala
Validated[List[String], (Int, Int)]
//        --- error --  -- data --
```

How would we change it to make sure that there's always at least one error?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# NonEmptyList!

> How would we change it to make sure that there's always at least one error?

```diff
-Validated[List[String], (Int, Int)]
+Validated[NonEmptyList[String], (Int, Int)]
```

It pops up again!

Now it's impossible for the validation process to produce an empty list

of errors in the failure case

---

# Aside: Nec

Could also use `NonEmptyChain`

---

# ValidatedNel

```scala
Validated[NonEmptyList[String], (Int, Int)]
```

This is so common there's an alias for it:

```scala
type ValidatedNel[E, A] = Validated[NonEmptyList[E], A]
```

---

# Fixing it

`AddValidatedNel.scala`

To the vim!
