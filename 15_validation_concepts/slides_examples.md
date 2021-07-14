---
author: Rohan
date: 2021-07-12
title: Validation Examples
---

```
__     __    _ _     _       _   _             
\ \   / /_ _| (_) __| | __ _| |_(_) ___  _ __  
 \ \ / / _` | | |/ _` |/ _` | __| |/ _ \| '_ \ 
  \ V / (_| | | | (_| | (_| | |_| | (_) | | | |
   \_/ \__,_|_|_|\__,_|\__,_|\__|_|\___/|_| |_|
                                               
 _____                           _           
| ____|_  ____ _ _ __ ___  _ __ | | ___  ___ 
|  _| \ \/ / _` | '_ ` _ \| '_ \| |/ _ \/ __|
| |___ >  < (_| | | | | | | |_) | |  __/\__ \
|_____/_/\_\__,_|_| |_| |_| .__/|_|\___||___/
                          |_|                
```

Follow up from last session

---

# Recap

> (1) A runtime check that your data meets your requirements
>
> (2) (If errors) Collecting as many errors as possible
>
> (3) (If valid) Encoding that knowledge such that it won't deteriorate

---

# Observations

> (1) A runtime check that your data meets your requirements
>
> (2) (If errors) Collecting as many errors as possible
>
> (3) (If valid) Encoding that knowledge such that it won't deteriorate

- Usually good at 1


- Often not great at 2 (for another time)


- Not great at 3

Usually picking types out of convenience rather than capturing what we validated

---

# Consequences

> (1) A runtime check that your data meets your requirements
>
> (2) (If errors) Collecting as many errors as possible
>
> (3) (If valid) Encoding that knowledge such that it won't deteriorate

Consequence of not doing 3 well is usually one of:

- having to re-validate your data over and over
    - unnecessary effects like `Option`
    - extra unit tests


- "validation by convention"
    - unsafe code
    - undefined behavior for bad inputs
    - uncaught bugs sneaking through

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

```
    _              
   / \   __ _  ___ 
  / _ \ / _` |/ _ \
 / ___ \ (_| |  __/
/_/   \_\__, |\___|
        |___/      
```

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

# Limiting the range

- age should be _at least_ non-negative


- in many contexts, users have to be at least 18 to use a service (excludes Enxhell)


- values above 150 would also probably raise red flags (excludes me)

---

# Upper bound issues

> values above 150 would also probably raise red flags (excludes me)

Where to draw the line?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Validation logic

- parse to a numeric type (like `Int` or `Short`)


- then limit the range

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

Not saying you have to use a wrapper type

just showing the pro's and con's and how you might do this

---

```
 __  __             _   _     
|  \/  | ___  _ __ | |_| |__  
| |\/| |/ _ \| '_ \| __| '_ \ 
| |  | | (_) | | | | |_| | | |
|_|  |_|\___/|_| |_|\__|_| |_|
                              
```

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

```
 ____            _   
|  _ \ ___  _ __| |_ 
| |_) / _ \| '__| __|
|  __/ (_) | |  | |_ 
|_|   \___/|_|   \__|
                     
```

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

```
 ___ ____  
|_ _|  _ \ 
 | || |_) |
 | ||  __/ 
|___|_|    
           
    _       _     _                   
   / \   __| | __| |_ __ ___  ___ ___ 
  / _ \ / _` |/ _` | '__/ _ \/ __/ __|
 / ___ \ (_| | (_| | | |  __/\__ \__ \
/_/   \_\__,_|\__,_|_|  \___||___/___/
                                      
```

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

- split by '.' or use a regex (be careful of the "/32" tail)


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

Example of a perfect validation type that isn't intrinsically practical

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

# Final representation - 4 ints

What some of you might come up with:

```scala
case class IP(token1: Int, token2: Int, token3: Int, token4: Int) {
  def asString: String = s"$token1.$token2.$token3.$token4"
}
```

Very practical, but unsafe

---

# Final representation - String

Or just be lazy and use a `String` and all code assumes it's been validated

---

```
 ____       
|___ \  _   
  __) || |_ 
 / __/_   _|
|_____||_|  
            
 _     _     _   
| |   (_)___| |_ 
| |   | / __| __|
| |___| \__ \ |_ 
|_____|_|___/\__|
                 
```

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

## Puns

Definitely some puns to be had with `TwoList.toList` and `list.toTwoList`

```scala
val tutus: List[Tutu] = ...

tutus.toTwoList
```

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

```scala
case Array(head, head2, tail@_*) => TwoList(head, head2, tail)
```

- `_*` means "0 or more of anything"


- we want to use that tail on the RHS so it needs a name


- `@` is used for "outer" labels


- `tail@` gives it the name `tail`

---

```
 ____             _           _ 
/ ___|  ___  _ __| |_ ___  __| |
\___ \ / _ \| '__| __/ _ \/ _` |
 ___) | (_) | |  | ||  __/ (_| |
|____/ \___/|_|   \__\___|\__,_|
                                
 _     _     _   
| |   (_)___| |_ 
| |   | / __| __|
| |___| \__ \ |_ 
|_____|_|___/\__|
                 
```

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

```
 _   _       _                  
| | | |_ __ (_) __ _ _   _  ___ 
| | | | '_ \| |/ _` | | | |/ _ \
| |_| | | | | | (_| | |_| |  __/
 \___/|_| |_|_|\__, |\__,_|\___|
                  |_|           
 _     _     _   
| |   (_)___| |_ 
| |   | / __| __|
| |___| \__ \ |_ 
|_____|_|___/\__|
                 
```

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

Not just about changing your data to a more convenient type

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

Can be awkward to work with refined types with complex logic

But at least weigh up the pro's and con's (ie. think about it!)

---

# Refined

The Refined library is useful for this kind of thing

We might do a separate training on it one day

---

# QnA
