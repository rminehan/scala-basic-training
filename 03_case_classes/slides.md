---
author: Rohan
title: Session 3 - Case classes
date: 2021-04-22
---

```
  ____               
 / ___|__ _ ___  ___ 
| |   / _` / __|/ _ \
| |__| (_| \__ \  __/
 \____\__,_|___/\___|
                     
      _                         
  ___| | __ _ ___ ___  ___  ___ 
 / __| |/ _` / __/ __|/ _ \/ __|
| (__| | (_| \__ \__ \  __/\__ \
 \___|_|\__,_|___/___/\___||___/
                                
```

(and companion objects)

---

# Reminder

Just yell out if you have questions.

---

# What we'll learn

- how scala represents java's "static" concept


- difference between regular classes and case classes


- understand the role of the companion object

---

# How we'll do it

By translating a java app to a scala one.

That helps you see how java concepts map to scala concepts.

---

# Starting example

A java wrapper for stictly positive integers.

```java
public class PositiveInt {
  private int _value;
  
  public PositiveInt(int value) {
    if (value > 0) {
      _value = value;
    }
    else {
      throw new IllegalArgumentException("Input isn't positive: " + value);
    }
  }

  public int get() {
    return _value;
  }

  public PositiveInt add(PositiveInt other) {
    return new PositiveInt(this.get() + other.get());
  }

  public static PositiveInt one() {
    return new PositiveInt(1);
  }
}
```

---

# Zooming out

Looking at the api:

```java
public class PositiveInt {
  
  public int get() { ... }

  public PositiveInt add(PositiveInt other) { ... }

  public static PositiveInt one() { ... }
}
```

`get()` and `add(PositiveInt)` are stateful - called on an instance.

`one` is static/stateless - really a "factory method"

They are mixed together in the same class.

---

# Scala and static?

There's no static keyword.

Static concepts live in the "companion object".

---

# First pass at migrating it

Java (compressed to fit on one slide)

```java
public class PositiveInt {
  private int _value;
  
  public PositiveInt(int value) {
    if (value > 0) { _value = value; }
    else { throw new IllegalArgumentException("Input isn't positive: " + value); }
  }

  public int get() { return _value; }

  public PositiveInt add(PositiveInt other) { return new PositiveInt(this.get() + other.get()); }

  public static PositiveInt one() { return new PositiveInt(1); }
}
```

Scala

```scala
public class PositiveInt(value: Int) {
  if (value <= 0) {
    throw new IllegalArgumentException(s"Input isn't positive: $value")
  }
  
  public def get: Int = value
  
  public def add(other: PositiveInt): PositiveInt = new PositiveInt(this.get + other.get)
}

object PositiveInt {
  def one: PositiveInt = new PositiveInt(1)
}
```

---

# Observations

```java
public class PositiveInt {
  public int get() { ... }
  public PositiveInt add(PositiveInt other) { ... }
  public static PositiveInt one() { ... }
}
```

```scala
public class PositiveInt(value: Int) {
  if (value <= 0) {
    throw new IllegalArgumentException(s"Input isn't positive: $value")
  }

  public def get: Int = value
  
  public def add(other: PositiveInt): PositiveInt = new PositiveInt(this.get + other.get)
}

object PositiveInt {
  public def one: PositiveInt = new PositiveInt(1)
}
```

- the class body _is_ the primary constructor


- there's an `object` with the same name as the class (called the "companion" object)


- the static factory method went to the companion object

---

# Second pass

```scala
class PositiveInt(val value: Int) {
  if (value <= 0) {
    throw new IllegalArgumentException(s"Input isn't positive: $value")
  }
  
  def add(other: PositiveInt): PositiveInt = new PositiveInt(this.value + other.value)
}

object PositiveInt {
  def one: PositiveInt = new PositiveInt(1)
}
```

---

# Comparing first and second attempts

Diff format (lines aligned for readability)

```scala
- public class PositiveInt(value: Int) {
+        class PositiveInt(val value: Int) {
   if (value <= 0) {
     throw new IllegalArgumentException(s"Input isn't positive: $value")
   }

-  public def get: Int = value

-  public def add(other: PositiveInt): PositiveInt = new PositiveInt(this.get   + other.get)
+         def add(other: PositiveInt): PositiveInt = new PositiveInt(this.value + other.value)
 }

object PositiveInt {
-  public def one: PositiveInt = new PositiveInt(1)
+         def one: PositiveInt = new PositiveInt(1)
}
```

- `public` keywords gone - that's the default in scala


- there's no `get` wrapper needed - the input value is exposed by `val`

```scala
val p = PositiveInt(3)

println(p.value) // prints 3
```

---

# Compare java to final scala version

Java (compressed)

```java
public class PositiveInt {
  private int _value = 0;
  
  public PositiveInt(int value) {
    if (value > 0) { _value = value; }
    else { throw new IllegalArgumentException("Input isn't positive: " + value); }
  }

  public int get() { return _value; }

  public PositiveInt add(PositiveInt other) { return new PositiveInt(this.get() + other.get()); }

  public static PositiveInt one() { return new PositiveInt(1); }
}
```

Scala (second iteration)

```scala
class PositiveInt(val value: Int) {
  if (value <= 0) {
    throw new IllegalArgumentException(s"Input isn't positive: $value")
  }
  
  def add(other: PositiveInt): PositiveInt = new PositiveInt(this.value + other.value)
}

object PositiveInt {
  def one: PositiveInt = new PositiveInt(1)
}
```

---

# Observations

- java has a lot of boilerplate!


- scala separates the "static" stuff from the "instance" stuff (which I like)

---

# A common pattern

Put all your factory methods in your companion objects.

Use the `apply` function.

```scala
object PositiveInt {
  def apply(i: Int): PositiveInt = {
    if (i > 0)
      new PositiveInt(i)
    else
      throw new IllegalArgumentException(...)
  }
}
```

Example:

```scala
val p = PositiveInt.apply(4)

// Can shorten to
val p = PositiveInt(4)
```

---

# Understanding apply syntax

When you see `foo(x)`, where `foo` is a thing (not a function),

then that is really `foo.apply(x)`.

(Syntactic sugar)

In our case, `PositiveInt(4)` is short for `PositiveInt.apply(4)`.

---

# (Aside) Going further

This is an example of a "refined type".

We're taking an existing type and limiting the values it can take.

---

# Going further

For a case like this we could go further and _only_ allow creation through the factory functions.

All policing logic would live in `apply`.

`apply` would return an effect like `Option` or `Either` instead of throwing an exception.

More on this in the refined videos!

---

# Improving our example

We want to pattern match it.

```scala
val p = new PositiveInt(5)

p match {
  case PositiveInt(3) => println("Got a 3")
  case PositiveInt(1) => println("Got a 1")
  case PositiveInt(i) if i <= 0 => println("Hmmm... this shouldn't happen")
  case _ => println("Default case")
}
```

---

# Try it out:

```scala
p match {
  case PositiveInt(3) => println("Got a 3")
  case PositiveInt(1) => println("Got a 1")
  case PositiveInt(i) if i <= 0 => println("Hmmm... this shouldn't happen")
  case _ => println("Default case")
}
```

Computer says no:

```
cmd3.sc:2: object PositiveInt is not a case class, nor does it have an unapply/unapplySeq member
  case PositiveInt(3) => println("Got a 3")
       ^
cmd3.sc:3: object PositiveInt is not a case class, nor does it have an unapply/unapplySeq member
  case PositiveInt(1) => println("Got a 1")
       ^
cmd3.sc:4: object PositiveInt is not a case class, nor does it have an unapply/unapplySeq member
  case PositiveInt(i) if i <= 0 => println("Hmmm... this shouldn't happen")
       ^
Compilation Failed
```

We haven't defined an unapply for it.

---

# unapply

Error:

```
object PositiveInt is not a case class, nor does it have an unapply/unapplySeq member
```

If you want to understand this more, there's a separate training on how unapply/extractors work.

For today, just know that turning it into a "case class" solves this.

---

# Do as the compiler says!

```scala
case class PositiveInt(val value: Int) {
  if (value <= 0) {
    throw new IllegalArgumentException(s"Input isn't positive: $value")
  }
  
  def add(other: PositiveInt): PositiveInt = new PositiveInt(this.value + other.value)
}

object PositiveInt {
  def one: PositiveInt = new PositiveInt(1)
}
```

Note the "case" keyword before "class" on line 1.

---

# Tighten it up

```scala
- case class PositiveInt(val value: Int) {
+ case class PositiveInt(value: Int) {
   if (value <= 0) {
     throw new IllegalArgumentException(s"Input isn't positive: $value")
   }
  
-  def add(other: PositiveInt): PositiveInt = new PositiveInt(this.value + other.value)
+  def add(other: PositiveInt): PositiveInt =     PositiveInt(this.value + other.value)
 }

 object PositiveInt {
-  def one: PositiveInt = new PositiveInt(1)
+  def one: PositiveInt =     PositiveInt(1)
 }
```

- don't need the `val` for case classes, all the "data" is public


- don't need `new` keyword to create instances anymore

---

# Q: Why don't we need the `new` keyword?

```scala
object PositiveInt {
  def one: PositiveInt = PositiveInt(1)

  def one: PositiveInt = PositiveInt.apply(1)
}
```

A: We're actually calling a factory method.

---

# Q: But we didn't define a factory method...

```scala
object PositiveInt {
  def one: PositiveInt = PositiveInt(1)

  def one: PositiveInt = PositiveInt.apply(1)

  // Ghost method
  def apply(int: Int): PositiveInt = new PositiveInt(int)
}
```

The compiler created a hidden one for us.

---

# Important to understand

A regular `class` is fairly literal. What you see is what you get.

A `case class` is like a class with a lot of extra hidden goodies made for you by the compiler.

---

# Some built in goodies

- factory methods (apply)


- extractors for pattern matching (unapply)


- copy


---

# Motivating copy

First understand: case classes are immutable by default

---

# Example

```scala
case class Person(name: String, age: Int)

val person = Person("Boban", 28)

person.name = "Bobanita"  // <---- naughty
```

Compiler error:

```
cmd7.sc:1: reassignment to val
val res7 = person.name = "Bobanita"
                       ^
Compilation Failed
```

(Note you don't need braces for a simple case class definition without methods like this)

---

# How do we change a field then?

Make an immutable copy with the `copy` function:

```scala
case class Person(name: String, age: Int)

val boban = Person("Boban", 28)

val bobanita = boban.copy(name = "Bobanita")
```

All fields are identical except those overridden (`name` in this example).

---

# Combining them

You can combine copy operations too:

```scala
case class ComplexThing(a: Int, b: Int, c: Int, d: Int, e: Int, f: Int)

val thing1 = ComplexThing(a = 0, b = 1, c = 2, d = 3, e = 4, f = 5)

val thing2 = thing1.copy(b = -1, c = -2)
```

Makes more sense here.

---

# Reinforcing: class vs case class 

Goodies like pattern matching and `copy` are just for case classes.

Regular classes don't have this.

---

# Natural question:

Q: When to use case classes vs classes?

A: To understand this, let's zoom out and think about FP a little.

---

# Design styles

OO: Combine data and behavior

FP: Separate into data and functions

---

# Small example

A scoreboard for a match.

Two values which get incremented when that team scores a point.

---

# Java style

Behavior and data is mixed together.

```java
// TODO - make threadsafe
public class Scoreboard {
  private int _left = 0;
  private int _right = 0;

  public Scoreboard() { }

  public Scoreboard(int left, int right) {
    _left = left;
    _right = right;
  }

  public int left() {
    return _left;
  }

  public int right() {
    return _right;
  }

  public void incrementLeft() {
    _left + 1;
  }

  public void incrementRight() {
    _right + 1;
  }
}
```

---

# Scala style

Notice the separation of data and transformations of the data.

```scala
case class Score(left: Int, right: Int)

object Score {
  def start: Score = Score(0, 0)

  def incrementLeft(score: Score): Score = score.copy(left = score.left + 1)

  def incrementRight(score: Score): Score = score.copy(right = score.right + 1)
}
```

Demo

```scala
val score0 = Score.start

// Fight!
val score1 = incrementLeft(score0)
val score2 = incrementLeft(score1)
val score3 = incrementRight(score2)
val score4 = incrementLeft(score3)
```

---

# Stylistic differences

Some people will do this:

```scala
case class Score(left: Int, right: Int) {
  def incrementLeft: Score = this.copy(left = this.left + 1)

  def incrementRight: Score = this.copy(right = this.right + 1)
}

object Score {
  def start: Score = Score(0, 0)
}
```

---

# Comparing them

I personally don't like the second one, but not a huge deal.

They're fundamentally the same though (immutable copies),

just but using a different syntactic style.

First version:

```scala
val score0 = Score.start

// Fight!
val score1 = incrementLeft(score0)
val score2 = incrementLeft(score1)
val score3 = incrementRight(score2)
val score4 = incrementLeft(score3)
```

Second version:

```scala
val score0 = Score.start

// Fight!
val score1 = score0.incrementLeft
val score2 = score1.incrementLeft
val score3 = score2.incrementRight
val score4 = score3.incrementLeft
```

---

# Summary

- scala has `class` and `case class`


- `class` is vanilla, `case class` has chocolate with smarties and lychee


- `case class` makes sense for representing "data" (FP mindset)


- static stuff goes into the companion object

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
