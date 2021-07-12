---
author: Rohan
date: 2021-07-08
title: NonEmptyChain Extras
---

Slides that were removed from the original presi for time

---

# Playing with it

```scala
import cats.data.NonEmptyChain
```

To the repl!

---

# Summary - constructing

```scala
@ val nec = NonEmptyChain(1, 2, 3) 
// Append(Singleton(1), Wrap(ArraySeq(2, 3)))
// ^^^^^^ ^^^^^^^^^     ^^^^
// Same structures Chain uses

@ NonEmptyChain.one(3) 
// Singleton(3)
```

---

# Summary - chain interop

```scala
@ NonEmptyChain.fromChain(Chain.empty) 
// None

@ NonEmptyChain.fromChain(Chain(1, 2, 3, 4)) 
// Some(Wrap(ArraySeq(1, 2, 3, 4)))
```

---

# Summary - nel interop

```scala
@ import cats.data.NonEmptyList 

@ NonEmptyChain.fromNonEmptyList(NonEmptyList.of(1, 2, 3)) 
// Wrap(List(1, 2, 3))
// No option needed

@ nec.toNonEmptyList 
```

---

# Summary - api

```scala
@ nec.map(_ * 2) 
// NonEmptyChain[Int] = Wrap(Vector(0, 2, 4, 6))

@ nec.filter(_ > 1) 
// Chain[Int] = Append(Singleton(2), Singleton(3))
// Collapses to a Chain
```

---

# Reuse

`NonEmptyChain` doesn't need a separate ADT

It reuses the `Chain` ADT:

```scala
// Not this one
case object Empty extends Chain[Nothing]

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

# Grumpy Compiler

Sees them as different types:

```scala
@ val chain: Chain[Int] = NonEmptyChain(0, 1, 2, 3) 
//cmd32.sc:1: type mismatch;
// found   : cats.data.NonEmptyChain[Int]
//    (which expands to)  cats.data.NonEmptyChainImpl.Type[Int]
// required: cats.data.Chain[Int]

@ val chain: Chain[Int] = NonEmptyChain(0, 1, 2, 3).toChain 
// Works
```

It's good that the compiler is grumpy

Otherwise you could pass `Chain` where `NonEmptyChain` is expected

---

# toChain under the hood

From the source:

```scala
  final def toChain: Chain[A] = NonEmptyChainImpl.unwrap(value)

  private[data] def unwrap[A](s: Type[A]): Chain[A] =
    s.asInstanceOf[Chain[A]]
```

---

# Being a troll

Use reflection to create an empty `NonEmptyChain`:

```scala
@ Chain.empty[Int].asInstanceOf[NonEmptyChain[Int]]
```

This works!

:pirate-parrot:
