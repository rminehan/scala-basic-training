---
author: Rohan
title: Session 2 - The standard library
date: 2021-04-20
---

The:

```
 ____  _                  _               _ 
/ ___|| |_ __ _ _ __   __| | __ _ _ __ __| |
\___ \| __/ _` | '_ \ / _` |/ _` | '__/ _` |
 ___) | || (_| | | | | (_| | (_| | | | (_| |
|____/ \__\__,_|_| |_|\__,_|\__,_|_|  \__,_|
                                            
 _     _ _                          
| |   (_) |__  _ __ __ _ _ __ _   _ 
| |   | | '_ \| '__/ _` | '__| | | |
| |___| | |_) | | | (_| | |  | |_| |
|_____|_|_.__/|_|  \__,_|_|   \__, |
                              |___/ 
```

---

# Reminder

Just yell out if you have questions.

---

# Q: Which one?

- java one


- scala one

---

# Answer?

Both

---

# We'll look at:

Common useful stuff

- primitives


- String (and its extensions)


- tuples


- List and collections in general


- BigInt, BigDecimal

---

```
 ____       _           _ _   _                
|  _ \ _ __(_)_ __ ___ (_) |_(_)_   _____  ___ 
| |_) | '__| | '_ ` _ \| | __| \ \ / / _ \/ __|
|  __/| |  | | | | | | | | |_| |\ V /  __/\__ \
|_|   |_|  |_|_| |_| |_|_|\__|_| \_/ \___||___/
                                               
```

---

# Primitives

All languages have the same basic primitives.

I'll mention some specifics for java/scala and differences to python.

---

# Common primitives

- Int


- Boolean


- Long


- Float


- Double


- Array

---

# Int

32 bits/4 bytes, signed (independent of your OS, hardware)

---

# Overflow

```scala
@ Int.MaxValue 
res0: Int = 2147483647

@ Int.MinValue 
res1: Int = -2147483648
```

Will overflow around 2.1 billion, not good for factorial!

---

# Is it `int` or `Int`?

Java code:

```java
int i = 3;
```

Scala code:

```scala
val i: Int = 3
```

Are they the same?

---

# int vs Int

> Are they the same?

A: Most of the time scala's `Int` is the same as java's `int`.

(Close enough, just think of them as the same thing)

To get started, just think: Scala = Upper case

---

# Float and Double

## Float

32 bits/4 bytes, signed (independent of your OS, hardware)

## Double

64 bits/8 bytes, signed (independent of your OS, hardware)

## Precision

Not used much in the analytics codebase.

Consider `BigDecimal` for higher precision and to avoid overflows.

---

# Array

"Primitive" in the sense that it built into the jvm.

---

# Mutable vs Immutable

Fixed length (can't resize).

Elements can change though.

```scala
@ val a = Array(1, 2, 3) 
a: Array[Int] = Array(1, 2, 3)

@ a(1) = -5 

@ a 
res6: Array[Int] = Array(1, -5, 3)

@ a.append(4) 
cmd3.sc:1: value append is not a member of Array[Int]
val res3 = a.append(4)
             ^
Compilation Failed
```

---

# Type parameter

Arrays have a type parameter:

```scala
@ val bools = Array(true, true, false) 
bools: Array[Boolean] = Array(true, true, false)

@ val floats = Array(1.3f, 2.0f, -3.01f) 
floats: Array[Float] = Array(1.3F, 2.0F, -3.01F)


def gimmeBools(bools: Array[Boolean]): Unit = {
  println(bools)
} 

gimmeBools(floats) 
cmd51.sc:1: type mismatch;
 found   : Array[Float]
 required: Array[Boolean]
val res51 = gimmeBools(floats)
                       ^
Compilation Failed
```

---

# How java thinks about types

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
```

Java uses lowercase to differentiate primitives from reference types.

There are different syntactic rules for them.

---

# Scala doesn't have such a stark difference

One big family

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

Everything extends `Any`

---

# Can be confusing

- Type system concepts


- Runtime concepts

For java, these are mostly the same

For scala, these are quite different

e.g. a scala `Int` might translate to a primitive java `int` at runtime

---

# Confusion

Don't stress about the details just yet.

You don't need to understand these subtle concepts to get stuff done.

More on this in our COMPAT course (coming in 2025).

---

```
 ____  _        _             
/ ___|| |_ _ __(_)_ __   __ _ 
\___ \| __| '__| | '_ \ / _` |
 ___) | |_| |  | | | | | (_| |
|____/ \__|_|  |_|_| |_|\__, |
                        |___/ 
```

---

# String

Good ol' `java.lang.String`.

---

# Common operations

All your java-ry stuff is still there

```scala
val s = "abcdef"

s.length // 6

s.toUpperCase // "ABCDEF"

s.toLowerCase // "abcdef"

s.substring(3) // "def"
```

---

# String

Immutable

```scala
@ val s = "abcdef" 
s: String = "abcdef"

@ s(1) = 'B' 
cmd9.sc:1: value update is not a member of String
val res9 = s(1) = 'B'
           ^
Compilation Failed
```

---

# New stuff?

Scala:

```scala
@ val s = "1234" 

@ s.toInt 
res14: Int = 1234
```

Java:

```java
String s = "1234";

s.toInt(); // Won't compile
```

What is this magic?

---

# Calm the farm

It's still your favourite `java.lang.String`.

It's been _extended_.

The scala standard library defines some useful extensions for common types like `String`.

---

# The gist

Essentially:

```scala
// What you type
s.toInt

// Compiler translates it to something like:
StringOps.toInt(s)
```

"Syntactic sugar"

---

# How is this done?

Using implicits

More on this in a later training

---

# Why I mention it?

Java and Scala start to diverge here.

Recall copy pasting from stack overflow in lesson 1.

Sometimes there's a more idiomatic scala way to do things that doesn't have a java equivalent.

---

# Observation about java and scala

Java is very "literal". What you code is a very close representation of what gets run.

> "Java is high level byte code"

Scala is often not so direct. The compiler is working hard for you.

Enables a powerful syntax but the extra layers can be confusing.

---

```
 _____            _           
|_   _|   _ _ __ | | ___  ___ 
  | || | | | '_ \| |/ _ \/ __|
  | || |_| | |_) | |  __/\__ \
  |_| \__,_| .__/|_|\___||___/
           |_|                
```

Pairs, triplets, quadruplets etc...

---

# Pairs

```scala
// Create a pair of String and Int (name and age)
val enxhell = ("Enxhell", 16)

// With an explicit type annotation
val zij: (String, Int) = ("Zij", 26)
```

---

# Triplets

```scala
// Create a triplet of String, Int and String (name and age and hobby)
val enxhell = ("Enxhell", 16, "Reading academic papers")

// With an explicit type annotation
val zij: (String, Int, String) = ("Zij", 26, "Linguistic fun facts")
```

---

# Quadruplets

Throw in a key value:

```scala
val thilo = ("Thilo", 28, "Reading scala puzzlers", ("nationality", "German"))

val zij: (String, Int, String, (String, String)) = ("Zij", 26, "Linguistic fun facts", ("alias", "Dmitri"))
```

---

# Question for the Uncles

Q: How high can we go?

---

# Pattern matching

Can destructure them these nested structures nicely:

```scala
val zij = ("Zij", 26, "Linguistic fun facts", ("alias", "Dmitri"))

zij match {
  case (_, 26, hobby, (key, value)) => println(s"[Hobby:'$hobby'][$key:'$value']")
  case _ => println("Not a 26 year old")
}
```

Prints:

```
[Hobby:'Linguistic fun facts'][alias:'Dmitri']
```

More on pattern matching in a later training!

---

# Direct access

Can use `_x` notation:

```scala
val zij = ("Zij", 26, "Linguistic fun facts", ("alias", "Dmitri"))

if (zij._2 == 26) {
  println(s"[Hobby:'${zij._3}'][${zij._4._1}:'${zij._4._2}']")
}
else {
  println("Not a 26 year old")
}
```

---

# Compare them

```scala
val zij = ("Zij", 26, "Linguistic fun facts", ("alias", "Dmitri"))

// Pattern Matching
zij match {
  case (_, 26, hobby, (key, value)) => println(s"[Hobby:'$hobby'][$key:'$value']")
  case _ => println("Not a 26 year old")
}

// Underscore syntax
if (zij._2 == 26) {
  println(s"[Hobby:'${zij._3}'][${zij._4._1}:'${zij._4._2}']")
}
else {
  println("Not a 26 year old")
}
```

Which one is easier to read?

---

```
  ____      _ _           _   _                 
 / ___|___ | | | ___  ___| |_(_) ___  _ __  ___ 
| |   / _ \| | |/ _ \/ __| __| |/ _ \| '_ \/ __|
| |__| (_) | | |  __/ (__| |_| | (_) | | | \__ \
 \____\___/|_|_|\___|\___|\__|_|\___/|_| |_|___/
                                                
```

Let's start with List

---

# List

"List" is a very general term.

Means different things in different languages.

Scala's List is a very specific kind of structure, different to the "list" from other languages (e.g. python).

---

# List

Totally immutable

- can't add new elements


- can't change the existing elements

---

# Problem

I have a list of names and I want to add a new one on.

```scala
val developersWhoLoveVim = List(
  "Clement",
  "Rohan",
  "Zij",
  "Valentine",
  "Alan"
) 
```

Want to add "Zack"...

But can't modify the list.

---

# Answer

Construct a new list.

```scala
val updatedDevelopersWhoLoveVim = "Zack" :: developersWhoLoveVim
// List("Zack", "Clement", "Rohan", "Zij", "Valentine", "Alan")
```

Prepended Zack on the front.

(There's a video which explains the odd `::` operator)

---

# Hmmm...

Q: If we're always building new lists, isn't that really inefficient? 

---

# Answer

> Q: If we're always building new lists, isn't that really inefficient? 

A: Not if we're using collections like `List` in the way there were designed for.

(Covered more in that video)

---

# Collections: Mutable vs Immutable

Java: Generally all mutable

Scala: Immutable and Mutable

---

# Scala Packages

Divided into immutable and mutable.

```
scala.collection.immutable
  - List
  - Vector
scala.collection.mutable
  - ListBuffer
  - ArrayBuffer
```

---

# Scala "prefers" immutable ones

You don't have to import anything for immutable ones.

```scala
$ amm

Loading...
Welcome to the Ammonite Repl 1.6.9
(Scala 2.12.8 Java 1.8.0_192)
If you like Ammonite, please support our development at www.patreon.com/lihaoyi

@ List(1, 2, 3) 
res0: List[Int] = List(1, 2, 3)

@ Vector(10, 11, 12) 
res1: Vector[Int] = Vector(10, 11, 12)
```

---

# Mutable ones?

```scala
@ ListBuffer(0, 1, 2) 
cmd2.sc:1: not found: value ListBuffer
val res2 = ListBuffer(0, 1, 2)
           ^
Compilation Failed

@ import scala.collection.mutable.ListBuffer 

@ val buffer = ListBuffer(0, 1, 2) 

@ buffer.append(3) 

@ buffer 
res6: ListBuffer[Int] = ListBuffer(0, 1, 2, 3)
```

---

# General principle

> If you want developers to do the right thing, make that the easiest thing

By not having to import immutable collections, lazy developers are more likely to use them.

In this way scala nudges you towards immutable collections.

---

# But why?

> What's so great about immutable collections?

Avoiding side effects and mutations has a lot of benefits.

Crucial concept to FP.

More on that another time.

---

# Another point of divergence:

> Java: Generally all mutable
> 
> Scala: Immutable and Mutable (prefers immutable)

Idiomatic scala will use immutable concepts more.

---

```
 ____  _             _          __  __ 
| __ )(_) __ _   ___| |_ _   _ / _|/ _|
|  _ \| |/ _` | / __| __| | | | |_| |_ 
| |_) | | (_| | \__ \ |_| |_| |  _|  _|
|____/|_|\__, | |___/\__|\__,_|_| |_|  
         |___/                         
```

`BigInt`, `BigDecimal`

---

# BigInt: Motivation

`Int` and `Long` use a fixed number of bytes (4 and 8 respectively).

This bounds the size you can get from them, e.g. Int's go up to roughly 2 billion.

For counting truly massive numbers, we need an integral type that can grow dynamically.

This is `BigInt`.

---

# Trivia

Q: What is the space complexity of factorial (tail rec solution)?

---

# Trivia

A: We used tail recursion to get rid of the stack frames, 

but the return value itself takes us space.

```scala
def fac(n: Int): XXX = ...
```

---

# Fixed width

e.g. Int, Long

Constant memory.

And it will overflow very very quickly.

So whilst it's constant memory, it's not a useful function.

---

# Dynamic width

e.g. BigInt

How many bytes do you need to represent fac(n)?

k bits can represent roughly up to 2^k

Flipping it around, n!=2^k can be represented by k bits.

So roughly log2(n!)

---

# Python?

(I think) python uses dynamic width.

---

# Aside

We've only ever had one candidate mention this during our interviews.

---

# BigInt in action

```scala
val twoBillion = 2000000000 

twoBillion * twoBillion 
// -1651507200

val twoBillionBigInt = BigInt(twoBillion) 

twoBillionBigInt * twoBillionBigInt 
// 4000000000000000000
```

---

# BigDecimal

Floating point equivalent of BigInt.

Not just useful for big values, but also _precise_ values.

---

# Precision

Floating point numbers have "gaps".

A 4 byte float can only represent 2^32 possible values.

But there are an infinite number of values, e.g. just between 0 and 1.

---

# Precision

Floating point numbers are very sparse.

If you closed your eyes and threw a dart at the number line,

technically you have 0% chance of hitting a number covered by `Float`.

---

# Example

```scala
var total = 0f 

for (i <- 0 until 200) total += 0.1f 

println(total)
// 20.00004F
```

Numbers like `0.1f` are not perfectly represented.

e.g. `0.1f` is not really 1/10 (even when it prints as "0.1f")

If these were currency values, you'd be creating money!

---

# BigDecimal version

```scala
var total = BigDecimal("0") 

for (i <- 0 until 200) total += BigDecimal("0.1") 

println(total)
// 20.0
```

Note the use of strings.

---

# Why use Strings?

```scala
var total = BigDecimal("0") 

for (i <- 0 until 200) total += BigDecimal(0.1f) 

println(total)
// 20.00000029802322400
```

The issue is `BigDecimal(0.1f)`.

0.1 is first being represented as a float, then converted to a BigDecimal.

But 0.1 doesn't have a representation as a Float,
so the error has already snuck in before introducing `BigDecimal`.

---

# Should we always use BigDecimal?

It doesn't always matter.

For example an analytics report might report the average cost per lead:

```scala
println(totalCost.toFloat/numLeads)
```

There's no cumulative error here and we only care about it to 2.d.p anyway.

---

```
 ____                                             
/ ___| _   _ _ __ ___  _ __ ___   __ _ _ __ _   _ 
\___ \| | | | '_ ` _ \| '_ ` _ \ / _` | '__| | | |
 ___) | |_| | | | | | | | | | | | (_| | |  | |_| |
|____/ \__,_|_| |_| |_|_| |_| |_|\__,_|_|   \__, |
                                            |___/ 
```

---

# Summary

- primitives (tend to be fixed width)


- String (java string, immutable, many extensions on it)


- tuples (easy to build and pattern match)


- colletions (immutable and mutable)


- Big numbers (unbounded equivalents of their primitives)

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

                      ?
                                                   
```
