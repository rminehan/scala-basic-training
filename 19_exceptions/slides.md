---
author: Rohan
title: Exceptions
date: 2021-10-07
---

```
 _____                    _   _                 
| ____|_  _____ ___ _ __ | |_(_) ___  _ __  ___ 
|  _| \ \/ / __/ _ \ '_ \| __| |/ _ \| '_ \/ __|
| |___ >  < (_|  __/ |_) | |_| | (_) | | | \__ \
|_____/_/\_\___\___| .__/ \__|_|\___/|_| |_|___/
                   |_|                          
```

---

# What's today about?

Filling some gaps beyond the basic stuff

---

# Slides

Lesson [19](https://github.com/rminehan/scala-basic-training/tree/master/19_exceptions)
of scala training

---

# Disclaimer

I'm not a java guy

---

# Warning

Will definitely go over time

Not even going to pretend

Feel free to drop out

Usually I would split into 2 sessions...

---

# Collaboration

I will do some slides

Willy will do the demos

---

# Why learn about exceptions?

Aren't they to be avoided?

---

# Why learn about exceptions?

> Aren't they to be avoided?

They'll always pop up here and there

Important to think about them correctly

---

# Today

- quick recap of basic syntax


- hierarchy of exceptions


- understanding `Try`


- scala vs java stack traces


- performance notes


- nested exceptions


- red herrings

---

# Quick recap

```scala
val result =
  try {
    doSomeCalculation(a, b, c)
  }
  catch {
    case _: BobanException => 3
    case b: BobanitaException =>
      println("Got a BobanitaException: $b")
      throw b
  }
  finally {
    println("Computed result")
  }
```

---

# Clarifying points

```scala
val result =
  try {
    doSomeCalculation(a, b, c)
  }
  catch {
    case _: BobanException => 3
    case b: BobanitaException =>
      println("Got a BobanitaException: $b")
      throw b
  }
  finally {
    println("Computed result")
  }
```

- `try/catch/finally` is an expression


- `catch` logic is represented as a pattern match (possibly partial)


- `finally` logic always runs

---

# Pun time

```scala
val result =
  try {
    doSomeCalculation(a, b, c)
  }
  catch {
    case _: BobanException => 3
    case b: BobanitaException =>
      println("Got a BobanitaException: $b")
      throw b
  }
  finally {
    println("Computed result")
  }
```

(And usually we'd make `doSomeCalculation` return an effect,

but I guess this time we can make an `Exception`)

:dad-parrot:

---

# Checked exceptions?

Not in scala

---

# Hierarchy

Questions you may have asked:

> What's the difference between `Throwable` and `Exception` and `Error`?
>
> What should I catch for when I use exceptions? `Throwable`? `Exception`?
>
> What kind of thing should I throw when I hit a failure?

---

# Java vs Scala developers

Java devs are more immersed in this stuff

Scala devs are less familiar

> Pfft... dirty java stuff

---

# Hierarchy

It's helpful to understand the hierarchy and the way these things were intended to be used

```
             Throwable

          /             \

   Exception             Error
   /   |   \             / | \
```

---

# Throwable

The Big Daddy/Mummy of fail

```
             Throwable

          /             \

   Exception             Error
   /   |   \             / | \
```

From the docs:

> The Throwable class is the superclass of all errors and exceptions in the Java language.
>
> Only objects that are instances of this class (or subclasses) are thrown by the JVM "throw" statement.

---

# Throwable

```
             Throwable

          /             \

   Exception             Error
   /   |   \             / | \
```

Abstraction to represent all things throwable

If it aint `Throwable`, you can't throw it

---

# Error

Something we can't recover from

We broke the fabric of the universe

```
                 Throwable

          /                    \

   Exception                     Error
   /   |   \                    /      \
                 VirtualMachineError    ...
                    /       |    \
        StackOverflowError ...  OutOfMemoryError
```

KO

---

# Error

```
                 Throwable

          /                    \

   Exception                     Error
   /   |   \                    /      \
                 VirtualMachineError    ...
                    /       |    \
        StackOverflowError ...  OutOfMemoryError
```

From the docs:

> An Error is a subclass of Throwable that indicates serious problems
>
> that a reasonable application should not try to catch.
>
> Most such errors are abnormal conditions.

---

# Clarification

> that a reasonable application should not try to catch.

I think they really mean catch and not rethrow, ie. stifling

```scala
try {
  doStuff()
} catch {
  case e: Error =>
    // Probably okay (as long as println still works)
    println(s"KO: $e")
    throw e
}
```

---

# Exception

Typical issues related to our logic

```
                 Throwable

          /                    \

   Exception                     Error
   /   |   \                    /      \
```

From the docs:

> The class Exception and its subclasses are a form of Throwable that indicates conditions
>
> that a reasonable application might want to catch.

---

# Exception hierarchy

```
Throwable
    Exception
      RuntimeException (unchecked)
          IllegalArgumentException
              NumberFormatException
          NoSuchElementException
          IndexOutOfBoundsException
          NullPointerException
      ReactiveMongoException
```

---

# Back to our questions

> What's the difference between `Throwable` and `Exception` and `Error`?
>
> What should I catch for when I use exceptions? `Throwable`? `Exception`?
>
> What kind of thing should I throw when I hit a failure?

---

# What's the diffence?

> What's the difference between `Throwable` and `Exception` and `Error`?

`Throwable` is the most general and abstract

`Error` is for universal tearing epic failure that you usually can't recover from

`Exception` is run of the mill exceptions from application code

---

# Catching?

> What should I catch/stifle for when I use exceptions? `Throwable`? `Exception`?

_Generally_ `Error`'s should be allowed to escalate past us (some exceptions)

Might make sense to log it but don't prevent the escalation

```scala
try {
  doStuff()
}
catch {
  case er: Error =>
    println("Got error: '$er' Oh well...") // :hmm-parrot:
}
```

---

# Catching?

> What should I catch/stifle for when I use exceptions? `Throwable`? `Exception`?

`Throwable` is even more general than `Error`

ie. if we caught `Throwable` we'd be catching `Error` as a byproduct

So _generally_ we shouldn't be stifle `Throwable`'s

```scala
try {
  doStuff()
}
catch {
  case t: Throwable =>
    println("Got throwable: '$t' Oh well...") // :hmm-parrot:
}
```

---

# Catching?

> What should I catch/stifle for when I use exceptions? `Throwable`? `Exception`?

What about `Exception`?

Depends on your context and the exception, still might not be able to recover

---

# Example

```scala
val bobanAge: Option[Int] =
  try {
    Some(catchABoban().age)
  } catch {
    case BobanNotFoundException => None // Recoverable
    case AlbanianHackersGotUsException => // Not recoverable
      println("Albanian Hackers are onto us, we're doomed!")
      throw new Exception("We're doomed", AlbanianHackersGotUsException)
    case t: Throwable =>
      println(s"All is good, carry on, just hit this $t") // :hmmm-parrot:
      None
  }
```

---

# Throwing a tanty?

> What kind of thing should I throw when I hit a failure?

An `Exception` of some kind

Could be `Exception` itself, or one of its kin

---

# An appropriate exception

> Could be `Exception` itself, or one of its kin

Sometimes there's an existing exception that's a good fit, e.g.

- `IllegalArgumentException`


- `NullPointerException`


- `IndexOutOfBoundsException`

---

# FP

> What kind of thing should I throw when I hit a failure?

Assumes you should be throwing exceptions in the first place

Or maybe you're returning an `Exception` (e.g. in a `ZIO`)

---

# Try vs try

```scala
val result: Try[Boban] = Try(catchABoban())

// vs

val result =
  try {
    Some(catchABoban())
  }
  catch {
    ... // Return None if it makes sense
  }
```

---

# Peeking inside `Try`

```scala
object Try {
  def apply[T](r: => T): Try[T] =
    try Success(r) catch {
      case NonFatal(e) => Failure(e)
    }
}
```

What is this `NonFatal` extractor?

---

# NonFatal?

```scala
object Try {
  def apply[T](r: => T): Try[T] =
    try Success(r) catch {
      case NonFatal(e) => Failure(e)
    }
}

object NonFatal {
   /** Returns true if the provided `Throwable` is to be considered non-fatal,
    * or false if it is to be considered fatal
    */
   def apply(t: Throwable): Boolean = t match {
     // VirtualMachineError includes OutOfMemoryError and other fatal errors
     case VirtualMachineError | ThreadDeath | InterruptedException | LinkageError | ControlThrowable => false
     case _ => true
   }

  /**
   * Returns Some(t) if NonFatal(t) == true, otherwise None
   */
  def unapply(t: Throwable): Option[Throwable] = if (apply(t)) Some(t) else None
}
```

```
                 Throwable

          /                    \

   Exception                     Error
   /   |   \                    /      \
                 VirtualMachineError    ...
                    /       |    \
        StackOverflowError ...  OutOfMemoryError
```

---

# Equivalent to

```scala
object Try {
  def apply[T](r: => T): Try[T] =
    try Success(r)
    catch {
        // pseudocode, won't compile
      case t: VirtualMachineError | ThreadDeath | InterruptedException | LinkageError | ControlThrowable =>
        // escalate the exception as it's fatal
        throw t
      case t: Throwable =>
        // It's a non-fatal throwable, capture it into a `t`
        // It won't propagate
        Failure(t)
    }
}
```

---

# Summarizing Try

- will let a sensible hand picked group of weird errors escalate


- doesn't have a `finally` concept

---

# Summarizing Try

Works pretty well for most situations

Saves you a lot of boilerplate

Caters for `ControlThrowable`...

---

# Control mechanisms

What is this `ControlThrowable` about?

```scala
object Try {
  def apply[T](r: => T): Try[T] =
    try Success(r)
    catch {
      case t: VirtualMachineError | ThreadDeath | InterruptedException | LinkageError | ControlThrowable =>
          //                                                                            ^^^^^^^^^^^^^^^^
          ...
}
```

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Peeking under the covers

That's not java code!

```scala
package scala
package util.control

/** A marker trait indicating that the `Throwable` it is mixed into is intended for flow control.
 *
 *  ...
 *
 *  Instances of `Throwable` subclasses marked in this way should not normally be caught.
 */
trait ControlThrowable extends Throwable with NoStackTrace
```

---

# Related

Did you know: Scala doesn't have a native `break` command?

Demo time!

---

# How does `break` work?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

(Hint: It relates to the topic of this talk)

---

# How does `break` work?

Throwing an exception:

```scala
  def break(): Nothing = { throw breakException }

  ...

  private val breakException = new BreakControl
```

---

# Why do we wrap our block in `breakable`?

```scala
breakable {
  people.foreach { person =>
    if (person.name == "Boban") break
    else println(person.parrot)
  }
}
```

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Why do we wrap our block in `breakable`?

In anticipation of break exceptions being thrown:

```scala
  def breakable(op: => Unit) {
    try {
      op
    } catch {
      case ex: BreakControl =>
        if (ex ne breakException) throw ex
    }
  }
```

---

# Back to `NonFatal`

We were wondering what this `ControlThrowable` doodad was about:

```scala
object NonFatal {
   /**
    * Returns true if the provided `Throwable` is to be considered non-fatal, or false if it is to be considered fatal
    */
   def apply(t: Throwable): Boolean = t match {
     // VirtualMachineError includes OutOfMemoryError and other fatal errors
     case _: VirtualMachineError | _: ThreadDeath | _: InterruptedException | _: LinkageError | _: ControlThrowable => false
     case _ => true
   }
  /**
   * Returns Some(t) if NonFatal(t) == true, otherwise None
   */
  def unapply(t: Throwable): Option[Throwable] = if (apply(t)) Some(t) else None
}
```

Why is it grouping `ControlThrowable` with these bad boys of the JVM?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# NonFatal?

> Why is it grouping `ControlThrowable` with these bad boys of the JVM?

It doesn't want to interfere with control flow logic

---

# Example

```scala
breakable {
  people.foreach { person =>
    Try {
      if (isBoban(person)) break
      else hack(person)
    }
  }
}
```

If `Try` intercepted our break exception, our logic would change

---

# Fell in with the wrong crowd

```scala
     case VirtualMachineError | ThreadDeath | InterruptedException | LinkageError | ControlThrowable => ...
```

`ControlThrowable` is shoe horned in here

_Not_ because it's a bad boy exception burning down the JVM

But because they don't want you to catch it

---

# Take care with try

```scala
trait ControlThrowable extends Throwable with NoStackTrace

...

breakable {
  people.foreach { person =>
    try {
      if (isBoban(person)) break
      else hack(person)
    }
    catch {
      case t: Throwable => ... // Will catch your break exception
    }
  }
}
```

---

# Why no stack trace?

```scala
trait ControlThrowable extends Throwable with NoStackTrace
//                                            ^^^^^^^^^^^^
```

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Why no stack trace?

```scala
trait ControlThrowable extends Throwable with NoStackTrace
//                                            ^^^^^^^^^^^^
```

Keeps it cheap

It's not a "real" exception

---

# My thoughts

`break` is a hack (glorified goto)

Abuses exceptions which creates all these caveats and landmines

---

# My thoughts

I've never needed it

Helpful for imperative developers using scala as java++

Better practice to use FP style, e.g. `takeWhile`

---

# Scary stack traces

---

# Quick aside

Don't be scared

Scroll to the top, check the error message

---

# Demo time!

Run a java and scala program and observe differences in stack trace

To the repl!

---

# What's going on?

Weird dollar signs?

An extra stack frame?

```
$ java DemoScala
Exception in thread "main" java.lang.RuntimeException: Boban's Everywhere!
    at DemoScala$.function3(DemoScala.scala:4)
    at DemoScala$.function2(DemoScala.scala:8)
    at DemoScala$.function1(DemoScala.scala:12)
    at DemoScala$.main(DemoScala.scala:16)
    at DemoScala.main(DemoScala.scala)

$ java DemoJava
Exception in thread "main" java.lang.RuntimeException: Boban's Everywhere!
    at DemoJava.function3(DemoJava.java:3)
    at DemoJava.function2(DemoJava.java:7)
    at DemoJava.function1(DemoJava.java:11)
    at DemoJava.main(DemoJava.java:15)

 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Hint

```
$ java DemoScala
Exception in thread "main" java.lang.RuntimeException: Boban's Everywhere!
    at DemoScala$.function3(DemoScala.scala:4)
    at DemoScala$.function2(DemoScala.scala:8)
    at DemoScala$.function1(DemoScala.scala:12)
    at DemoScala$.main(DemoScala.scala:16)
    at DemoScala.main(DemoScala.scala)

$ java DemoJava
Exception in thread "main" java.lang.RuntimeException: Boban's Everywhere!
    at DemoJava.function3(DemoJava.java:3)
    at DemoJava.function2(DemoJava.java:7)
    at DemoJava.function1(DemoJava.java:11)
    at DemoJava.main(DemoJava.java:15)
```

Think back to your JVM training grasshopper...

Our MVP for JVM-05 was Clement (no pressure)

---

# Another hint

```
$ ls | grep class
DemoJava.class
DemoScala$.class
DemoScala.class
```

---

# Extra scala overhead

```
$ ls | grep class
DemoJava.class
DemoScala$.class
DemoScala.class

$ java DemoScala
Exception in thread "main" java.lang.RuntimeException: Boban's Everywhere!
    at DemoScala$.function3(DemoScala.scala:4)
    at DemoScala$.function2(DemoScala.scala:8)
    at DemoScala$.function1(DemoScala.scala:12)
    at DemoScala$.main(DemoScala.scala:16)
    at DemoScala.main(DemoScala.scala)          <---- regular static wrapper method in DemoScala.class
```

---

# Recall your training grasshopper

Decompile the less scary: `DemoScala.class`

More wrappers than a Korean candy:

```java
// javap -c DemoScala.class
public final class DemoScala {
  public static void main(java.lang.String[]);
    Code:
       0: getstatic     #17                 // Field DemoScala$.MODULE$:LDemoScala$;
       3: aload_0
       4: invokevirtual #19                 // Method DemoScala$.main:([Ljava/lang/String;)V
       7: return

  public static void function1();
    Code:
       0: getstatic     #17                 // Field DemoScala$.MODULE$:LDemoScala$;
       3: invokevirtual #23                 // Method DemoScala$.function1:()V
       6: return

  public static void function2();
    Code:
       0: getstatic     #17                 // Field DemoScala$.MODULE$:LDemoScala$;
       3: invokevirtual #26                 // Method DemoScala$.function2:()V
       6: return

  public static void function3();
    Code:
       0: getstatic     #17                 // Field DemoScala$.MODULE$:LDemoScala$;
       3: invokevirtual #29                 // Method DemoScala$.function3:()V
       6: return
}
```

---

# The singleton

```java
// javap -c DemoScala\$.class
public final class DemoScala$ {
  public static DemoScala$ MODULE$;

  public static {};
    Code:
       0: new           #2                  // class DemoScala$
       3: invokespecial #12                 // Method "<init>":()V
       6: return

  public void function3();
    Code:
       0: new           #15                 // class java/lang/RuntimeException
       3: dup
       4: ldc           #17                 // String Boban\'s Everywhere!
       6: invokespecial #20                 // Method java/lang/RuntimeException."<init>":(Ljava/lang/String;)V
       9: athrow

  public void function2();
    Code:
       0: aload_0
       1: invokevirtual #24                 // Method function3:()V
       4: return

  public void function1();
    Code:
       0: aload_0
       1: invokevirtual #27                 // Method function2:()V
       4: return

  public void main(java.lang.String[]);
    Code:
       0: aload_0
       1: invokevirtual #32                 // Method function1:()V
       4: return
}
```

---

# Visually

```
$ java DemoScala
Exception in thread "main" java.lang.RuntimeException: Boban's Everywhere!
    at DemoScala$.function3(DemoScala.scala:4)
    at DemoScala$.function2(DemoScala.scala:8)
    at DemoScala$.function1(DemoScala.scala:12)
    at DemoScala$.main(DemoScala.scala:16)
    at DemoScala.main(DemoScala.scala)

                                                        // DemoScala$.class

                                                        def function3(): Unit = {   <-
                                                          throw new RuntimeException(...)
                                                        }                             |
                                                                                      |
                                                        def function2(): Unit = {   <-
                                                          function3()                 |
                                                        }                             |
                                                                                      |
          Execution:                                    def function1(): Unit = {   <-
                                                          function2()                 |
        // DemoScala.class                              }                             |
                                                                                      |
        def main(): Unit = {                            def main(): Unit = {          |
          MODULE$.main()         --------->               function1()   --------------
        }                                               }
```

---

# Recapping

Scala stack traces are more complex

There are extra `$` stack frames due to going through wrapper/delegation methods

Don't be intimidated! You have been trained grasshopper!

---

# Future?

See more complex stack traces

To the repl!

---

# Hmmm

```
java.base/java.lang.Thread.getStackTrace(Thread.java:1607)
FutureDemo$.doWork(FutureDemo.scala:9)
FutureDemo$.$anonfun$main$1(FutureDemo.scala:14)
scala.runtime.java8.JFunction0$mcI$sp.apply(JFunction0$mcI$sp.java:23)
scala.concurrent.Future$.$anonfun$apply$1(Future.scala:659)
scala.util.Success.$anonfun$map$1(Try.scala:255)
scala.util.Success.map(Try.scala:213)
scala.concurrent.Future.$anonfun$map$1(Future.scala:292)
scala.concurrent.impl.Promise.liftedTree1$1(Promise.scala:33)
scala.concurrent.impl.Promise.$anonfun$transform$1(Promise.scala:33)
scala.concurrent.impl.CallbackRunnable.run(Promise.scala:64)
java.base/java.util.concurrent.ForkJoinTask$RunnableExecuteAction.exec(ForkJoinTask.java:1426)
java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:290)
java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1020)
java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1656)
java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1594)
java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:183)
```

Questions:

- what is all this noise?


- why is there no entry for our main method?


---

# What is all this noise?

```
java.base/java.lang.Thread.getStackTrace(Thread.java:1607)

// Thread.currentThread().getStackTrace().foreach(println)
//                        ^^^^^^^^^^^^^^^ went in here                  
FutureDemo$.doWork(FutureDemo.scala:9)

// val future = Future(doWork())
FutureDemo$.$anonfun$main$1(FutureDemo.scala:14)

// All Future stuff
scala.runtime.java8.JFunction0$mcI$sp.apply(JFunction0$mcI$sp.java:23)
scala.concurrent.Future$.$anonfun$apply$1(Future.scala:659)
scala.util.Success.$anonfun$map$1(Try.scala:255)
scala.util.Success.map(Try.scala:213)
scala.concurrent.Future.$anonfun$map$1(Future.scala:292)
scala.concurrent.impl.Promise.liftedTree1$1(Promise.scala:33)
scala.concurrent.impl.Promise.$anonfun$transform$1(Promise.scala:33)
scala.concurrent.impl.CallbackRunnable.run(Promise.scala:64)
java.base/java.util.concurrent.ForkJoinTask$RunnableExecuteAction.exec(ForkJoinTask.java:1426)
java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:290)
java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1020)
java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1656)
java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1594)
java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:183)
```

---

# Understanding this

The fork join pool was asked to run a job (represented as some callback)

---

# Understanding this

It needs to spin up its own resources...

```
...
java.base/java.util.concurrent.ForkJoinTask$RunnableExecuteAction.exec(ForkJoinTask.java:1426)
java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:290)
java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1020)
java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1656)
java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1594)
java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:183)
```

---

# Understanding this

Then call that function we gave to it

```
...
scala.runtime.java8.JFunction0$mcI$sp.apply(JFunction0$mcI$sp.java:23)
scala.concurrent.Future$.$anonfun$apply$1(Future.scala:659)
scala.util.Success.$anonfun$map$1(Try.scala:255)
scala.util.Success.map(Try.scala:213)
scala.concurrent.Future.$anonfun$map$1(Future.scala:292)
scala.concurrent.impl.Promise.liftedTree1$1(Promise.scala:33)
scala.concurrent.impl.Promise.$anonfun$transform$1(Promise.scala:33)
scala.concurrent.impl.CallbackRunnable.run(Promise.scala:64)
...
```

"Promise" being used under the hood to keep track of when it's done

---

# Looking at our stack trace again

```
FutureDemo$.doWork(FutureDemo.scala:9)                  | Run our
FutureDemo$.$anonfun$main$1(FutureDemo.scala:14)        | code
scala.runtime.java8.JFunction0$mcI$sp.apply(JFunction0$mcI$sp.java:23) |
scala.concurrent.Future$.$anonfun$apply$1(Future.scala:659)            |
scala.util.Success.$anonfun$map$1(Try.scala:255)                       | Setup a promise
scala.util.Success.map(Try.scala:213)                                  | to track the task
scala.concurrent.Future.$anonfun$map$1(Future.scala:292)               |
scala.concurrent.impl.Promise.liftedTree1$1(Promise.scala:33)          |
scala.concurrent.impl.Promise.$anonfun$transform$1(Promise.scala:33)   |
scala.concurrent.impl.CallbackRunnable.run(Promise.scala:64)           |
java.base/java.util.concurrent.ForkJoinTask$RunnableExecuteAction.exec(ForkJoinTask.java:1426)  |
java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:290)                       |
java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1020)      | Fire up
java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1656)                        | the old girl
java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1594)                   |
java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:183)          |
```

---

# Why am I showing you this?

To help you filter out the noise in stack traces

---

# Why am I showing you this?

> To help you filter out the noise in stack traces

A lot of our code runs inside other frameworks with their own thread pools

e.g. play framework, spark

This leads to some hairy looking stack traces

---

# Fear not!

> This leads to some hairy looking stack traces

Don't be intimated

Just find the spot where control switches from the framework to your callback

---

# Library noise

We'll generate a stack trace from a `foreach`

To the repl!

---

# Noise mixed in

```
Exception in thread "main" java.lang.Exception: No bobans!
    at ForeachDemo$.$anonfun$main$1(ForeachDemo.scala:4)          | Our code again
    at ForeachDemo$.$anonfun$main$1$adapted(ForeachDemo.scala:3)  |
    at scala.collection.IndexedSeqOptimized.foreach(IndexedSeqOptimized.scala:36)   | Standard
    at scala.collection.IndexedSeqOptimized.foreach$(IndexedSeqOptimized.scala:33)  | library noise
    at scala.collection.mutable.ArrayOps$ofRef.foreach(ArrayOps.scala:198)          |
    at ForeachDemo$.main(ForeachDemo.scala:3)          |
    at ForeachDemo.main(ForeachDemo.scala)             | Our code
```

---

# Firstly

Does `Array` have a `foreach` method?

```scala
  def main(args: Array[String]): Unit = {
    args.foreach { ... }
    //   ^^^^^^^
  }
```

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Firstly

> Does `Array` have a `foreach` method?

```scala
  def main(args: Array[String]): Unit = {
    args.foreach { ... }
    //   ^^^^^^^
  }
```

Nah, it's some implicit magic turning 

```
Exception in thread "main" java.lang.Exception: No bobans!
    at ForeachDemo$.$anonfun$main$1(ForeachDemo.scala:4)
    at ForeachDemo$.$anonfun$main$1$adapted(ForeachDemo.scala:3)
    at scala.collection.IndexedSeqOptimized.foreach(IndexedSeqOptimized.scala:36)
    at scala.collection.IndexedSeqOptimized.foreach$(IndexedSeqOptimized.scala:33)
    at scala.collection.mutable.ArrayOps$ofRef.foreach(ArrayOps.scala:198)      <----- wrap the array into a seq
    at ForeachDemo$.main(ForeachDemo.scala:3)
    at ForeachDemo.main(ForeachDemo.scala)
```

---

# IndexedSeqOptimized

Now that we've scala-fied our array into an `IndexedSeqOptimized`,

we call `foreach` (which itself is a complex method):

```scala
trait IndexedSeqOptimized {

  def foreach[U](f: A => U): Unit = {
    var i = 0
    val len = length
    while (i < len) { f(this(i)); i += 1 }
  }

  ...
}
```

```
Exception in thread "main" java.lang.Exception: No bobans!
    at ForeachDemo$.$anonfun$main$1(ForeachDemo.scala:4)
    at ForeachDemo$.$anonfun$main$1$adapted(ForeachDemo.scala:3)
    at scala.collection.IndexedSeqOptimized.foreach(IndexedSeqOptimized.scala:36)  <---- call f
    at scala.collection.IndexedSeqOptimized.foreach$(IndexedSeqOptimized.scala:33) <----
    at scala.collection.mutable.ArrayOps$ofRef.foreach(ArrayOps.scala:198)
    at ForeachDemo$.main(ForeachDemo.scala:3)
    at ForeachDemo.main(ForeachDemo.scala)
```

---

# Back to our code

Execute the `f` we passed in:

```scala
object ForeachDemo {
  def main(args: Array[String]): Unit = {
    args.foreach { name => // Line 3
        if (name.toLowerCase == "boban") throw new Exception("No bobans!")
        else println(name)
      }
  }
}
```

```
Exception in thread "main" java.lang.Exception: No bobans!
    at ForeachDemo$.$anonfun$main$1(ForeachDemo.scala:4)
    at ForeachDemo$.$anonfun$main$1$adapted(ForeachDemo.scala:3)            <--- back to our code
    at scala.collection.IndexedSeqOptimized.foreach(IndexedSeqOptimized.scala:36)
    at scala.collection.IndexedSeqOptimized.foreach$(IndexedSeqOptimized.scala:33)
    at scala.collection.mutable.ArrayOps$ofRef.foreach(ArrayOps.scala:198)
    at ForeachDemo$.main(ForeachDemo.scala:3)
    at ForeachDemo.main(ForeachDemo.scala)
```

---

# Odd things

```scala
object ForeachDemo {
  def main(args: Array[String]): Unit = {
    args.foreach { name => // Line 3
        if (name.toLowerCase == "boban") throw new Exception("No bobans!")
        else println(name)
      }
  }
}
```

```
Exception in thread "main" java.lang.Exception: No bobans!
    at ForeachDemo$.$anonfun$main$1(ForeachDemo.scala:4)                    <--- why two stack frames?
    at ForeachDemo$.$anonfun$main$1$adapted(ForeachDemo.scala:3)            <---
    at scala.collection.IndexedSeqOptimized.foreach(IndexedSeqOptimized.scala:36)
    at scala.collection.IndexedSeqOptimized.foreach$(IndexedSeqOptimized.scala:33)
    at scala.collection.mutable.ArrayOps$ofRef.foreach(ArrayOps.scala:198)
    at ForeachDemo$.main(ForeachDemo.scala:3)
    at ForeachDemo.main(ForeachDemo.scala)
```

Funny names? 

```
$anonfun$main$1$adapted
-----------------------
     ???
```

To the repl!

---

# Decompiling

```java
  public void main(java.lang.String[]);
    Code:
       0: new           #12                 // class scala/collection/mutable/ArrayOps$ofRef
       3: dup
       4: getstatic     #30                 // Field scala/Predef$.MODULE$:Lscala/Predef$;
       7: aload_1
       8: checkcast     #32                 // class "[Ljava/lang/Object;"
       // Implicit stuffs...
      11: invokevirtual #36                 // Method scala/Predef$.refArrayOps:([Ljava/lang/Object;)[Ljava/lang/Object;
      14: invokespecial #39                 // Method scala/collection/mutable/ArrayOps$ofRef."<init>":([Ljava/lang/Object;)V
      // Funky invokedynamic stuff
      // My guess: building a function reference on the fly to the "adapted" method
      // Pushes that function location onto the stack
      17: invokedynamic #62,  0             // InvokeDynamic #0:apply:()Lscala/Function1;
      // Invoke the function adapted function that was just pushed
      22: invokevirtual #66                 // Method scala/collection/mutable/ArrayOps$ofRef.foreach:(Lscala/Function1;)V
      25: return

      // Contains the meat
  public static final void $anonfun$main$1(java.lang.String);
    Code:
       0: aload_0
       1: invokevirtual #77                 // Method java/lang/String.toLowerCase:()Ljava/lang/String;
       4: ldc           #79                 // String boban
       ....
      36: getstatic     #30                 // Field scala/Predef$.MODULE$:Lscala/Predef$;
      39: aload_0
      40: invokevirtual #93                 // Method scala/Predef$.println:(Ljava/lang/Object;)V
      43: return

      // Call the method above
      // Acts as a shim, maybe because the JVM needs a method that return Object
  public static final java.lang.Object $anonfun$main$1$adapted(java.lang.String);
    Code:
       0: aload_0
       1: invokestatic  #99                 // Method $anonfun$main$1:(Ljava/lang/String;)V
       4: getstatic     #105                // Field scala/runtime/BoxedUnit.UNIT:Lscala/runtime/BoxedUnit;
       7: areturn
```

---

# Why did I show you all that?

To make the point:

- relatively simple scala libraries also scatter a lot of noise through your callstack

```
Exception in thread "main" java.lang.Exception: No bobans!
    at ForeachDemo$.$anonfun$main$1(ForeachDemo.scala:4)
    at ForeachDemo$.$anonfun$main$1$adapted(ForeachDemo.scala:3)
    at scala.collection.IndexedSeqOptimized.foreach(IndexedSeqOptimized.scala:36)
    at scala.collection.IndexedSeqOptimized.foreach$(IndexedSeqOptimized.scala:33)
    at scala.collection.mutable.ArrayOps$ofRef.foreach(ArrayOps.scala:198)
    at ForeachDemo$.main(ForeachDemo.scala:3)
    at ForeachDemo.main(ForeachDemo.scala)
```

- particularly when you pass functions around

---

# Again

Don't be intimidated!

Just learn to filter out the noise

---

# Nested exceptions

---

# Nested exceptions

Sometimes spark or play spits out these complex double-decker exceptions

```
Exception in thread "main" java.lang.Exception: Our dangerous stuff failed
    at NestedDemo$.function1(NestedDemo.scala:12)
    ...
    at NestedDemo$.main(NestedDemo.scala:17)
    at NestedDemo.main(NestedDemo.scala)
Caused by: java.lang.Exception: Boban encountered
    at NestedDemo$.doDangerousStuff(NestedDemo.scala:3)
    ...
    at NestedDemo$.function1(NestedDemo.scala:8)
    ... 2 more
```

To the repl!

---

# Nested Exceptions

- adds caller context


- helps translate exception conventions

```
Exception in thread "main" java.lang.Exception: Our dangerous stuff failed
    at NestedDemo$.function1(NestedDemo.scala:12)
    at NestedDemo$.main(NestedDemo.scala:17)
    at NestedDemo.main(NestedDemo.scala)
Caused by: java.lang.Exception: Boban encountered
    at NestedDemo$.doDangerousStuff(NestedDemo.scala:3)
    at NestedDemo$.function1(NestedDemo.scala:8)
    ... 2 more
```

---

# And be careful...

```scala
try {
  doStuff()
}
catch {
  case t: Throwable =>
    throw new Exception("Encountered failure doing stuff", t)
}
```

Can you spot the issue?

---

# Changed the kind of error it is

```scala
try {
  doStuff()
}
catch {
  case t: Throwable =>
    throw new Exception("Encountered failure doing stuff", t)
}
```

Somewhere higher up:

```scala
try {
  // Run the code above
  ...
}
catch {
  case e: Exception =>
    log(s"Bad stuff happened $e, oh well try again with the next request...")

  case er: Error =>
    log(s"Oh snap, epic failure: $er")
    throw er
}
```

---

# Red herrings

Misleading exception messages.

To the repl!

---

# Red herrings summary

Common trap is to assume that any error generated has one root cause

But! Maybe:

- you screwed up _calling_ the method


- you triggered some other kind of exception

---

# Red herrings summary

This happens a lot

Wastes a lot of time

---

# How it typically happens

- calls library, no problems, no try-catch stuff

---

# How it typically happens

- calls library, no problems, no try-catch stuff


- new kind of input triggers some kind of exception :fix-parrot:

---

# How it typically happens

- calls library, no problems, no try-catch stuff


- new kind of input triggers some kind of exception :fix-parrot:


- developer says:

> we need to handle this case
>
> okay if the method throws an exception, I'll catch it and deal with this case

---

# How it typically happens

- calls library, no problems, no try-catch stuff


- new kind of input triggers some kind of exception :fix-parrot:


- developer says:

> we need to handle this case
>
> okay if the method throws an exception, I'll catch it and deal with this case

But! They end up accidentally catching other undiscovered cases

---

# Or

- calls library, no problems, no try-catch stuff


- new kind of input triggers some kind of exception :fix-parrot:


- developer says:

> we need to handle this case
>
> okay if the method throws an exception, I'll catch it and deal with this case
>
> and I was a good developer and checked this is the only case where this happens

---

# Or

- calls library, no problems, no try-catch stuff


- new kind of input triggers some kind of exception :fix-parrot:


- developer says:

> we need to handle this case
>
> okay if the method throws an exception, I'll catch it and deal with this case
>
> and I was a good developer and checked this is the only case where this happens

- but then we upgrade to a new version of `Lib` that changes its exception behavior

(no one would think to update the consumer code)

---

# As an author...

In your haste, be careful where you assign blame for an exception

Are you effectively assuming that _any_ error that emerges from a library must have one root cause?

---

# The annoying of reality of exceptions...

> Are you effectively assuming that _any_ error that emerges from a library must have one root cause?

Often the only way to really know is to break it open and see how it works...

(And hope it doesn't change later)

---

# FP!

> Often the only way to really know is to break it open and see how it works...

This is why exceptions are bad 

The behavior of a method _isn't_ encoded into its type signature

Not enforced by a compiler

Doc strings are helpful but ultimately can't be trusted

You just have to "know"

---

# As a reader of stack traces...

Apply some skepticism to exception messages (particularly nested ones)

Check the inner exception message

You can often get misdirected and waste hours of time

---

# Summarizing stack traces

- scala stack traces are full of noise (singletons, callbacks)


- scala stack traces get split up when execution jumps threads (plug for ZIO)


- don't be intimidated!


- before bugging your senior developer or posting on SO, actually read the exception message


- but apply skepticism, they are often red herrings

---

# One final rant

"Try and see" approach

Triggers me a bit...

Relates to my [uber post](https://gitlab.com/leadiq/dataverse/analytics-scripts/-/merge_requests/194#note_680224183)

---

# Example

```scala
val smallestOpt = Try(list.min).toOption


// Nil -> Nil.min -> boom! -> Failure(boom) -> None

// Nel -> Nel.min -> 4     -> Success(4)    -> Some(4)
```

---

# Red herrings

```scala
val smallestOpt = Try(list.min).toOption
//                    ^^^^^^^^
```

Assumes the only kind of exception that could arise is because the list is empty

```scala
val smallestOpt = Try(list.minBy(myOrdering)).toOption
```

What if `myOrdering` throws an exception because it's buggy?

---

# Terrible performance

```scala
val smallestOpt = Try(list.min).toOption
```

In the failure case we're:

- building an exception (with a stack trace)


- throwing it


- catching it (inside `Try.apply`)

Rather than just checking if the list is empty

If your failure rate is high, this will add up

---

```
__        __                     _             
\ \      / / __ __ _ _ __  _ __ (_)_ __   __ _ 
 \ \ /\ / / '__/ _` | '_ \| '_ \| | '_ \ / _` |
  \ V  V /| | | (_| | |_) | |_) | | | | | (_| |
   \_/\_/ |_|  \__,_| .__/| .__/|_|_| |_|\__, |
                    |_|   |_|            |___/ 
 _   _       
| | | |_ __  
| | | | '_ \ 
| |_| | |_) |
 \___/| .__/ 
      |_|    
```

It's a shame my last talk was on ugly old exceptions...

---

# Overall

Exceptions are a pretty ugly mechanism

Hard to reason about, lots of weird edge cases

Strong motivation for FP

---

# But

Scala runs on the JVM

So there are times where we have to deal with them

Particularly when building frameworks

---

# Hierarchy

```
             Throwable

          /             \

   Exception             Error
   /   |   \             / | \
(regular failure)   (epic failure)
```

---

# So

Beware of catching `Throwable`

You might stifle an epic failure that is supposed to crash the JVM

---

# Catch and release

Probably okay to catch, log and rethrow though

Otherwise you'll have no idea what happened

---

# Try vs try

`Try` is designed to let right kinds of exceptions escalate

(Fatal exceptions and flow control exceptions)

---

# Flow control exceptions

Hacks used to replicate "goto" style control flow ala java/C

Beware!

---

# Stack traces

Scala stack traces are noisy and often split as control jumps around different threads

Hopefully they don't seem so scary now

There's a lot of noise, but you can generally ignore it

---

# Read the error message!

Scroll up to the top and see what the message is before bugging someone else

---

# Red herrings

But take it with a grain of salt...

Validate your theory

Misdirections can cause hours of wasted time

---

# When writing your own code

When interpreting an exception,

be careful with how you interpret the root cause

---

# "Try and see" style logic

Generally if there's a safe way to check something without exceptions do that

Safer logically (avoids red herrings)

Performs better

---

# "Try and see" style logic

The only good reason to do it is to trigger me

But soon that's not going to count

---

# Congratulations

You've graduated from the SCALA course!

---

```
  ___           _    
 / _ \ _ __    / \   
| | | | '_ \  / _ \  
| |_| | | | |/ ___ \ 
 \__\_\_| |_/_/   \_\
                     
```
