---
author: Rohan
title: Session 6 - the type system
date: 2021-05-04
---

```
 _____ _
|_   _| |__   ___
  | | | '_ \ / _ \
  | | | | | |  __/
  |_| |_| |_|\___|

 _____
|_   _|   _ _ __   ___
  | || | | | '_ \ / _ \
  | || |_| | |_) |  __/
  |_| \__, | .__/ \___|
      |___/|_|
 ____            _
/ ___| _   _ ___| |_ ___ _ __ ___
\___ \| | | / __| __/ _ \ '_ ` _ \
 ___) | |_| \__ \ ||  __/ | | | | |
|____/ \__, |___/\__\___|_| |_| |_|
       |___/
```

A look at some of the quirkier scala-esque aspects of the type system.

---

# Related talk

MISC-01 covers similar concepts in more detail.

---

# Agenda

- Unit


- Any


- Nothing

---

# Recall

- cousins: scala and java


- concepts: compile time vs runtime


- scala has compile time concepts that get compiled away


---

```
 _   _       _ _
| | | |_ __ (_) |_
| | | | '_ \| | __|
| |_| | | | | | |_
 \___/|_| |_|_|\__|
```

---

# Expressions vs Statements

Expression: produce something

Statement: do something

---

# Expression

"Produce something"

- `1 + 3`


- `"abc" + "def"`


- `func(input)`

---

# Statement

"Do something"

- `println("Hello world")`


- `db.insert(person)`

---

# Statement

"Do something"

Really means a side effect.

e.g. writing to standard output, inserting into a database

---

# Java vs Scala

## Java

Mix of statements and expressions.

`if` is typically a statement.

Functions can return `void`.

## Scala

_Everything_ is an expression.

---

# Example: If as an expression

We want to set the variable to "Dodgy" if country is Albania, otherwise "Okay".

## Java

```java
String status;

if (country == "Albania") {
  status = "Dodgy"
}
else {
  status = "Okay"
}
```

## Scala

```scala
val status = if (country == "Albania") "Dodgy" else "Okay"
```

## Comparing

`if` statements aren't expressions in java, you can only use them as statements.

Hence we used a side effect.

---

# Assignment

Expressions can be assigned to a variable.

Hence:

> "everything is an expression"

means

> "everything can be assigned to a variable"

```scala
val status = if (country == "Albania") "Dodgy" else "Okay"
```

---

# Question...

`println` comes from the java standard library and is `void`.

It would look something like:

```java
public void println(String message) {
  ...
}
```

Then what is the type of `x` below?

```scala
val x = println("Hello world")
```

---

# Scala doesn't have `void`

`void` is the idea of something _not_ producing a result.

In scala everything is an expression, so `void` doesn't make sense.

---

# Unit

Q: Then what is the type of `x` below?

```scala
val x = println("Hello world")
```

A: `Unit`

---

# Unit

A compile time concept.

The expression equivalent of `void`.

---

# Compile time vs runtime

We talk about functions that "return Unit".

That is compile time talk.

Usually that will get compiled away to a function that is `void` on the JVM.

---

# Value?

Q: If `Unit` is a type, does it have a value?

For example I might want to pass an instance of `Unit` to something.

---

# Try making one

```scala
new Unit
// class Unit is abstract; cannot be instantiated
```

---

# The Unit

A: `()` ("The" Unit)

---

# Example

```scala
def demo(): Unit = {
  println("Hello world")
  ()
}

List(1, 2, 3).map(_ => ())
```

Functions explicitly using "the unit".

---

# Singleton

The unit `()` is the only instance of `Unit`. You can't create your own.

Because the type has only one instance,

we tend to talk about the type and value interchangeably.

---

# Unit insertion

The last expression in a function block is what gets returned.

This won't compile because `4` isn't a `String`.

```scala
def gimmeString: String = {
  4
}
```

---

# What about this then?

```scala
def gimmeString: String = {
  4
}

// How about this guy? 4 isn't a Unit either
def gimmeUnit: Unit = {
  4
}
```

---

# Turns out it does compile

The compiler has some special rules for unit:

```scala
def gimmeUnit: Unit = {
  4
  ()   // <--- as if the compiler puts this in
}
```

This makes it behave the same as the analogous java function:

```java
public void gimmeVoid() {
  4
}
```

---

# Recapping Unit

In scala, everything is an expression.

The jvm has concepts like `void` which aren't true expressions.

Unit is the bridge for making statements feel like expressions.

It's a type system concept that will usually get compiled away to `void`.

---

```
    _                
   / \   _ __  _   _ 
  / _ \ | '_ \| | | |
 / ___ \| | | | |_| |
/_/   \_\_| |_|\__, |
               |___/ 
```

---

# Recap

We've see `Any` already from lesson 1:

```
                Java's World View

----------------------------------------------------------
      Primitives        |     Reference Types (Objects)
----------------------------------------------------------
                        |
        int             |         String
                        |
        float           |         MyClass
                        |
        boolean[]       |         ZonedDateTime
                        |
----------------------------------------------------------


                Scala's World View

----------------------------------------------------------
                      Any
                    /     \
             AnyVal         AnyRef
              / \          /      \
            Int Boolean  String   ZonedDateTime
----------------------------------------------------------
```

---

# In java

All non-primitives (aka reference types) inherit from `Object`.

```java
class Foo {
}

// is really
class Foo extends Object {
}
```

`Object` is the base type for all of them.

---

# So is Any just scala's version of Object?

No, because in scala everything is an `Any` (even primitives).

(Hence it's name `Any`).

---

# Any vs Object

`Any` is weaker or more general than `Object`

---

# Any <- Object

You can set an `Any` using an `Object`:

```scala
val o: Object = "abc" 

// Compiles
val a: Any = o 
```

---

# Object <- Any

You can't set an `Object` using an `Any`:

```scala
val a: Any = 1 

val o: Object = a 
// type mismatch;
// found   : Any
// required: Object
// val o: Object = a
//                 ^
```

---

# The point

Even if you don't get all the details,

the main point is:

```
       _                
      / \   _ __  _   _ 
     / _ \ | '_ \| | | |
    / ___ \| | | | |_| |
   /_/   \_\_| |_|\__, |
                  |___/ 
           _       
          | |_____ 
          | |_____|
          |_|_____|
          (_)      
            
    ___  _     _           _   
   / _ \| |__ (_) ___  ___| |_ 
  | | | | '_ \| |/ _ \/ __| __|
  | |_| | |_) | |  __/ (__| |_ 
   \___/|_.__// |\___|\___|\__|
            |__/               
```

(Any != Object)

Any is weaker.

---

# Object is more like AnyRef

```
                Scala's World View

----------------------------------------------------------
                      Any
                    /     \
             AnyVal         AnyRef
              / \          /      \
            Int Boolean  String   ZonedDateTime
----------------------------------------------------------
```

Compiles both ways

```scala
val o: Object = "abc" 

val ar: AnyRef = o 

val o2: Object = ar 
```

---

# Recapping Any

- it's the "top" type that unifies all types


- it doesn't have an equivalent in java as java separates primitives and reference types

---

```
 _   _       _   _     _             
| \ | | ___ | |_| |__ (_)_ __   __ _ 
|  \| |/ _ \| __| '_ \| | '_ \ / _` |
| |\  | (_) | |_| | | | | | | | (_| |
|_| \_|\___/ \__|_| |_|_|_| |_|\__, |
                               |___/ 
```

---

# Recall

Everything in scala is an expression.

ie. everything can be assigned back to a variable.

That variable will always have a type.

---

# What about exceptions

```scala
val x = throw new IllegalArgumentException("No Bobans!")
```

Throwing an exception is an expression.

We can assign it to a variable.

Q: What should we make the type of `x` here?

---

# Nothing

Q: What is the type of `x` here?

A: We _could_ use `Unit`, but there is a more appropriate type: `Nothing`

---

# Scala and Java

`Nothing` is scala's way of representing code paths that will never gracefully terminate.

---

# Quirky Nothing

- "uninhabited" ie. no instances of Nothing (hence its name)


- bottom type (opposite of `Any`)

---

# Uninhabited

You can't create an instance of `Nothing`.

Q: But what about this:

```scala
val x: Nothing = throw new IllegalArgumentException("No Bobans!")
```

(Not in the repl)

---

# Uninhabited

```scala
val x: Nothing = throw new IllegalArgumentException("No Bobans!")
```

This will _compile_.

At runtime `x` will never be set with a value though.

The execution will exit with an exception before that happens.

---

# You can make your own uninhabited types

Make a file with _just_ this:

```scala
sealed trait Foo

def gimmeFoo(foo: Foo): Unit = {
  println(foo)
}
```

This will compile but you'll never be able to instantiate a real `Foo` and pass it to the function.

(You can cheat and use `null` though)

---

# Bottom type

Everything inherits from `Any`.

`Nothing` inherits from everything.

```
                Scala's World View

----------------------------------------------------------
                      Any
                    /     \
             AnyVal         AnyRef
              / \          /      \
            Int Boolean  String   ZonedDateTime
               \    \       /    /

                    Nothing
----------------------------------------------------------
```

---

# Compile time vs Run time

`Nothing` is most likely just a compile concept.

When the code is compiled it will get boiled away.

For example the JVM wouldn't allow a type that inherits from every other type.

---

# Aside: Bottom types are useful

When you learn about variance this will make sense.

For example `Nil` (the empty list) is a `List[Nothing]`.

This makes it inherit from `List[A]` for all `A`.

```scala
def gimmeInts(input: List[Int]): Unit = ...
def gimmeDoubles(input: List[Double]): Unit = ...
def gimmeStrings(input: List[String]): Unit = ...

// All of these compile
gimmeInts(Nil)
gimmeDoubles(Nil)
gimmeStrings(Nil)
```

`Nil` can be used where any type of list is expected.

This is the kind of clever type trickery scala uses.

---

# Recapping Nothing

- everything is an expression and has a type


- we use `Nothing` as the type for code paths that can't yield a value (e.g. throwing an exception)


- `Nothing` is uninhabited


- `Nothing` inherits from everything

---

# Summary for today

- looked at quirky scala types: Unit, Any, Nothing


- hopefully starting to get a feel for the layer scala puts on top of java


- there often isn't a direct translation between scala compile time concepts and JVM concepts


- how this translation happens can be very complex, see COMPAT course (eta 2030)

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

