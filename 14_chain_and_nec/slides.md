---
author: Rohan
date: 2021-07-08
title: Chain and NonEmptyChain
---

```
  ____ _           _       
 / ___| |__   __ _(_)_ __  
| |   | '_ \ / _` | | '_ \ 
| |___| | | | (_| | | | | |
 \____|_| |_|\__,_|_|_| |_|
                           
```

and

```
 _   _             _____                 _          ____ _           _       
| \ | | ___  _ __ | ____|_ __ ___  _ __ | |_ _   _ / ___| |__   __ _(_)_ __  
|  \| |/ _ \| '_ \|  _| | '_ ` _ \| '_ \| __| | | | |   | '_ \ / _` | | '_ \ 
| |\  | (_) | | | | |___| | | | | | |_) | |_| |_| | |___| | | | (_| | | | | |
|_| \_|\___/|_| |_|_____|_| |_| |_| .__/ \__|\__, |\____|_| |_|\__,_|_|_| |_|
                                  |_|        |___/                           
```

---

# Recap

List/NonEmptyList

Simple data structure

O(1) prepend

O(n) append

O(m) concat (length of the left list)

---

# Left folding

> Left folding a large number of small lists

`n` defined as the length of the final `List`

```scala
val lists = List(
  List(1, 2),
  List(3, 4),
  List(5, 6),
  List(7, 8),
  List(9)
)

// Left           Right
   Nil        ++  List(1, 2)
   List(1, 2) ++  List(3, 4)
   List(1..4) ++  List(5, 6)
   List(1..6) ++  List(7, 8)
   List(1..8) ++  List(9)


// ^^^^^^^^^
// Acc is on the left :(
```

If the input lists are smallish, then there's approximately `n` of them

The time for each append depends on the left size (which is approaching `n`)

O(n^2) overall :(

---

# Core problem

If we want to left fold,

find something faster for left appending

---

# That's Chain

To the docs!

---

# Today

Pretty good article, but glosses over a lot of details

Aim is to fill in the gaps

---

```
 ____  _                   _                  
/ ___|| |_ _ __ _   _  ___| |_ _   _ _ __ ___ 
\___ \| __| '__| | | |/ __| __| | | | '__/ _ \
 ___) | |_| |  | |_| | (__| |_| |_| | | |  __/
|____/ \__|_|   \__,_|\___|\__|\__,_|_|  \___|
                                              
```

How do structures relate to performance?

---

# Root cause

Q: Why is appending slow?

```scala
sealed trait List[+A]

//  ------ -------  
// | head | tail -|--->
//  ------ -------  
case class Cons[A](head: A, tail: List[A]) extends List[A]

// Terminus
case object Nil extends List[Nothing]
```

First consider:

Q: Why is prepending fast?

---

# Root cause of slowness of appending?

```scala
sealed trait List[+A]

//  ------ -------  
// | head | tail -|--->
//  ------ -------  
case class Cons[A](head: A, tail: List[A]) extends List[A]

// Terminus
case object Nil extends List[Nothing]
```

First consider:

> Q: Why is prepending fast?

A: Natively supported by the `Cons` structure

(Cons really means "prepend" or "concat")

---

# Root cause of slowness of appending?

```scala
sealed trait List[+A]

//  ------ -------  
// | head | tail -|--->
//  ------ -------  
case class Cons[A](head: A, tail: List[A]) extends List[A]

// Terminus
case object Nil extends List[Nothing]
```

> Q: Why is appending slow?

It's really the _absence_ of an appending structure

List is too constrained in how you can build it

---

# Modifying List

```scala
sealed trait List[+A]

case class Cons[A](head: A, tail: List[A]) extends List[A]

case object Nil extends List[Nothing]

// New
case class Append[A](left: List[A], right: List[A]) extends List[A]
```

Define a new data structure specifically for appending

---

# Recapping Foldable

```scala
fold(List(1, 2, 3), foldingLogic)


trait Foldable[A] {

  def seed: A

  def combine(left: A, right: A): A

}
```

---

# Reimplementing Foldable

With our new team member:

```scala
case class Append[A](left: List[A], right: List[A]) extends List[A]
```

we can reimplement `Foldable` for `List[A]`:

```scala
class ListFoldable[A] extends Foldable[List[A]] {

  def seed: List[A] = List.empty[A]

  def combine(left: List[A], right: List[A]): List[A] = Append(left, right)
  //                                                    ^^^^^^^^^^^^^^^^^^^ yey!

}
```

Combining is O(1) now - you're just wrapping the two lists into a new structure

---

# Con's?

Increased complexity brings issues...

---

# Complexity - pattern matching

More cases to handle when pattern matching

```scala
def doSomething(list: List[Int]): Unit = list match {
  case Nil => ...
  case Cons(head, tail) => ...
  case Append(left, right) => ...
}
```

---

# Complexity - ambiguous representations

```scala
List(1, 2, 3)

// could produce any one of:

1 :: 2 :: 3 :: Nil
Cons(1, Cons(2, Cons(3, Nil))

(1 :: Nil) ++ (2 :: 3 :: Nil)
Append(Cons(1, Nil), Cons(2, Cons(3, Nil)))

(1 :: 2 :: Nil) ++ (3 :: Nil)
Append(Cons(1, Cons(2, Nil)), Cons(3, Nil))
```

Not a real issue, but does smell a bit

---

# Complexity - potential waste

Want to avoid this appending empty lists:

```scala
Append(Nil, list)
Append(list, Nil)

// could be simplified to just

list
```

---

# Reflecting on this

- ambiguous representations


- potential waste

Probably better to avoid having users directly building lists using `Cons` and `Append`

---

# Responsibilities

> ambiguous representations

Stems partly from `Cons` and `Append` having similar responsibilities

- `Cons` appends a list to a value


- `Append` appends a list to a list

---

# Split Cons

> `Cons` appends a list to a value

Split it into two decoupled concepts:

- lifting a value into list


- appending lists

---

# Singleton

```diff
 // ADT
 ...

-case class Cons[A](head: A, tail: List[A]) extends List[A]
+case class Singleton[A](a: A) extends List[A]
 case class Append[A](left: Chain[A], right: Chain[A]) extends Chain[A]


 // Usage
-Cons(1, tail)
+Append(Singleton(1), tail)
```

---

# Summary

When you want an operation to be efficient,

define a structure to represent it

e.g. `List` prepending is efficient because that's what a cons cell really is

---

# Summary

But try not to make your structures feel "bolted on"

---

```
  ____ _           _       
 / ___| |__   __ _(_)_ __  
| |   | '_ \ / _` | | '_ \ 
| |___| | | | (_| | | | | |
 \____|_| |_|\__,_|_|_| |_|
                           
```

Basically what we just did

---

# Introducing the ADT

```scala
sealed abstract class Chain[+A]

case object Empty extends Chain[Nothing]

case class Singleton[A](a: A) extends Chain[A]

case class Append[A](left: Chain[A], right: Chain[A]) extends Chain[A]

case class Wrap[A](seq: Seq[A]) extends Chain[A]
```

---

# Empty vs Nil

## Similarities

Represents an empty structure

## Differences

Empty isn't the "terminus" anymore

`Singleton` and `Wrap` do that

---

# Concatenation

How would you define it?

```scala
def concat[A](left: Chain[A], right: Chain[A]): Chain[A] = ???
```

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Concatenation

Don't want waste

```scala
def concat[A](left: Chain[A], right: Chain[A]): Chain[A] = (left, right) match {
  case (Empty, _) => right
  case (_, Empty) => left
  case _ => Append(left, right)
}
```

(only introduce more structure if necessary)

---

# Converting from a Seq?

```scala
def fromSeq[A](seq: Seq[A]): Chain[A] = ???
```

...

---

# Converting from a Seq?

```scala
def fromSeq[A](seq: Seq[A]): Chain[A] = ???
```

We _do_ have a structure for this:

```scala
case class Wrap[A](seq: Seq[A]) extends Chain[A]
```

but we want to avoid waste like:

```scala
fromSeq(Vector.empty)
// Wrap(Vector.empty) is a bit wasteful
// Empty would be better

fromSeq(List(1))
// Wrap(List(1)) is a bit wasteful
// Singleton(1) would be better
```

How to implement it?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Converting from a Seq?

How about this?

```scala
def fromSeq[A](seq: Seq[A]): Chain[A] = {
  seq.length match {
    case 0 => Empty
    case 1 => Singleton(seq.head)
    case _ => Wrap(seq)
  }
}
```

:hmmm-parrot:

What's the issue here?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Converting from a Seq

```scala
def fromSeq[A](seq: Seq[A]): Chain[A] = {
  seq.length match {
    case 0 => Empty
    case 1 => Singleton(seq.head)
    case _ => Wrap(seq)
  }
}
```

Calling `length` on a sequence is dangerous

What if it's an infinite lazy list?

Solution?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Solution

```scala
def fromSeq[A](seq: Seq[A]): Chain[A] = {
  seq.length match {
    case 0 => Empty
    case 1 => Singleton(seq.head)
    case _ => Wrap(seq)
  }
}
```

Subtle difference between these approaches:

> What is your length? (Check if it's 0 or 1)

> Is your length 0 or 1?

You can follow the second approach without knowing the length of the sequence

---

# For example

Write a simple method to check if a list has a particular length:

```scala
def hasLength[A](list: List[A], length: Int): Boolean = {
  if (length < 0) false
  else list match {
    case Nil => length == 0
    case _ :: tail => hasLength(tail, length - 1)
  }
}
```

---

# Test it out

```scala
def hasLength[A](list: List[A], length: Int): Boolean = {
  if (length < 0) false
  else list match {
    case Nil => length == 0
    case _ :: tail => hasLength(tail, length - 1)
  }
}
```

Test it out:

```scala
hasLength(List(0, 1, 2, 3, 4), 1)

// (non-empty case)
hasLength(List(1, 2, 3, 4), 0)

// (non-empty case)
hasLength(List(2, 3, 4), -1)

// length < 0 case
false
```

---

# Summary

```scala
hasLength(List(0, 1, 2, 3, 4), 1)

// (non-empty case)
hasLength(List(1, 2, 3, 4), 0)

// (non-empty case)
hasLength(List(2, 3, 4), -1)

// length < 0 case
false
```

We answered the question without traversing the whole list

> What is your length? (Check if it's 0 or 1)

> Is your length 0 or 1?

---

# Infinite lazy list?

An approach like the above would have worked too

---

# lengthCompare

To the repl!

---

# lengthCompare

## Returns Int

> collection.length - input

- negative: collection is smaller than value


- zero: same size


- positive: collection is larger than value

## Pro's over .length

- safer


- probably more efficient (can bail out sooner)

---

# Back to our problem

```scala
// Old
def fromSeq[A](seq: Seq[A]): Chain[A] = {
  seq.length match {
    case 0 => Empty
    case 1 => Singleton(seq.head)
    case _ => Wrap(seq)
  }
}

// New
def fromSeq[A](seq: Seq[A]): Chain[A] = {
  if (seq.lengthCompare(0) == 0) Empty
  else if (seq.lengthCompare(1) == 0) Singleton(seq.head)
  else Wrap(seq)
}
```

---

```
 ____  _             _             
|  _ \| | __ _ _   _(_)_ __   __ _ 
| |_) | |/ _` | | | | | '_ \ / _` |
|  __/| | (_| | |_| | | | | | (_| |
|_|   |_|\__,_|\__, |_|_| |_|\__, |
               |___/         |___/ 
          _ _   _     
__      _(_) |_| |__  
\ \ /\ / / | __| '_ \ 
 \ V  V /| | |_| | | |
  \_/\_/ |_|\__|_| |_|
                      
  ____ _           _       
 / ___| |__   __ _(_)_ __  
| |   | '_ \ / _` | | '_ \ 
| |___| | | | (_| | | | | |
 \____|_| |_|\__,_|_|_| |_|
                           
```

We've looked at the conceptual model 

---

# Time to play

To the repl!

```scala
import $ivy.`org.typelevel::cats-core:2.1.1`

import cats.data.Chain
```

(in `load.sc`)

---

# Summary - imports

```scala
import $ivy.`org.typelevel::cats-core:2.1.1`

import cats.data.Chain
```

---

# Summany - constructing a Chain

```scala
@ val chain = Chain(1, 2, 3, 4) 
// Wrap(ArraySeq(1, 2, 3, 4))

@ Chain.one(1) 
// Singleton(1)

@ Chain.fromSeq(List(1, 2, 3)) 
// Wrap(List(1, 2, 3))

@ Chain.fromSeq(List(1)) 
// Singleton(1)

@ Chain.fromSeq(Nil) 
// Chain()
```

---

# Summary - api

All the usual stuff

```scala
@ chain. 
++                collectFirst      dropWhile         foldLeft          ...
+:                collectFirstSome  exists            foldRight         ...
:+                concat            filter            forall            ...
===               contains          filterNot         get               ...
append            deleteFirst       find              groupBy           ...
collect           distinct          flatMap           hash              ...
```

---

# Summary - api

```scala
@ chain.map(_ * 2) 
// Seemed to flatten out the insides

@ chain.filter(_ > 0) 
// Append(Append(Append(Singleton(1), Singleton(2)), Singleton(3)), Singleton(4))
// Looks like it packed all the matching elements into Singleton's
```

---

# Summary - appending

```scala
@ chain 
// Wrap(ArraySeq(1, 2, 3, 4))

@ chain.concat(Chain.empty) 
// Wrap(ArraySeq(1, 2, 3, 4))

@ chain ++ chain 
// Append(Wrap(ArraySeq(1, 2, 3, 4)), Wrap(ArraySeq(1, 2, 3, 4)))
```

---

# Summary - interop

```scala
@ chain.toList 
// List(1, 2, 3, 4)

@ chain.toVector 
// Vector(1, 2, 3, 4)
```

---

```
 _   _       _           _                          _ 
| | | |_ __ | |__   __ _| | __ _ _ __   ___ ___  __| |
| | | | '_ \| '_ \ / _` | |/ _` | '_ \ / __/ _ \/ _` |
| |_| | | | | |_) | (_| | | (_| | | | | (_|  __/ (_| |
 \___/|_| |_|_.__/ \__,_|_|\__,_|_| |_|\___\___|\__,_|
                                                      
 _____              
|_   _| __ ___  ___ 
  | || '__/ _ \/ _ \
  | || | |  __/  __/
  |_||_|  \___|\___|
                    
```

Chain is really a quirky unbalanced tree

---

# Examples

```scala
Chain(1, 2, 3, 4, 5, 6)
```

Many possible representations

```
               Append                  Leaf x ~ Singleon(1)
               |    |
          Append    Append
          |    |    |    |
     Append    3    4    Wrap(List(5, 6))
     |    |
     1    2

               Append
               |    |
    Wrap(1 to 4)    Append
                    |    |
                    5    6

                     Append   Append
                     |    |   |    |
                Append    6   1    Append
                |    |             |    |
           Append    5             2    Append
           |    |                       |    |
      Append    4                       3    Append
      |    |                                 |    |
 Append    3                                 4    Append
 |    |                                           |    |
 1    2                                           5    6
```

---

# So many representations

Hard to reason about it

---

# Example: performance of .head?

```
                     Append   Append
                     |    |   |    |
                Append    6   1    Append
                |    |             |    |
           Append    5             2    Append
           |    |                       |    |
      Append    4                       3    Append
      |    |                                 |    |
 Append    3                                 4    Append
 |    |                                           |    |
 1    2                                           5    6
```

Left representation: n steps

Right representation: 1 step

"Worst case": n steps

---

# Wrap

Subsections of your chain are other sequences

```scala
Chain(1, 2, 3, ..., 1_000_000)
```

```
               Append
               |    |
       Wrap(Seq)    Wrap(Seq)
     1 - 500_000    500_001 - 1_000_000
```

Performance of `head` depends on the performance of that left one

Could be 1 step

Could be 500_000 steps

---

# The docs

Analysis from [the docs](https://typelevel.org/cats/datatypes/chain.html):

> but what about operations like map or fold?
>
> Fortunately we’ve also benchmarked these (again, higher score is better):

```
Benchmark                           Mode  Cnt          Score         Error  Units
ChainBench.foldLeftLargeChain      thrpt   20        117.267 ±       1.815  ops/s
ChainBench.foldLeftLargeList       thrpt   20        135.954 ±       3.340  ops/s
ChainBench.foldLeftLargeVector     thrpt   20         61.613 ±       1.326  ops/s

ChainBench.mapLargeChain           thrpt   20         59.379 ±       0.866  ops/s
ChainBench.mapLargeList            thrpt   20         66.729 ±       7.165  ops/s
ChainBench.mapLargeVector          thrpt   20         61.374 ±       2.004  ops/s
```

> While not as dominant, Chain holds its ground fairly well.
>
> It won’t have the random access performance of something like Vector,
>
> but in a lot of other cases, Chain seems to outperform it quite handily.
>
> So if you don’t perform a lot of random access on your data structure,
>
> then you should be fine using Chain extensively instead.

---

# What chains did they use?

Details :not-found-parrot:

I'm guessing they were excluding chains with `Wrap` in them, ie. didn't compare:

```scala
val list = List(...)

val chain = Chain.fromSeq(list)
```

---

```
__        __               
\ \      / / __ __ _ _ __  
 \ \ /\ / / '__/ _` | '_ \ 
  \ V  V /| | | (_| | |_) |
   \_/\_/ |_|  \__,_| .__/ 
                    |_|    
```

Problems of reasoning

---

# Wrap?

Do we strictly need `Wrap`?

---

# Wrap?

Wrap smells a bit in creating ambiguity

For example we don't need `Empty` if we have `Wrap`:

```
Wrap(Nil)
```

---

# Wrap

Can't we just convert other sequences into "native" chains

using `Singleton` and `Append`?

For example:

```scala
Chain.fromSeq(Vector(0, 1, 2, 3))
```

```
            Append
            |    |
       Append    Append
       |    |    |    |
       0    1    2    3

              vs

     Wrap(Vector(0, 1, 2, 3))
```

Thoughts?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Wrap - performance

> Do we strictly need `Wrap`?

Helps when trying to concatenate sequences

Can quickly lift them into chains, then concat as chains

---

# Wrap - abstraction

> Do we strictly need `Wrap`?

Helps contain unusual sequences

```scala
Chain.fromSeq(0 to 1_000_000)

Chain.fromSeq(naturals) // infinite
```

---

# Wrap

Like an ensemble

Can combine different kinds of sequences together

---

# Wrap

> Can combine different kinds of sequences together

Or even sequences of the same kind:

```scala
val bigList1: List[Int] = List(...)

val bigList2: List[Int] = List(...)

// Compare:

(bigList1 ++ bigList2).map(_ * 2)

(Chain(bigList1) ++ Chain(bigList2)).map(_ * 2)
```

---

# Wrap

```scala
(bigList1 ++ bigList2).map(_ * 2)

(Chain(bigList1) ++ Chain(bigList2)).map(_ * 2)
```

Second one doesn't create an inefficient intermediate collection

## But!

But if you need a `List` as your final result,

it will be the same:

```scala
// Already a list
(bigList1 ++ bigList2).map(_ * 2)

// Convert to a list
(Chain(bigList1) ++ Chain(bigList2)).map(_ * 2).toList
```

---

# Wrap Mini Summary

Allows an ad-hoc ensemble of clumps of data

Clumps can maintain their unique efficient representations

As opposed to flattening it all out into a homogeneous structure

---

# Wrap Mini Summary

It's a performance optimization for appending

Makes sense as this is the purpose of `Chain`

---

# Wrap Mini Summary

> It's a performance optimization for appending

It introduces complexity to the mental model

We are generally shielded from that

But...

---

```
 ____       _   _           _             _           _ 
|  _ \ __ _| |_| |__   ___ | | ___   __ _(_) ___ __ _| |
| |_) / _` | __| '_ \ / _ \| |/ _ \ / _` | |/ __/ _` | |
|  __/ (_| | |_| | | | (_) | | (_) | (_| | | (_| (_| | |
|_|   \__,_|\__|_| |_|\___/|_|\___/ \__, |_|\___\__,_|_|
                                    |___/               
  ____ _           _           
 / ___| |__   __ _(_)_ __  ___ 
| |   | '_ \ / _` | | '_ \/ __|
| |___| | | | (_| | | | | \__ \
 \____|_| |_|\__,_|_|_| |_|___/
                               
```

Chain inherits any pathological behavior from sequences it wraps 

---

# Example

```scala
val quirkyChain: Chain[Int] = Chain.fromSeq(naturals) ++ Chain.one(42)
// Append(Wrap(naturals), Singleton(42))
```

```
                              Append
                              |    |
   Wrap(LazyList(0, 1, 2, ...))    42
```

:hmm-parrot:

The 42 is kind of "hidden"

---

# Sequence?

```
                              Append
                              |    |
   Wrap(LazyList(0, 1, 2, ...))    42
```

Is this chain conceptually a sequence?

ie. does it have a deterministic concept of the i'th element?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Sequence?

```
                              Append
                              |    |
   Wrap(LazyList(0, 1, 2, ...))    42
```

> Q: Is this chain conceptually a sequence?
>
> ie. does it have a deterministic concept of the i'th element?

For example, how would you answer the question:

> What's the 5'th element?

---

# Sequence

> What's the 5'th element?

You would say:

> 5

or maybe:

> A creative and weird 1997 scifi movie starring
>
> Bruce Willis, Gary Oldman and Milla Jovovich

:old-timey-parrot:

---

# My hunch

The authors of `Chain` are ignoring cases this or just didn't consider it

---

# Conversion to finite sequences

For example `Chain` has `.toList` and `.toVector`

---

# `filter` and `map` are broken

```scala
@ def nats(start: Int): LazyList[Int] = start #:: nats(start + 1) 

@ val quirkyChain = Chain.fromSeq(nats(0)) ++ Chain.one(42) 
// Append(Wrap(LazyList(...)), Singleton(42))

@ quirkyChain.map(_ * 2) 
// :fidget-spinner:
```

---

# Main use case

Wrapping errors:

```scala
List(
  // Name validation
  Chain("Invalid character in name"),
  // Password validation
  Chain("Password is too short", "Password must contain a digit"),
  // Email validation
  Chain("Email already in use"),
)

// flatten down to a single list of errors

Chain(
  "Invalid character in name",
  "Password is too short",
  "Password must contain a digit",
  "Email already in use"
)
```

Lots of small chains being combined into a big chain

---

# Infinite errors?

```scala
Chain(
  "Invalid character in name",
  "Password is too short",
  "Password must contain a digit",
  "Email already in use"
)
```

> Lots of small chains being combined into a big chain

As bad as our users are,

they probably won't be generating infinite chains of validation errors

---

# My thoughts

## Localized optimizations

Chain seems to make sense for speeding up localized operations where:

- want to avoid some heavy intermediate collections due to appending


- it's easy to reason about the data going into it


- easy to understand the structure of the chain that will be created

## Not a good abstraction

```scala
def doSomething(chain: Chain[Int]): Unit = ...
```

Similar to `Seq`, when you're given a `Chain`, you know nothing about:

- topology


- clumps of data inside `Wrap` nodes

---

```
 _   _ _     _ _             
| | | (_) __| (_)_ __   __ _ 
| |_| | |/ _` | | '_ \ / _` |
|  _  | | (_| | | | | | (_| |
|_| |_|_|\__,_|_|_| |_|\__, |
                       |___/ 
 _   _          
| |_| |__   ___ 
| __| '_ \ / _ \
| |_| | | |  __/
 \__|_| |_|\___|
                
 ____       _        _ _     
|  _ \  ___| |_ __ _(_) |___ 
| | | |/ _ \ __/ _` | | / __|
| |_| |  __/ || (_| | | \__ \
|____/ \___|\__\__,_|_|_|___/
                             
```

The ADT I showed before was a simplification

---

# Hiding details away

This is what it actually looks like:

```scala
sealed abstract class Chain[+A] { ... }

object Chain extends ChainInstances {

  private[data] case object Empty extends Chain[Nothing] { ... }

  final private[data] case class Singleton[A](a: A) extends Chain[A] { ... }

  final private[data] case class Append[A](left: Chain[A], right: Chain[A]) extends Chain[A] { ... }

  final private[data] case class Wrap[A](seq: Seq[A]) extends Chain[A] { ... }

  ...

  // All return `Chain` specifically

  def empty[A]: Chain[A] = nil

  def one[A](a: A): Chain[A] = Singleton(a)

  def concat[A](c: Chain[A], c2: Chain[A]): Chain[A] = ...
}
```

It's good that it's all hidden away

---

```
 _   _             _____                 _          ____ _           _       
| \ | | ___  _ __ | ____|_ __ ___  _ __ | |_ _   _ / ___| |__   __ _(_)_ __  
|  \| |/ _ \| '_ \|  _| | '_ ` _ \| '_ \| __| | | | |   | '_ \ / _` | | '_ \ 
| |\  | (_) | | | | |___| | | | | | |_) | |_| |_| | |___| | | | (_| | | | | |
|_| \_|\___/|_| |_|_____|_| |_| |_| .__/ \__|\__, |\____|_| |_|\__,_|_|_| |_|
                                  |_|        |___/                           

cats.data.NonEmptyChain
```

---

# NonEmptyChain

Same old deal so will skip the demo

---

# Reuse

`NonEmptyChain` reuses the `Chain` ADT:

```scala
// case object Empty extends Chain[Nothing]

case class Singleton[A](a: A) extends Chain[A]

case class Append[A](left: Chain[A], right: Chain[A]) extends Chain[A]

case class Wrap[A](seq: Seq[A]) extends Chain[A]
```

Factory methods police the creation of these (they're private)

---

# Reuse

Structurally, a `NonEmptyChain` really is just a `Chain` that's not-empty

```scala
@ nec 
// NonEmptyChain[Int] = Append(Singleton(0), Wrap(ArraySeq(1, 2, 3)))

@ nec.toChain 
//         Chain[Int] = Append(Singleton(0), Wrap(ArraySeq(1, 2, 3)))
```

Compare with `List/NonEmptyList` which are different ADT's

---

# One point

`Chain` and `NonEmptyChain` are different types to the compiler

But their runtime representations are identical (except Empty)

(Implemented with a lot of complex type trickery)

---

# Being a troll

Use reflection to create an empty `NonEmptyChain`:

```scala
@ Chain.empty[Int].asInstanceOf[NonEmptyChain[Int]]
```

This works!

:pirate-parrot:

---

```
__        __                     _             
\ \      / / __ __ _ _ __  _ __ (_)_ __   __ _ 
 \ \ /\ / / '__/ _` | '_ \| '_ \| | '_ \ / _` |
  \ V  V /| | | (_| | |_) | |_) | | | | | (_| |
   \_/\_/ |_|  \__,_| .__/| .__/|_|_| |_|\__, |
                    |_|   |_|            |___/ 
 _   _ _ __  
| | | | '_ \ 
| |_| | |_) |
 \__,_| .__/ 
      |_|    
  ____ _           _       
 / ___| |__   __ _(_)_ __  
| |   | '_ \ / _` | | '_ \ 
| |___| | | | (_| | | | | |
 \____|_| |_|\__,_|_|_| |_|
                           
```

```scala
Wrap(chain)
```

```
type mismatch;
 found   : cats.data.Chain[Int]
 required: Seq[?]
Chain.fromSeq(chain)
              ^
```

---

# Chain ADT

```scala
sealed abstract class Chain[+A]

case object Empty extends Chain[Nothing]
case class Singleton[A](a: A) extends Chain[A]
case class Append[A](left: Chain[A], right: Chain[A]) extends Chain[A]
case class Wrap[A](seq: Seq[A]) extends Chain[A]
```

A structure optimized for appending

A beefier version of `List`

---

# Chain - details hidden

Can't interact directly with the ADT

Use factory methods to create them

---

# NonEmptyChain

Uses the same ADT under the hood

Factory methods ensure you can't create an empty one

---

# lengthCompare

And don't forget to start using `lengthCompare`!

---

# Further reading

[Cats Chain docs](https://typelevel.org/cats/datatypes/chain.html)

(Hopefully it will make sense now)

---

# Next time

Validation

---

# QnA
