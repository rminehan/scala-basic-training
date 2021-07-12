---
author: Rohan
date: 2021-07-06
title: List and NonEmptyList
---

An aside chopped out from the original presi for time reasons.

Originally went after the "Abstraction vs Performance" section.

Willy covered this very briefly in his talk and I wanted to reiterate it.

---

# Aside: Abstraction vs Performance

(Recap from Willy's talk)

`Seq`

---

# Seq

The abstraction for sequence types:

- `List`


- `Vector`


- `LazyList`


- `Range`

etc...

ie. has a concept of an i'th element

(not map or set)

---

# Aside: Seq

You have heard it said:

> Program against abstractions, not implementations

(Generally good advice)

```scala
def processUsers(users: Seq[User]): Seq[User] = {
  ...
}

// Can use with many Sequence types
processUsers(list)
processUsers(vector)
processUsers(array)
```

---

# Aside: Seq

You have heard it said:

> Program against abstractions, not implementations

But I say to you:

> For sequences the con's often outweigh the pro's

---

# In the author's seat

```scala
def processUsers(users: Seq[User]): Seq[User] = {
  // Prepend? O(1)? O(n)? O(n^2)?

  // Append?

  // Map?

  // Length? Is that safe?
}
```

No idea how these operations will perform

---

# Example from previous training

```scala
(0 to 1000000)  // range - constant memory represented by start, stop, step

(0 to 1000000).filter(_ != 1000) // Becomes a vector, O(n) memory
```

Filtering makes it bigger!

Oh the irony!

---

# Behavior vs Data

> Program against abstractions, not implementations

## Behavior

Abstractions make sense for behavior, e.g.

```scala
trait Database {
  def find(id): Option[Person]
  def write(person: Person): Unit
}
```

## Data

We tend to want to know as much as possible about it

Abstractions would cause us to throw away information

---

# Benefit of different seq's?

```scala
def processUsers(users: Seq[User]): Seq[User] = {
  ...
}

processUsers(list)
processUsers(vector)
processUsers(array)
```

How often is this really useful?

Usually these methods are in a very specific context

where the data will always be in a particular format

(So often there's no real pro's of abstraction in this case)
