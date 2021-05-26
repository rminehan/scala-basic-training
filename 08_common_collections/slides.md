---
author: Wohan/Rilly
date: 2021-05-25
title: Common Collections
---

```
  ____                                      
 / ___|___  _ __ ___  _ __ ___   ___  _ __  
| |   / _ \| '_ ` _ \| '_ ` _ \ / _ \| '_ \ 
| |__| (_) | | | | | | | | | | | (_) | | | |
 \____\___/|_| |_| |_|_| |_| |_|\___/|_| |_|
                                            
  ____      _ _           _   _                 
 / ___|___ | | | ___  ___| |_(_) ___  _ __  ___ 
| |   / _ \| | |/ _ \/ __| __| |/ _ \| '_ \/ __|
| |__| (_) | | |  __/ (__| |_| | (_) | | | \__ \
 \____\___/|_|_|\___|\___|\__|_|\___/|_| |_|___/
                                                
```

Collections = Map, Set, List, Array, etc...

---

# Why are we looking at this

Collections are used everywhere in your code.

Each collection is designed to be used a particular way.

Using the way they were intended will avoid:

- messy code


- slow code


- unsafe code (e.g. concurrency bugs)

---

# Overview

We'll look through the collections from the scala standard library.

It will be ad-hoc bits and pieces, not a comprehensive treatment.

We'll meander around through different ideas then summarize it in a more

structured "Singaporean" way at the end.

---

# Clarifications

- _scala_ standard library (not java)


- scala 2.13 (not 2.12)

Might cause some questions...

---

# Questions

Scala/Java

> Why does scala have it's own collections library?
>
> Why not use the java one?

Scala 2.12 vs 2.13?

> What's the difference?

---

# Scala/Java

> Why does scala have it's own collections library?
>
> Why not use the java one?

Java has collections under the `java.util` package, e.g. `java.util.List`.

As a scala developer you almost never use these. Why?

Here's a few reasons:

- they are _mutable_


- they don't fit the idiomatic way scala developers work
    - e.g. using `null` instead of `Option`


- they don't have the rich api of scala collections

---

# Mutable

Below we create an empty java linked list and append to it twice:

```scala
import java.util.LinkedList

val javaList = new LinkedList[Int] 

javaList.add(0) 
javaList.add(1) 
```

---

# Why is mutable bad?

Doesn't fit with the scala mindset.

Leads to race conditions, inefficient locking strategies etc...

---

# Rich api

Where is `map`, `filter`, `find` etc... ?

As a scala user, you quickly get addicted to these things,

and can't imagine using collections without them.

---

# Scala 2.12 vs 2.13

Every few years scala has a major update:

- 2.10
- 2.11
- 2.12 (analytics team is here)
- 2.13 (latest stable)
- 2.14 (coming soon)
- 3

Updates include:

- additions to the language
- removal of some old features
- performance improvements to the standard library and compiler

---

# Scala collections historically

From 2.12 backwards, scala collections have been internally quite confusing and problematic.

2.13 did a big clean up of the collections library.
From the [release notes](https://github.com/scala/scala/releases/tag/v2.13.0):

> Standard library collections have been overhauled for simplicity, performance, and safety.
>
> This is the centerpiece of the release.

---

# Scala 2.12 vs 2.13

> But the analytics team is still on 2.12?
>
> Why are we learning about 2.13?

We're hoping to move to 2.13 in the next couple of months.

So it makes sense to teach you about the collections you're most likely to use longer term.

---

# Scala 2.12 vs 2.13

> Will the differences matter?

In most cases no, you won't even be aware of it.

A lot of the cleanup is internal design and refactoring.

As an end user most things will be the same.

---

# Ammonite

So for today, we'll be using an ammonite for scala 2.13.

If you want both installed simultaneously, just install them and rename them, e.g. `amm12` and `amm13`.

The binary should be at `/usr/local/bin/amm`.

```bash
cd /usr/local/bin
sudo mv amm amm12
```

---

# Immutable vs Mutable

Scala still provides mutable collections, but you have to import them explicitly.

Some immutable collections like `List` and `Vector` don't need importing.

We saw earlier this was scala's way of nudging you towards them.

---

# Immutable vs Mutable

Packaging:

- `scala.collection.mutable`


- `scala.collection.immutable`

Usage examples:

```scala
// Have to explicitly import mutable ones
import scala.collection.mutable.ListBuffer 
ListBuffer(1, 2, 3)

// No import needed
List(1, 2, 3) 
// equivalent to this
scala.collection.immutable.List(1, 2, 3)
```

---

# Scala terminology: Seq (the sequence)

`Seq`:

> A collection with a deterministic concept of ordering

ie. a concept like "Element at index i" makes sense.

---

# Building intuition

Which of these have a deterministic concept of ordering?

```scala
val set = Set(0, 1, 2, 3, 4)

val list = List(0, 1, 2, 3, 4)

val map = Map("Zij" -> 0, "Willy" -> 1, "Zack" -> 2)

val array = Array(0, 1, 2, 3, 4)
```

---

# Building intuition

> Which of these have a deterministic concept of ordering?

```scala
val set = Set(0, 1, 2, 3, 4)

val list = List(0, 1, 2, 3, 4)

val map = Map("Zij" -> 0, "Willy" -> 1, "Zack" -> 2)

val array = Array(0, 1, 2, 3, 4)
```

`Set` and `Map` _don't_.

`List` and `Array` do.

---

# Set

```scala
@ Set(0, 1, 2, 3, 4, 5).foreach(println) 
0
5
1
2
3
4

@ Set(0, 1, 2, 3, 4, 5, 6).foreach(println) 
0
5
1
6
2
3
4
```

You can't make any assumptions about the order you'll traverse elements.

It comes down to internal implementation details for how the set hashes things.

Might change in a new version of the library.

`Map` is similar.

---

# List and Array

> a concept like "Element at index i" makes sense.

`List(8, 4, 1)` has a deterministic concept of elements at a particular index:

- index 0: 8


- index 1: 4


- index 2: 1

This will always print the same way:

```scala
@ List(8, 4, 1).foreach(println) 
8
4
1
```

---

# Seq

Seq is our abstraction for collections with this deterministic ordering.

```scala
trait Seq[A] {
  ...
}
```

(`trait` is like java's `interface`)

Examples of concrete implementations:

- `List`


- `Vector`


- `ArraySeq`

---

# Abstract and Concrete

```scala
def printSeq(seq: Seq[Int]): Unit = {
  println("---------------------")
  println(s"Seq has length: ${seq.length}")
  println("Seq has elements:")
  seq.foreach(println)
}

printSeq(List(0, 1, 2, 3))
printSeq(Vector(0, 1, 2, 3))

// Set tries to sneak in...
printSeq(Set(0, 1, 2, 3))
```

Compiler error for Set:

```
type mismatch;
 found   : scala.collection.immutable.Set[Int]
 required: Seq[Int]
val res23 = printSeq(Set(0, 1, 2, 3))
                        ^
```

---

# Seq.apply

You can create a `Seq` using its `apply` function:

```scala
val seq = Seq(0, 1, 2, 3) 
// seq: Seq[Int] = List(0, 1, 2, 3)
```

Observations:

- compile time type is `Seq[Int]` (which is just an abstraction)


- a "concrete" implementation gets built (`List` in this case)

---

# Subtle bug

There's a problem with this function:

```scala
def printSeq(seq: Seq[Int]): Unit = {
  println("---------------------")
  println(s"Seq has length: ${seq.length}")
  println("Seq has elements:")
  seq.foreach(println)
}
```

Code Uncles: Any guesses?

---

# Subtle bug

> There's a problem with this function:

```scala
def printSeq(seq: Seq[Int]): Unit = {
  println("---------------------")
  println(s"Seq has length: ${seq.length}")
  println("Seq has elements:")
  seq.foreach(println)
}
```

It assumes our `Seq` has a length (ie. finite).

Does a sequence have to be finite and have a length?

---

# Essence of Seq

> Does a sequence have to be finite and have a length?

No.

It just needs a deterministic concept of the "i'th" element.

---

# Mathematical sequence

At school maybe you learnt about mathematical sequences, e.g.

```
0, 1, 2, 3, 4, ...
```

The above sequence:

- is infinite


- has a deterministic concept of the i'th element (in this case it's i)

---

# Implementing this in code

We can use a `LazyList` to model infinite sequences.

They are a lazy structure that can be potentially infinite.

(Compatibility note: `LazyList`'s were added in 2.13 to replace `Stream`'s from 2.12)

---

# Aside: LazyList example

This function recursively generates a lazy list of numbers where the i'th number is i:

```scala
@ def infinite(start: Int): LazyList[Int] = start #:: infinite(start + 1)

@ val list = infinite(start = 0)

// Print the first 5 elements
@ list.take(5).foreach(println) 
0
1
2
3
4
```

The `#::` operator is similar to regular `List`'s prepend operator.

---

# Memory

Q: How do you fit an infinite list into memory?

---

# Memory

> Q: How do you fit an infinite list into memory?

A: You don't.

You define how it works, but only evaluate deeper into it on demand.

It's "lazy".

```scala
// Evaluate the first 1000 elements
list.take(1000).foreach(println)
```

We evaluated the first 1000 elements into the list.

Under the hood it will have cached those.

---

# Back to Seq's

Is `LazyList` a `Seq`?

ie. does it have a deterministic concept of the i'th element?

---

# Back to Seq's

> Is `LazyList` a `Seq`?
>
> ie. does it have a deterministic concept of the i'th element?

In our example yes.

The i'th element was i.

---

# Does `LazyList` have a length?

Nope.

Run `list.length` in the repl and you'll see it goes into an infinite loop.

It will keep unravelling the list trying to get to the end to find the length.

---

# The point

Don't worry if you didn't understand the details of `LazyList`.

The point was just to show that a `Seq` doesn't have to be finite.

It just needs a deterministic concept of an i'th element.

---

# Summing that up

`Seq` is very abstract.

It could represent any kind of collection that has a concept of deterministic ordering.

Don't assume it has a length.

---

# Abstract or Concrete

We made our function accept a very abstract type.

```scala
def printSeq(seq: Seq[Int]): Unit = {
  println("---------------------")
  // println(s"Seq has length: ${seq.length}")
  println("Seq has elements:")
  seq.foreach(println)
}
```

That allowed us to pass more types of collections to it.

But it means we can't make as many assumptions:

- we don't know if it's finite/infinite


- we don't know its performance characteristics and memory usage

With collections, it's often better to use a more concrete type.

---

# Tension

Generally coding against abstractions is good.

Allows you to reuse your code in more scenarios.

But with collections, you can get performance problems or bugs.

---

# General rule

Generally speaking it's better to use more concrete collection types.

Easier to reason about them and make assumptions.

The standard library makes it fairly easy to convert between them.

---

# Zooming out

We've got a general feeling for what is and what isn't a `Seq`.

The standard library of collections is broken up into 3 main families of collections:

- `Seq`


- `Map` (dictionaries)


- `Set`

These are the 3 big abstractions with many concrete implementations under them.

The most interesting one is `Seq` and it's what we'll talk about today.

---

# Array?

Q: Is `Array` a `Seq`?

---

# Array?

> Q: Is `Array` a `Seq`?

Hmmm...

It's certainly got a deterministic concept of ordering.

Code Uncles: What issues might we have making it a `Seq`?

---

# Array

> What issues might we have making it a `Seq`?

Hmmm...

A couple spring to mind:

- it was created before the scala standard library


- it's kind of mutable

---

# Java Array

To be a `Seq`, a type needs to `extend` it, e.g.

```scala
sealed trait List[A] extends Seq[A]
//                   ^^^^^^^^^^^^^^
```

Hardcoded at the source level.

`Seq` comes from the scala standard library.

`Array` comes from the java standard library.

---

# Asking the Oracle team to change Array

> Dear Oracle Team,
>
> Can you please change the source code for Array
> so that Array extends Seq?
>
> Yours sincerely
>
> Scala Community

Response:

> Dear Scala community,
>
> No

---

# Aside: Common issue

A weakness of inheritance based systems:

> You can't make old classes extend new interfaces/traits

It's hard-coded at the source level.

Type classes are a much more powerful pattern (not for today).

---

# Mutability issue

Array is only partially immutable.

The length can't be changed.

But the elements inside _can_ be changed.

```scala
@ val array = Array(0, 1, 2, 3) 

@ array.append(4) 
// value append is not a member of Array[Int]

@ array(0) = 20 

@ array 
Array(20, 1, 2, 3)
```

---

# What is a `Seq`?

But does something have to be immutable to be a `Seq`?

Our definition was just that it needs a deterministic concept of the i'th element.

---

# Two `Seq`'s

> Does something have to be immutable to be a `Seq`?

No.

Turns out there's two `Seq`'s:

- `scala.collection.immutable.Seq`


- `scala.collection.mutable.Seq`

---

# Two `Seq`'s

Q: So which one is the default?

For example when I use `Seq` without the full package name,

which one are we getting?

```scala
val seq = Seq(0, 1, 2)
```

What type is inferred for `seq`?

---

# One way to check

Just try to mutate it.

---

# Summary

```scala
@ val mutableSeq = scala.collection.mutable.Seq(0, 1, 2) 

@ mutableSeq(0) = 100 

@ mutableSeq 
// ArrayBuffer(100, 1, 2)

@ val immutableSeq = scala.collection.immutable.Seq(0, 1, 2) 

@ immutableSeq(0) = 100 
// value update is not a member of Seq[Int]
// did you mean updated?
// val res12 = immutableSeq(0) = 100
//             ^

@ immutableSeq 
// List(0, 1, 2) (unchanged)

@ val mysterySeq = Seq(0, 1, 2) 

@ mysterySeq(0) = 100 
// value update is not a member of Seq[Int]
// did you mean updated?
// val res14 = mysterySeq(0) = 100
//             ^
```

So it's the immutable one.

---

# Another way to check

Look at the source code!

[2.13.2 source](https://github.com/scala/scala/blob/v2.13.3/src/library/scala/package.scala#L19):

```scala
type Seq[+A] = scala.collection.immutable.Seq[A]
```

This is an example of a "type alias".

Basically a shorthand name for something.

---

# Functional bias

It makes sense that `Seq` is the immutable one.

Scala will nudge us towards immutable collections.

---

# Back to Array

Our instinct would be that `Array` would be closest to a mutable `Seq`.

---

# Try it out

Make a function that takes a mutable `Seq` and see if we can pass `Array` to it:

```scala
def eatMutableSeq(seq: scala.collection.mutable.Seq[Int]): Unit = {
  println(seq)
}

eatMutableSeq(Array(0, 1, 2))

// What happens?
```

To the repl!

---

# Results

Oddly it worked.

You say:

> But I thought an Array can't be a Seq?

---

# Conversion

> But I thought an Array can't be a Seq?

It's not.

Look at the output closely:

```scala
@ eatMutableSeq(Array(0, 1, 2)) 
// ArraySeq(0, 1, 2)
```

The function printed the input, and we can see it printed an `ArraySeq`.

---

# Try it with an immutable one too

```scala
def eatImmutableSeq(seq: Seq[Int]): Unit = {
  println(seq)
}

eatImmutableSeq(Array(0, 1, 2))

// What happens?
```

---

# Results

Also worked.

```scala
@ eatImmutableSeq(Array(0, 1, 2)) 
// ArraySeq(0, 1, 2)
```

---

# What's going on?

Anyone want to guess what's happening?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Implicit conversion

Something like this:

```scala
// What you type
eatMutableSeq(Array(0, 1, 2))

// What's really happening
eatMutableSeq(array2MutableSeq(Array(0, 1, 2)))
```

(It won't be called `array2MutableSeq` - I just made that up)

`ArraySeq` is from the standard library (mutable and immutable versions).

Your array gets converted to this implicitly. 

---

# Is Array a Seq

Not technically.

It's just plain old java array.

But you can use it in places where both mutable and immutable `Seq`'s are expected.

So it _feels_ like a `Seq`.

---

# Other common immutable `Seq`'s

- List


- Vector


- Range


- LazyList

---

# Motivating Vector

We can't modify immutable collections,

we can only make copies.

---

# Example: inserting into the middle

```
             3

             |

Seq(0, 1, 2,   4, 5, 6)
```

If it were a list, it would be inefficient.

You can only build lists be preprending.

---

# List

We'd have to rebuild from the list starting at 4:

```
                    4 :: 5 :: 6 :: Nil (list starting at 4)
               3 :: 4 :: 5 :: 6 :: Nil (prepend 3 to it)
          2 :: 3 :: 4 :: 5 :: 6 :: Nil (prepend 2 to that)
     1 :: 2 :: 3 :: 4 :: 5 :: 6 :: Nil ...
0 :: 1 :: 2 :: 3 :: 4 :: 5 :: 6 :: Nil
```

There ended up being 4 steps: 0, 1, 2, 3

---

# List insertion

Generally speaking, inserting into `List` is k steps,

where k is the index you're inserting into.

---

# List insertion

Worst case k = length of the list (call it `n`).

So we'd say:

> inserting into a list is O(n)

---

# List insertion is inefficient

If you were doing lots of inserts this would be very inefficient.

A better structure might be `Vector`.

---

# Vector

Uses a flat tree structure.

Each node has up to 32 children:

```
                      root
           /     /     |     \      \  (32 branches)
        child        child         child
        //|\\        //|\\         //|\\
         ...          ...           ...
```

(Not suitable for ascii diagrams!)

With up to 32 children per node, you end up with a very flat wide tree.

---

# Why tree?

Why is a tree useful?

It's easy to make copies of the vector that reuse most of it.

Back to our problem 

```
BEFORE         root                             3
           /         \             
        node        node                        |
        /  \        /  \           
       0    node   4   node        Seq(0, 1, 2,   4, 5, 6)
            / \        / \
           1   2       5  6

           -----------------

AFTER          root
           /    |    \
        node    3   node
        /  \        /  \
       0    node   4   node
            / \        / \
           1   2       5  6
```

Here we would just have to rebuild the root node.

We can reuse most of the other structures.

---

# Structure reuse

Similar to this:

```scala
val tail = List(1, 2, 3)

val list1 = 0 :: tail

val list2 = 100 :: tail
```

Both `list1` and `list` are reusing the same `tail` structure.

```
list1 0
       \____ tail
       /
list2 100
```

Their tail pointers point to the same object.

---

# Vector

My explanation was quick and dirty and not totally accurate.

---

# Vector

Don't worry if you don't get it.

Realistically we barely use it.

That would require:

- an insert heavy workload


- a big enough list to justify it

Rarely happens.

---

# Ranges

```scala
val range1 = 0 until 100

val range2 = 0 to 100
```

Code Uncles: What's the difference?

---

# Ranges

```scala
val range1 = 0 until 100

val range2 = 0 to 100
```

> What's the difference?

`until` is exclusive: 0, 1, ..., 99  (100 elements)

`to` is inclusive: 0, 1, ..., 100 (101 elements)

---

# C++ loops

```scala
0 until 100
```

is analogous to the typical C++ for loop:

```c++
for (int i = 0; i < 100; i++) {
  ...
}
```

(note the `<` and not `<=`)

---

# Changing the step

```scala
val by5Exclusive = 0 until 100 by 5

// Range(0, 5, 10, ..., 95)

val by5Inclusive = 0 to 100 by 5

// Range(0, 5, 10, ..., 95, 100)
```

---

# Space complexity

Code Uncles: How much space does `0 until n` take?

---

# Space complexity

> How much space does `0 until n` take?

Constant.

From the [source code](https://github.com/scala/scala/blob/v2.13.3/src/library/scala/collection/immutable/Range.scala#L59):

```scala
sealed abstract class Range(
  val start: Int,
  val end: Int,
  val step: Int
)
```

Under the hood `0 to 100` would translate to say `Range(start = 0, end = 100, step = 1)`

Thankfully it's not generating all the numbers from 0 to 100.

The only data being kept for the range itself is those values.

---

# Traversal?

Q: How do we actually get those intermediate numbers then?

When you traverse a range, it generates an `Iterator` that walks through the numbers.

Iterators are a separate concept we'll cover in a later training.

---

# Back to space complexity

> Thankfully it's not generating all the numbers from 0 to 100.

You say:

> But Willy you rogue, in ammonite, it prints all the intermediate values

```scala
@ 0 until 10 
res10: Range = Range(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
```

That is the ammonite repl.

It sees that a `Seq` is being created and invokes special printing logic to print each element.

It does the same thing with lazy lists (can be annoying for demos!)

---

# Range.toString

To see how the author wanted it to print, print it directly like so:

```scala
@ println(0 until 10) 
// Range 0 until 10
```

Under the hood this is using the
[toString](https://github.com/scala/scala/blob/d23424cd3aa17f7ad95b81018c70ceed2566962b/src/library/scala/collection/immutable/Range.scala#L479)
method:

```scala
final override def toString: String = {
  val preposition = if (isInclusive) "to" else "until"
  val stepped = if (step == 1) "" else s" by $step"
  val prefix = if (isEmpty) "empty " else if (!isExact) "inexact " else ""
  s"${prefix}Range $start $preposition $end$stepped"
}
```

We could have just called `toString` on it and had ammonite print the string for us:

```scala
@ (0 until 10).toString 
// "Range 0 until 10"
```

---

# Funny syntax?

Maybe you're thinking:

> "0 until 10" looks like funny syntax.
>
> Does scala have special syntax for ranges?

---

# Funny syntax?

> "0 until 10" looks like funny syntax.
>
> Does scala have special syntax for ranges?

No.

It's just a combination of a few scala tricks:

```scala
0 until 10

// really

0.until(10)

// which is really something like
rangeThingy(0).until(10)
```

---

# Messing around with ranges

Might as well solve a few puzzles.

Generate a `Seq` of all the numbers:

- from 0 to 99


- can't be between 50 and 60 inclusive


- must be even

---

# Solution

```scala
(0 until 100 by 2).filter(i => i < 50 || i > 60) 
```

generated output:

```
Vector(
  0,
  2,
  4,
  6,
  ...
  44,
  46,
  48,
  62, <---- skips 50-60
  64,
  66,
  68,
  70,
  72,
  ...
  98
)
```

Note it ended up being a `Vector`.

Once we filtered it, it couldn't stay as a `Range`.

You can't represent a complex seq like this with "start, end, step".

---

# Messing around with ranges

Generate the squares of the numbers from 30 to 40 inclusive

(but not 37, Rohan hates that guy),

and produce them biggest to smallest, e.g. 40^2, 39^2, ...

---

# Solution

```scala
(40 to 30 by -1).filter(_ != 37).map(i => i * i) 
```

generated output:

```
Vector(1600, 1521, 1444, 1296, 1225, 1156, 1089, 1024, 961, 900)
       40^2  39^2  38^2  36^2                               30^2
                        |
                      no 37^2
```

Code Uncles: What's a way we could have done this in one pass?

---

# Alternative

> Code Uncles: What's a way we could have done this in one pass?

`collect`!

```scala
(40 to 30 by -1).filter(_ != 37).map(i => i * i) 
// Vector(1600, 1521, 1444, 1296, 1225, 1156, 1089, 1024, 961, 900)

(40 to 30 by -1).collect { case i if i != 37 => i * i } 
// Vector(1600, 1521, 1444, 1296, 1225, 1156, 1089, 1024, 961, 900)
```

---

# collect aside

When you see "filter + map", think "collect".

Filter logic goes into the guard.

Map logic goes after the arrow.

```
blah.collect { ... if FILTER => MAPPER }
```

For example

```scala
(40 to 30 by -1).filter(_ != 37).map(i => i * i) 
(40 to 30 by -1).collect { case i if i != 37 => i * i } 
```

## Elegance

Sometimes `collect` can make your code a lot more readable.

You can reuse the destructuring boilerplate for both cases.

(This is not a good example for that)

## Performance

`collect` avoids building an intermediate collection and an extra pass.

---

# map + filter?

> When you see "filter + map", think "collect".

Not so elegant with "map + filter" though.

`collect` first executes filtering logic in the guard before the mapping logic.

```
blah.collect { ... if FILTER => MAPPER }
```

---

# List Performance

We know `List` pretty well now.

Just a few notes on performance:

---

# Prepending

Very fast: O(1)

e.g.

```scala
val oldList = List(1, 2, 3)

val newList = 0 :: oldList
```

---

# Appending

Very slow: O(n)

```scala
val oldList = List(1, 2, 3)

val newList = oldList :+ 4
```

It has to rebuild a whole new list starting at 4:

```
                    Nil
               4 :: Nil
          3 :: 4 :: Nil
     2 :: 3 :: 4 :: Nil
1 :: 2 :: 3 :: 4 :: Nil
```

---

# Beware of this kind of thing

```scala
val left = List(0, 1, 2, 3, 4)
val right = List(5, 6, 7, 8)

// Append my two lists together
var finalList = left

// One by one, append each element of right to left
for (element <- right) {
  finalList = finalList :+ element
}
```

Code Uncles: What is the time complexity of this?

---

# Beware of this kind of thing

```scala
val left = List(0, 1, 2, 3, 4)
val right = List(5, 6, 7, 8)

// Append my two lists together
var finalList = left

// One by one, append each element of right to left
for (element <- right) {
  finalList = finalList :+ element
}
```

> Code Uncles: What is the time complexity of this?

`O(n^2)` where `n` is the total number of elements.

Appending 5 causes us to build a new list:

```
                              Nil
                         5 :: Nil
                    4 :: 5 :: Nil      ~5 steps -
               3 :: 4 :: 5 :: Nil     that's the length
          2 :: 3 :: 4 :: 5 :: Nil     of `final` currently
     1 :: 2 :: 3 :: 4 :: 5 :: Nil
0 :: 1 :: 2 :: 3 :: 4 :: 5 :: Nil
```

Appending 6 causes us to build a new list:

```
                                   Nil
                              6 :: Nil
                         5 :: 6 :: Nil
                    4 :: 5 :: 6 :: Nil      ~6 steps -
               3 :: 4 :: 5 :: 6 :: Nil     that's the length
          2 :: 3 :: 4 :: 5 :: 6 :: Nil     of `final` currently
     1 :: 2 :: 3 :: 4 :: 5 :: 6 :: Nil
0 :: 1 :: 2 :: 3 :: 4 :: 5 :: 6 :: Nil
```

Appending 7 causes us to build a new list:

```
                                        Nil
                                   7 :: Nil
                              6 :: 7 :: Nil
                         5 :: 6 :: 7 :: Nil
                    4 :: 5 :: 6 :: 7 :: Nil      ~7 steps -
               3 :: 4 :: 5 :: 6 :: 7 :: Nil     that's the length
          2 :: 3 :: 4 :: 5 :: 6 :: 7 :: Nil     of `final` currently
     1 :: 2 :: 3 :: 4 :: 5 :: 6 :: 7 :: Nil
0 :: 1 :: 2 :: 3 :: 4 :: 5 :: 6 :: 7 :: Nil
```

etc...

---

# Beware!

```scala
val left = List(0, 1, 2, 3, 4)
val right = List(5, 6, 7, 8)

// Append my two lists together
var finalList = left

// One by one, append each element of right to left
for (element <- right) {
  finalList = finalList :+ element
}
```

This is ugly imperative "java" style code and it performs terribly.

Code Uncles: How would you improve this?

---

# Better version

> Code Uncles: How would you improve this?

Just use list concatenation.

```scala
val left = List(0, 1, 2, 3, 4)
val right = List(5, 6, 7, 8)

val finalList = left ++ right
```

`++` will build off the `right` list:

```
                         5 :: 6 :: 7 :: 8 :: Nil
                    4 :: 5 :: 6 :: 7 :: 8 :: Nil  ~5 steps -
               3 :: 4 :: 5 :: 6 :: 7 :: 8 :: Nil  the length
          2 :: 3 :: 4 :: 5 :: 6 :: 7 :: 8 :: Nil  of left
     1 :: 2 :: 3 :: 4 :: 5 :: 6 :: 7 :: 8 :: Nil
0 :: 1 :: 2 :: 3 :: 4 :: 5 :: 6 :: 7 :: 8 :: Nil
```

Time is O(left.size) - completely independent of right.

---

# Tip

Suppose we were combining two lists of significantly different sizes

and order doesn't matter:

```scala
val peopleWhoLoveVim = List(  // <---- massive
  ...
)

val peopleWhoLoveEmacs = List( // <---- tiny
  ...
)

// Which do we put on the left?

val peopleWhoLoveVimOrEmacs1 =
  peopleWhoLoveVim ++ peopleWhoLoveEmacs

val peopleWhoLoveVimOrEmacs2 =
  peopleWhoLoveEmacs ++ peopleWhoLoveVim
```

Which way around is better?

---

# Tip

> Time is O(left.size) - completely independent of right.

Put the tiny one on the left.

```scala
val peopleWhoLoveVimOrEmacs2 =
  peopleWhoLoveEmacs ++ peopleWhoLoveVim
```

It will only be 2 or 3 operations to concatenate them.

---

# Bringing it all together

Today was an ad-hoc tour of collections and bits of pieces of knowledge.

Let's put it all together.

---

# Scala 2.12 vs 2.13

2.13 overhauled the collections library.

The analytics codebase is going to move to 2.13 eventually.

Today's code samples were run in the 2.13 ammonite repl.

---

# Zooming out

There are 3 fundamental families of collections:

- maps


- sets


- sequences (`Seq`)

---

# Zooming out

Each fundamental type has an abstraction that represents it:

- `Map[Key, Value]`


- `Set[A]`


- `Seq[A]`

---

# Abstractions

These are abstractions.

When you use a factory method, it will use a concrete type to generate the object:

```scala
val seq = Seq(0, 1, 2, 3) 
seq: Seq[Int] = List(0, 1, 2, 3)
```

Compile time type is `Seq`.

Runtime type is `List`.

---

# Seq

Represents a collection with a defined deterministic order.

ie. the concept of i'th element is sensible

---

# Mutable vs Immutable

For each family, there's mutable and immutable variants.

---

# So there's two dimensions

```
                                 FAMILY

                          Map    Set    Seq
                       ----------------------
             Mutable   |      |      |      |
MUTABILITY             ----------------------
            Immutable  |      |      |      |
                       ----------------------
```

---

# Immutable Seq's

We mainly looked at immutable sequences,

that's where the most action happens in daily coding.

```
                                 FAMILY

                          Map    Set    Seq
                       ----------------------
             Mutable   |      |      |      |
MUTABILITY             ----------------------
            Immutable  |      |      |!HERE!|
                       ----------------------
```

It's more likely that you'll need to think about which kind of `Seq`

you use to get good performance.

---

# We saw some immutable `Seq`'s

- List


- LazyList


- Vector


- Range


- ArraySeq (proxy to Array)

---

# Random time/space complexity notes

## List

Great for prepending.

Sucky for appending.

## Vector

Pretty great for insertion anywhere.

## Range

Great for ranges of numbers.

Uses constant space.

## LazyList

Models infinite sequences with constant memory.

---

# Of course there's much more!

We can't cover the collections library comprehensively in 1 hour.

This was just to get you going.

---

# Further reading

- [Scala 2.13.0 release notes](https://github.com/scala/scala/releases/tag/v2.13.0)


- Martin Odersky's standard book: Programming in Scala (4th edition covers 2.13)


- MISC-06 talk Rohan did - goes into more details here and there


- MISC-18 - Zij's talk on parallelism with some high quality ascii art

---

```
  ____          _      
 / ___|___   __| | ___ 
| |   / _ \ / _` |/ _ \
| |__| (_) | (_| |  __/
 \____\___/ \__,_|\___|
                       
 _   _            _           
| | | |_ __   ___| | ___  ___ 
| | | | '_ \ / __| |/ _ \/ __|
| |_| | | | | (__| |  __/\__ \
 \___/|_| |_|\___|_|\___||___/
                              
```

Any thoughts or reflections?

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

---

# Extra stuff we couldn't squeeze in

Converting between scala and java collections.

Sometimes you use a java api that gives you back java collections.

You want to turn them into scala collections.
