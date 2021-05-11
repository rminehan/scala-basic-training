---
author: Zohan/Rij
title: Option
date: 2021-05-20
---

```
  ___        _   _             
 / _ \ _ __ | |_(_) ___  _ __  
| | | | '_ \| __| |/ _ \| '_ \ 
| |_| | |_) | |_| | (_) | | | |
 \___/| .__/ \__|_|\___/|_| |_|
      |_|                      
```

A much much better `null`

---

# Option - a quick overview

- comes from the scala standard library


- use it to model something which may or may not exist


- has a lot of nice FP properties (beyond the scope of today)

---

# A typical problem

We have a list of people:

```scala
case class Person(name: String, age: Int)

val people = List(
  Person("Boban", 28),
  Person("Willy", 30),
  Person("Clement", 26),
  Person("James", 45),
  ...
)
```

We want to find the record that has name "Thilo"

(secret motive: we want to know how old Thilo really is)

---

# Conceptually

We're asking the question:

> Find me the person with name "Thilo"

```scala
val people = List(
  Person("Boban", 28),
  Person("Willy", 30),
  Person("Clement", 26),
  Person("James", 45),
  ...
)
```

---

# First attempt

Suppose we wrote a function that finds by name:

```scala
val people = List(
  Person("Boban", 28),
  Person("Willy", 30),
  Person("Clement", 26),
  Person("James", 45),
  ...
)

def find(name: String): Person = {
  // ... implementation details
}

val thilo = find("Thilo")
println(s"Thilo is ${thilo.age} years old")
```

Code Uncles: What's the problem with this approach?

---

# The problem

`:thilo-not-found:`

What would we return if there is no one with that name though?

```scala
def find(name: String): Person = {
  // ... implementation details
}
```

(There's also the problem of duplicates, but let's assume names are unique)

---

# The java developers answer

> What would we return if there is no one with that name though?

Java developer: `null`

```scala
def find(name: String): Person = {
  // ... implementation details
}

val thilo = find("Thilo")

if (thilo == null)
  println("Couldn't find Thilo in the database, ask his wife how old he is")
else
  println(s"We found Thilo! He is ${thilo.age} years old")
```

---

# What is null?

null is a pointer to "nowhere".

Usually it's some predefined address (e.g. 0) which the JVM understands to mean `null`.

Remember that in java/scala, references are really 4 byte pointers.

---

# The problem with null

It's very easy to do this:

```scala
val thilo = find("Thilo")

println(s"We found Thilo! He is ${thilo.age} years old")
```

Code Uncles: What will this code do if Thilo is not in the database?

---

# `NullPointerException`

```scala
val thilo = find("Thilo")

println(s"We found Thilo! He is ${thilo.age} years old")
```

This code is treating the `thilo` reference as a `Person`, trying to access `.age`.

If `thilo == null`, the JVM will detect the special `null` address (e.g. 0).

There is no meaningful data to call `.age` on.

So it will throw a `NullPointerException`.

---

# Recapping this

With `null`, it's very easy to forget to do the runtime check.

Nothing is reminding you to do it.

```scala
val thilo = find("Thilo")

if (thilo == null)  // <---- don't forget this check!
  println("Couldn't find Thilo in the database, ask his wife how old he is")
else
  println(s"We found Thilo! He is ${thilo.age} years old")
```

Naturally people often forget and `null` values sneak into their data

causing issues later in execution, like ticking time bombs.

---

# From the perspective of the type system

The root of the problem:

```scala
def find(name: String): Person = {
  // ... implementation details
}

val thilo = find("Thilo")
```

We are using `Person` to represent the output of our function.

But really there are two cases:

- a person exists (and here's the data)


- a person doesn't exist

---

# Squeezing

> But really there are two cases:
>
>- a person exists (and here's the data)
>
>
>- a person doesn't exist

We are squeezing both cases into the same type representation: `Person`

```scala
def find(name: String): Person
  //                    ^^^^^^
```

It's only at runtime that we can differentiate them by doing a `null` check.

The compiler isn't smart enough to make sure we always do that.

---

# Runtime to compile time

> But really there are two cases:
>
>- a person exists (and here's the data)
>
>
>- a person doesn't exist

If we represent the two cases using **types**,

then the compiler can make sure we always perform the check.

---

# (Re)introducing Option

```scala
- def find(name: String): Person
+ def find(name: String): Option[Person]
```

`Option` is a type for representing that data _might_ exist.

It has two cases:

- `Some(data)`


- `None`

---

# Forcing us to check

```scala
def find(name: String): Option[Person] = {
  // ... implementation details
}

val thiloOpt = find("Thilo")  // type is now Option[Person], not Person

// Won't compile
println(thiloOpt.age)

// Have to handle both cases
thiloOpt match {
  case Some(thilo) => println(s"We found The Thilo! Age is: ${thilo.age}")
  case None => println("Thilo not found. Ask Mrs Thilo")
}
```

---

# Type parameter

`Option` is really `Option[Data]`.

`Data` can be any type you want:

- `Int`
- `Person`
- `String`
- ...

---

# Aside

Encoding more information into the type system is a typical scala-ry thing to do.

It gives the compiler more insight into what you're doing.

That allows the compiler to catch more errors reducing the chance of runtime errors.

---

# Java vs Scala

So whilst java and scala both have type checkers,

the way scala developers write their programs tends to mean _more_

errors get caught at compile time.

---

# Spectrum

Type safety is more of a spectrum than a binary yes/no concept.

```
      DYNAMIC                          STATIC

 unsafe             |                                      safe
  <-----------------|----------------------------------------->
                    |
 Python             |     C++     Java                Scala
                    |             Go                      Haskell
```

It's about what percentage of errors you catch at compile time vs runtime.

---

# Quick recap: overview

`Option` is a great tool for modelling scenarios where we may or may not have some data.

`Some(data)` represents the case where we found something (`data`).

`None` represents the case where we found nothing.

We can use pattern matching, e.g.

```scala
opt match {
  case Some(thing) => println(s"Found: $thing")
  case None => println("404 - couldn't find anything...")
}
```

---

# Quick recap: Option vs null

Option is more typesafe.

It lets the compiler in on the action.

The compiler will _force_ you to handle both cases.

With `null` it's easy to forget to check and you get runtime exceptions.

---

# Case Study

Rohan porting Option to C# at Quantium.

---

# More than just null++

Option can do a lot more than just being "a more typesafe null".

But there's a lot of handy utilities that make it much easier to model complex logic.

---

# Example

We're pretty sure that Thilo is 28, but not super sure.

Also Thilo is feeling a bit insecure about his age.

We'll shave a few years off for him.

---

# Logic

- look in the database for Thilo by name


- if we find Thilo in there, get his age and remove 5 years


- otherwise fall back to our guess (28)

---

# Coding it up

```scala
val thiloAge = find("Thilo").map(_.age).map(_ - 5).getOrElse(28)
```

`map` will transform the value using the function (when it exists).

`getOrElse` will use the value in `Some` if it exists, or use the fallback value.

---

# Visualizing it

```scala
val thiloAge = find("Thilo").map(_.age).map(_ - 5).getOrElse(28)
```

Visualizing it in terms of "tracks":

```
----------------------------------------------------------------------------
      |
Type  |   Option[Person]       Option[Int]      Option[Int]      Int
      |
----------------------------------------------------------------------------
      |
Code  |   find("Thilo")        .map(_.age)     .map(_ - 5)    .getOrElse(28)
      |
----------------------------------------------------------------------------
      |
Some  |  Some(Person(...)) ---> Some(63)   --->  Some(57)
track |
      |
None  |       None         --->   None     --->   None
track |
      |
Int   |                                                          57, 28
----------------------------------------------------------------------------
```

---

# Step by step

Original code:

```scala
val thiloAge = find("Thilo").map(_.age).map(_ - 5).getOrElse(28)
```

Breaking it into steps and adding explicit types:

```scala
def find(name: String): Option[Person] = ...

val thiloOpt: Option[Person] = find("Thilo")

// Zoom in on the age if it exists
val ageOpt: Option[Int] = thiloOpt.map(thilo => thilo.age)

// Adjust the age if it exists
val adjustedAgeOpt: Option[Int] = ageOpt.map(age => age - 5)

// Collapse back to a final value using the default if necessary.
val thiloAge: Int = adjustedAgeOpt.getOrElse(28)
```

---

# map - thinking about types more generally

```scala
val optA: Option[A] = ...

val f: A => B

val optB: Option[B] = optA.map(f)
```

`map` lets us map the function over our `Option`.

This changes the type.

Cases:

- if `optA` is `Some(a)`, then apply the function to `a` and wrap in a `Some`


- if `optA` is `None`, then don't use the function as stay as `None`

---

# Conceptually

```scala
val optA: Option[A] = ...

val f: A => B

val optB: Option[B] = optA.map(f)
```

As a "Rohan" diagram:

```
Option[A]           map         Option[B]
f: A => B       ---------->
```

---

# map - implementation

`optA.map(f: A => B)` is a bit like this:

```scala
opt match {
  case Some(data) => Some(f(data))
  case None => None
}
```

Observations:

- no "switching tracks"
    - if the incoming data is `Some`, then the outgoing data is also
    - if the incoming data is `None`, then the outgoing data is also


- `f` is only used in the `Some` case
    - makes sense, in the `None` case there's nothing to apply it to

---

# Aside

`map` eh? Sounds kind of familiar.

We looked at `map` in "Higher Order Functions":

e.g. map each String to it's length:

```
List(                       List(
  "Boban",                    5,
  "Enxhell",      ---->       7,
  "Rohan",                    5,
  "Zij"                       3
)                           )
```

---

# Aside

Remember how we muddled our way towards `map` by increasingly abstracting:

```
          lengths                 (Specific List[String] => List[Int] functions)
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

# map

We ended up with a very abstracted function `map`:

```scala
def map[A, B](list: List[A])(f: A => B): List[B] = ...
```

```
List[A]       List[B]

List(         List(
  a1,   f       f(a1),
  a2,  --->     f(a2),
  a3,           f(a3)
  ...           ...
)
```

We mapped `f` "over" our list.

---

# As a "Rohan" diagram

```scala
def map[A, B](list: List[A])(f: A => B): List[B] = ...
```

```
List[A]            map         List[B]
f: A => B      ----------> 
```

Kind of feels familiar...

---

# Comparing diagrams:

```
List[A]            map         List[B]
f: A => B      ----------> 


Option[A]          map         Option[B]
f: A => B      ---------->
```

Eep! They're almost identical!

Just do a find-replace List -> Option.

---

# Structure preserving

Notice how both `map`s preseve structure:

## List

```
List[A]       List[B]

List(         List(
  a1,   f       f(a1),
  a2,  --->     f(a2),
  a3,           f(a3)
  ...           ...
)
```

Length and order preserved.

## Option

```
Some(a) -> Some(f(a))

None -> None
```

No jumping tracks.

---

# Hmm...

Clearly we've stumbled onto something quite deep...

Our FP antenna tell us there's an abstraction hiding somewhere.

---

# Functor

What we've realized is that `List` and `Option` both have a "map" concept.

Map is structure preserving.

ie. they are "functors".

But we're not here to learn FP, back to `Option`!

---

# getOrElse

Simpler concept.

You leave the world of `Option` and collapse down to your data.

`optA.getOrElse(fallback)` is basically:

```scala
optA match {
  case Some(a) => a
  case None => fallback
}
```

---

# (Advanced) New example

Person has extra data:

```scala
case class Person(
  name: String,
  age: Int,
  favouriteEmojiOpt: Option[String],
  bestyOpt: Option[Person]
)
```

(Translation: "besty" means "best friend")

Note that not everyone necessarily has a besty, so it's optional.

---

# Problem

We want to figure out the age of Thilo's besty.

We suspect his besty is Paul, so if we can't find one in the database we'll use Paul's age.

```scala
val thiloBestyAge: Int = ???
```

---

# Two layers of optionality

There might be no Thilo in the database.

Even if there's a Thilo, he might not have a besty.

Three cases:

- no Thilo (fall back to Paul)


- Thilo and no besty (fall back to Paul)


- Thilo and a besty (use the besty from the db)

Complex logic like this is where `Option` shines and `null` sucks.

---

# Advanced material

This introduces a new concept `flatMap` which is a bit too much to cover today.

We'll just briefly look at the solution and maybe revisit this in a later training.

---

# Solution

```scala
val paul = Person(
  name = "Paul",
  age = 30,
  favouriteEmojiOpt = Some(":zio:"),
  bestyOpt = Some(jonathan)
)

val thiloBestyAge = find("Thilo").flatMap(_.bestyOpt).getOrElse(paul).age
```

If there's a Thilo record in the db like:

```scala
Person(
  name = "Thilo",
  age = 63,
  favouriteEmojiOpt = Some(":no-cookie:"),
  bestyOpt = None
)
```

we'd get Paul's age.

---

# The point of all this

It's to show that `Option` has good tools for handling complex logic.

(Don't worry if you weren't able to keep up with all the details)

So if the type safety argument didn't convince you `Option` is worth it,

hopefully this will.

---

# List search methods that use `Option`

- `find`


- `headOption`

---

# A wrinkle

```scala
val opt = Some(3)
```

What is the type of `opt` above?

---

# A wrinkle

```scala
val opt = Some(3)
```

> What is the type of `opt` above?

`Some[Int]` (not `Option[Int]`)

---

# Understanding this

Option is defined something like this:

```scala
sealed trait Option[A]

case object None extends Option[Nothing]

case class Some[Data](data: Data) extends Option[Data]
```

The important point is that `Some` is a case class.

It's a class in its own right that inherits from `Option`.

---

# Back to our question

Our question:

```scala
case class Some[Data](data: Data) extends Option[Data]

// What is the type of opt?
val opt = Some(3)
```

If you were asked a question like:

```scala
case class Foo(s: String)

// What is the type of x?
val x = Foo("abc")
```

Naturally you'd say `Foo` (and you'd be right!)

The name `opt` is perhaps throwing you off a bit.

---

# Class hierarchy

```
   Any

    |

   ...

    |

  Option[Data]

    |

  Some[Data]
```

So it is an `Option`,

but the type inferer will make it `Some` as that's stronger.

```scala
val opt = Some(3)

// is really
val opt: Some[Int] = Some(3)

// and not
val opt: Option[Int] = Some(3)
```

---

# Why does this matter?

```scala
val opt: Some[Int] = Some(3)
```

Sometimes it can cause confusion for the compiler.

There _might_ be times when you need it to specifically be an `Option`.

A lot of the time it's fine though.

---

# And None?

It's even worse with `None`.

```scala
// What's the type of this?
val optInt = None
```

(It's not going to be `Option[Int]` - we haven't indicated `Int` anywhere)

---

# Solution 1

Add an explicit type annotation:

```scala
val opt1: Option[Int] = Some(3)

val opt2: Option[Int] = None
```

---

# Solution 2

Use the `Option.apply` method:

```scala
val opt = Option(3)
```

internally it does something like:

```scala
def apply[Data](input: Data): Option[Data] = input match {
  case null => None
  case data => Some(data)
}
```

`opt` will get its type from the return type of `apply` (solving our problem).

We also get a bit of `null` safety thrown in to prevent `Some(null)`.

This is good for wrapping values that come from nully java functions:

```scala
val personOpt = Option(dirtyJavaFunctionThatReturnsNull(1, 2, 3))
```

---

# Solution 3

Some FP libraries have built in functions like `none` and `some`,

```scala
// Library
def none[Data]: Option[Data] = None

def some[Data](data: Data): Option[Data] = Some(data)
```

The return types on those functions give us what we need.

```scala
val opt1 = some(3)

val opt2 = none[Int]
```

(Or write it yourself to avoid a dependency - they're pretty simple!)

---

# Summary

- `Option` comes from the standard library


- use it to model data that may or may not exist
    - `Some(data)` when it exists
    - `None` when it doesn't


- it has a generic type parameter representing the data (`Option[Data]`)


- it's far superior to `null` as a strategy
    - more type safe
    - has a rich library of functions for handling complex logic


- the standard library uses `Option` a lot
    - for example `List` uses it for `find` and `headOption`


- in some situations type inference can label your data as `Some/None` and not `Option`
    - there's a few solutions to that

---

# A call to arms

Uncle Zij needs you!

Turn your `null`s into `Option`s.

Join the cause and help us eradicate null pointer exceptions for good!

---

# Further Reading

- Other useful `Option` functions: `foreach`, `filter`, `contains`, `orElse` and others


- using `flatMap` and for comprehensions with `Option`


- functors

---

```
  ____          _        _   _            _
 / ___|___   __| | ___  | | | |_ __   ___| | ___  ___
| |   / _ \ / _` |/ _ \ | | | | '_ \ / __| |/ _ \/ __|
| |__| (_) | (_| |  __/ | |_| | | | | (__| |  __/\__ \ ?
 \____\___/ \__,_|\___|  \___/|_| |_|\___|_|\___||___/
```

Comments/reflections from (non-Zij) Code Uncles?

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __|
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \ ?
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/

  ____                                     _
 / ___|___  _ __ ___  _ __ ___   ___ _ __ | |_ ___
| |   / _ \| '_ ` _ \| '_ ` _ \ / _ \ '_ \| __/ __|
| |__| (_) | | | | | | | | | | |  __/ | | | |_\__ \ !
 \____\___/|_| |_| |_|_| |_| |_|\___|_| |_|\__|___/

```
