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
