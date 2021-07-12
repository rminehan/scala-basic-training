---
author: Rohan
date: 2021-07-13
title: Validation Concepts
---

```
__     __    _ _     _       _   _             
\ \   / /_ _| (_) __| | __ _| |_(_) ___  _ __  
 \ \ / / _` | | |/ _` |/ _` | __| |/ _ \| '_ \ 
  \ V / (_| | | | (_| | (_| | |_| | (_) | | | |
   \_/ \__,_|_|_|\__,_|\__,_|\__|_|\___/|_| |_|
                                               
  ____                           _       
 / ___|___  _ __   ___ ___ _ __ | |_ ___ 
| |   / _ \| '_ \ / __/ _ \ '_ \| __/ __|
| |__| (_) | | | | (_|  __/ |_) | |_\__ \
 \____\___/|_| |_|\___\___| .__/ \__|___/
                          |_|            
```

If you're looking for validation,

you've come to the right place

---

# Overview Recap

```
- List/NonEmptyList


- Chain/NonEmptyChain


- Validation Concepts <--- you are here


- Validation Coding
```

---

# Today's big question

What does it really mean to validate something?

(Getting our philosopher hats on)

Next time more hands on

---

# Overview

- information theory


- validation concepts


- interactive examples

---

# Longer session

Might cut it in half if you guys looking bored

---

```
 ___        __                            _   _             
|_ _|_ __  / _| ___  _ __ _ __ ___   __ _| |_(_) ___  _ __  
 | || '_ \| |_ / _ \| '__| '_ ` _ \ / _` | __| |/ _ \| '_ \ 
 | || | | |  _| (_) | |  | | | | | | (_| | |_| | (_) | | | |
|___|_| |_|_|  \___/|_|  |_| |_| |_|\__,_|\__|_|\___/|_| |_|
                                                            
 ____       _            _                 _   _             
|  _ \  ___| |_ ___ _ __(_) ___  _ __ __ _| |_(_) ___  _ __  
| | | |/ _ \ __/ _ \ '__| |/ _ \| '__/ _` | __| |/ _ \| '_ \ 
| |_| |  __/ ||  __/ |  | | (_) | | | (_| | |_| | (_) | | | |
|____/ \___|\__\___|_|  |_|\___/|_|  \__,_|\__|_|\___/|_| |_|
                                                             
```

Information slips through our fingers

---

# Main principle

> The further you are from something,
>
> the less you know about it

ie. as you get further away, your information deteriorates

---

# Example

Consider this grouping logic:

```scala
val people = List( // Could be sourced from a db or file
  "Boban",
  "Jimbo",
  "Bobanita",
  "Tom",
  ...
)

people.groupBy(_.length)

// Map(
//   3 -> List("Tom", "Bob", "Sal", ...),
//   4 -> List("Hans", "Jane", ...),
//   5 -> List("Boban", "Jimbo", ...),
//   6 -> ...
//   7 -> ...
//   ...
// )
```

---

# Knowledge?

```scala
val people = List( // Could be sourced from a db or file
  "Boban",
  "Jimbo",
  "Bobanita",
  "Tom",
  ...
)

people.groupBy(_.length)

// Map(
//   3 -> List("Tom", "Bob", "Sal", ...),
//   4 -> List("Hans", "Jane", ...),
//   5 -> List("Boban", "Jimbo", ...),
//   6 -> ...
//   7 -> ...
//   ...
// )
```

Within this context, we know a lot about the group we just made:

- `Map[Int, List[String]]` type


- key represents length (typically values from 1 to ~50)


- the values correspond to names (mostly alphabetical characters, small in length)


- strings are all non-empty/non-null


- map is non-empty


- each group will be non-empty (recall how grouping by key generates non-empty lists)


- map is probably a simple hash map from the standard library


- small number of large groups


- potential duplicates in groups, but no duplicates across groups

---

# One step away

Now the group gets passed into another method:

```scala
val group = generateGroup
analyzeGroup(group)

// Methods:

def generateGroup: Map[Int, List[String]] = {
  val people = List( // Could be sourced from a db or file
    "Boban",
    "Jimbo",
    "Bobanita",
    "Tom",
    ...
  )

  people.groupBy(_.length)
}

def analyzeGroup[Value](group: Map[Int, List[Value]]): Unit = {
  // Do some processing
  ....
}
```

Q: What does `analyzeGroup` know about our group?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# One step away

> Q: What does `analyzeGroup` know about our group?

```scala
def analyzeGroup[Value](group: Map[Int, List[Value]]): Unit = {
  ....
}
```

A: Really just the type

The word "group" indicates that it probably at least knows it represents the outcome of grouping

(perhaps there is some implicit context there)

Potentially other groups from other sources are being passed to it

Doesn't know concrete type of `Value`

---

# One step away

> Q: What does `analyzeGroup` know about our group?

A lot less than the author of `generateGroup`!

We lost a lot of information by moving one step away

---

# Two steps away

Inside our analysis method, it hands off a list to another function

```scala
def analyzeGroup(group: Map[Int, List[Value]]): Unit = {
  ....
  group.get(3) match {
    case Some(threeGroup) => analyzeSeq(threeGroup)
    case None => {}
  }
  ...
}

// List -> Seq
def analyzeSeq[Value](seq: Seq[Value]): Unit = {
  ...
}
```

What does `analyzeSeq` know about this sequence?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Two steps away 

```scala
// List -> Seq
def analyzeSeq[Value](seq: Seq[Value]): Unit = {
  ...
}
```

> What does `analyzeSeq` know about this sequence?

Even less

Doesn't even know that it's a `List`

Can't assume it's non-empty (which the layer above _may_ have known implicitly)

---

# Recap

> The further you are from something,
>
> the less you know about it

With each step, you potentially lose information

---

# Thinking about the first step away

Which information definitely survived?

## Definitely Survived

- it's a map from Int to a List of something

## Maybe didn't survive

- key represents length (typically values from 1 to ~50)
- the values correspond to names (mostly alphabetical characters, small in length)
- strings are all non-empty/non-null
- map is non-empty
- each group will be non-empty (recall how grouping by key generates non-empty lists)
- map is probably a simple hash map from the standard library
- small number of large groups
- potential duplicates in groups, but no duplicates across groups

---

# Why

> it's a map from Int to a List of something

Why did this information survive where the others didn't?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Survival of the type-ist

> Why did this information survive where the others didn't?

Encoded into the type itself

---

# Hmmm...

Value level information deteriorates quickly

Type level information also deteriorates, just much slower

:python-explode:

---

```
 _____ 
|___ / 
  |_ \ 
 ___) |
|____/ 
       
                                   
 ___  ___  _   _ _ __ ___ ___  ___ 
/ __|/ _ \| | | | '__/ __/ _ \/ __|
\__ \ (_) | |_| | | | (_|  __/\__ \
|___/\___/ \__,_|_|  \___\___||___/
                                   
```

We'll look at 3 places where we get information

(there might be others!)

---

# 1 - types

Pretty self-explanatory

Tightly integrated with the language

---

# 2 - context

```scala
class Demo {
  def method1(): Unit = {
    eatString("foo")
  }

  def method2(): Unit = {
    eatString("bar")
  }

  def method3(): Unit = {
    eatString("baz")
  }

  // Safe - see proof
  private eatString(s: String): Char = s.head
}
```

Proof:

- method is private
- all uses of it are within the class
- all uses within the class are passing a non-empty string
- no need for an effect or handling

---

# 3 - scoped runtime checks

```scala
val name = ... // read from command line

// Can't assume name is non-empty

if (name.nonEmpty) {
  // For this scope,
  // can assume
  // name is non-empty
  println(name.head)
  ...
}

// Can't assume name is non-empty
```

---

# 3 - scoped runtime checks

Pattern matching is the same thing

```scala
name match {
  case "Boban" => ... // We know name == "Boban"
  case _ if name.startsWith("Bob") => ...
  case _ => // Know name doesn't start with "Bob"
}
```

---

# Summary

- types


- contextual reasoning


- scoped runtime checks

---

```
 ____                           _ 
/ ___|  ___ ___  _ __   ___  __| |
\___ \ / __/ _ \| '_ \ / _ \/ _` |
 ___) | (_| (_) | |_) |  __/ (_| |
|____/ \___\___/| .__/ \___|\__,_|
                |_|               
 ____              _   _                
|  _ \ _   _ _ __ | |_(_)_ __ ___   ___ 
| |_) | | | | '_ \| __| | '_ ` _ \ / _ \
|  _ <| |_| | | | | |_| | | | | | |  __/
|_| \_\\__,_|_| |_|\__|_|_| |_| |_|\___|
                                        
  ____ _               _        
 / ___| |__   ___  ___| | _____ 
| |   | '_ \ / _ \/ __| |/ / __|
| |___| | | |  __/ (__|   <\__ \
 \____|_| |_|\___|\___|_|\_\___/
                                
```

Drill into this one a bit more

---

# Reversing deterioration

This is our tool for recovering lost information:

```scala
def producer(): Unit = {
  // Compile time constant
  // Contextual argument that it's non-empty
  val x = "abcdef"
  consumer(x)
}

...

def consumer(x: String): Option[Char] = {
  // Not sure if x is non-empty
  // Recover information with a runtime check
  if (x.nonEmpty)
    Some(x.head)
  else
    None
}
```

---

# Knowledge and effects

```scala
def consumer(x: String): Option[Char] = {
//                       ^^^^^^
  if (x.nonEmpty)
    Some(x.head)
  else
    None
}
```

Scoped runtime checks might fail

Hence we typically end up with effects (or exceptions) to model failures

---

# Limitation

> Scoped runtime knowledge doesn't transfer beyond that scope

---

# Scenario:

> A name is passed from command line as a String
>
> e.g. "Boban"
>
> We expect that the name must be non-empty
>
> Many of the our name utilites require this too

---

# Coding this

```scala
def main(args: Array[String]): Unit = {

  // Scoped runtime check
  val name = args match {
    case Array(name) =>
      if (name.isEmpty)
        throw new Exception("Name can't be empty")
    case _ =>
        throw new Exception("One name parameter expected")
  }

  Lib.doStuff(name)
}
```

---

# Defensive doStuff

```scala
def main(args: Array[String]): Unit = {

  // Scoped runtime check
  val name = args match {
    case Array(name) =>
      if (name.isEmpty)
        throw new Exception("Name can't be empty")
    case _ =>
        throw new Exception("One name parameter expected")
  }

  Lib.doStuff(name)
}

...

object Lib {
  // Public util
  // Could be called from anywhere
  // Must be defensive!
  def doStuff(name: String): Option[User] = {
    if (name.isEmpty)
      None
    else {
      // Happy path logic
      ...
      Some(user)
    }
  }
}
```

---

# QA Analogy

The point of the validation code:

```scala
def main(args: Array[String]): Unit = {

  val name = args match {
    case Array(name) =>
      if (name.isEmpty)
        throw new Exception("Name can't be empty")
    case _ =>
        throw new Exception("One name parameter expected")
  }

                              // | "QA Approved" sticker
                              // | "I've checked it, it's all good"
  Lib.doStuff(name)           // |
                              // | Name isn't empty
  ...                         // |
}
```

---

# Sticker falls off  :(

The sticker is only good for the `main` function

Can't take it with us into `doStuff`

```scala
object Lib {
  def doStuff(name: String): Option[User] = {
    if (name.isEmpty)
      None
    else {
      ...           // Generates its own sticker
      ...           //
      Some(user)    //
    }
  }
}
```

---

# Not first class

Scoped runtime checks really just exist in the developer's mind

Not represented as first class concepts in the language

---

# Re-validation Summary

- we validated our name in `main`


- that knowledge did not pass to `doStuff`


- `doStuff` needed to re-validate it


- `doStuff` has an effect

---

# Analogy

Returning a tupperware

Lack of trust, unclear responsibilities

---

```
__        ___           _   
\ \      / / |__   __ _| |_ 
 \ \ /\ / /| '_ \ / _` | __|
  \ V  V / | | | | (_| | |_ 
   \_/\_/  |_| |_|\__,_|\__|
                            
           _     
          (_)___ 
          | / __|
          | \__ \
          |_|___/
                 
            _ _     _       _   _            ___ 
__   ____ _| (_) __| | __ _| |_(_) ___  _ __|__ \
\ \ / / _` | | |/ _` |/ _` | __| |/ _ \| '_ \ / /
 \ V / (_| | | | (_| | (_| | |_| | (_) | | | |_| 
  \_/ \__,_|_|_|\__,_|\__,_|\__|_|\___/|_| |_(_) 
                                                 
```

---

# Good validation

> (1) A runtime check that your data meets your requirements
>
> (2) (If errors) Collecting as many errors as possible
>
> (3) (If valid) Encoding that knowledge such that it won't deteriorate

---

# Back to our name example

```scala
def main(args: Array[String]): Unit = {

  val name = args match {
    case Array(name) =>
      if (name.isEmpty)
        throw new Exception("Name can't be empty")
    case _ =>
        throw new Exception("One name parameter expected")
  }

  Lib.doStuff(name)
}
```

> (1) A runtime check that your data meets your requirements (TICK!)
>
> (2) (If errors) Collecting as many errors as possible      (TICK! - simple case)
>
> (3) (If valid) Encoding that knowledge such that it won't deteriorate (FAIL)


---

# Observations

---

# Deterioration

> (1) A runtime check that your data meets your requirements
>
> (2) (If errors) Collecting as many errors as possible
>
> (3) (If valid) Encoding that knowledge such that it won't deteriorate

(1) is usually required because our knowledge of our data is weak

Often due to deterioration, or because it entered our system in a weak state

---

# Error handling

> (1) A runtime check that your data meets your requirements
>
> (2) (If errors) Collecting as many errors as possible
>
> (3) (If valid) Encoding that knowledge such that it won't deteriorate

More on (2) next time

---

# Encoding knowledge

> (1) A runtime check that your data meets your requirements
>
> (2) (If errors) Collecting as many errors as possible
>
> (3) (If valid) Encoding that knowledge such that it won't deteriorate

(3) means we don't have to re-validate again

We worked hard to reverse the deterioration,

and we're not going to let that information slide away

---

# Encoding knowledge

> (1) A runtime check that your data meets your requirements
>
> (2) (If errors) Collecting as many errors as possible
>
> (3) (If valid) Encoding that knowledge such that it won't deteriorate

A strong encoding for (3) will mean we avoid a lot of unnecessary

effects and exceptions

---

```
 ____  _                         
/ ___|| |_ _ __ ___  _ __   __ _ 
\___ \| __| '__/ _ \| '_ \ / _` |
 ___) | |_| | | (_) | | | | (_| |
|____/ \__|_|  \___/|_| |_|\__, |
                           |___/ 
 _____                      
|_   _|   _ _ __   ___  ___ 
  | || | | | '_ \ / _ \/ __|
  | || |_| | |_) |  __/\__ \  ?
  |_| \__, | .__/ \___||___/
      |___/|_|              
```

"Strong" being used in an informal sense here

---

# 3 ways to gather information

Recall:

- types


- contextual


- scoped runtime checks

## Moving information around

Of these 3, in scala,

the _most practical_ way to package up information and ship it around is with "types"

(Other languages might have more tricks)

---

# Actually! Aside

When we looked at type erasure,

we actually saw how the compiler will encode information into a value (a type tag)

(Not very practical as a general solution to our problem)

---

# Types

We've already seen this with `NonEmptyList.fromList`

```scala
def main(args: Array[String]): Unit = {
  
  // We require at least one name from the cli
  val names = NonEmptyList.fromList(args.toList).getOrElse(
    throw new Exception("At least one input name is required")
  )

  println(longest(names))
}

def longest(names: NonEmptyList[String]): String = names.toList.maxBy(_.length)
```

`longest` didn't need to revalidate our list

We encoded the knowledge we gained into a type (`NonEmptyList`)

---

# Objection!

You say:

> But Rohan, I know all this,
>
> I already convert incoming data into a "strong" type that's easier to work with

Yes but that's not the same thing as validation

---

```
__     __    _ _     _       _   _             
\ \   / /_ _| (_) __| | __ _| |_(_) ___  _ __  
 \ \ / / _` | | |/ _` |/ _` | __| |/ _ \| '_ \ 
  \ V / (_| | | | (_| | (_| | |_| | (_) | | | |
   \_/ \__,_|_|_|\__,_|\__,_|\__|_|\___/|_| |_|
                                               

                       
            __   _____ 
            \ \ / / __|
             \ V /\__ \
              \_/ |___/
                       

  ____                           _                     
 / ___|___  _ ____   _____ _ __ (_) ___ _ __   ___ ___ 
| |   / _ \| '_ \ \ / / _ \ '_ \| |/ _ \ '_ \ / __/ _ \
| |__| (_) | | | \ V /  __/ | | | |  __/ | | | (_|  __/
 \____\___/|_| |_|\_/ \___|_| |_|_|\___|_| |_|\___\___|
                                                       
```

We often convert weak types into stronger types,

but what is the motive for most developers?

---

# Example - seconds

> A script receives a string input from the command line
>
> That input represents the seconds of the minute

---

# Mindset

> I'm not going to deal with seconds as a string,
>
> that's too hard to work with
>
> Int!
>
> Int is stronger than String

```scala
def main(args: Array[String]): Unit = {

  val seconds: Int = args match { ... }

  Lib.doStuff(seconds)
}

// Elsewhere
object Lib {
  def doStuff(seconds: Int): Option[DateTime] = {
    ...
  }
}
```

---

# Zooming in

```scala
def main(args: Array[String]): Unit = {
  val seconds: Int = args match {
    case Array(secondsStr) =>
      if (secondsStr.matches("\\d+"))
        throw new Exception("Input must be a second value, e.g. 50")

      val secondsInt = secondsStr.toInt

      if (secondsInt < 0 || secondsInt > 59)
        throw new Exception("Seconds input must be from 0 to 59 inclusive")

      secondsInt

    case _ => throw new Exception("Exactly one integer input expected")
  }

  Lib.doStuff(seconds)
}
```

---

# Capturing validation

```scala
val seconds: Int = args match {
  case Array(secondsStr) =>
    if (secondsStr.matches("\\d+"))
      throw new Exception("Input must be a second value, e.g. 50")

    val secondsInt = secondsStr.toInt

    if (secondsInt < 0 || secondsInt > 59)
      throw new Exception("Seconds input must be from 0 to 59 inclusive")

    secondsInt

  case _ => throw new Exception("Exactly one integer input expected")
}
```

Two questions:

1. What did you validate about your input?

2. What did you encode about your input?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
```

---

# Capturing validation

```scala
val seconds: Int = args match {
  case Array(secondsStr) =>
    if (secondsStr.matches("\\d+"))
      throw new Exception("Input must be a second value, e.g. 50")

    val secondsInt = secondsStr.toInt

    if (secondsInt < 0 || secondsInt > 59)
      throw new Exception("Seconds input must be from 0 to 59 inclusive")

    secondsInt

  case _ => throw new Exception("Exactly one integer input expected")
}
```

Two questions:

> 1. What did you validate about your input?

Int from 0 to 59

> 2. What did you encode about your input?

Int (ie. numeric)

:angry-rohan-parrot:

---

# Zoom out

```scala
def main(args: Array[String]): Unit = {

  val seconds: Int = args match { ... }

  Lib.doStuff(seconds)
}

object Lib {
  def doStuff(seconds: Int): Option[DateTime] = {
    if (seconds < 0 || seconds > 59)
      None
    else {
      ...
      Some(...)
    }
      
  }
}
```

`doStuff` repeats the validation and returns `Option`

Why: we didn't encode that information

---

# Keeping concepts separate

Two things swirling in your brains when you're validating:

- validating it


- converting it to an "easy to use" type

---

# My observation

> - validating it
>
> - converting it to an "easy to use" type

People conflate these

Subconsciously they focus on ease of use

Validation usually isn't done in a principled consistent way

---

# Common example

Using `LocalDate` to represent a month

e.g. "2021-03-01" represents March 2021

---

# Strings for Id's

Example:

> SignupDate-Group-Hash
>
> e.g.
> 20210312-145-FE1239A0

---

# Things we might want to do

> 20210312-145-FE1239A0

- split it into tokens

```scala
id.split("-") match {
  case Array(date, group, hash) => ...
}
```

- check someone signed up on Jan 3rd 2019

```scala
id.startsWith("20190103")
```

_Super_ convenient to represent it as a string

---

# Weak String

> _Super_ convenient to represent it as a string

But String is super wide, a String could be "Boban" or ""

---

# String doesn't encode validation

You can imagine this kind of validation:

```scala
val id = ... // from somewhere untrustworthy

if (isValid(id))
  doStuff(id)
else
  // handle failure


...

def doStuff(id: String): Unit = {
  // Will probably not bother to validate it

  // Or will need to validate it and return `Option`
}
```

---

# Very common

Developers will use String for id's that originated as stronger types, e.g.

- UUID's


- mongo ObjectID's

---

# Recap: The point

> But Rohan, I know all this,
>
> I already convert incoming data into a "strong" type that's easier to work with

Developers are usually focused on the "easiest" type to use

(Usually the one closest at hand that "does the job")

---

# Recap: The point

> Developers are usually focused on the "easiest" type to use

That is not the same as validation

They sometimes overlap, but they are different motives

---

# Recap: The point

> Developers are usually focused on the "easiest" type to use

Leads to inconsistent ad-hoc validation

Leads to:

- unnecessary re-validation


- unsafe code (if you just don't bother)


- unclear responsibilities about whose job it is to check something (tupperware analogy)

--- 

```
 ____                 _   _           _ _ _   _           
|  _ \ _ __ __ _  ___| |_(_) ___ __ _| (_) |_(_) ___  ___ 
| |_) | '__/ _` |/ __| __| |/ __/ _` | | | __| |/ _ \/ __|
|  __/| | | (_| | (__| |_| | (_| (_| | | | |_| |  __/\__ \
|_|   |_|  \__,_|\___|\__|_|\___\__,_|_|_|\__|_|\___||___/
                                                          
```

Sometimes it's too hard

---

# Avoiding misunderstandings

> Rohan is saying to encode absolutely everything into types

Nope

Sometimes that's too hard

---

# Avoiding misunderstandings

Firstly I don't want you to think that choosing an "easy to work with" type is validation

---

# Avoiding misunderstandings

Then once the concepts are clear,

make a sensible decision about the best type representation taking into account both:

- convenience of the type


- how much information it encodes

---

# Requirements that are easy to encode

A non-empty collection of things (`NonEmpty---`)

A day of the year, e.g. May 2nd (`MonthDay`)

---

# Requirements that are hard to encode

A date that must be on or after 2020-01-01

Ordered list has no duplicates

A pair of numbers where the left is less than the right

---

# Dual approach

> A date that must be on or after 2020-01-01

Most of your utilities just need to know it's a date

The "after 2020-01-01" is probably only needed in a narrow scope

---

# Dual approach

> A date that must be on or after 2020-01-01

```scala
val date: LocalDate = ... // Encode the date knowledge into a type

// Used scoped runtime check
if (date.isBefore(LocalDate.of(2020, 1, 1))) {
  // Handle error
}
else {
  // Happy path
  ...

  // Unlikely that doStuff requires it to be after 2020-01-01 as it's a util
  // Okay to lose the information
  doStuff(date)
}
```

---

```
__     __    _ _     _       _   _             
\ \   / /_ _| (_) __| | __ _| |_(_) ___  _ __  
 \ \ / / _` | | |/ _` |/ _` | __| |/ _ \| '_ \ 
  \ V / (_| | | | (_| | (_| | |_| | (_) | | | |
   \_/ \__,_|_|_|\__,_|\__,_|\__|_|\___/|_| |_|
                                               
 ____        
| __ ) _   _ 
|  _ \| | | |
| |_) | |_| |
|____/ \__, |
       |___/ 
  ____                           _   _             
 / ___|___  _ ____   _____ _ __ | |_(_) ___  _ __  
| |   / _ \| '_ \ \ / / _ \ '_ \| __| |/ _ \| '_ \ 
| |__| (_) | | | \ V /  __/ | | | |_| | (_) | | | |
 \____\___/|_| |_|\_/ \___|_| |_|\__|_|\___/|_| |_|
                                                   
```

---

# Validation by Convention

Sometimes we just say:

> I'll just assume people follow a sensible convention
>
> That saves us a lot of effort and keeps things simple

As the author you're just trusting the user is sensible

---

# Example

```scala
type Age = Int

def doStuff(age: Age): Unit = {
  // Don't bother to check it
  // Just assume it's fine
  ...
}
```

Keeps things simple

- `Int` is cheap, simple and close at hand


- don't have to define custom types

---

# Number of data inputs

This approach works well if there's only a small number of places data comes in

You can more realistically police all those entry points

---

# Pertinent example - null

Scala developers pretend `null` doesn't exist

We never write code like this:

```scala
def doStuff(s: String): Option[Int] = {
  if (s == null)
    None
  else {
    ...
    Some(...)
  }
}
```

You would have effects everywhere and lots of extra unit tests

---

# Reasoning behind this

- scala standard library follows a strict convention of not using null


- pseudo-standard libraries like cats and ZIO do too


- angry code reviewers will yell at juniors when they use null


- developers of moderate experience wouldn't even think to use null


- intellij (hopefully) puts squiggly lines or other warnings in when it sees null 


- if a null sneaks in, we'll notice it pretty quickly due to `NullPointerException`

---

# Ignore null by convention

The chances of a null creeping in are very small

The effort to deal with it properly and consistently is high

## Conclusion

Just act like it's not there

---

# Smelly optimism

The responsibility has been put onto the caller

```scala
type Age = Int

def doStuff(age: Age): Age = {
  // Don't bother to check it
  // Just assume it's fine
  age + 1
}
```

Author:

> I'm sure they'll do the right thing, nothing will go wrong

It smacks of optimism

---

# Trust issues

```scala
type Age = Int

def doStuff(age: Age): Age = {
  // Don't bother to check it
  // Just assume it's fine
  age + 1
}
```

Nothing enforces that trust though

(That's the nature of "convention" - it only exists in the developer's mind)

---

# Complexity

Later we'll see some more complex "stronger types"

Your reaction might be:

> That's too much effort, the types get in my way

---

# Conversation

Python developer writes this:

```python
def doStuff(name):
  # Process the name
  # ...
```

Scala developer says:

> What is stopping someone passing in a bad value?

---

# Conversation

Python developer writes this:

```python
def doStuff(name):
  # Process the name
  # ...
```

Scala developer says:

> What is stopping someone passing in a bad value?

Python developer says:

> _Obviously_ no one would do that.
>
> The types are just getting in the way.

(ie. trust the user to follow a convention and do the right thing)

---

# Conversation

Python developer writes this:

```python
def doStuff(name):
  # Process the name
  # ...
```

Scala developer says:

> What is stopping someone passing in a bad value?

Python developer says:

> _Obviously_ no one would do that.
>
> The types are just getting in the way.

(ie. trust the user to follow a convention and do the right thing)

Scala developer says:

> I know it seems obvious,
>
> but you're under-estimating humanity's ability to make mistakes.
>
> You're being too optimistic.

---

# Scala

That same scala developer then writes this code:

```scala
type Age = Int

def doStuff(age: Age): Age = {
  // Don't bother to check it
  // Just assume it's fine
  age + 1
}
```

> A stronger type is too much effort.
>
> We can just trust the user...

:inconsistent-parrot:

Did they decide they can trust the user before or after

thinking about how much effort it will be?

---

# TDD

Validation by convention doesn't mix well with testing

```scala
type Age = Int

def doStuff(age: Age): Age = {
  // Don't bother to check it
  // Just assume it's fine
  age + 1
}
```

> Just assume the value will be 18-150

How does that translate into tests?

The behavior is deliberately unspecified

---

# Specifying error cases

If you say this:

> Passing a negative value should cause an IllegalArgumentException

then that will force the implementation to add code like this:

```scala
type Age = Int

def doStuff(age: Age): Age = {
  // Don't bother to check it
  // Just assume it's fine      <--- not anymore!
  if (age < 0)
    throw new IllegalArgumentException("Age can't be negative")

  
  age + 1
}
```

Now you're dealing with exceptions which are ugly and harder to test

Might as well use an effect

---

# No tests!

If you're being true to your convention,

you will deliberately not test it

:crazy-parrot:

---

# Non-deterministic behavior

If the error handling behavior isn't specified,

then it's non-deterministic

ie. someone could change it and your tests will still pass

---

# Things that might happen

- it works (when it shouldn't)

```scala
def doStuff(age: Age): Age = {
  age + 1
}

doStuff(-2)
// -1
```

- throw an exception


- infinite loop

---

# Example from the wild

Implicit assumption that the byte limit is non-negative

Put in a negative byte limit and it goes into an infinite loop

```scala
def trimToMaxBytes(byteLimit: Int)(str: String): String = {
  @tailrec
  def truncate(str: String, byteLimit: Int): String = {
    if (str.getBytes().length <= byteLimit) str.trim
    else truncate(str.dropRight(1), byteLimit)
    //                ^^^^^^^^^^^^
  }
  truncate(str.trim, byteLimit)
}

@ "abc".dropRight(1) 
// "ab"

@ "".dropRight(1) 
// ""
```

Some of these functions are too "helpful"

(drop, take, slice)

---

# Non-deterministic

Behavior is a quirk of the implementation

Comes down to how `dropRight` works

```scala
def trimToMaxBytes(byteLimit: Int)(str: String): String = {
  @tailrec
  def truncate(str: String, byteLimit: Int): String = {
    if (str.getBytes().length <= byteLimit) str.trim
    else truncate(str.dropRight(1), byteLimit)
    //                ^^^^^^^^^^^^
  }
  truncate(str.trim, byteLimit)
}

@ "abc".dropRight(1) 
// "ab"

@ "".dropRight(1) 
// ""
```

`substring` would have caused different behavior

```scala
@ "abc".substring(0, "abc".length - 1) 
res17: String = "ab"

@ "".substring(0, "".length - 1)
// java.lang.StringIndexOutOfBoundsException: String index out of range: -1
```

---

# Sql example

```scala
def buildInsert(name: String, age: Integer): String =
  s"INSERT INTO people VALUES ('$name', $age)"

@ buildInsert("Boban", 28) 
// "INSERT INTO people VALUES ('Boban', 28)"
```

---

# null?

What happens if we pass null in?

```scala
@ buildInsert(null, 28) 

@ buildInsert("Boban", null) 
```

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Meet Mr/Mrs null

Did you think it would throw a `NullPointerException`?

Need to beat that optimism out of you

```scala
def buildInsert(name: String, age: Integer): String =
  s"INSERT INTO people VALUES ('$name', $age)"

@ buildInsert(null, 28) 
// "INSERT INTO people VALUES ('null', 28)"

@ buildInsert("Boban", null) 
// "INSERT INTO people VALUES ('Boban', null)"
```

Both are valid sql

If age is a nullable column, the second will work

(Similar issues with json and it's null)

---

# Why no exception?

```scala
@ null.toString 
// java.lang.NullPointerException

def buildInsert(name: String, age: Integer): String =
  s"INSERT INTO people VALUES ('$name', $age)"
  // Isn't it just doing this?
  s"INSERT INTO people VALUES ('" + name.toString + "', " + age.toString + ")" 
```

---

# Why no exception?

I lied a little when I said:

> String interpolation is just calling `.toString` under the hood

Logic is more "helpful":

```scala
if (foo == null) "null"
else foo.toString
```

Similar to the `.toString` mindset of being designed for humans, not for logic

---

# Fallout

Fixing a data bug isn't just about fixing the bug itself,

you have to undo all the damage done to the database

```scala
@ buildInsert(null, 28) 
// "INSERT INTO people VALUES ('null', 28)"

@ buildInsert("Boban", null) 
// "INSERT INTO people VALUES ('Boban', null)"
```

Spotting Mr/Mrs null isn't too hard

But knowing which null ages are valid and which are from the bug is hard

---

# Contrast with a strong type

```scala
// Need tests around the empty case
def oldest(people: List[Person]): Person = ...
def oldest(people: List[Person]): Option[Person] = ...

// No tests needed, the compiler does that for us
def oldest(people: NonEmptyList[Person]): Person = ...
```

Becomes handy if you have _many_ methods (e.g. `Bigify`)

Stronger input types => Less tests

---

```
 ____                            _             
/ ___| _   _ _ __ ___  _ __ ___ (_)_ __   __ _ 
\___ \| | | | '_ ` _ \| '_ ` _ \| | '_ \ / _` |
 ___) | |_| | | | | | | | | | | | | | | | (_| |
|____/ \__,_|_| |_| |_|_| |_| |_|_|_| |_|\__, |
                                         |___/ 
             
 _   _ _ __  
| | | | '_ \ 
| |_| | |_) |
 \__,_| .__/ 
      |_|    
 _   _          
| |_| |__   ___ 
| __| '_ \ / _ \
| |_| | | |  __/
 \__|_| |_|\___|
                
 _   _                           
| |_| |__   ___  ___  _ __ _   _ 
| __| '_ \ / _ \/ _ \| '__| | | |
| |_| | | |  __/ (_) | |  | |_| |
 \__|_| |_|\___|\___/|_|   \__, |
                           |___/ 
```

---

# My goals

- the concepts are clear and not conflated


- the pro's and con's are clear

---


# My goals

I'm not trying to be dogmatic

Sometimes it's impractical to capture very specific information in a type

But think about it

---

```
 ___       _                      _   _           
|_ _|_ __ | |_ ___ _ __ __ _  ___| |_(_)_   _____ 
 | || '_ \| __/ _ \ '__/ _` |/ __| __| \ \ / / _ \
 | || | | | ||  __/ | | (_| | (__| |_| |\ V /  __/
|___|_| |_|\__\___|_|  \__,_|\___|\__|_| \_/ \___|
                                                  
 _____                           _           
| ____|_  ____ _ _ __ ___  _ __ | | ___  ___ 
|  _| \ \/ / _` | '_ ` _ \| '_ \| |/ _ \/ __|
| |___ >  < (_| | | | | | | |_) | |  __/\__ \
|_____/_/\_\__,_|_| |_| |_| .__/|_|\___||___/
                          |_|                
```

I'll throw some scenarios at you in the form:

> A command line program receives an argument representing ...
>
> How would you validate it?

Stuff like:

- clarifying the specification


- the nuts and bolts of validating it


- final representation

For these examples we'll assume we're not doing "validation by convention"

---

# Scenario - age

> A command line program receives an argument representing someone's age.
>
> How would you validate it?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Clarifying the spec

The number of complete years since their birth

ie.

- an integral value (e.g. 50)


- no months


- rounded down

---

# Validation logic

- parse to a numeric type (like `Int` or `Short`)


- then limit the range

---

# Limiting the range

- age should be _at least_ non-negative


- in many contexts, users have to be at least 18 to use a service (excludes Enxhell)


- values above 150 would also probably raise red flags (excludes me)

---

# Final representation: Integral type?

- `Int` or `Short` encodes the numericalness of it.


- `Short` is a little cheaper but scares the children

Doesn't encode:

- non-negative


- between 18 and 150

---

# Final representation: Refined wrapper type?

Complete encoding?

```scala
class Age private(inner: Short) {

  def toInt: Int = inner.toInt

  def toShort: Short = inner

  def isEqual(other: Short): Boolean = this.inner == other.inner

  // And define <, <=, >, >=
  ...

  override def toString: String = s"Age($inner)"
}

object Age {
  // The only way to construct an Age
  def fromInt(age: Short): Option[Age] = {
    if (age < 18 || age > 150) None
    else Some(new Age(inner))
  }
}
```

Any `new Age` code needs to be heavily policed - too free and hippy-ish to be trusted

Makes sense to define comparison operators for age, but not most arithmetic operations

---

# Effort

Not saying you have to use a refined type

just showing the pro's and con's and how you might do this

---

# Scenario - month

> A command line program receives an argument representing a month.
>
> e.g. "01" or "02" or "11"
>
> How would you validate it?

---

# Clarifying the spec

> A command line program receives an argument representing a month.
>
> e.g. "01" or "02" or "11"
>
> How would you validate it?

- 1-based, e.g. "1" means January (not "0")


- we'll allow zero padding on single digits, e.g. "05"

---

# Validation logic

Validation logic:

- parse to something integral like `Int`


- check it's 1-12

---

# Final representation - integral type?

e.g. `Int` or `Short`

- they are _very_ wide (only need 12 values)


- they are ambiguous - is January 1 or 0? Will cause bugs

---

# Final representation - ADT/Enum?

Small number of values

Enum or ADT is a good fit

---

# java.time.Month

`java.time` already has a java enum for this

```scala
@ import java.time.Month 

@ Month.
APRIL      DECEMBER   JANUARY    JUNE       MAY        OCTOBER    from       valueOf
AUGUST     FEBRUARY   JULY       MARCH      NOVEMBER   SEPTEMBER  of         values

@ Month.of(2) 
// FEBRUARY
// 1 based

@ Month.values 
// Array(JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY,
//       AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER)

@ Month.APRIL. 
adjustInto           get                  getValue             minLength            plus
compareTo            getDeclaringClass    isSupported          minus                query
firstDayOfYear       getDisplayName       length               name                 range
firstMonthOfQuarter  getLong              maxLength            ordinal

@ Month.APRIL.ordinal 
// Int = 3
// Zero-based
```

Ticks many boxes:

- close at hand (standard library)


- nice api


- fully encodes everything we validated

Lots of pro's, no real con's

---

# Scenario - port

> A command line program receives an argument representing a port.
>
> e.g. "27127", "8888"
>
> How would you validate it?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Clarifying spec:

> A command line program receives an argument representing a port.
>
> e.g. "27127", "8888"
>
> How would you validate it?


- should be parseable as an int


- can't be negative


- upper bound? (e.g. max 99999)


- allowing all ports? (some low numbered ports are reserved or more secure)


- context specific restrictions? e.g. for analytics, postgres and mongo have reserved ranges on the ec2 machine

---

# Validation logic:

- convert to an `Int` or `Short`


- check your specific range requirements

---

# Final representation

`Int/Short` doesn't encode all your range checking

Too many values for an enum (unless you have a spare intern or vim macro)

Could make a similar refined type

```scala
class Port private(inner: Short) {
  // For sticking it into connection strings in _logical_ contexts
  def asString: String = inner.toString

  // For human readability
  override def toString: String = s"Port($inner)"
}
```

Don't need any kind of arithmetic - all we care about with ports is their value

(In an alternate universe, ports could have been strings like "Boban")

---

# Scenario - ip address

> A command line program receives an argument representing an ip address.
>
> e.g. "192.168.1.32", "0.0.0.0", "0.0.0.0/32"
>
> How would you validate it?

---

# Clarifying the sepc

> A command line program receives an argument representing an ip address.
>
> e.g. "192.168.1.32", "0.0.0.0", "0.0.0.0/32"
>
> How would you validate it?

- just old-school 32 bit ones? Or the fancy new ones too?


- allowing trailing "/32"?


- allowing zero padding, e.g. "001.002.003.004"?

---

# Validation logic

- split by '.' or use a regex


- make sure there's 4 tokens


- check for illegal padding if we decided to ban it


- convert all to ints


- check values are 0-255

---

# Final representation - Int

It's 4 bytes on all JVM's

Perfectly encodes our validation logic

Cheap

But is very hard to work with on its own

```scala
type IP = Int

// Turn an int into the xxx.xxx.xxx.xxx style string
def asString(ip: IP): String = ...

def token1(ip: IP): Int = ...
def token2(ip: IP): Int = ...
def token3(ip: IP): Int = ...
def token4(ip: IP): Int = ...
```

---

# Final representation - wrapped String

```scala
class IP private(inner: String) {
  def asString: String = inner
}

object IP {
  def fromString(ip: String): Option[IP] = ... // validation logic
}
```

This might be more practical if the only thing you want it for

is to put into a connection string

(ie. no logic related to the individual numbers)

---

# Final representation - wrapped String

```scala
// Validate it's a valid ip
val ip = IP.fromString("192.168.1.14").getOrElse(...)

// Validate it's a master of Wing Chun
val ipMan = IP.fromString("Man").getOrElse(...)
```

---

# Final representation - String

Or just be lazy and use a `String` and all code assumes it's been validated

---

# Scenario - two element list

> A command line program receives arguments (plural) representing a collection of at least 2 strings
>
> a.g. foo bar baz
>
> How would you validate it?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Clarifying the spec

- is there an upper limit? Might make sense on the CLI


- inputs are all `String`'s and we don't have any restrictions on the strings themselves

---

# Final representation

- `NonEmptyList` feels closer but doesn't up solving anything better than `List`


- if you're not going to build a custom type, then might as well just use `List`

---

# Custom type

`NonEmptyList` is conceptually:

> A list plus an element

```scala
case class NonEmptyList[+A](head: A, tail: List[A])
```

---

# Custom type

`TwoList` is conceptually:

> A list plus two elements

```scala
case class NonEmptyList[+A](head: A, tail: List[A])

case class TwoList[+A](head: A, head2: A, tail: List[A])
```

Pretty easy

Definitely some puns to be had with `TwoList.toList` and `list.toTwoList`

---

# Api

If you're too lazy to add a rich api (like `map`, `filter`, etc...)

then just add a `toList` and use it instead

```scala
case class TwoList[+A](head: A, head2: A, tail: List[A]) {
  // Chose `val` not `def` or `lazy val` - depends on context
  val toList: List[A] = head :: head2 :: tail
}

twoList.toList.filter(_ > 1)  // List

twoList.toList.map(_ * 2) // List :(
```

---

# Preserving type information

Could add methods specific methods:

```scala
case class TwoList[+A](head: A, head2: A, tail: List[A]) {
  val toList: List[A] = head :: head2 :: tail

  val toNel: NonEmptyList[A] = NonEmptyList(head, head2 :: tail)

  def tailNec: NonEmptyList[A] = NonEmptyList(head2, tail)

  def map[B](f: A => B): TwoList[B] = TwoList(f(head), f(head2), tail.map(f))
}
```

How much work you want to put in depends on how often you'll use it

---

# Validation logic

If we go with `TwoList` then it's a snug fit for `unapplySeq`:

```scala
val twoList = args match {

  case Array(head, head2, tail@_*) => TwoList(head, head2, tail)

  case _ => // error handling
}
```

(Wouldn't be so clean if we had to validate individual elements)

## Breaking down `tail@_*`

- `_*` means "0 or more of anything"


- we want to use that tail on the RHS so it needs a name


- `@` is used for "outer" labels


- `tail@` gives it the name `tail`

---

# Scenario - sorted list

> A command line program receives arguments (plural) representing a sorted list
>
> e.g. "0 1 3 10 12 15"
>
> How would you validate it?

---

# Clarifying the spec

> A command line program receives arguments (plural) representing a sorted list

- sorting ints


- are duplicates allowed?


- ascending (not descending)


- if it's not sorted, should we auto-correct it? (Can recover)

---

# Auto-correcting

This is a case where we can always recover from an issue

Can sort it ourselves

(No effect needed)

---

# Should we auto-correct though?

Might be output from another program, and being unsorted indicates some fatal issue

Incorrect data might indicate a deeper issue

Perhaps we shouldn't trust it and should just bail out

---

# Final representation

No suitable type close at hand

You can use a simple list and auto-correct

Will add a performance overhead

---

# Performance

> Will add a performance overhead

Could be an issue for a really big lists

```scala
def main(args: Array[String]): Unit = {
  ...
  val sorted = args.sorted

  doStuff(sorted)
}

// No effect needed on the return type - we can recover
def doStuff(input: List[Int]): Unit = {
  val sorted = input.sorted // O(nlog(n))
  // or
  val sorted =
    if (input.isSorted) input // O(n)
    else input.sorted         // O(nlog(n))

  ...
}
```

Would be nice to capture the fact that it's sorted and not have to recheck

---

# Capturing that it's sorted

```scala
case class SortedList[A](inner: List[A], ordering: Ordering[A])
```

Ship the ordering around with the list

---

# Watch out for trolls

```scala
case class SortedList[A](inner: List[A], ordering: Ordering[A])

val list = List(3, 2, 10, -5, 1)

val sorted = SortedList(list, Ordering[Int]) // Hey! You didn't sort it!
```

---

# Seize the means of production!

Can't use case classes as they have lots of built in public factory methods

```scala
class SortedList[A] private(val inner: List[A], val ordering: Ordering[A])

object SortedList {
  // For cases where you expect the list to already be sorted: O(n)
  def fromListSorted[A](list: List[A])(implicit ordering: Ordering[A]): Option[SortedList[A]] = {
    if (inner.isSorted(ordering))
      Some(new SortedList(list, ordering))
    else
      None
  }

  // From cases where the list may need sorting: O(nlogn)
  // Option not needed
  def fromListUnsorted[A](list: List[A])(implicit ordering: Ordering[A]): SortedList[A] =
    new SortedList(list.sorted(ordering), ordering)
}
```

---

# Leveraging stronger type

Now internal methods which need a sorted list take one of these as input:

```scala
def doStuff(list: SortedList[A]): Unit = ...
```

Now we know it's sorted, but sorted _how_

---

# Sorted how?

> Now we know it's sorted, but sorted _how_

A list can be sorted many ways

```scala
def doStuff(list: SortedList[A]): Unit = {
  val sorted =
    if (list.ordering == Ordering[Int]) list.inner
    else list.inner.sorted(Ordering[Int])

  ...
}
```

---

# Common pattern

Might end up using similar patterns over and over inside your libraries

```scala
def doStuff1(list: SortedList[A]): Unit = {
  val sorted =
    if (list.ordering == Ordering[Int]) list.inner
    else list.inner.sorted(Ordering[Int])

    ...
}

def doStuff2(list: SortedList[A]): Unit = {
  val sorted =
    if (list.ordering == Ordering[Int]) list.inner
    else list.inner.sorted(Ordering[Int])

    ...
}

def doStuff3(list: SortedList[A]): Unit = {
  val sorted =
    if (list.ordering == Ordering[Int]) list.inner
    else list.inner.sorted(Ordering[Int])

    ...
}
```

---

# Common pattern

Could add some nice helpers to `SortedList`

```scala
class SortedList[A] private(val inner: List[A], val ordering: Ordering[A]) {
  // Make sure it's sorted according to the ordering passed
  // For cases where you want to bail out if it's not sorted 
  def verify(ordering: Ordering[A]): Option[List[A]] =
    if (this.ordering == ordering) Some(inner)
    else None

  // Use the inner list if it's already sorted the right way
  // Otherwise fallback to just sorting it yourself
  // For cases where you want to auto-correct
  def verifyOrSort(ordering: Ordering[A]): List[A] =
    verify(ordering).getOrElse(inner.sorted(ordering))
}
```

---

# Scenario - sorted list

Might be overkill, depends on how big these lists are

(and how lazy you feel)

---

# Scenario - unique list

> A command line program receives arguments (plural) representing a list with no duplicates.
>
> e.g. "0 3 4 1 6 10"
>
> How would you validate it?

---

# Scenario - unique list

> A command line program receives arguments (plural) representing a list with no duplicates.
>
> e.g. "0 3 4 1 6 10"
>
> How would you validate it?

Thoughts:

- `Set[Int]` fits well, but we might need to preserve the original ordering


- similar to previous one, we can fix errors by removing duplicates


- but they might be indicative of some issue so we might still fail


- and it's not clear which duplicate you'd remove (if ordering matters)

---

# Scenario - unique list

If ordering needs to be preserved, could use the same approach as before:

```scala
class UniqueList[A] private(inner: List[A], equality: (A, A) => Boolean)

object UniqueList {
  // Usual factory methods
  ...
}
```

We've generalized the above to go beyond `==`

---

# Scenario - unique list

```scala
val list = UniqueList.fromListDuplicated(
  List(user1, user2, user3),
  (user1, user2) => user1.id == user2.id
)

Lib.doStuff(list)
```

How does `doStuff` validate that you compared by id?

```scala
def doStuff(list: UniqueList[A]): Unit = {
  val uniqued =
    if (list.equality == (user1, user2) => user1.id == user2.id) list.inner
    //                   ---------------------------------------
    //                     Different instance of the same logic
    else // unique-ify it yourself

  ...
}
```

Need to be careful of creating different objects that represent the same logic

Didn't have this issue with `Ordering` as we were already using pre-built objects

---

# Scenario - unique list

Make the comparer part of `Lib`:

```scala
val list = UniqueList.fromListDuplicated(
  List(user1, user2, user3),
  Lib.compareById
)

...

object Lib {
  val compareById: (User, User) => Boolean = (user1, user2) => user1.id == user2.id

  def doStuff(list: UniqueList[A]): Unit = {
    val uniqued =
      if (list.equality == compareById) list.inner
      else // unique-ify it yourself

    ...
  }
}
```

We're able to capture the information we've already proved,

and can save other methods having to do the same validation

(in this case avoiding performance)

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

# Validation

Not about changing your data to a more convenient type

(Although that's useful)

---

# Good Validation

It's about checking your data is valid,

then capturing what you check

That avoids revalidation

---

# Information deterioration

Scoped runtime information and contextual information deteriorates very quickly

Information encoded in a type deteriorates more slowly

(So a "strong type" makes sense)

---

# Practicalities

Not always practical to encode information in a type

But at least weigh up the pro's and con's (ie. think about it!)

---

# QnA
