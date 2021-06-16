---
author: Rohan
date: 2021-06-16
title: Iterators
---

```
 ___ _                 _                 
|_ _| |_ ___ _ __ __ _| |_ ___  _ __ ___ 
 | || __/ _ \ '__/ _` | __/ _ \| '__/ __|
 | || ||  __/ | | (_| | || (_) | |  \__ \
|___|\__\___|_|  \__,_|\__\___/|_|  |___/
                                         
```

Dirty but useful

---

# Warning

Iterators are dangerous buggy things.

---

# Why are we learning about them?

Because they're used in the analytics code base.

---

# Alternatives?

There are better alternatives to iterators,

but they involve introducing more complex libraries.

We are trying to keep things simple in that code base.

---

# What problem do iterators solve?

Processing a gigantic structure with a constant amount of memory.

e.g.

```
SELECT *
FROM leads
```

(Table has millions of rows)

----

# What we can't do:

```scala
val leads: List[Lead] = runQuery("SELECT * FROM leads")
```

This would mean loading the entire thing into memory simultaneously.

In general, not a scalable approach.

---

# Processing example

We want to count the number of leads satisfying some condition represented by:

```scala
def predicate(lead: Lead): Boolean = ...
```

---

# Where to perform the logic

Where possible, you try and do this on the database side.

```
            request
           -------->
   client             db
            response
           <--------
```

---

# Client side processing


```
            request (get me all rows)
           -------->
   client             db
            response
           <--------
           100M rows
```

IO heavy

Sends back all the data

---

# Server side processing

```
            request (count rows satisfying predicate)
           -------->
   client             db
            response
           <--------
        (int) 44,238,630
```

Just sends back a result

Able to leverage internal sql optimizations

---

# Usually

Usually you want to do it on the database server.

But sometimes it's too hard to translate your `predicate` to sql.

---

# Issues with sql

Doesn't handle complex logic well.

Easy to make subtle mistakes (no type checking).

To represent complex logic you sometimes have to rely on lesser known features.

Extra complexity (like subqueries) can end up with bad performance anyway.

Sometimes we already have a nice scala `predicate` function (DRY).

---

# Example 1

> Count all the leads with email starting with 'a'.

Clear case for database side

---

# Example 2

> Find all the leads that have an Eagle signal followed
>
> by a contradicting Scorpion signal within 60 days
>
> where the Eagle signal has been confirmed by an oracle signal

Could probably do this with sql, but will be tricky.

We typically do this client side.

---

# Aside - auxiliary fields

We add auxiliary fields to make queries like example 2 easier.

e.g. parity, confirmation/contradiction fields

These are fields you won't find in prod mongo.

---

# Client or Server processing?

Not always clear.

Today's training is for those times you choose client side processing.

---

# Back to the scenario

We need to process millions of rows coming back from the database.

The memory of our JVM won't be enough to hold that simultaneously.

That rules out most `Seq` style collections from the standard library.

(Array, List, Vector, ...)

---

# Conceptually

Often we're building a final report that _will_ fit in constant memory.

e.g. a simple count

> Count the number of leads satisfying `predicate`

---

# Processing in batches

> Often we're building a final report that _will_ fit in constant memory.

We can process the data in big batches:

```scala
var counter = 0

for (batch <- batches)
  counter += batch.count(predicate)

println(s"Final count: $counter")
```

(Just for illustrative purposes, care needed here)

---

# Batch size

The crucial thing is that the client only needs enough memory to process one batch at a time.

e.g. 100K records

It can achieve its result using constant memory,

even if the query itself is effectively unbounded.

---

# Batches?

Under the hood it looks like this:

- client: query is `SELECT * FROM leads`


- client: 100K rows please


- database: here you go, saving your place at 100K


- (client processes those 100K updating the counter)


- client: another 100K rows please


- database: here you go, saving your place at 200K


- (client processes those 100K updating the counter)

...

(repeat until the database reports no more data)

---

# Cursor

The client and database have a concept of where they're up to (called a cursor)

> database: here you go, saving your place at 100K
>
> database: here you go, saving your place at 200K

The client pulls more when it can, updating the cursor

---

# Implementing batches

Q: How will this appear to us?

A: As iterators.

---

# Introducing iterators

To the repl!

---

# Summary of session

```scala
@ val list = List(0, 1, 2, 3, 4) 

@ val iterator = list.iterator 
iterator: Iterator[Int] = non-empty iterator

@ iterator.hasNext 
res2: Boolean = true

@ iterator.next() 
res3: Int = 0

@ iterator.hasNext 
res4: Boolean = true

@ iterator.next() 
res5: Int = 1

@ iterator.foreach(println) 
2
3
4
```

---

# What did the session teach us?

---

# Collections can produce iterators

```scala
val list = List(0, 1, 2, 3, 4) 

val iterator = list.iterator 
```

(So can other things)

---

# Iterators have a type parameter

```scala
@ val iterator = list.iterator 
// iterator: Iterator[Int] = non-empty iterator
//                    ^^^
```

---

# Iterators are _mutable_

Warning! Warning!

(Picture the robot from Lost in Space (the old one))

---

# Iterators are _mutable_

Calling `next()` on it pops out an element.

```scala
@ iterator.next() 
res3: Int = 0

@ iterator.hasNext 
res4: Boolean = true

@ iterator.next() 
res5: Int = 1

@ iterator.foreach(println) 
2
3
4
```

It's changing in place, not returning a new iterator.

Hence the empty brackets: `()`

---

# Understanding iteration

```scala
val list = List(0, 1, 2, 3, 4)

for (i <- list) {
  println(i)
}
```

Under the hood:

- the list is being asked for an iterator


- the list produces one setup at the start of the list


- the for comprehension iterates its way through it using `hasNext` and `next()`

```scala
while (iterator.hasNext) {
  val element = iterator.next()
  // Apply logic to element
  ...
}
```

---

# Ranges

Recall when we introduced ranges saying they are represented like:

```scala
case class IntRange(start: Int, stop: Int, step: Int)
```

e.g. `(1 to 100 by 2)` is `IntRange(start = 1, stop = 100, step = 2)`

---

# Producing values

```scala
case class IntRange(start: Int, stop: Int, step: Int)
```

Uses constant memory (3 ints).

> How do you iterate all the values in between then?

```scala
(1 to 10).foreach(println)
// 1
// 2
// ...
// 10
```

---

# Under the hood

`foreach` is asking our range for an iterator.

The iterator is separate to the range itself.

---

# Separation of iterable and iterator

Something is "iterable" if it has a mechanism to produce an iterator.

e.g. range

---

# Confusion: Iterable and Iterator

Don't confuse these terms, they're different

An `Iterable[A]` can produce an `Iterator[A]`.

```scala
trait Iterable[A] {
  def iterator: Iterator[A]
}
```

This lets `Iterable[A]` be immutable (e.g. range).

---

# Implementing our own range

Range is a good structure to build an intuition for iterators.

The iterable and iterator are quite different.

To the repl!

---

# Summary

```scala
case class Range(start: Int, stop: Int, step: Int) extends Iterable[Int] {
  def iterator: Iterator[Int] = new Iterator[Int] {
    var current = start
    def hasNext: Boolean = current < stop // stop is exclusive
    def next(): Int = {
      val ret = current
      current += step
      ret
    }
  }
}
```

The `current` is playing the same role as a cursor,

keeping track of where we're up to.

---

# Interview question

I ask people to implement factorial using `foldLeft`.

They write:

```scala
def fac(n: Int): Int = (1 to n).foldLeft(1)(_ * _)
```

---

# Interview question

Then I ask:

```scala
def fac(n: Int): Int = (1 to n).foldLeft(1)(_ * _)
```

> What's the time/space complexity?

(Let's assume Jon isn't at the interview to keep things simple)

:curse-of-the-jonathan:

---

# Interview question

> What's the time/space complexity?

```scala
def fac(n: Int): Int = (1 to n).foldLeft(1)(_ * _)
```

They talk about the internals of `foldLeft` forgetting about the range.

---

# Interview question

So I say:

> What about the range?

```scala
def fac(n: Int): Int = (1 to n).foldLeft(1)(_ * _)
```

And they get stumped.

The distinction between iterable/iterator is not clear.

Part of their brain sees `n` and thinks `O(n)`.

Part of their brain knows that doesn't make sense.

---

# Interview question

> What about the range?

```scala
def fac(n: Int): Int = (1 to n).foldLeft(1)(_ * _)
```

## Range

The `(1 to n)` builds a `IntRange` object which is constant memory.

## Iterator

The `foldLeft` causes it to produce an iterator like ours:

```scala
case class Range(start: Int, stop: Int, step: Int) extends Iterable[Int] {
  def iterator: Iterator[Int] = new Iterator[Int] {
    var current = start // <--- only state
    def hasNext: Boolean = current < stop
    def next(): Int = {
      val ret = current
      current += step
      ret
    }
  }
}
```

The only "data" that iterator has is `current`, so it's also constant memory.

## foldLeft

Internally we can assume it's using tail recursion or a loop, so is constant memory.

## Overall

Constant memory

---

# Why I don't like iterators

Spot the bug:

```scala
val leadsIterator: Iterator[Lead] = ...

// Count the leads matching predicate 1
val pred1Count = leadsIterator.count(predicate1)

// Count the leads matching predicate 2
val pred2Count = leadsIterator.count(predicate2)
```

To the repl!

---

# Example

```scala
@ val intIterator = (0 until 10).iterator 

@ val numEvens = intIterator.count(_ % 2 == 0) 
// numEvens: Int = 5

@ val numOdds = intIterator.count(_ % 2 != 1) 
// numOdds: Int = 0
```

Did the laws of maths change?

The issue is we drained the iterator on the first `count`.

The second one compiles and doesn't throw any runtime exceptions. Sneaky bug.

---

# The issue

Iterators feel like regular collections (same api).

We start to use them like that.

But they are mutable and can trick us.

---

# Mutability

Also being mutable they are also subject to concurrency bugs.

(Not so relevant in the analytics code base)

---

```
 ____                 _ _   ____       _   
|  _ \ ___  ___ _   _| | |_/ ___|  ___| |_ 
| |_) / _ \/ __| | | | | __\___ \ / _ \ __|
|  _ <  __/\__ \ |_| | | |_ ___) |  __/ |_ 
|_| \_\___||___/\__,_|_|\__|____/ \___|\__|
                                           
```

To sql now...

---

# The sql driver

```scala
import java.sql.{ResultSet, Statement}

val connection = ... // Setup with connection info

val statement: Statement = connection.createStatement()

val resultSet: ResultSet = statement.executeQuery("SELECT * FROM dataiq_analytics.leads LIMIT 100")
```

Your query result is represented by a `ResultSet`

---

# `ResultSet`

Iterator-like concept.

Has a `next` method.

```java
    boolean next() throws SQLException;
```

Doc string:

```
Moves the cursor forward one row from its current position.

...

Returns: true if the new current row is valid; false if there are no more rows
```

---

# Where's the data?

```java
class ResultSet {
    boolean next() throws SQLException;
    ...
}
```

vs Iterator:

```scala
trait Iterator[A] {
  def hasNext: Boolean
  def next(): A
}
```

The `ResultSet` _is_ the data.

---

# ResultSet as a cursor

The ResultSet is mutable.

It's _at_ a current row.

To get data from it, you call a `get*(column)` method (e.g. `getInt`, `getString`).

---

# Example for getting the emails out

```scala
import java.sql.{ResultSet, Statement}

val connection = ... // Setup with connection info

val statement: Statement = connection.createStatement()

val resultSet: ResultSet = statement.executeQuery("SELECT value FROM dataiq_analytics.leads LIMIT 2")

// Move the cursor to the first row (see doc string)
if (!resultSet.next())
  throw new Exception("Missing row 1")

// Extract data from it
val value1 = resultSet.getString("value")

// Move the cursor to the second row
if (!resultSet.next())
  throw new Exception("Missing row 2")

val value2 = resultSet.getString("value")
```

From the docstring:

```
A ResultSet cursor is initially positioned before the first row;
                                           ^^^^^^
the first call to the method next makes the first row the current row;
the second call makes the second row the current row, and so on.
```

...


---

# ResultSet

A very dirty java-ry way to process a cursor.

It's _like_ an iterator in how it mutates in place as you move through the cursor.

---

# Layers

```
Application (ResultSet)
  .next()     .next()    .next()      .next()   .next()   .next() ...


Driver
  3 loaded                            3 more please
                                             |       /|\
                                             |        |
Database                                    \|/       |
  3 served                                Process more


Time -------------------->
```

IO is seemless.

Controlled by:

```scala
statement.setFetchSize(250000)
```

(We wouldn't use 3! Too much IO!)

---

```
 ___ _                 _             
|_ _| |_ ___ _ __ __ _| |_ ___  _ __ 
 | || __/ _ \ '__/ _` | __/ _ \| '__|
 | || ||  __/ | | (_| | || (_) | |   
|___|\__\___|_|  \__,_|\__\___/|_|   
                                     
```

A `ResultSet` is like an iterator.

Let's turn it into a scala iterator so that we can use the nice iterator api,

e.g. `map`, `filter` etc...

An "adapter".

---

# Example

Conceptually we want an iterator of these:

```scala
case class EmailStatus(value: String, overallStatus: String)
```

from a query like:

```sql
SELECT value, overall_status
FROM dataiq_analytics.leads
WHERE lead_type = 'Email'
```

---

# Plan

- run the query to get a `ResultSet`


- build an iterator around that (same approach for `IntRange`)

---

# Code

```scala
case class EmailStatus(value: String, overallStatus: String)

val resultSet = statement.executeQuery(
  """SELECT value, overall_status
    |FROM dataiq_analytics.leads
    |WHERE lead_type = 'Email'
    """.stripMargin)

val iterator: Iterator[EmailStatus] = new Iterator[EmailStatus] {
  var lastNext = true

  def hasNext: Boolean = lastNext

  def next(): EmailStatus = {
    lastNext = resultSet.next()
    val value = resultSet.getString("value")
    val overallStatus = resultSet.getString("overall_status")
    EmailStatus(value, overallStatus)
  }
}
```

---

# Alternative

Now suppose it was a different query

```scala
case class Lead(value: String, overallStatus: String, updatedAt: Instant)

val resultSet = statement.executeQuery(
  """SELECT value, overall_status, updated_at
    |FROM dataiq_analytics.leads
    """.stripMargin)

val iterator: Iterator[Lead] = new Iterator[Lead] {
  var lastNext = true

  def hasNext: Boolean = lastNext

  def next(): Lead = {
    lastNext = resultSet.next()
    val value = resultSet.getString("value")
    val overallStatus = resultSet.getString("overall_status")
    val updatedAt = resultSet.getString("updated_at")
    Lead(value, overallStatus, updatedAt)
  }
}
```

Looks the same...

---

# Abstracting this

The parts that are different are how to parse your model from the `ResultSet`.

Otherwise the scaffolding is the same.

What does this call for?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Higher order function!

```scala
def resultSetToIterator[Record](resultSet: ResultSet)
                               (extractRecord: ResultSet => Record): Iterator[Record] = ...
```

---

# Implementing it

```scala
def resultSetToIterator[Record](resultSet: ResultSet)
                               (extractRecord: ResultSet => Record): Iterator[Record] =
  new Iterator[Record] {
    var lastNext = true

    def hasNext: Boolean = lastNext

    def next(): EmailStatus = {
      lastNext = resultSet.next()
      extractRecord(resultSet)
    }
  }
```

---

# Our code base

We have a method just like this, but it's implemented using existing tools from the standard library.

The effect is the same.

---

```
  ____                      _           
 / ___|___  _ __ ___  _ __ | | _____  __
| |   / _ \| '_ ` _ \| '_ \| |/ _ \ \/ /
| |__| (_) | | | | | | |_) | |  __/>  < 
 \____\___/|_| |_| |_| .__/|_|\___/_/\_\
                     |_|                
 _____     _     _ _             
|  ___|__ | | __| (_)_ __   __ _ 
| |_ / _ \| |/ _` | | '_ \ / _` |
|  _| (_) | | (_| | | | | | (_| |
|_|  \___/|_|\__,_|_|_| |_|\__, |
                           |___/ 
```

---

# Complex example

Count the number of leads that have:

- exactly one signal from all 3 vendors


- the scorpion signal is going against the crowd

ie. definitive ("Verified" or "Invalid") and disagrees with the other 2

```
-----------------------------------
| lead_id | source   | status     |
-----------------------------------
| lead-X  | scorpion | Verified   |
| lead-X  | eagle    | Verified   |
| lead-X  | impala   | Unverified |
-----------------------------------
| lead-Y  | impala   | Verified   |
| lead-Y  | scorpion | Invalid    |   <--- ding!
| lead-Y  | eagle    | Verified   |
-----------------------------------
| lead-Z  | impala   | Invalid    |
| lead-Z  | scorpion | Verified   |
-----------------------------------
...
```

---

# Our approach

Query like this:

```sql
SELECT lead_id, source, status
FROM dataiq_analytics.signals
WHERE source IN ('scorpion', 'eagle', 'impala')  -- reduce IO a bit
ORDER BY lead_id
```

then client side processing as this is too hard for sql.

```
-----------------------------------
| lead_id | source   | status     |
-----------------------------------
| lead-X  | scorpion | Verified   |
| lead-X  | eagle    | Verified   |
| lead-X  | impala   | Unverified |
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

---

# Client side logic

> Count the number of leads that have:
> - exactly one signal from all 3 vendors
> - the scorpion signal is going against the crowd

We're not examining row by row, but in chunks.

Not a typical `map/filter/foldLeft` operation.

```
-----------------------------------
| lead_id | source   | status     |
-----------------------------------
| lead-X  | scorpion | Verified   |
| lead-X  | eagle    | Verified   |
| lead-X  | impala   | Unverified |
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

---

# Zooming out

This is an example of a more general chunking problem:

```
X1     |
X2     | chunk
X3     |
---
Y1
Y2
Y3
---
Z1
Z2
...
```


We want to fold by chunks.

---

# Fold recap

You have an accumulator and process each element one by one updating the accumulator:

```scala
var acc = 0

for (i <- numbers)
  acc = acc + i

acc
```

There is some `combine(A, A): A` function.

---

# Folding chunks

This time we have our accumulator, but each update to it comes from processing a chunk.

```
X1     |
X2     | chunk
X3     |
---
Y1
Y2
Y3
---
Z1
Z2
...
```

```scala
var acc = ... // some seed

for (chunk <- chunks)
  acc = combine(acc, chunk) // Fold a new chunk into the accumulator

acc
```

We need a chunk based version of fold.

---

# Identifying chunks

Our fold function will be processing the iterator element by element.

How does it know when a chunk starts and ends?

```
X1
X2
X3
---   <---- ???
Y1
Y2
Y3
---   <---- ???
Z1
Z2
...
```

---

# Identifying chunks

Elements in the same chunk will share some kind of property.

For example the same lead id.

```
-----------------------------------
| lead_id | source   | status     |
-----------------------------------
| lead-X  | scorpion | Verified   |
| lead-X  | eagle    | Verified   |
| lead-X  | impala   | Unverified |
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

---

# Label

Call this property a "label" and represent it with `L`.

e.g. lead id is our label, and `L` might be `String` (depends on how it's represented).

---

# Label comparison

```
Data       Label   
--------------------
X1         X
X2         X
X3         X
Y1         Y     <------ label change
Y2         Y
Y3         Y
Z1         Z     <------ label change
Z2         Z
...        ...
```

If we generate a label for each element,

we can spot the chunk boundaries by seeing when the label changes.

---

# As code

We need a `computeLabel` parameter:

```scala
computeLabel: A => L
```

where `A` represents an element from our iterator.

---

# Simple folding

Recall our simple fold:

```scala
def fold[A](seq: Seq[A], seed: A)(combine: (A, A) => A): A = ...
```

---

# Folding to different types

Briefly we mentioned that sometimes we fold into a different type:

```scala
val charCount = List("abc", "def", "ghi").foldLeft(0) {
  case (accInt, nextStr) => accInt + nextStr.length
}
```

Folding strings into an `Int`.


---

# A and B

So we need two type parameters:

- `A` - the thing being folded


- `B` - the accumulator type

---

# Fill the gaps

> A - the thing being folded
>
> B - the accumulator type

What are the `?` below?

```scala
def fold[A, B](seq: Seq[?], seed: ?)(combine: (?, ?) => ?): ?
```

---

# Fill the gaps

> A - the thing being folded
>
> B - the accumulator type

What are the `???` below?

```scala
def fold[A, B](seq: Seq[A], seed: B)(combine: (B, A) => B): B
//                      |         |            |  |     |   |
//                    being     initial       acc |    new  final
//                   folded      acc              next acc  acc
```

---

# Folding 

```scala
val charCount = List("abc", "def", "ghi").foldLeft(0) {
  case (accInt, nextStr) => accInt + nextStr.length
}
// A = String
// B = Int
```

---

# Recapping our type parameters

- A - the thing being folded, e.g. `Iterator[A]`


- B - the accumulator type (seed, return type)


- L - the label for differentiating between chunks

---

# Folding chunks

Putting it together:

```scala
def foldLeftChunks[A, B, L](iter: Iterator[A], seed: B)
                           (computeLabel: A => L)
                           (combine: (Chunk[A], B) => B): B = ...
```

---

# Execution

```scala
def foldLeftChunks[A, B, L](iter: Iterator[A], seed: B)
                           (computeLabel: A => L)
                           (combine: (Chunk[A], B) => B): B = ...
```
```
acc = seed
X1   ->  X
X2   ->  X
X3   ->  X
Y1   ->  Y and Y != X
acc = combine(acc, X chunk)
Y2   -> Y
Y3   -> Y
Z1   -> Z and Z != Y
acc = combine(acc, Y chunk)
Z2   -> Z
...
(run out)
acc = combine(acc, final chunk)
```

It's a bit trickier than `foldLeft` because it can't process an old chunk

until it detects the start of a new chunk

or the end of the iterator.

---

# Back to our problem

> Count the number of leads that have:
> - exactly one signal from all 3 vendors
> - the scorpion signal is going against the crowd

```
-----------------------------------
| lead_id | source   | status     |
-----------------------------------
| lead-X  | scorpion | Verified   |
| lead-X  | eagle    | Verified   |
| lead-X  | impala   | Unverified |
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

## A - the element of the iterator

```scala
case class SignalRow(leadId: String, source: String, status: String)
```

## B - what we're folding into

`Int` or `Long` (a count)

## L - label for a group

`String` (the `leadId`)

---

# Putting it together

```scala
case class SignalRow(leadId: String, source: String, status: String)

val resultSet = statement.executeQuery("""
  |SELECT lead_id, source, status
  |FROM dataiq_analytics.signals
  |WHERE source IN ('scorpion', 'eagle', 'impala')
  |ORDER BY lead_id""".stripMargin)

val iterator = resultSetToIterator(rs) { rs =>
  SignalRow(rs.getString("lead_id"), rs.getString("source"), rs.getString("status"))
}

// Some predicate to determine if a chunk matches our condition:
// - exactly one signal from all 3 vendors
// - the scorpion signal is going against the crowd
def chunkMatchesCondition(chunk: Chunk[A]): Boolean = ...

val count = foldLeftChunks(iterator, 0)(_.leadId) { (currentCount, chunk) =>
  if (chunkMatchesCondition(chunk)) currentCount + 1
  else currentCount
}

println(s"Found $count leads that satisfy condition...")
```

where our method was:

```scala
def foldLeftChunks[A, B, L](iter: Iterator[A], seed: B)
                           (computeLabel: A => L)
                           (combine: (Chunk[A], B) => B): B = ...
```

---

# Contiguous

Note: `foldLeftChunks` assumes contiguous data.

Make sure to stick an `ORDER BY` in your query!

```
X1
X2
---
Y1
Y2
---
X3   will process this as a separate group
X4
---
Y3
---
X5
```

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

Where possible, we try to do processing on the database server.

Reduces IO.

---

# Summary

But sometimes the logic is too complex for sql.

Hence we have to pull the data and do client side processing.

---

# Summary

Iterators are useful for processing an unbounded data set using constant memory.

---

# Summary

The iterator is essentially a cursor marking where you're up to.

---

# Summary

Iterators are mutable and therefore dangerous

(particularly when you become used to working with immutable things)

---

# Summary

There are alternatives to iterators,

but that requires introducing more complex libraries.

---

# Summary

A sql query is surfaced to us as a `ResultSet`.

We have tools for turning that into an iterator.

From there we can use the nice iterator api.

---

# Summary

For "chunky" client side processing, use `foldLeftChunks`.

---

# QnA?
