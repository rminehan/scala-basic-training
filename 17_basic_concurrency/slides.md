---
author: Rohan
date: 2021-07-28
title: Basic Concurrency
---

```
 ____            _      
| __ )  __ _ ___(_) ___ 
|  _ \ / _` / __| |/ __|
| |_) | (_| \__ \ | (__ 
|____/ \__,_|___/_|\___|
                        
  ____                                                      
 / ___|___  _ __   ___ _   _ _ __ _ __ ___ _ __   ___ _   _ 
| |   / _ \| '_ \ / __| | | | '__| '__/ _ \ '_ \ / __| | | |
| |__| (_) | | | | (__| |_| | |  | | |  __/ | | | (__| |_| |
 \____\___/|_| |_|\___|\__,_|_|  |_|  \___|_| |_|\___|\__, |
                                                      |___/ 
```

---

# Analytics Code Base

Small amount of concurrency

Need a little bit of training

---

# DataIQ Code Base

Much more concurrency

Concepts will be very applicable

---

# Overall Goal

Vaccinating you against concurrency bugs

- understanding how they work


- instill a healthy fear of side effects


- basic awareness of tools for dealing with simple concurrency scenarios

Also a little on concepts like blocking and why it's bad

---

# Agenda

- dangers of mutable state


- clarifying mutations 


- mid-demo quiz


- concurrency concepts


- atomic primitives


- understanding locks conceptually


- anti-blocking brainwashing

---

```
 ____                                  
|  _ \  __ _ _ __   __ _  ___ _ __ ___ 
| | | |/ _` | '_ \ / _` |/ _ \ '__/ __|
| |_| | (_| | | | | (_| |  __/ |  \__ \
|____/ \__,_|_| |_|\__, |\___|_|  |___/
                   |___/               
        __ 
  ___  / _|
 / _ \| |_ 
| (_) |  _|
 \___/|_|  
           
                 _        _     _      
 _ __ ___  _   _| |_ __ _| |__ | | ___ 
| '_ ` _ \| | | | __/ _` | '_ \| |/ _ \
| | | | | | |_| | || (_| | |_) | |  __/
|_| |_| |_|\__,_|\__\__,_|_.__/|_|\___|
                                       
     _        _       
 ___| |_ __ _| |_ ___ 
/ __| __/ _` | __/ _ \
\__ \ || (_| | ||  __/
|___/\__\__,_|\__\___|
                      
```

---

# Dangers of mutable state

Other people can "mess with" your data:

```scala
val myData = Array(0, 10, 5, -3)

// Use Boban's analysis library
val report1 = Bobanware.analyze(myData)

// Use Bobanita's analysis library
val report2 = Bobanitaware.analyze(myData)
```

---

# Messing with data

```scala
val myData = Array(0, 10, 5, -3)

// Use Boban's analysis library
val report1 = Bobanware.analyze(myData)

// Use Bobanita's analysis library
val report2 = Bobanitaware.analyze(myData)
```

The `Bobanware` library:

```scala
object Bobanware {
  def analyze(data: Array[Int]): String = {
    ...
    data(0) = 10
    ...
  }
}
```

---

# Defensive copying

You really can't guarantee that other code will play nicely

Leads to defensive copying:

```scala
val myData = Array(0, 10, 5, -3)

val copy1 = Array.copyOf(myData, myData.length)
val report1 = Bobanware.analyze(copy1)

val copy2 = Array.copyOf(myData, myData.length)
val report2 = Bobanitaware.analyze(copy2)
```

Easy to forget this and get subtle bugs (particularly with arrays)

---

# Immutable data

Very easy to reason about

You can share it far and wide without any worries

No defensive copying needed

(Recall Java Jungle analogy)

---

# "Mutating" immutable data

Q: But what about when we want to modify it?

A: Make an immutable copy

---

# Functional data structures

> Make an immutable copy

Pick a structure that allows reuse:

e.g.

- `List` for prepending


- `Chain` for appending


- `Vector` for random insertions

---

# Array?

Not designed for reuse

A new array can't delegate some of its data to an old array

---

# Array?

> Not designed for reuse

Copying is expensive

People get lazy and will mutate it in place

---

# Summary of section

- mutable structures expose you to subtle side effects


- can lead to defensive copying which is expensive


- immutable structures don't have this problem


- but pick an immutable structures suitable to your "mutation" to avoid inefficient copying

---

# Summary of section

ie. a good functional data structure can give us the best of both worlds:

- immutable, safe


- no expensive copying

---

```
  ____ _            _  __       _             
 / ___| | __ _ _ __(_)/ _|_   _(_)_ __   __ _ 
| |   | |/ _` | '__| | |_| | | | | '_ \ / _` |
| |___| | (_| | |  | |  _| |_| | | | | | (_| |
 \____|_|\__,_|_|  |_|_|  \__, |_|_| |_|\__, |
                          |___/         |___/ 
     _     _      
 ___(_) __| | ___ 
/ __| |/ _` |/ _ \
\__ \ | (_| |  __/
|___/_|\__,_|\___|
                  
       __  __           _       
  ___ / _|/ _| ___  ___| |_ ___ 
 / _ \ |_| |_ / _ \/ __| __/ __|
|  __/  _|  _|  __/ (__| |_\__ \
 \___|_| |_|  \___|\___|\__|___/
                                
```

---

# Clarifying side effects

Two kinds of side effects:

- mutating where something points


- mutating what it points at

---

# Where

> mutating where something points

Relates to `var`

```scala
var list = List(1, 2, 3)

list = List(4, 5, 6)
```

```
            List(1, 2, 3)       List(4, 5, 6)
list ------/\                  /\
                               ||
list ----------------------------
```

---

# Clarifying where

> mutating where something points

The original list isn't changed

```scala
@ var list = List(1, 2, 3) 

@ val list2 = list 

@ list = List(4, 5, 6) 

@ list2 
// List(1, 2, 3)
```

```
             List(1, 2, 3)       List(4, 5, 6)
list  ------/\                  /\
list2 ------/\                  ||
                                ||
list  ----------------------------
```

A mutable pointer, pointing to an immutable object

---

# What

> mutating what it points at

```scala
val array = Array(1, 2, 3)

array(0) = 10
```

```
                  10
                  ||
                  \/
             Array(1, 2, 3)
array ------/\
```

An immutable pointer, pointing to a mutable object

---

# Confusion I see

> "Array is mutable so I should use var"

```scala
var array = Array(1, 2, 3)
//^

array(0) = 10
```

No

Now you have a mutable pointer pointing to a mutable object

---

# val vs var

`val` means your pointer is stuck pointing at the one thing

```scala
@ val list = List(1, 2, 3) 

@ list = List(4, 5, 6) 
// reassignment to val
// list = List(4, 5, 6)
//      ^
```

It _doesn't_ guarantee that the data it's pointing at won't change:

```scala
val array = Array(0, 1, 2)

array(0) = 100
```

---

# Terminology: Mutating "in place"

There's a single object (usually an array)

Not producing a new object

But modifying that object

```scala
def sort(array: Array[Int]): Unit = {
  ... //                     ^^^^
}

val array = Array(10, 6, 8)
sort(array)
println(array) // Array(6, 8, 10)
```

```
                   6   8 10
                  ||  || ||
                  \/  \/ \/
             Array(10, 6, 8)
array ------/\
```

Efficient but awkward and dangerous

---

# Aside

Spire's `cfor` will be useful to us for mutating arrays "in place"

:boom:

---

```
 __  __ _     _       _                           
|  \/  (_) __| |     | | ___  ___ ___  ___  _ __  
| |\/| | |/ _` |_____| |/ _ \/ __/ __|/ _ \| '_ \ 
| |  | | | (_| |_____| |  __/\__ \__ \ (_) | | | |
|_|  |_|_|\__,_|     |_|\___||___/___/\___/|_| |_|
                                                  
             _     
  __ _ _   _(_)____
 / _` | | | | |_  /
| (_| | |_| | |/ / 
 \__, |\__,_|_/___|
    |_|            
```

---

# Question 1

True or false

> String is mutable

---

# Question 1

True or false

> String is mutable

False

`String` _is_ from the java standard library,

but thankfully they made it immutable!

We "modify" strings by making copies of them

---

# Question 2

What's the issue with this code snippet?

```scala
def gimmeList: List[Int] = {
  var list1 = List(1, 3, 5)
  val list2 = randomList(length = 3)
  list1 ++ list2
}
```

---

# Question 2

What's the issue with this code snippet?

```scala
def gimmeList: List[Int] = {
  var list1 = List(1, 3, 5)
//  ^
  val list2 = randomList(length = 3)
  list1 ++ list2
}
```

It's needlessly making `list` a `var`

It's never reassigned so just make it a `val`

---

# Question 3

How could you improve this code?

```scala
var currentUsers = Array(user1, user2, user3, ...)
var newUsers = Array(user4, user5, user6, ...)

// Combine current and new users watching out for some condition
var allUsers = currentUsers
for (newUser <- newUsers)
  if (allowed(allUsers, newUser))
    allUsers = allUsers ++ Array(newUser)
```

Might need to add them one at a time because of some rule like:

- can't have more than 5 people from the US


- can't have more than 10 people without id's

---

# Question 3

How could you improve this code?

```scala
var currentUsers = Array(user1, user2, user3, ...)
var newUsers = Array(user4, user5, user6, ...)

// Combine current and new users watching out for some condition
var allUsers = currentUsers
for (newUser <- newUsers)
  if (allowed(allUsers, newUser))
    allUsers = allUsers ++ Array(newUser)
```

- needlessly using `var` for `currentUsers` and `newUsers`


- array is not a good structure - you're getting O(n^2) copying overhead...

---

# Buffers

If we need to add them incrementally like this, use an intermediate buffer:

```scala
import scala.collection.mutable.ArrayBuffer

val allUsersBuffer = ArrayBuffer(currentUsers: _*)

for (newUser <- newUsers)
  if (allowed(allUsers, newUser))
    allUsers.append(newUser)

val allUsers = allUsersBuffer.toArray
```

A buffer approach makes sense:

- more direct than the FP equivalent (`fold`)


- the buffer never leaves our local context so don't need to worry about it being messed with

---

# Fold equivalent

```scala
val seed: List[User] = currentUsers.toList.reverse

val allUsersList: List[User] = newUsers.foldLeft(seed) {
  case (acc, newUser) => 
    if (allowed(acc, newUser)) newUser :: acc
    else acc
}

val allUsers: Array = allUsersList.reverse.toArray
```

O(n) but has two 4 extra passes:

- two reverses
- converting from array to list back to array

A bit more convoluted than the buffer approach (depends what you're used to)

---

# Question 4

What's the issue here?

```scala
var users: Array[User] = ...

val signupDistribution = generateDistribution(users)

writeToDB(users)
```

---

# Question 4

What's the issue here?

```scala
var users: Array[User] = ...

val signupDistribution = generateDistribution(users)

writeToDB(users)
```

- `users` doesn't need to be a `var`

It's mutability is inherently represented by the `Array` itself

- `generateDistribution` might mutate our array before writing out to the db

---

# End of quiz

The random number generator award goes to:

...

---

```
  ____                                                      
 / ___|___  _ __   ___ _   _ _ __ _ __ ___ _ __   ___ _   _ 
| |   / _ \| '_ \ / __| | | | '__| '__/ _ \ '_ \ / __| | | |
| |__| (_) | | | | (__| |_| | |  | | |  __/ | | | (__| |_| |
 \____\___/|_| |_|\___|\__,_|_|  |_|  \___|_| |_|\___|\__, |
                                                      |___/ 
  ____                           _       
 / ___|___  _ __   ___ ___ _ __ | |_ ___ 
| |   / _ \| '_ \ / __/ _ \ '_ \| __/ __|
| |__| (_) | | | | (_|  __/ |_) | |_\__ \
 \____\___/|_| |_|\___\___| .__/ \__|___/
                          |_|            
```

---

# Concurrency

When a program has 2+ logical "stories" it's running

---

# Example

> Developer doing 2 jira issues

The developer works on one issue for a few hours progressing it some steps

```
     Task 1                  Task 2
     - do x                  - do a
     - do y                  - do b
     - do z                  - do c
                             - do d
```

Then context switches to another issue and progresses it

---

# Concurrency != Parallelism

> Developer doing 2 jira issues

The developer isn't doing them simultaneously

One story gets put on hold while the other is being worked on

(Context switching)

---

# python: Concurrency != Parallelism

Typically does one thing at a time (GIL)

But you can have multiple "logical stories" that it works through

---

# Concurrency ==? Parallelism

Technically not the same

In most contexts they go together

(Particularly JVM languages as the JVM has built in parallelism)

So we get lazy and conflate the terms

---

# "Check then act" bugs

Scenario:

We have a multi-tasking robot doing two tasks:

```
Quiz Score: 0

      Task 1                   Task 2
      - A fetch score            - D fetch score
      - B add 5                  - E add 3
      - C update score           - F update score
```

When both tasks are finished, the score should be 8

---

# Scenario

```
Quiz Score: 0

      Task 1                   Task 2
      - A fetch score            - D fetch score
      - B add 5                  - E add 3
      - C update score           - F update score
```

Suppose the robot does this:

- A fetch score (0)
- B add 5 (5)
- (context switch)
- D fetch score (0)
- E add 3 (3)
- F update score (score=3)
- (context switch)
- C update score (score=5)

The final score is 5

---

# "Check then act"

```
Quiz Score: 0

      Task 1                   Task 2
      - A fetch score            - D fetch score   |  <-- check
      - B add 5                  - E add 3         |
      - C update score           - F update score       |  <-- act
```

Tasks 1 and 2 use a "check then act" pattern

The danger is when they get separated

---

# Check then act

```
- A fetch score (0)       |   <-- check
- B add 5 (5)             |
- (context switch)
- D fetch score (0)
- E add 3 (3)
- F update score (score=3)
- (context switch)
- C update score (score=5)   | <-- act
```

Stuff happened between the check and the action

It violates the implicit assumption

The final write is based on stale data

---

# Root causes - 1

> mutable shared state

Two separate stories are mutating the same state (the score)

---

# Root causes - 2

> concurrency

Concurrency makes it possible for instructions to be executed between "check" and "act"

```
- A fetch score (0)       |   <-- check
- B add 5 (5)             |
- (context switch)
- D fetch score (0)
- E add 3 (3)
- F update score (score=3)
- (context switch)
- C update score (score=5)   | <-- act
```

We can't control the execution of instructions

---

# Root causes - parallelism?

Didn't need parallelism for this bug

Would happen in a concurrent non-parallel setup

---

```
    _   _                  _      
   / \ | |_ ___  _ __ ___ (_) ___ 
  / _ \| __/ _ \| '_ ` _ \| |/ __|
 / ___ \ || (_) | | | | | | | (__ 
/_/   \_\__\___/|_| |_| |_|_|\___|
                                  
 ____       _           _ _   _                
|  _ \ _ __(_)_ __ ___ (_) |_(_)_   _____  ___ 
| |_) | '__| | '_ ` _ \| | __| \ \ / / _ \/ __|
|  __/| |  | | | | | | | | |_| |\ V /  __/\__ \
|_|   |_|  |_|_| |_| |_|_|\__|_| \_/ \___||___/
                                               
```

A simple solution from the java standard library

---

# Atomics

```scala
import java.util.concurrent.atomic._
```

Objects designed to be mutated "in place"

To the repl!

---

# How does it work?

Basically a wrapper around a little piece of state (e.g. an `Int`)

Mutations get queued up

```
 -----
|     |
|  1  |             
|     |   queue    +1     +3    +2   ....
 -----    here
```

Each update "locks" the data. It has exclusive access.

Makes "check then act" safe

---

# Back to our example

```scala
import java.util.concurrent.atomic.AtomicInteger

val score = new AtomicInteger(0)

// Task 1
score.getAndAdd(5)

// Task 2 (running somewhere else)
score.getAndAdd(3)
```

---

# But don't do this

```scala
import java.util.concurrent.atomic.AtomicInteger

val score = new AtomicInteger(0)

// Task 1
val currentScore = score.get
val newScore = currentScore + 5
score.set(newScore)

// Task 2 (running somewhere else)
val currentScore = score.get
val newScore = currentScore + 3
score.set(newScore)
```

This misses the point of how to use atomics

You need to capture the entire "check then act" as a single operation

The code is the effectively the same as using a regular `Int`

---

# Atomic summary

Atomics give us two things:

- nice api of atomic operations for "check then act" use cases


- thread safe if these things are happening in parallel

---

```
 _               _    
| |    ___   ___| | __
| |   / _ \ / __| |/ /
| |__| (_) | (__|   < 
|_____\___/ \___|_|\_\
                      
  ____            _             _   _             
 / ___|___  _ __ | |_ ___ _ __ | |_(_) ___  _ __  
| |   / _ \| '_ \| __/ _ \ '_ \| __| |/ _ \| '_ \ 
| |__| (_) | | | | ||  __/ | | | |_| | (_) | | | |
 \____\___/|_| |_|\__\___|_| |_|\__|_|\___/|_| |_|
                                                  
```

---

# Locking

Atomics are an example of "locking" state

```scala
val score = new AtomicInteger(0)

// Forced to wait until its our turn, then returns
score.getAndAdd(5)
```

What issue does this cause?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Lock Contention

> What issue does this cause?

```scala
// Thread 1
score.getAndAdd(1)
score.getAndAdd(2)

// Thread 2
score.getAndAdd(15)
score.getAndAdd(4)

// Thread 3
score.getAndAdd(3)
score.getAndAdd(3)
score.getAndAdd(6)

// Thread 4
score.getAndAdd(2)
score.getAndAdd(2)
score.getAndAdd(5)
score.getAndAdd(2)
```

You have to wait in line doing nothing waiting for your update

---

# Blocking vs Non-blocking

Today's todo list:

- buy vegemite


- go to the gym


- do laundry 

---

# Blocking vs Non-blocking

UPTO: Buying vegemite from the vegemite store

## Walk in (blocking)

Have to stand in line to buy your vegemite

## Online order (non-blocking)

Order online and get a notification for when to pick it up

In the meantime can go to the gym and do laundry

---

# Lock Contention

Waiting in line at the vegemite store is analogous to "lock contention"

It's bad because you're doing nothing

---

```
 ____  _            _    _             
| __ )| | ___   ___| | _(_)_ __   __ _ 
|  _ \| |/ _ \ / __| |/ / | '_ \ / _` |
| |_) | | (_) | (__|   <| | | | | (_| |
|____/|_|\___/ \___|_|\_\_|_| |_|\__, |
                                 |___/ 
```

Brain washing section

---

# Locking => Blocking

Being forced to wait for something is a form of "blocking"

Locking is an example of blocking (everyone is blocked waiting for the lock)

---

# Group chant

Everyone unmute and we'll all say together:

> Blocking is bad
>
> Blocking is bad
>
> Blocking is bad

---

# Non-blocking

Generally has the vibe of:

- put a message into a queue (fast)


- get notified when your message is processed

More complex, but uses resources much more efficiently

---

# Synonyms

Blocking = Synchronous

Non-blocking = Asynchronous

---

# Atomics?

Blocking/synchronous :(

You're stuck waiting until it completes:

```scala
val oldValue = score.getAndAdd(3) // Wait until it's processed
// Continue execution
println(s"Score updated from $oldValue")
```

---

# Non-blocking options?

Beyond the scope of today :sad-parrot:

Would look a bit like:

```scala
score.getAndAdd(3).map { oldValue =>
  println("Score updated")
}
```

---

# Further reading

- `Ref` from cats-effect for example


- `Future` from the standard library


- `ZIO`

---

# Analytics Scripts

> Blocking is bad

It's okay for analytics scripts as they aren't concurrent

Nothing else to do

Different to a server with many requests

---

```
  ____               
 / ___|__ _ ___  ___ 
| |   / _` / __|/ _ \
| |__| (_| \__ \  __/
 \____\__,_|___/\___|
                     
 ____  _             _       
/ ___|| |_ _   _  __| |_   _ 
\___ \| __| | | |/ _` | | | |
 ___) | |_| |_| | (_| | |_| |
|____/ \__|\__,_|\__,_|\__, |
                       |___/ 
```

Usages in the analytics codebase

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

Mutable state is dangerous to pass around

Someone might mess with it

---

# Summary

In concurrent situations it's even more dangerous to have shared mutable state

Can get "check then act" bugs

---

# Summary

Atomic types are helpful for us

They block but it's okay in that context

---

# Summary

Blocking is bad in contexts where there's always something to do

---

# QnA
