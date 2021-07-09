---
author: Rohan
date: 2021-07-06
title: List and NonEmptyList
---

```
 _     _     _   
| |   (_)___| |_ 
| |   | / __| __|
| |___| \__ \ |_ 
|_____|_|___/\__|
                 
```

    and

```
 _   _             _____                 _         _     _     _   
| \ | | ___  _ __ | ____|_ __ ___  _ __ | |_ _   _| |   (_)___| |_ 
|  \| |/ _ \| '_ \|  _| | '_ ` _ \| '_ \| __| | | | |   | / __| __|
| |\  | (_) | | | | |___| | | | | | |_) | |_| |_| | |___| \__ \ |_ 
|_| \_|\___/|_| |_|_____|_| |_| |_| .__/ \__|\__, |_____|_|___/\__|
                                  |_|        |___/                 
```

---

# Overview

Three parter related to validation

---

# Overview

## Today

- List


- NonEmptyList


## Next time

- Chain


- NonEmptyChain

## Next^2 time

Validation

---

Introducing:

```
  ____      _       
 / ___|__ _| |_ ___ 
| |   / _` | __/ __|
| |__| (_| | |_\__ \
 \____\__,_|\__|___/
                    
```

The cats library

:party-cat:

:scream-cat:

---

# Overview

Cats - a library that defines common FP concepts

- type classes


- functional data structures (e.g. NonEmptyList)

---

# Cats docs

To the browser!

---

# Cats and us

```
soft-core                                   hard-core
   FP                                          FP
<------------------------------------------------>
     ^
   we are
    here
```

We just use 2 simple functional data structures:

- NonEmptyList (today)


- Validated (later session)

(No type classes)

---

# Chain

Why look at it then?

---

# Chain

> Why look at it then?

- potentially useful


- simple


- probably start using it with our validation code

---

# Quick summary: List vs Chain

## List

Simple

O(1) prepend

O(n) append

## Chain

Complex

O(1) prepend

O(1) append

## Summary

Chain is more complex, but is faster for appending

As a developer, you don't really see that complexity though

---

# Making things interactive

```scala
val awards = NonEmptyList.of(
  punAward,
  bestAndFairest,
  memoryAward
)
```

---

```
 _     _     _   
| |   (_)___| |_ 
| |   | / __| __|
| |___| \__ \ |_ 
|_____|_|___/\__|
                 
```

Quick recap

(Mixed experience in the audience)

---

# List overview

Approximation:

```scala
sealed trait List[+A]

//  ------ -------  
// | head | tail -|--->
//  ------ -------  
case class Cons[A](head: A, tail: List[A]) extends List[A]

// Terminus
case object Nil extends List[Nothing]
```

---

# Built from the back

e.g. want to build `List(1, 2, 3, 4)`

---

# Start from the back - Nil

```
                                                           
                                                                 Nil
                                                              
```

```scala
Nil
```

---

# Prepend 4

```
                                                 ------ ---
                                                |  4   |   |---> Nil
                                                 ------ ---
```

```scala
Cons(4, Nil)
```

---

# Prepend 3

```
                                 ------ ---      ------ ---
                                |  3   |   |--->|  4   |   |---> Nil
                                 ------ ---      ------ ---
```

```scala
Cons(3, Cons(4, Nil))
```

---

# Prepend 2

```
                 ------ ---      ------ ---      ------ ---
                |  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
                 ------ ---      ------ ---      ------ ---
```


```scala
Cons(2, Cons(3, Cons(4, Nil)))
```

---

# Prepend 1

```
 ------ ---      ------ ---      ------ ---      ------ ---
|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---
```

```scala
Cons(1, Cons(2, Cons(3, Cons(4, Nil))))
```

---

# Done!

```
 ------ ---      ------ ---      ------ ---      ------ ---
|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---
```

```scala
Cons(1, Cons(2, Cons(3, Cons(4, Nil))))
```

Inside our final list are many other lists

---

# Reuse

```
 ------ ---      ------ ---      ------ ---      ------ ---
|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---
 ------ ---    / \    ------ ---   / \
|  10  |   |----|    |  11  |   |---|
 ------ ---            ------ ---     
```

Build `List(10, 2, 3, 4)` and `List(11, 3, 4)`

---

# Reuse

```
 ------ ---      ------ ---      ------ ---      ------ ---
|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---
 ------ ---    / \    ------ ---   / \
|  10  |   |----|    |  11  |   |---|
 ------ ---            ------ ---     
```

Lists are immutable data structures

That makes sharing simple

---

# Prepending

Just wrapping in another cons cell, e.g. prepending 1

```
 ------ ---      ------ ---      ------ ---      ------ ---
|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---
```

```scala
Cons(1, Cons(2, Cons(3, Cons(4, Nil))))
```

O(1)

---

# Appending 5?

:hmmm-parrot:

```
                                                           ------ ---     
                                                          |  5   |   |--->
                                                           ------ ---     
 ------ ---      ------ ---      ------ ---      ------ ---
|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---
```

---

# Point 5 to Nil

Build from the back

```
                                                                 ------ ---     
                                                                |  5   |   |---> Nil
                                                                 ------ ---     
                                                          
 ------ ---      ------ ---      ------ ---      ------ ---
|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---
```

Need to make the 4 cell point at the 5 cell

---

# Point 4 to 5?

Can't, it's immutable - need to make a new cons cell

```
                                                 ------ ---      ------ ---     
                                                |  4   |   |--->|  5   |   |---> Nil
                                                 ------ ---      ------ ---     
                                                   !=
 ------ ---      ------ ---      ------ ---      ------ ---
|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---
```

---

# Point 3 to 4?

Again we need to make a new cell

```
                                 ------ ---      ------ ---      ------ ---     
                                |  3   |   |--->|  4   |   |--->|  5   |   |---> Nil
                                 ------ ---      ------ ---      ------ ---     
                                   !=              !=
 ------ ---      ------ ---      ------ ---      ------ ---
|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---
```

---

# Lump in the carpet

We have to work our way back to the front of the list

Can't reuse any of the existing structure

:sad-parrot:

---

# Final result

```
 ------ ---      ------ ---      ------ ---      ------ ---      ------ ---     
|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |--->|  5   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---      ------ ---     
   !=              !=              !=              !=
 ------ ---      ------ ---      ------ ---      ------ ---
|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---
```

Had to recreate n cells

O(n) time

---

# Concatenation

```scala
list1 ++ list2
// m       n
```

Running time?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Concatenation

```scala
list1 ++ list2
// m       n
```

O(m)

---

# Why?

Prepend elements of the left list onto the right list

```scala
List(1, 2, 3) ++ List(4, 5)
```

We can at least reuse the right list's structure

```

                                                 ------ ---      ------ ---     
                                                |  4   |   |--->|  5   |   |---> Nil
                                                 ------ ---      ------ ---     

                                 ------ ---      ------ ---      ------ ---     
                                |  3   |   |--->|  4   |   |--->|  5   |   |---> Nil
                                 ------ ---      ------ ---      ------ ---     

                 ------ ---      ------ ---      ------ ---      ------ ---     
                |  2   |   |--->|  3   |   |--->|  4   |   |--->|  5   |   |---> Nil
                 ------ ---      ------ ---      ------ ---      ------ ---     

 ------ ---      ------ ---      ------ ---      ------ ---      ------ ---     
|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |--->|  5   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---      ------ ---     
```

Number of steps is the length of the left list

---

# Tip

If concatenating a big and small list and the order doesn't matter,

put the big one on the right so that you can reuse its structure

---

# Summary of List so far

Simple data structure

"Singly linked" list (left to right)

Recursive in nature

Immutable - designed for reuse

O(1) prepend

O(n) append

O(m) concatenation

---

# The "real" List

```scala
// Really an abstract class with loads of stuff inside
sealed trait List[+A]

// Really called `::`
case class Cons[A](head: A, tail: List[A]) extends List[A]

// Terminus
case object Nil extends List[Nothing]
```

---

```
 __  __               
|  \/  | __ _ ___ ___ 
| |\/| |/ _` / __/ __|
| |  | | (_| \__ \__ \
|_|  |_|\__,_|___/___/
                      
    _                               _ _             
   / \   _ __  _ __   ___ _ __   __| (_)_ __   __ _ 
  / _ \ | '_ \| '_ \ / _ \ '_ \ / _` | | '_ \ / _` |
 / ___ \| |_) | |_) |  __/ | | | (_| | | | | | (_| |
/_/   \_\ .__/| .__/ \___|_| |_|\__,_|_|_| |_|\__, |
        |_|   |_|                             |___/ 
```

The problem with list

---

# Mass appending

Imagine you had to concatenate a list of lists by left folding them together:

```scala
val lists = List(
  List(1, 2, 3),
  List(4, 5, 6, 7)
  List(8, 9),
  List(10, 11)
)

val concatenated = lists.foldLeft(List.empty[Int])(_ ++ _)
```

What would be the running time?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Mass appending

O(n^2) where n is the length of the final list

```scala
val lists = List(
  List(1, 2, 3),
  List(4, 5, 6, 7)
  List(8, 9),
  List(10, 11)
)

val concatenated = lists.foldLeft(List.empty[Int]) {
  (acc, next) => acc ++ next
}
```

Play out the fold:

```
List()

List() ++ List(1, 2, 3)           --> List(1, 2, 3)    (0 steps)

List(1, 2, 3) ++ List(4, 5, 6, 7) --> List(1, ..., 7)  (3 steps)

List(1, ..., 7) ++ List(8, 9)     --> List(1, ..., 9)  (7 steps)

List(1, ..., 9) ++ List(10, 11)   --> List(1, ..., 11) (9 steps)
```

As the list grows, each subsequent concatenation is growing too

---

# Improvements?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Improvements

Remember our tip: put the big list on the right

ie. fold from the _right_

```scala
val concatenated = lists.foldRight(List.empty[Int]) {
  (next, acc) => next ++ acc
}
```

(Note the reversed order of parameters)

---

# foldRight

```scala
val lists = List(
  List(1, 2, 3),
  List(4, 5, 6, 7)
  List(8, 9),
  List(10, 11)
)

val concatenated = lists.foldRight(List.empty[Int]) {
  (next, acc) => next ++ acc
}
```

Play it out:

```
List()

List(10, 11) ++ List()               --> List(10, 11)      (2 steps)

List(8, 9) ++ List(10, 11)           --> List(8, ..., 11)  (2 steps)

List(4, 5, 6, 7) ++ List(8, ..., 11) --> List(4, ..., 11)  (4 steps)

List(1, 2, 3) ++ List(4, ..., 11)    --> List(1, ..., 11)  (3 steps)
```

We aren't reprocessing the same data

Each step just depends on the size of the new values

Adding all of those up we get O(n)

(Starting at the back of the outer list might not be so efficient though)

---

# The problem

Imagine you're building a generalized abstract framework for folding things together.

```scala
trait Foldable[A] {
  def seed: A

  def combine(left: A, right: A): A
}

def fold(list: List[A], foldable: Foldable[A]): A = {
  ???
}
```

It's abstract and general - how would you implement it?

---

# Implementation

```scala
trait Foldable[A] {
  def seed: A

  def combine(left: A, right: A): A
}

def fold(list: List[A], foldable: Foldable[A]): A = {
  // Simple iterative solution we've seen
  var acc = foldable.seed

  for (a <- list)
    acc = foldable.combine(acc, a)

  acc
}
```

---

# Using it with our List of Lists

We'd define our foldable logic (seed and combine)

How?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Foldable for a List

```scala
class ListFoldable[A] extends Foldable[List[A]] {
  def seed: List[A] = List.empty[A]

  def combine(left: List[A], right: List[A]): List[A] = left ++ right
}
```

---

# Then use it!

```scala
val lists = List(
  List(1, 2, 3),
  List(4, 5, 6, 7)
  List(8, 9),
  List(10, 11)
)

class ListFoldable[A] extends Foldable[List[A]] {
  def seed: List[A] = List.empty[A]

  def combine(left: List[A], right: List[A]): List[A] = left ++ right
}

fold(lists, new ListFoldable[Int])
```

Hoorah!

But what's the issue?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# The issue

> But what's the issue?

Our `fold` function is really the same as `foldLeft`

```scala
def fold(list: List[A], foldable: Foldable[A]): A = {
  var acc = foldable.seed

  // left to right
  for (a <- list)
    acc = foldable.combine(acc, a)

  acc
}
```

It will lead to reprocessing the same data:

```
List()

List() ++ List(1, 2, 3)           --> List(1, 2, 3)    (0 steps)

List(1, 2, 3) ++ List(4, 5, 6, 7) --> List(1, ..., 7)  (3 steps)

List(1, ..., 7) ++ List(8, 9)     --> List(1, ..., 9)  (7 steps)

List(1, ..., 9) ++ List(10, 11)   --> List(1, ..., 11) (9 steps)
```
---

# Our problem as the author

Abstraction vs Performance

---

# Abstraction

You deliberately throw away information about your data structure

View it through a narrow lens

`List` is represented by an instance of `Foldable` - that's all we know about it

---

# Performance

Better performance is usually associated with knowing more about your structure

The more you know, the more assumptions you can make

---

# Abstraction vs Performance

They sometimes pull in different directions

---

# Our problem as an author

Trying to make something general purpose (abstract)

Abstractions generally are constructs from the type/logic realm

They don't represent performance

---

# Our problem as an author

Have to make a choice: Left vs Right

Left makes more sense for a `List`

Unfortunately we end up with a solution that performs badly for some structures

---

# Why I'm mentioning this

Cats has a generalized `fold` that would fold from the left

Gets used internally with validation to concatenate lists/chains of errors together (later lesson)

---

# Why I'm mentioning this

`List` has O(n^2) performance with it 

`Chain` has O(n)

(motivating `Chain`)

---

```
 _   _             _____                 _         _     _     _   
| \ | | ___  _ __ | ____|_ __ ___  _ __ | |_ _   _| |   (_)___| |_ 
|  \| |/ _ \| '_ \|  _| | '_ ` _ \| '_ \| __| | | | |   | / __| __|
| |\  | (_) | | | | |___| | | | | | |_) | |_| |_| | |___| \__ \ |_ 
|_| \_|\___/|_| |_|_____|_| |_| |_| .__/ \__|\__, |_____|_|___/\__|
                                  |_|        |___/                 
```

Sometimes abbreviated to "Nel" (not to be confused with `Nil`)

---

# What is a NonEmptyList?

The same says it all

---

# Why is it useful?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Uses

Scenarios where your logic only makes sense if there's some data

e.g.

```scala
def oldest(people: List[Person]): Person = ...
```

---

# Stronger type representation

```scala
// Too loose
def oldest(people: List[Person]): Person = people match {
  case Nil => throw new IllegalArgumentException("Can't find oldest person on an empty list!")
  case _ => people.maxBy(_.age)
}

// A bit better
// Empty     ----> None
// Non-empty ----> Some(max)
def oldest(people: List[Person]): Option[Person] = people match {
  case Nil => None
  case _ => Some(people.maxBy(_.age))
}

// Even better - one code path and no need for an exception or effect
// Non-empty requirement is made clear from the type signature
def oldest(people: NonEmptyList[Person]): Person = people.toList.maxBy(_.age)
```

---

# Refinement

An example of a more general principle:

> Model your data using strong types - minimize wiggle room

---

# Error handling responsibility

```scala
// Author handles the issue
def oldest(people: List[Person]): Person = ...

// Caller handles the issue
def oldest(people: List[Person]): Option[Person] = ...
def oldest(people: NonEmptyList[Person]): Person = ...
```

---

# Error handling responsibility

```scala
// Exception doesn't appear in the contract
// easy for the caller to forget,
// more awkward to handle and it's hard to reason about exceptions
def oldest(people: List[Person]): Person = ...

// Caller must handle the issue
def oldest(people: List[Person]): Option[Person] = ...
def oldest(people: NonEmptyList[Person]): Person = ...
```

---

# Data structure

How to build a `NonEmptyList`?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Data structure

> How to build a `NonEmptyList`?

With regular `List`:

- cons cell is the representation of non-empty


- `Nil` is the representation of empty

Just copy-paste the cons cell approach (but no Nil)

---

# Data structure

> Just copy-paste the cons cell approach (but no Nil)

From before:

```scala
case class Cons[A](head: A, tail: List[A]) extends List[A]

// copy paste

case class NonEmptyList[A](head: A, tail: List[A])
```

---

# From the source

```scala
final case class NonEmptyList[+A](head: A, tail: List[A]) {
  ...
}
```

---

# Not quite recursive

```scala
final case class NonEmptyList[+A](head: A, tail: List[A]) {
//                                               ^^^^ Not a NonEmptyList
  ...
}
```

A `NonEmptyList` of length 1 won't have a non-empty tail

This lack of recursion lets us avoid having a terminus concept

---

# Working with NonEmptyList

```scala
import $ivy.`org.typelevel::cats-core:2.1.1`

import cats.data.NonEmptyList
```

(Data structures live in `cats.data`)

---

# Pre-load

Can use `load.sc` to preload the repl with some useful stuff

To the repl!

---

# Summary - basics

```scala
@ val nel = NonEmptyList(1, List(2, 3))

@ val nel = NonEmptyList.of(1, 2, 3) 

@ nel.head  // Totally safe
// 1

@ nel.tail  // Produces regular List as it might be empty
// List(2, 3)
```

---

# Summary - api

```scala
// Many operations analogous to those from Seq
@ nel. 
++                   concat               foldLeft             ...
:+                   concatNel            foldRight            ...
::                   copy                 forall               ...
:::                  distinct             groupBy              ...
===                  exists               groupByNem           ...
append               filter               head                 ...
canEqual             filterNot            init                 ...
coflatMap            find                 last                 ...
collect              flatMap              length               ...
```

Has most of what `List` has

---

# Summary - size based reasoning

```scala
@ nel.map(_ * 2)  // Produces NEL as the length doesn't change
// NonEmptyList(2, List(4, 6))

// filter and collect reduce the size making the output potentially empty
@ nel.filter(_ > 2) 
// List(3)

@ nel.collect {
    case i if i > 2 => i * i
  } 
// List(9)
```

---

# Summary - safety

```scala
@ nel.head

@ nel.toList.minBy(...)

@ nel.toList.maxBy(...)

// Totally safe
@ nel.reduceLeft(_ + _) 
// 6
```

---

# Summary - concatenation

```scala
@ nel ::: nel

@ nel ++ List(1, 2, 3)
```

---

# Summary - interop with List

```scala
// Use `fromList` to validate regular Lists
@ NonEmptyList.fromList(List(0, 1, 2, 3)) 
// Some(NonEmptyList(0, List(1, 2, 3)))

@ NonEmptyList.fromList(List.empty[Int]) 
// None

// Use `.toList` to switch back to a regular List
@ nel.toList
// List(1, 2, 3)
```

---

```
 _   _          
| | | |___  ___ 
| | | / __|/ _ \
| |_| \__ \  __/
 \___/|___/\___|
                
                         
  ___ __ _ ___  ___  ___ 
 / __/ _` / __|/ _ \/ __|
| (_| (_| \__ \  __/\__ \
 \___\__,_|___/\___||___/
                         
```

for when to _produce_ a `NonEmptyList`

---

# Group by operations

Great use case for `NonEmptyList`

---

# Example

Group all these people by their age:

```scala
case class Person(name: Int, age: Int)

val people = List(
  Person("Boban", 28),
  Person("Lulu", 28),
  Person("Zij", 27),
  Person("Clement", 27),
  Person("James", 45),
)
```

Don't think about the implementation,

instead think about the structure you'd get back

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Example

```scala
case class Person(name: Int, age: Int)

val people = List(
  Person("Boban", 28),
  Person("Lulu", 28),
  Person("Zij", 27),
  Person("Clement", 27),
  Person("James", 45),
)

// produce something like:

Map(
  27 -> NonEmptyList(clement, zij),
  28 -> NonEmptyList(boban, lulu),
  45 -> NonEmptyList(james)
)
```

---

# Why NonEmptyList?

```scala
Map(
  27 -> NonEmptyList(clement, zij),
  28 -> NonEmptyList(boban, lulu),
  45 -> NonEmptyList(james)
  50 -> List() // <--- makes no sense, where did 50 come from?
)
```

It's impossible to have a key with no data

---

# Emptiness?

```scala
Map()

// or

Map(
  27 -> NonEmptyList(clement, zij),
  28 -> NonEmptyList(boban, lulu),
  45 -> NonEmptyList(james)
)
```

We might not have any groups (if the input list is empty),

but any groups we do have _must_ be non-empty

---

# Seq.groupBy

```scala
@ people.groupBy(_.age) 
// Map[Int, List[Person]]
```

The type used with `Seq.groupBy` is a `Map[Key, Seq[Value]]`

`NonEmptyList` isn't part of the standard library so it wouldn't make sense

---

# Our own code

If we're implementing our own groupers,

we can use `NonEmptyList` to model the groups

(And in fact we have!)

---

# foldLeftChunks

Recall `foldLeftChunks` from iterators lesson

```scala
def foldLeftChunks[A, B, L](iter: Iterator[A], seed: B)
                           (computeLabel: A => L)
                           (combine: (Chunk[A], B) => B): B
```

```
-----------------------------------
| lead_id | source   | status     |
-----------------------------------
| lead-X  | scorpion | Verified   |   |
| lead-X  | eagle    | Verified   |   | chunk
| lead-X  | impala   | Unverified |   |
-----------------------------------
| lead-Y  | impala   | Verified   |
| lead-Y  | scorpion | Invalid    |
| lead-Y  | eagle    | Verified   |
-----------------------------------
| lead-Z  | impala   | Invalid    |
| lead-Z  | scorpion | Verified   |
-----------------------------------
...
```

Our chunks are a form of grouping

Similar concept of discovering them by computing a label

---

# foldLeftChunks

```
-----------------------------------
| lead_id | source   | status     |
-----------------------------------
| lead-X  | scorpion | Verified   |   |
| lead-X  | eagle    | Verified   |   | chunk
| lead-X  | impala   | Unverified |   |
-----------------------------------
| lead-Y  | impala   | Verified   |
| lead-Y  | scorpion | Invalid    |
| lead-Y  | eagle    | Verified   |
-----------------------------------
| lead-Z  | impala   | Invalid    |
| lead-Z  | scorpion | Verified   |
-----------------------------------
...
```

Can chunks ever be empty?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# foldLeftChunks

```
-----------------------------------
| lead_id | source   | status     |
-----------------------------------
| lead-X  | scorpion | Verified   |   |
| lead-X  | eagle    | Verified   |   | chunk
| lead-X  | impala   | Unverified |   |
-----------------------------------
| lead-Y  | impala   | Verified   |
| lead-Y  | scorpion | Invalid    |
| lead-Y  | eagle    | Verified   |
-----------------------------------
| lead-Z  | impala   | Invalid    |
| lead-Z  | scorpion | Verified   |
-----------------------------------
...
```

> Can chunks ever be empty?

No

By construction, our chunks are generated by computing a label on a value

That value becomes part of the group corresponding to that label

---

# foldLeftChunks

Hand-waving

```scala
type Chunk[A] = ???

def foldLeftChunks[A, B, L](iter: Iterator[A], seed: B)
                           (computeLabel: A => L)
                           (combine: (Chunk[A], B) => B): B =
```

What is `Chunk` a type alias for? (No peeking at the code!)

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# foldLeftChunks

> Q: What is `Chunk` a type alias for? (No peeking at the code!)

```scala
type Chunk[A] = NonEmptyList[A]

def foldLeftChunks[A, B, L](iter: Iterator[A], seed: B)
                           (computeLabel: A => L)
                           (combine: (Chunk[A], B) => B): B =
```

Used a type alias to not scare the children

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

# List/NonEmptyList

Simple

O(1) prepend

O(n) append

Doesn't work well with generalized folding algorithms

---

# Chain (coming soon)

Complex

O(1) prepend

O(1) append (single elements and entire chains)

Will work better with generalized folding algorithms

---

# NonEmptyList

Basically a non-recursive cons cell (the non-empty half of `List`)

---

# NonEmptyList

Forces people to think about empty cases

- better error handling logic


- less runtime exceptions

---

# NonEmptyList

Similar api to `List`

A little ad-hoc in that some operations return nel and some List

---

# NonEmptyList

Legitimate use cases pop up here and there

- consuming: safe head/max/min/reduce


- producing: grouping by a key/label

---

# Further reading

[Cats docs for nel](https://typelevel.org/cats/datatypes/nel.html)

---

# Awards?

```scala
val awards = NonEmptyList.of(
  punAward,
  bestAndFairest,
  memoryAward
)
```

---

# Next time

Chain

(then Validation)

---

# QnA?

---

# Appendix - efficient foldLeft

Can get O(n) performance with `foldLeft`

Need reversing trickery

```scala
val concatenated = lists.foldLeft(List.empty[Int]) {
  (acc, next) => next.reverse ++ acc
}.reverse
```

Each sublist gets reversed once (n)

The final list gets reversed (n)

So adds 2n steps over foldRight

Not as elegant, but still O(n)
