---
author: Rohan
date: 2021-07-14
title: Validation Hands On
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

Code Uncles: What type class comes to the rescue here?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# What rescues us?

> Code Uncles: What type class comes to the rescue here?

mapN

Powered under the hood by applicative

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
(parse("abc"), parse("def")) match {
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

---

```
  ____ _           _       
 / ___| |__   __ _(_)_ __  
| |   | '_ \ / _` | | '_ \ 
| |___| | | | (_| | | | | | ?
 \____|_| |_|\__,_|_|_| |_|
                           
```

Tying it together

---

# Typical validation scenario

Several pieces of data arrive

Each one has potentially multiple issues

If one error occurs, we add them all together and go into the invalid state

---

# Example

```scala
// Name validation (failed)
Invalid(NonEmptyList.one("Invalid character in name"))

// Password validation (failed)
Invalid(NonEmptyList.of("Password is too short", "Password must contain a digit"))

// Email validation (passed)
Valid(email)

// Address (passed)
Valid(address)

// Passport number (failed)
Invalid(NonEmptyList.one("Unexpected character in passport number"))
```

Flattens out to:

```scala
NonEmptyList.of(
  "Invalid character in name",
  "Password is too short",
  "Password must contain a digit",
  "Unexpected character in passport number"
)
```

---

# Chain and NonEmptyChain

We've been using lists as they're familiar

But `Chain` will perform better

---

# Analytics codebase?

We have been using `NonEmptyList/ValidatedNel`,

but could starting using `NonEmptyChain/ValidatedNec`

---

```
 _____     _     _       _     _      
|  ___|__ | | __| | __ _| |__ | | ___ 
| |_ / _ \| |/ _` |/ _` | '_ \| |/ _ \
|  _| (_) | | (_| | (_| | |_) | |  __/
|_|  \___/|_|\__,_|\__,_|_.__/|_|\___|
                                      
```

Are `NonEmptyChain` and `NonEmptyList` foldable?

```scala
val lists = List(
  NonEmptyList.of(1, 2, 3),
  NonEmptyList.of(4, 5, 6),
  NonEmptyList.of(7, 8)
)

class NonEmptyListFolable[A] extends Foldable[NonEmptyList[A]] { ... }

fold(lists, new NonEmptyListFolable[Int])

// NonEmptyList.of(1, ..., 8)
```

Any issues?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Questions to think about?

What will we bootstrap the `fold` with?

```scala
class NonEmptyListFolable[A] extends Foldable[NonEmptyList[A]] {

  def seed: NonEmptyList[A] = ... // <--- what will you use?

  def combine(left: NonEmptyList[A], right: NonEmptyList[A]): NonEmptyList[A] = ...
}
```

:hmmm-parrot:

---

# Folding an empty collection?

Q: If we were adding ints, what would we bootstrap the `fold` with?

```scala
object IntAddition extends Foldable[Int] {
  def seed: Int = 0

  def combine(left: Int, right: Int): Int = left + right
}

fold(List(1, 2, 3), IntAddition)
```

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Seed

> Q: If we were adding ints, what would we bootstrap the `fold` with?

0

---

# Folding nel's?

What would the seed be?

```scala
class NonEmptyListFoldable[A] extends Foldable[A] {
  def seed: NonEmptyList[A] = ???

  def combine(left: NonEmptyList[A], right: NonEmptyList[A]): NonEmptyList[A] = left ::: right
}
```

```
 ___ ___ ___ 
|__ \__ \__ \
  / / / / / /
 |_| |_| |_| 
 (_) (_) (_) 
             
```

---

# Folding nel's?

> What would the seed be?

There isn't one.

You can't have an empty list of type `NonEmptyList` (that's the point!)

---

# Conclusion

`NonEmptyList` isn't foldable

e.g. we couldn't fold an empty collection of `NonEmptyList`s

---

# Not foldable!

> But Rohan,

you say angrily

> this whole thing has been leading up to folding lists of lists together!
>
> Now you tell us that you can't even fold these things!

---

# Purpose of seed

Why do we need a seed?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Purpose of seed

> Why do we need a seed?

To bootstrap it (particularly if we're folding an empty collection)

---

# Back to our example

Is it possible to be in the error state, but have 0 groups of errors?

(Below we have 3 groups of errors)

```scala
// Name validation (failed)
Invalid(NonEmptyList.one("Invalid character in name"))

// Password validation (failed)
Invalid(NonEmptyList.of("Password is too short", "Password must contain a digit"))

// Email validation (passed)
Valid(email)

// Address (passed)
Valid(address)

// Passport number (failed)
Invalid(NonEmptyList.one("Unexpected character in passport number"))
```

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# nel's of nel's

> Is it possible to be in the error state, but have 0 groups of errors?

No

If none of the groups failed, then overall it's a success (by construction)

ie. if we're in the error state we have a `NonEmptyList[NonEmptyList[String]]`

ie. the outer list is non-empty, and all the inner lists are non-empty

---

# Alternative to folding

Our fold takes a regular list:

```scala
def fold[A](list: List[A], foldable: Foldable[A]): A = ...

trait Foldable[A] {
  def seed: A

  def combine(left: A, right: A): A
}
```

If we were folding a `NonEmptyList[A]`, would we need a seed?

```scala
def foldNel[A](nel: NonEmptyList[A], foldable: Foldable[A]): A = ...
```

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# First element as seed

> If we were folding a `NonEmptyList[A]`, would we need a seed?

Nope

We can just use the first element

It's guaranteed to exist because it's non-empty!

---

# Reducible

A weaker version of `Foldable`:

```scala
def foldNel[A](nel: NonEmptyList[A], reducible: Reducible[A]): A = {
  val acc = nel.head

  for (a <- nel.tail)
    acc = reducible.combine(acc, a)

  acc
}

trait Reducible[A] {
  // No seed needed

  def combine(left: A, right: A): A
}
```

---

# Reducing nel's of nel's?

```scala
class NelReducible[A] extends Reducible[NonEmptyList[A]] {

  def combine(left: NonEmptyList[A], right: NonEmptyList[A]): NonEmptyList[A] = left ::: right

}
```

---

# Summarizing that section

Nel's aren't foldable

But in the context of error handling, you always have a nel of nels

You can still combine them by using the first nel as the seed,

then folding through the rest using `:::`

---

# Apology

You say:

> Sorry I got angry at you Rohan

No worries

---

# Aside: Category Theory Jargon

"Monoid" = Foldable

"Semigroup" = Reducible (ie. monoid without seed)

---

# Interview question

> What's an example of a semigroup that isn't a monoid?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Interview question

> What's an example of a semigroup that isn't a monoid?

Non-empty collections under concatenation, e.g.

- `NonEmptyList`


- `NonEmptyChain`


- `NonEmptyVector`

---

Last topic:

```
 ____        
/ ___|  ___  
\___ \ / _ \ 
 ___) | (_) |
|____/ \___/ 
             
                             
 _ __ ___   __ _ _ __  _   _ 
| '_ ` _ \ / _` | '_ \| | | |
| | | | | | (_| | | | | |_| |
|_| |_| |_|\__,_|_| |_|\__, |
                       |___/ 
                     _      _     
 _ __ ___   ___   __| | ___| |___ 
| '_ ` _ \ / _ \ / _` |/ _ \ / __|
| | | | | | (_) | (_| |  __/ \__ \
|_| |_| |_|\___/ \__,_|\___|_|___/
                                  
       _   _   _  
      | | | | | | 
      | | | | | | 
      |_| |_| |_| 
      (_) (_) (_) 
                  
```

Explain by example

---

# The confusion of many models

Suppose internally this is our model:

```scala
case class Lead(
  id: Id,
  linkedinIdOpt: Option[LinkedinId], // Originally not optional
  name: String,
  age: Int,
  connections: List[Lead], // Added later
  created: Instant
)
```

---

# Many IO formats

```scala
case class Lead(
  id: Id,
  linkedinIdOpt: Option[LinkedinId],
  name: String,
  age: Int,
  connections: List[Lead]
)
```

Potentially your data arrives via many IO routes:

```
     ----      ----      ----
    |    |    |    |    |    |      sql database
    |    |    |    |    |    | <---------------
    |     ----      ----     |
    |                        |      POST /v1/user
    |                        | <---------------
    |                        |      POST /v2/user
    |                        | <---------------
    |                   Door |
    |                    --  |    Kafka message
    |                   |  | | <---------------
    |                   |  | |
     ------------------------
```

Each has its own validation model

---

# Comparing models - sql database

## Internal

```scala
case class Lead(
  id: Id,
  linkedinIdOpt: Option[LinkedinId],
  name: String,
  age: Int,
  connections: List[Lead],
  created: Instant
)
```

## Database

```diff
 case class Lead(
   id: Id,
   linkedinIdOpt: Option[LinkedinId],
   name: String,
   age: Int,
-  connections: List[Lead]  // Stored in another table - one-to-many
-  created: Instant,
+  created: ZonedDateTime
 )
```

---

# Comparing models - POST

## Internal

```scala
case class Lead(
  id: Id,
  linkedinIdOpt: Option[LinkedinId],
  name: String,
  age: Int,
  connections: List[Lead],
  created: Instant
)
```

## POST /v1/user

```diff
 case class Person(
-  id: Id,
-  linkedinIdOpt: Option[LinkedinId],
+  linkedinId: LinkedinId,
   name: String,
   age: Int,
-  leads: List[Lead]
  created: Instant,
 )
```

Back when it was made, all users had to have a linkedin id

That was used as the internal id

No concept of connections

Was replaced by `/v2/user`

---

# The point of this example?

Don't conflate data validation models with your internal model

They often look the same but it's important to keep them separate

(particularly if the internal model changes over time)

Can lead to many similar looking models

---

# Model conversion

Usually:

- external data gets converted to a validation model


- that validation model gets converted to the "true" internal model

They are often very similar

---

# Chimney

Chimney is a good library to convert between these similar models

---

```
__        __                     _             
\ \      / / __ __ _ _ __  _ __ (_)_ __   __ _ 
 \ \ /\ / / '__/ _` | '_ \| '_ \| | '_ \ / _` |
  \ V  V /| | | (_| | |_) | |_) | | | | | (_| |
   \_/\_/ |_|  \__,_| .__/| .__/|_|_| |_|\__, |
                    |_|   |_|            |___/ 
 _   _       
| | | |_ __  
| | | | '_ \ 
| |_| | |_) |
 \___/| .__/ 
      |_|    
```

Wrapping up the last 4 lessons

---

# Validation Concepts

Capture the information you validate

Prevents revalidation and the ickiness that comes with that

---

# Information deterioration

Types are the most practical mechanism to encode that information

such that it doesn't immediately deteriorate

---

# Validated

Use `cats.data.Validated` to represent the result of validation

```
                    --------> Valid(Strong)
                   /
Weak   ---------->
                   \
                    --------> Invalid(error)
```

---

# non-empties of non-empties

There are usually many different pieces of data being validated simultaneously

Each piece of data can produce non-empty errors

If at least group fails, the whole process fails

ie. failure will have non-empty of non-empties

They all need to be combined

---

# List vs Chain

## List/NonEmptyList

Simple structures

O(n) append performance

Implies O(n^2) performance when combining n `ValidatedNel`'s

## Chain/NonEmptyChain

Complex structures (a few weird edge cases)

O(1) append 

Implies O(n) performance when combining n `ValidatedNec`'s

---

# Significance?

Does the performance difference matter?

If there's 2-3 errors, then O(n^2) vs O(n) is meaningless

---

# Significance?

> If there's 2-3 errors, then O(n^2) vs O(n) is meaningless

Doesn't really matter

But it's a good habit

And there's no downside

---

# Many Models

Don't conflate validation models from internal models

Resist the urge to reuse models for different input mechanisms

---

# QnA

(No questions implies all future validation code will be perfect)
