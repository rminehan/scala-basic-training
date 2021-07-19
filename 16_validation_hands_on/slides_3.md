---
author: Rohan
date: 2021-07-15
title: Validation Hands On (part 3)
---

```
__     __    _ _     _       _   _             
\ \   / /_ _| (_) __| | __ _| |_(_) ___  _ __  
 \ \ / / _` | | |/ _` |/ _` | __| |/ _ \| '_ \ 
  \ V / (_| | | | (_| | (_| | |_| | (_) | | | |
   \_/ \__,_|_|_|\__,_|\__,_|\__|_|\___/|_| |_|
                                               
 _   _                 _     
| | | | __ _ _ __   __| |___ 
| |_| |/ _` | '_ \ / _` / __|
|  _  | (_| | | | | (_| \__ \
|_| |_|\__,_|_| |_|\__,_|___/
                             
  ___        
 / _ \ _ __  
| | | | '_ \ 
| |_| | | | |
 \___/|_| |_|
             
```

Part 3

---

# Recap - mapN

A generalization of `map`

```scala
(parse(arg1), parse(arg2), parse(arg3)).mapN {
  case (int1, int2, int3) => ...
}
```

Powered by Applicative

Some implicit magic needed to tell the compiler why:

- `Validated` is applicative


- `NonEmptyList/Chain` is foldable

---

# Recap - parMapN

Like `mapN` but for monads

Makes the monad act like an applicative, ie

- do independent "parallel" computations


- not sequential dependent computations that may short-circuit

---

# Recap - "monicative" validation

More complex validation can require a staged approach

```
   Array       (int1, int2)    (int1, int2)
 --------        --------        --------
| two    |      | inputs |      |overflow|
| inputs | ---> | are    | ---> |        | ---> ...
|        |      |integral|      | odd    |
 --------        --------        --------
```

The outer validation stages are dependent on each other (monad)

The inner validations are independent of each other (applicative)

---

# Recap - sorry Either

```
   Array       (int1, int2)    (int1, int2)
 --------        --------        --------
| two    |      | inputs |      |overflow|
| inputs | ---> | are    | ---> |        | ---> ...
|        |      |integral|      | odd    |
 --------        --------        --------
```

> The outer validation stages are dependent on each other (monad)

`Either` is a good fit because it's:

- monadic


- isomorphic to `Validated`

We shouldn't have trashed it so hard

---

# Recap - partial unification

Helps the compiler understand more complex types like `Map[String, Int]`

as a type function `Map[String, _]` being applied to a type value `Int`

Built into scala 2.13, a little more work needed for it in prior versions

---

# Today

Miscellaneous bits and pieces:

- append performance: connecting chain with validation


- foldable vs reducible


- so many models!


- Operation Analytics Muscle :bicep:

---

```
  ____ _           _       
 / ___| |__   __ _(_)_ __  
| |   | '_ \ / _` | | '_ \ 
| |___| | | | (_| | | | | | ?
 \____|_| |_|\__,_|_|_| |_|
                           
```

Tying it together

(Or should I say, chaining it together) 

---

# Typical validation scenario

Several pieces of data arrive

Each one has potentially multiple issues

If one error occurs, we add them all together and go into the invalid state

---

# Example

```scala
// Name validation (failed)
Invalid(NonEmptyList.one("Invalid character in name"))

// Password validation (failed)
Invalid(NonEmptyList.of("Password is too short", "Password must contain a digit"))

// Email validation (passed)
Valid(email)

// Address (passed)
Valid(address)

// Passport number (failed)
Invalid(NonEmptyList.one("Unexpected character in passport number"))
```

Flattens out to:

```scala
NonEmptyList.of(
  "Invalid character in name",
  "Password is too short",
  "Password must contain a digit",
  "Unexpected character in passport number"
)
```

---

# Chain and NonEmptyChain

We've been using lists as they're familiar

But `Chain` will perform better

---

# Analytics codebase?

We have been using `NonEmptyList/ValidatedNel`,

but could starting using `NonEmptyChain/ValidatedNec`

---

# Significance?

> But Rohan,

you say

> these are such tiny lists typically.
>
> If there's only 2-3 inputs in our scripts,
>
> then O(n^2) vs O(n) is meaningless
>
> Does the performance difference between matter?
>
> Is this premature optimization?

---

# Significance?

> If there's only 2-3 inputs in our scripts,
>
> then O(n^2) vs O(n) is meaningless

Doesn't really matter for us

But it's a good habit (might matter more in production code with high load)

And there's no downside (we already depend on cats for `NonEmptyList`)

---

# Premature optimization?

> Is this premature optimization?

It would be if it were making our lives harder

But here there's no cost

---

```
 _____     _     _       _     _      
|  ___|__ | | __| | __ _| |__ | | ___ 
| |_ / _ \| |/ _` |/ _` | '_ \| |/ _ \
|  _| (_) | | (_| | (_| | |_) | |  __/
|_|  \___/|_|\__,_|\__,_|_.__/|_|\___|
                                      
```

Are `NonEmptyChain` and `NonEmptyList` foldable?

```scala
val lists = List(
  NonEmptyList.of(1, 2, 3),
  NonEmptyList.of(4, 5, 6),
  NonEmptyList.of(7, 8)
)

class NonEmptyListFolable[A] extends Foldable[NonEmptyList[A]] { ... }

fold(lists, new NonEmptyListFolable[Int])

// NonEmptyList.of(1, ..., 8)
```

Any issues?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Questions to think about?

What will we bootstrap the `fold` with?

```scala
class NonEmptyListFolable[A] extends Foldable[NonEmptyList[A]] {

  def seed: NonEmptyList[A] = ... // <--- what will you use?

  def combine(left: NonEmptyList[A], right: NonEmptyList[A]): NonEmptyList[A] = ...
}
```

:hmmm-parrot:

---

# Folding an empty collection?

Q: If we were adding ints, what would we bootstrap the `fold` with?

```scala
object IntAddition extends Foldable[Int] {
  def seed: Int = 0

  def combine(left: Int, right: Int): Int = left + right
}

fold(List(1, 2, 3), IntAddition)
```

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Seed

> Q: If we were adding ints, what would we bootstrap the `fold` with?

0

---

# Folding nel's?

What would the seed be?

```scala
class NonEmptyListFoldable[A] extends Foldable[A] {
  def seed: NonEmptyList[A] = ???

  def combine(left: NonEmptyList[A], right: NonEmptyList[A]): NonEmptyList[A] = left ::: right
}
```

```
 ___ ___ ___ 
|__ \__ \__ \
  / / / / / /
 |_| |_| |_| 
 (_) (_) (_) 
             
```

---

# Folding nel's?

> What would the seed be?

There isn't one.

You can't have an empty list of type `NonEmptyList` (that's the point!)

---

# Conclusion

`NonEmptyList` isn't foldable

e.g. we couldn't fold an empty collection of `NonEmptyList`s

---

# Not foldable!

> But Rohan,

you say angrily

> this whole thing has been leading up to folding lists of lists together!
>
> Now you tell us that you can't even fold these things!

---

# Purpose of seed

Why do we need a seed?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Purpose of seed

> Why do we need a seed?

To bootstrap it (particularly if we're folding an empty collection)

---

# Back to our example

Is it possible to be in the error state, but have 0 groups of errors?

(Below we have 3 groups of errors)

```scala
// Name validation (failed)
Invalid(NonEmptyList.one("Invalid character in name"))

// Password validation (failed)
Invalid(NonEmptyList.of("Password is too short", "Password must contain a digit"))

// Email validation (passed)
Valid(email)

// Address (passed)
Valid(address)

// Passport number (failed)
Invalid(NonEmptyList.one("Unexpected character in passport number"))
```

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# nel's of nel's

> Is it possible to be in the error state, but have 0 groups of errors?

No

If none of the groups failed, then overall it's a success (by construction)

ie. if we're in the error state we have a `NonEmptyList[NonEmptyList[String]]`

ie. the outer list is non-empty, and all the inner lists are non-empty

---

# Alternative to folding

Our fold takes a regular list:

```scala
def fold[A](list: List[A], foldable: Foldable[A]): A = ...

trait Foldable[A] {
  def seed: A

  def combine(left: A, right: A): A
}
```

If we were folding a `NonEmptyList[A]`, would we need a seed?

```scala
def foldNel[A](nel: NonEmptyList[A], foldable: Foldable[A]): A = ...
```

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# First element as seed

> If we were folding a `NonEmptyList[A]`, would we need a seed?

Nope

We can just use the first element

It's guaranteed to exist because it's non-empty!

---

# Reducible

A weaker version of `Foldable`:

```scala
def foldNel[A](nel: NonEmptyList[A], reducible: Reducible[A]): A = {
  val acc = nel.head

  for (a <- nel.tail)
    acc = reducible.combine(acc, a)

  acc
}

trait Reducible[A] {
  // No seed needed

  def combine(left: A, right: A): A
}
```

---

# Reducing nel's of nel's?

```scala
class NelReducible[A] extends Reducible[NonEmptyList[A]] {

  def combine(left: NonEmptyList[A], right: NonEmptyList[A]): NonEmptyList[A] = left ::: right

}
```

---

# Summarizing that section

Nel's aren't foldable

But in the context of error handling, you always have a nel of nels

You can still combine them by using the first nel as the seed,

then folding through the rest using `:::`

---

# Apology

You say:

> Sorry I got angry at you Rohan

No worries

---

# Aside: Category Theory Jargon

"Monoid" = Foldable

"Semigroup" = Reducible (ie. monoid without seed)

---

# Interview question

> What's an example of a semigroup that isn't a monoid?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Interview question

> What's an example of a semigroup that isn't a monoid?

Non-empty collections under concatenation, e.g.

- `NonEmptyList`


- `NonEmptyChain`


- `NonEmptyVector`

---

```
 ____        
/ ___|  ___  
\___ \ / _ \ 
 ___) | (_) |
|____/ \___/ 
             
                             
 _ __ ___   __ _ _ __  _   _ 
| '_ ` _ \ / _` | '_ \| | | |
| | | | | | (_| | | | | |_| |
|_| |_| |_|\__,_|_| |_|\__, |
                       |___/ 
                     _      _     
 _ __ ___   ___   __| | ___| |___ 
| '_ ` _ \ / _ \ / _` |/ _ \ / __|
| | | | | | (_) | (_| |  __/ \__ \
|_| |_| |_|\___/ \__,_|\___|_|___/
                                  
       _   _   _  
      | | | | | | 
      | | | | | | 
      |_| |_| |_| 
      (_) (_) (_) 
                  
```

Explain by example

---

# The confusion of many models

Suppose internally this is our model:

```scala
case class Lead(
  id: Id,
  linkedinIdOpt: Option[LinkedinId], // Originally not optional
  name: String,
  age: Int,
  connections: List[Lead], // Added later
  created: Instant
)
```

---

# Many IO formats

```scala
case class Lead(
  id: Id,
  linkedinIdOpt: Option[LinkedinId],
  name: String,
  age: Int,
  connections: List[Lead]
)
```

Potentially your data arrives via many IO routes:

```
     ----      ----      ----
    |    |    |    |    |    |      sql database
    |    |    |    |    |    | <---------------
    |     ----      ----     |
    |                        |      POST /v1/user
    |                        | <---------------
    |                        |      POST /v2/user
    |                        | <---------------
    |                   Door |
    |                    --  |    Kafka message
    |                   |  | | <---------------
    |                   |  | |
     ------------------------
```

Each has its own validation model

---

# Comparing models - sql database

## Internal

```scala
case class Lead(
  id: Id,
  linkedinIdOpt: Option[LinkedinId],
  name: String,
  age: Int,
  connections: List[Lead],
  created: Instant
)
```

## Database

```diff
 case class Lead(
   id: Id,
   linkedinIdOpt: Option[LinkedinId],
   name: String,
   age: Int,
-  connections: List[Lead]  // Stored in another table - one-to-many
-  created: Instant,
+  created: ZonedDateTime
 )
```

---

# Comparing models - POST

## Internal

```scala
case class Lead(
  id: Id,
  linkedinIdOpt: Option[LinkedinId],
  name: String,
  age: Int,
  connections: List[Lead],
  created: Instant
)
```

## POST /v1/user

```diff
 case class Lead(
-  id: Id,
-  linkedinIdOpt: Option[LinkedinId],
+  linkedinId: LinkedinId,
   name: String,
   age: Int,
-  connections: List[Lead]
  created: Instant,
 )
```

Back when it was made, all users had to have a linkedin id

That was used as the internal id

No concept of connections

Was replaced by `/v2/user`

---

# The point of this example?

Don't conflate data validation models with your internal working model

They often look the same but it's important to keep them separate

(particularly if the internal model changes over time)

Can lead to many similar looking models

---

# So many models!

If data can enter and leave your application at many places,

you'll find lots of models

- graphql


- mongodb


- kafka


- elasticsearch

...

---

# Model conversion

Usually:

- external data gets converted to a validation model


- that validation model gets converted to the working internal model

They are often very similar

---

# Chimney

Resist the urge to abstract over coincidence

Chimney is a good library to convert between these similar models

---

# Fixed Names

Data validation models often have to follow the naming convention used in the data

This json:

```json
{
  "sources": [
    ...
  ],
  "status": ...
}
```

translates to:

```scala
case class Lead(
  sources: List[Signal],
  status: Status
)
```

Even though `sources` isn't a great name we have to use it

But in your internal working models, you can use better names (which we do)

---

# Example

Subtle reinterpretation of data

## Prod Mongodb

In `person_record`, a signal can be written over

Hence timestamp is called `updatedAt`

## Analytics

We combine `person_record` with `person_record_timeline` to reconstruct signals (thanks Willy!)

Signals are immutable

Hence timestamp is called `createdAt` in our working model

---

# Fixed Names

Also explains why you sometimes see this:

```scala
case class Thingy(
  `type`: ThingyType,
  ...
)
```

because the json uses the word "type" which is a reserved language word in scala:

```json
{
  "type": { ... },
  ...
}
```

---

```
 _____ _      _             
|  ___(_)_  _(_)_ __   __ _ 
| |_  | \ \/ / | '_ \ / _` |
|  _| | |>  <| | | | | (_| |
|_|   |_/_/\_\_|_| |_|\__, |
                      |___/ 
 ____        _        
|  _ \  __ _| |_ __ _ 
| | | |/ _` | __/ _` |
| |_| | (_| | || (_| |
|____/ \__,_|\__\__,_|
                      
```

Making your validator not so angry

(Late addition from code review)

---

# Helpful Bouncer

Maybe you go to a club that requires you to wear a hat

You forgot you hat, but the bouncer gives you one

---

# Name example

> Incoming String data represents a name

Suppose we require 1+ alphabetical characters (to keep things simple)

---

# Name example

> Suppose we require 1+ alphabetical characters

The string "boban " arrives (trailing whitespace)

Technically it doesn't meet our condition, but we know what it's supposed to mean

Perhaps the user copy-pasted it into the webform and it picked up a trailing space

---

# Name example

You don't always have to be so fascist

Can repair data (depending on context)

```
"boban " ----> Valid("boban")
```

---

# Other examples

Case sensitivity,

e.g. 

> Internally we require 1+ alphabetical characters all lower case

"Boban" could be repaired to "boban"

---

# Context dependent

Repairing data is dangerous

If the data is invalid, that could indicate there is a deeper issue

Perhaps we shouldn't trust it and should bail out

---

If time:

```
  ___                       _   _             
 / _ \ _ __   ___ _ __ __ _| |_(_) ___  _ __  
| | | | '_ \ / _ \ '__/ _` | __| |/ _ \| '_ \ 
| |_| | |_) |  __/ | | (_| | |_| | (_) | | | |
 \___/| .__/ \___|_|  \__,_|\__|_|\___/|_| |_|
      |_|                                     
    _                _       _   _          
   / \   _ __   __ _| |_   _| |_(_) ___ ___ 
  / _ \ | '_ \ / _` | | | | | __| |/ __/ __|
 / ___ \| | | | (_| | | |_| | |_| | (__\__ \
/_/   \_\_| |_|\__,_|_|\__, |\__|_|\___|___/
                       |___/                
 __  __                _      
|  \/  |_   _ ___  ___| | ___ 
| |\/| | | | / __|/ __| |/ _ \
| |  | | |_| \__ \ (__| |  __/
|_|  |_|\__,_|___/\___|_|\___|
                              
```

Time to hit the roids

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

Wrapping up the last 4 lessons

---

# Validation Concepts

Capture the information you validate

Prevents revalidation and the ickiness that comes with that

---

# Information deterioration

Types are the most practical mechanism to encode that information

such that it doesn't immediately deteriorate

---

# Validated

Use `cats.data.Validated` to represent the result of validation

```
                    --------> Valid(Strong)
                   /
Weak   ---------->
                   \
                    --------> Invalid(error)
```

---

# non-empties of non-empties

There are usually many different pieces of data being validated simultaneously

Each piece of data can produce non-empty errors

If at least group fails, the whole process fails

ie. failure will have non-empty of non-empties

They all need to be combined

---

# List vs Chain

## List/NonEmptyList

Simple structures

O(n) append performance

Implies O(n^2) performance when combining n `ValidatedNel`'s

## Chain/NonEmptyChain

Complex structures (a few weird edge cases)

O(1) append 

Implies O(n) performance when combining n `ValidatedNec`'s

---

# Many Models

Don't conflate validation models from internal models

Resist the urge to reuse models for different input mechanisms

---

# QnA

(No questions implies all future validation code will be perfect)
