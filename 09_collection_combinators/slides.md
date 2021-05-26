---
author: Rohan
date: 2021-05-27
title: Collection Combinators
---

```
  ____      _ _           _   _             
 / ___|___ | | | ___  ___| |_(_) ___  _ __  
| |   / _ \| | |/ _ \/ __| __| |/ _ \| '_ \ 
| |__| (_) | | |  __/ (__| |_| | (_) | | | |
 \____\___/|_|_|\___|\___|\__|_|\___/|_| |_|
                                            
  ____                _     _             _                 
 / ___|___  _ __ ___ | |__ (_)_ __   __ _| |_ ___  _ __ ___ 
| |   / _ \| '_ ` _ \| '_ \| | '_ \ / _` | __/ _ \| '__/ __|
| |__| (_) | | | | | | |_) | | | | | (_| | || (_) | |  \__ \
 \____\___/|_| |_| |_|_.__/|_|_| |_|\__,_|\__\___/|_|  |___/
                                                            
```

A fancy way to say: Playing around with collections (just `Seq` today)

---

# Recap

Last time Willy gave an overview of the collections library.

This is because we use collections everywhere.

Today is about showing you common operations you do with them!

---

# Examples

- mapping


- filtering


- grouping


- sorting


- zipping


- counting/searching


- slicing


- set based operations

---

# immutable Seq

For today we'll just play around with immutable sequences.

Most of the operations also make sense on other collections too though.

---

# Many implementations

Remember that `Seq` is just an abstraction, with many concrete implementations.

We'll mix up the implementations we use to increase exposure.

---

# Multiple sessions

This is too big for one session.

We'll see how far we get after 1 hour and pick it up next time.

---

# Predef

There's a bunch of variables I'll use throughout defined in `predef.sc`.

You can load them into a repl and follow the examples:

```
$ amm

@ import $file.predef, predef._ 
```

(And I'm using ammonite for scala 13)

---

# Problem based

If you see "Q:" it's a question for you to solve and answer out loud or in the chat.

No cheating!

---

# MVP

Zij will nominate an MVP at the end for best problem solver.

That way if there's any controversy Zij gets blamed.

Zij can be bribed or influenced though with pro-Romanian sentiments,

or miscellaneous linguistic trivia (ie. "fun facts")

---

```
 __  __                   _             
|  \/  | __ _ _ __  _ __ (_)_ __   __ _ 
| |\/| |/ _` | '_ \| '_ \| | '_ \ / _` |
| |  | | (_| | |_) | |_) | | | | | (_| |
|_|  |_|\__,_| .__/| .__/|_|_| |_|\__, |
             |_|   |_|            |___/ 
```

- map


- flatMap


- flatten


- collect

---

# map

Q: We've got a sequence of names and we want a corresponding list of first letters.

```scala
// Input
val names1 = Vector("Boban", "Enxhell", "Zij", "Cynthia")

// Output
Vector('B', 'E', 'Z', 'C')
```

---

# map

```scala
names1.map(_.head)
```

Q: This is unsafe, why?

---

# flatten

Q: This is unsafe, why?

What if name is empty? Will blow up!

```scala
names.map(_.head)
```

Problem is badly specified.

Q: Map names to their first character, dropping the ones that are empty.

---

# flatten

```scala
val naughtyNames = Vector("Boban", "", "Zij", "", "Cynthia")

naughtyNames.map(_.headOption).flatten
```

This maps each name to an `Option[Char]`, then flattens it back down.

`flatten` will remove the `None`'s and transform the `Some`'s to the values it wraps.

---

# flatMap

We want to splice together the sequences:

```
0^2, 1^2, 2^2, ...    (squaring)
0*2, 1*2, 2*2, ...    (doubling)
```

to get:

```
0^2, 0*2, 1^2, 1*2, 2^2, 2*2, 3^2
```

just from 0 to 10 inclusive.

---

# map and flatten

We could start with a range `0 to 10` and try to map each number i to a List: (i^2, i * i).

But then we'd end up with a list of lists.

Applying `flatten` would get it back to a simple list though.


```scala
(0 to 10).map(i => List(i*i, i*2)).flatten
```

---

# flatMap

Or do it in one pass with `flatMap`.

```scala
(0 to 10).flatMap(i => List(i*i, i*2))
```

This avoids one pass over the data and an intermediate collection.

---

# flatMap?

For now think of `flatMap` as `map + flatten`.

That is very crude but practical enough to get us going.

---

# Back to names

Q: Re-solve our names problem using `flatMap`:

> Q: Map names to their first letter, dropping out empty ones

---

# flatMap

```scala
val naughtyNames = Vector("Boban", "", "Zij", "", "Cynthia")

// Old solution
naughtyNames.map(_.headOption).flatten

// If flatMap = map + flatten, maybe we can use it here?
naughtyNames.flatMap(_.headOption)
```

---

# collect

Q: Re-solve our names problem using `collect`

ie.

```scala
naughtyNames.map(_.headOption).collect {
  ???
}
```

---

# collect

```scala
naughtyNames.map(_.headOption).collect {
  case Some(firstDigit) => digit
}
```

---

# flatMap vs collect

Q: Pro's and con's?

---

# flatMap vs collect

```scala
// flatMap
naughtyNames.flatMap(_.headOption)

// collect
naughtyNames.map(_.headOption).collect {
  case Some(firstDigit) => digit
}
```

`collect` is a bit more code and creates an intermediate collection.

---

```
 _____ _ _ _            _             
|  ___(_) | |_ ___ _ __(_)_ __   __ _ 
| |_  | | | __/ _ \ '__| | '_ \ / _` |
|  _| | | | ||  __/ |  | | | | | (_| |
|_|   |_|_|\__\___|_|  |_|_| |_|\__, |
                                |___/ 
```

- filter


- partition

---

# filter

Find all the Bobanish names, ie. names that start with "Bob".

```scala
// Input
val names2 = ArraySeq("Boban", "Enxhell", "Bobanita", "Bobanta", "Willy")

// Output
ArraySeq("Boban", "Bobanita", "Bobanta")
```

---

# filter

```scala
names2.filter(_.startsWith("Bob"))
```

---

# Hang on

Why isn't Enxhell in the list of Bobans?

---

# filterNot

Now get me all the non-Bobans!

---

# filterNot

```scala
names2.filterNot(_.startsWith("Bob"))

// Same as
names2.filter(!_.startsWith("Bob"))
```

---

# partition

Q: In one pass, find all the Bobanish names and all the non-Bobanish names as separate collections.

---

# partition

Same predicate, but different combinator.

```scala
val (bobans, noBobans) = names2.partition(_.startsWith("Bob"))
```

Useful because it doesn't throw away the ones that fail the predicate.

---

# partition

Useful for splitting your data into:

- that which can be processed


- that which can't - then generate a nice error message

---

```
  ____                       _             
 / ___|_ __ ___  _   _ _ __ (_)_ __   __ _ 
| |  _| '__/ _ \| | | | '_ \| | '_ \ / _` |
| |_| | | | (_) | |_| | |_) | | | | | (_| |
 \____|_|  \___/ \__,_| .__/|_|_| |_|\__, |
                      |_|            |___/ 
```

- grouped


- groupBy

---

# grouped

Q: Batch the names up into groups of 3:

```scala
// Input
val names3 = ArraySeq("Boban", "Enxhell", "Bobanita", "Bobanta", "Willy", "Zij", "McBoban", "Rohan")
//                    |----------------------------|  |-----------------------|  |----------------|
//                            Group 1                        Group 2                 Group 3

// Output
List(
  ArraySeq("Boban", "Enxhell", "Bobanita"),
  ArraySeq("Bobanta", "Willy", "Zij"),
  ArraySeq("McBoban", "Rohan")
)
```

You end up with a collection of collections.

The last collection will be "left overs" if your input isn't a clean multiple of 3.

---

# grouped

```scala
names3.grouped(3).toList
```

`grouped` returns a mutable "iterator" (will look more at these later).

---

# groupBy

Q: Organize our names into a map/dictionary where they're keyed by their length.

```scala
// Input
val names4 = List("Boban", "Enxhell", "Zij", "Willy", "Rohan")

// Output
Map(
  3 -> List("Zij"),
  5 -> List("Boban", "Willy", "Rohan"),
  7 -> List("Enxhell")
)
```

Note how each key is the length, and each value is a _List_ of names with that length

(there might be multiple!)

Also note that maps are unordered - the above display is probably not what you'll get printed out.

---

# groupBy

```scala
names4.groupBy(_.length)
```

---

# groupBy

Q: Group our people by age.

```scala
case class Person(name: String, age: Int)

// Input
val people = Array(
  Person("Boban", 26),
  Person("Enxhell", 16),
  Person("James", 45),
  Person("Clement", 26)
)

// Output
Map(
  16 -> Array(Person("Enxhell", 16)),
  26 -> Array(Person("Boban", 26), Person("Clement", 26)),
  45 -> Array(Person("James", 45))
)
```

---

# groupBy

```scala
people.groupBy(_.age)
```

---

```
 ____             _   _             
/ ___|  ___  _ __| |_(_)_ __   __ _ 
\___ \ / _ \| '__| __| | '_ \ / _` |
 ___) | (_) | |  | |_| | | | | (_| |
|____/ \___/|_|   \__|_|_| |_|\__, |
                              |___/ 
```

- sorted


- sortBy

---

# sorted

Sort our list of names alphabetically.

```scala
// Input
val names5 = List("Zack", "Boban", "Rohan")

// Output
List("Boban", "Rohan", "Zack")
```

---

# sorted

```scala
names5.sorted
```

---

# sortBy

Q: Sort a list of numbers based on their magnitude (distance from 0).

```scala
// Input
val numbers = Vector(-10, -6, -4, -1, 0, 3, 10, 12)

// Output
Vector(0, -1, 3, -4, -6, -10, 10, 12)
//                       ^^^^^^^ ambiguous
```

For ties, the order doesn't matter.

Hint: use `def math.abs(in: Int): Int` to compute absolute values

---

# sortBy

```scala
numbers.sortBy(maths.abs)

// which is short for
numbers.sortBy(i => maths.abs(i))
```

---

# sortBy

"Duplicates"?

My guess is the original order will be used.

Might just be an implementation quirk though,

_don't_ rely on that unless it's explicitly documented.

---

# sortBy

Q: Sort our list of names ordering them based on their reversed form.

```scala
// Input
val names5 = List("Zack", "Boban", "Rohan")

// Output
List("Zack", "Boban", "Rohan") // coincidentally the same order!
//   "kcaZ"  "naboB", "nahoR"
```

---

# sortBy

```scala
names5.sortBy(_.reverse)
```

---

# sortBy

Q: Sort this list of people by their age.

```scala
case class Person(name: String, age: Int)

// Input
val people = Array(
  Person("Boban", 26),
  Person("Enxhell", 16),
  Person("James", 45),
  Person("Clement", 26)
)

// Expected
Array(
  Person("Enxhell", 16),
  Person("Boban", 26),   // Ambiguous
  Person("Clement", 26), //
  Person("James", 45)
)
```

Order doesn't matter when there's a tie.

---

# sortBy

```scala
people.sortBy(_.age)
```

Again there are some duplicates.

---

# Array

Note here we used an `Array` like a `Seq`.

Recall from Willy's talk how implicit magic makes an array "feel" like a `Seq`.

---

```
 ______             _             
|__  (_)_ __  _ __ (_)_ __   __ _ 
  / /| | '_ \| '_ \| | '_ \ / _` |
 / /_| | |_) | |_) | | | | | (_| |
/____|_| .__/| .__/|_|_| |_|\__, |
       |_|   |_|            |___/ 
```

- zip


- zipWithIndex

---

# zip

We have a list of names from one data source and a list of ages from another.

Q: Zip them together into a list of pairs.

```scala
// Input
val names6 = List("Boban", "Enxhell", "James", "Clement")
val ages = List(26, 16, 45, 26)

// Output
List(
  ("Boban", 26),
  ("Enxhell", 16),
  ("James", 45),
  ("Clement", 26)
)
```

---

# zip

```scala
names6.zip(ages)

names6 zip ages // Represents the symmetry
```

---

# zip

Q: What happens if we zip sequences of different length?

Ideas:

- explode


- loop


- zip as far as it can and ignore the rest?

---

# zip

> Q: What happens if we zip sequences of different length?

It drops the extra parts silently.

Can be a subtle source of bugs.

---

# zipWithIndex

Pair each element of a list with its position in the list? (0 based)

```scala
// Input
val names6 = List("Boban", "Enxhell", "James", "Clement")

// Output
Array(
  ("Boban", 0),
  ("Enxhell", 1),
  ("James", 2),
  ("Clement", 3)
)
```

---

# zipWithIndex

```scala
names.zipWithIndex
```

Hmmm... a bit boring.

Q: Make the indexes 1 based (ordinals).

```scala
// Input
val names6 = List("Boban", "Enxhell", "James", "Clement")

// Output
Array(
  ("Boban", 1),
  ("Enxhell", 2),
  ("James", 3),
  ("Clement", 4)
)
```
---

# zipWithIndex

```scala
names6.zipWithIndex.map {
  case (name, index) => (name, index + 1)
}
```

---

# Order

Note that zipping only makes sense with `Seq` where there's a deterministic ordering.

Zipping sets and maps doesn't make sense.

---

```
  ____                  _   _             
 / ___|___  _   _ _ __ | |_(_)_ __   __ _ 
| |   / _ \| | | | '_ \| __| | '_ \ / _` |
| |__| (_) | |_| | | | | |_| | | | | (_| |
 \____\___/ \__,_|_| |_|\__|_|_| |_|\__, |
                                    |___/ 
 ____                      _     _             
/ ___|  ___  __ _ _ __ ___| |__ (_)_ __   __ _ 
\___ \ / _ \/ _` | '__/ __| '_ \| | '_ \ / _` |
 ___) |  __/ (_| | | | (__| | | | | | | | (_| |
|____/ \___|\__,_|_|  \___|_| |_|_|_| |_|\__, |
                                         |___/ 
```

- contains


- exists


- count


- find


- indexWhere

---

# contains

Determine if the name "Boban" is in our list of names.

```scala
// Input
val names5 = List("Zack", "Boban", "Rohan")

// Output: true
```

---

# contains

```scala
names5.contains("Boban")
```

---

# contains

Q: Under the hood, how do you think `contains` works.


```scala
// Input
val names5 = List("Zack", "Boban", "Rohan")

names5.contains("Boban")
```

---

# contains

> Q: Under the hood, how do you think `contains` works.

It's comparing each element in the collection with "Boban" using `==`.

When it hits one that matches, it short circuits out and returns.

From the [source code](https://github.com/scala/scala/blob/d23424cd3aa17f7ad95b81018c70ceed2566962b/src/library/scala/collection/Seq.scala#L531):

```scala
def contains[A1 >: A](elem: A1): Boolean = exists (_ == elem)
//                                         ^^^^^^^^^^^^^^^^^^
```

It's implemented using a more powerful/general function "exists".

---

# contains

> It's comparing each element in the collection with "Boban" using `==`.

```scala
def contains[A1 >: A](elem: A1): Boolean = exists (_ == elem)
```

Q: What is a risk of an approach like this?

---

# Risky `==`

`==` is notoriously dangerous in the java world.

It's a bit broken and needs to be viewed with suspicion.

---

# Examples of `==`

Q: What `Boolean` values will be produced for these comparisons?

```scala
List(1, 2, 3) == List(1, 2, 3)

Array(1, 2, 3) == Array(1, 2, 3)
```

---

# Examples of `==`

> Q: What `Boolean` values will be produced for these comparisons?

```scala
List(1, 2, 3) == List(1, 2, 3) // true

Array(1, 2, 3) == Array(1, 2, 3) // false :scream:
```

Q: Why?

---

# Why does `==` work differently?

```scala
List(1, 2, 3) == List(1, 2, 3) // true

Array(1, 2, 3) == Array(1, 2, 3) // false :scream:
```

- List - comparing by value


- Array - comparing by address

---

# Array

```scala
val a1 = Array(1, 2, 3)

a1 == a1 // true

val a2 = Array(1, 2, 3)

a1 == a2 // false
```

Here `==` is asking whether it's the _same_ array,

ie. do `a1` and `a2` point to the same block of memory.

---

# Intuition

My guess was your intuition was "value" based equality,

even if you never consciously articulated that.

---

# contains broken

```scala
@ val arrays = List(
    Array(1, 2),
    Array(3, 4),
    Array(5, 6)
  ) 

@ arrays.contains(Array(1, 2)) 
// false
```

---

# `==` in scala

Generally it's sensible and "value" oriented.

- `List`, `Vector`, etc...


- case classes


- `Option`


- etc...


---

# exists

Q: Determine if there exists a name that starts with 'B' and ends in 'a'.

```scala
// Input
val names7 = Vector("Enxhell", "Zij", "Boban", "Bobantha", "Cynthia", "Bobanita")
//                                              ^^^^^^^^               ^^^^^^^^

// Output: true
```

---

# exists

```scala
names7.exists(n => n.startsWith("B") && n.endsWith("a"))
```

---

# contains vs exists

Any search with `contains` can be translated to using `exists`, e.g.

```scala
foo.contains(bar)

foo.exists(_ == bar)
```

Now the logic for how they're compared is more explicit.

But `exists` can do much more as it takes a generalized predicate.

---

# count

Q: Find _how many_ elements start with 'B' and end in 'a'.

```scala
// Input
val names7 = Vector("Enxhell", "Zij", "Boban", "Bobantha", "Cynthia", "Bobanita")
//                                              ^^^^^^^^               ^^^^^^^^

// Output: 2
```

---

# count

```scala
names7.count(n => n.startsWith("B") && n.endsWith("a"))
```

Same predicate as before, just using `count` instead of `exists`.

---

# find

Q: Find the first element that starts with 'B' and ends in 'a' (if it exists).

```scala
// Input
val names7 = Vector("Enxhell", "Zij", "Boban", "Bobantha", "Cynthia", "Bobanita")
//                                              ^^^^^^^^               ^^^^^^^^
//                                               first

// Output: Some("Bobantha")
```

---

# find

```scala
names.find(n => n.startsWith("B") && n.endsWith("a"))
```

Again same predicate.

---

# Searching from the back?

Q: What if we wanted to search right to left?

---

# Searching from the back?

> Q: What if we wanted to search right to left?

`findLast`

---

# indexWhere

Q: Find the _index_ (not element) of the first element matching our predicate.

```scala
// Input
val names7 = Vector("Enxhell", "Zij", "Boban", "Bobantha", "Cynthia", "Bobanita")
//                                              ^^^^^^^^               ^^^^^^^^
//                                              first

// Output: 3
```

---

# indexWhere

```scala
names7.indexWhere(n => n.startsWith("B") && n.endsWith("a"))
```

---

# indexWhere

Q: Is this safe? Any thoughts?

---

# indexWhere

> Q: Is this safe? Any thoughts?

It's going to return -1 when it can't find stuff.

That's the same type as the success case (`Int`).

Easy for people to forget to check that.

Compiler can't enforce a runtime check.

Just like `null`.

Would be better for `indexWhere` to return an `Option[Int]`.

---

```
 ____  _ _      _             
/ ___|| (_) ___(_)_ __   __ _ 
\___ \| | |/ __| | '_ \ / _` |
 ___) | | | (__| | | | | (_| |
|____/|_|_|\___|_|_| |_|\__, |
                        |___/ 
```

- drop


- take


- slice

Working with contiguous blocks of elements.

---

# take

Gimme the first 3 names in the list (if they exist).

```scala
// Input
val names3 = ArraySeq("Boban", "Enxhell", "Bobanita", "Bobanta", "Willy", "Zij", "McBoban", "Rohan")
//                    |----------------------------|

// Output
Vector("Boban", "Enxhell", "Bobanita)
```

---

# take

```scala
names3.take(3)
```

---

# Short list?

Q: What if we take "too much"?

```scala
(0 until 3).take(1000000)
```

Ideas:

- blows up


- loops


- returns everything (3 elements) and ignores the overflow

---

# Short list?

> What if we take "too much"?

Returns the whole thing and ignores the overflow.

---

# Short list

> Returns the whole thing and ignores the overflow.

This can be another subtle source of bugs.

```scala
val listOf30Elements = someList.take(30)

println(listOf30Elements(29)) // What if `someList` was short?
```

---

# drop

Q: Gimme me the names but with first 3 _removed_.

```scala
// Input
val names3 = ArraySeq("Boban", "Enxhell", "Bobanita", "Bobanta", "Willy", "Zij", "McBoban", "Rohan")
//                    |----------------------------|  |-------------------------------------------|
//                                drop                                     keep

// Output
Vector("Bobanta", ...) // onwards
```

---

# drop

```scala
names(3).drop(3)
```

---

# drop

Q: What if the list is too short and we "over drop"?

```scala
(0 until 3).drop(1000000)
```

---

# drop

> What if the list is too short? We "over drop".

You'll get an empty list.

Another possible source of bugs.

---

# drop-take 

Q: Gimme names 3, 4 and 5.

```scala
// Input
val names3 = ArraySeq("Boban", "Enxhell", "Bobanita", "Bobanta", "Willy", "Zij", "McBoban", "Rohan")
//                                                    |-----------------------|

// Output
Vector("Bobanta", "Willy", "Zij")
```

---

# drop-take

```scala
names.drop(3).take(3)
```

---

# Order matters

These are not the same:

```scala
names.drop(3).take(3)
names.take(3).drop(3)
```

---

Creates an intermediate collection and might put some subtle bugs in your code.

---

# slice

Q: Try again with `slice`:

Gimme names 3, 4 and 5.

```scala
// Input
val names3 = ArraySeq("Boban", "Enxhell", "Bobanita", "Bobanta", "Willy", "Zij", "McBoban", "Rohan")
//                                                    |-----------------------|

// Output
Vector("Bobanta", "Willy", "Zij")
```

---

# slice

```scala
names.slice(3, 6)
//             |
//            index (not length)
```

---

# drop-take vs slice

Q: Pro's and con's of the two approaches?

---

# drop-take vs slice

> Q: Pro's and con's of the two approaches?

## Performance

Slice avoids an intermediate collection.

Doing `drop(3)` on a gigantic collection creates another giantic collection.

```scala
val millionNames = Array(.....)

val intermediate = millionNames.drop(3) // 999,9997 elements

val threeNames = intermediate.take(3) // 3 elements
```

## Bugs

Slice is also very permissive and can introduce bugs.

But at least it's one function step, and not two,

so there's one less chance for a subtle bug.

---

# slice and subString

`String` has a function `substring` which is like slicing.

```scala
  "Bobanita".substring(3, 6) 
// 01234567
//   "ani"
```

Unlike slice, it gets angry when you wander out of bounds:

```scala
"Bobanita".substring(3, 10) 
// java.lang.StringIndexOutOfBoundsException: String index out of range: 10
```

---

# Speaking of String's

Q: Remove all the 'B' characters, then gimme the first 3 three characters.

```scala
// Input
val name = "BoBanita"

// Output
"oan"
```

---

# filter and take

```scala
name.filterNot(_ == 'B').take(3)
```

Wh-wh-what?

These are "collectiony" operations.

But It's the plain ol' `java.lang.String` from the java standard library.

Q: How are we doing them on `String`?

---

# String magic

> Q: How are we doing them on `String`?

Probably an implicit conversion.

```scala
name.filterNot(_ == 'B')

// Something like
string2Seq(name).filterNot(_ == 'B')
```

Similar to `Array`.

---

# Implicit magic

By now, you're probably getting an intuition:

> Q: How does this thing magically work?
>
> A: Implicits magic

Very convenient and very confusing. Bitter sweet.

---

```
 ____       _     _                        _ 
/ ___|  ___| |_  | |__   __ _ ___  ___  __| |
\___ \ / _ \ __| | '_ \ / _` / __|/ _ \/ _` |
 ___) |  __/ |_  | |_) | (_| \__ \  __/ (_| |
|____/ \___|\__| |_.__/ \__,_|___/\___|\__,_|
                                             
                            _   _                 
  ___  _ __   ___ _ __ __ _| |_(_) ___  _ __  ___ 
 / _ \| '_ \ / _ \ '__/ _` | __| |/ _ \| '_ \/ __|
| (_) | |_) |  __/ | | (_| | |_| | (_) | | | \__ \
 \___/| .__/ \___|_|  \__,_|\__|_|\___/|_| |_|___/
      |_|                                         
```

- distinct


- intersect


- diff

(But being used on `Seq`'s)

---

# distinct

Q: Remove all the duplicate names from this lazy list.

```scala
// Input
val names8 = LazyList("Boban", "Boban", "Enxhell", "James", "Zij", "Zack", "James")
//                     ^^^^^----^^^^^               ^^^^^-------------------^^^^^

// Output
LazyList("Boban", "Enxhell", "James", "Zij", "Zack")
```

---

# distinct

```scala
names8.distinct
```

---

# distinctBy

Q: For each capital letter, we want at most one representative from our names that starts with that letter.

```scala
// Input
val names8 = LazyList("Boban", "Boban", "Enxhell", "James", "Zij", "Zack", "James")
//                     B        B        E          J        Z      Z       J

// Output
LazyList("Boban", "Enxhell", "James", "Zij")
```

Note: No "Zack" as "Zij" used up the "Z" slot.

Which representative gets used for a letter doesn't matter.

You're allowed to assume names are non-empty.

---

# distinctBy

```scala
names8.distinctBy(_.head)
```

---

# "...By" functions

We've now seen 3 "By" functions:

- sortBy


- groupBy


- distinctBy

Each has the concept of a "representative" or "proxy".

Each element in the sequence generates a proxy, and the logic is done based on the proxy,

but the original element is preserved.

That proxy is codified using a lambda: `A => ...`

---

# distinctBy

Q: Reduce the list so that no two elements have the same length.

```scala
// Input
val names8 = LazyList("Boban", "Boban", "Enxhell", "James", "Zij", "Zack", "James")

// Output
LazyList("Boban", "Enxhell", "Zij", "Zack")
//        5        7          3      4
```

Again we don't mind which representative is used for each length.

---

# distinctBy

```scala
names8.distinctBy(_.length)
```

---

# intersect

Find all the names shared by `names6` and `names7`:

```scala
// Input
val names6 = List("Boban", "Enxhell", "James", "Clement")
val names7 = Vector("Enxhell", "Zij", "Boban", "Cynthia", "Bobanita")

// Output
List("Boban", "Enxhell")
```

The order in the output doesn't matter.

---

# intersect

```scala
names6.intersect(names7)

names6 intersect names7
```

---

# intersect

Q: What will we get if we intersect these?

```scala
val arrays = List(
  Array(1, 2),
  Array(3, 4),
  Array(5, 6)
)

val moreArrays = List(
  Array(3, 4),
  Array(5, 6)
)

arrays.intersect(moreArrays)
```

---

# intersect

> Q: What will we get if we intersect these?

Empty list :sad-parrot:

Under the hood `intersect` is using `==` to compare them.

---

# diff

Find the elements in `names6` that aren't in `names7`:

```scala
// Input
val names6 = List("Boban", "Enxhell", "James", "Clement")
val names7 = Vector("Enxhell", "Zij", "Boban", "Cynthia", "Bobanita")

// Output
List("James", "Clement")
```

---

# diff

```scala
names6.diff(names7)
```

---

# union

Q: Find all the elements in name6 and name7 (without duplicates).

```scala
val names6 = List("Boban", "Enxhell", "James", "Clement")
val names7 = Vector("Enxhell", "Zij", "Boban", "Cynthia", "Bobanita")
```

Order doesn't matter.

---

# union?

```scala
(names6 ++ names7).distinct
```

Nah there's no `union` for `Seq`, but there is for `Set`.

---

```
 _____     _     _ _             
|  ___|__ | | __| (_)_ __   __ _ 
| |_ / _ \| |/ _` | | '_ \ / _` |
|  _| (_) | | (_| | | | | | (_| |
|_|  \___/|_|\__,_|_|_| |_|\__, |
                           |___/ 
```

- foldLeft


- foldRight


- reduceLeft


- reduceRight

Usually used to collapse of "fold" a collection down to a single thing.

---

# "fold"?

Picture a sheet being gradually folded into something small.

Similar to: `List[Int] -> Int`

---

# foldLeft

Q: Add all these numbers together:

```scala
// Input
val numbers = Seq(0, 1, 2, 3, 4)

// Output: 10
```

---

# sum!

```scala
numbers.sum
```

This should make you uncomfortable though...

---

# sum?

So it seems that `Seq` has a `sum` function built in.

What if we had a list of something un-summable?

---

# sum

Q: What happens if we try to sum up `people`?

```scala
case class Person(name: String, age: Int)

val people = Array(
  Person("Boban", 26),
  Person("Enxhell", 16),
  Person("James", 45),
  Person("Clement", 26)
)

people.sum // ??? What will it do?
           //     How can it add people when we haven't told it how
```

---

# What happens

It doesn't even compile!

It wasn't that it failed at runtime, it didn't even compile.

---

# Hmmm...

> It doesn't even compile!

```scala
numbers.sum // compiles

people.sum // doesn't compile
```

How can `List` have a `sum` method when it's `List[Int]` but not `List[Person]`?

---

# Other types?

Q: What about `Long` and `BigInt` and `String`?

Can you sum a list of them?

---

# Other types?

> What about `Long` and `BigInt` and `String`?

## Works

- Int


- Long


- BigInt

## Doesn't compile

- Person


- String

---

# So

Maybe `sum` defined like this?

```scala
trait Seq[A] {
  def sum[I only exist if A is an Int, Long or BigInt!]: A = ...
}
```

:troll-parrot:

---

# Magic

Q: When something magical is happening, what do we suspect the cause is?

---

# Magic

> When something magical is happening, what do we suspect the cause is?

Implicits something something...

---

# The error

```
Seq("abc", "def").sum 
could not find implicit value for parameter num: scala.math.Numeric[String]
val res38 = Seq("abc", "def").sum
                              ^
```

---

# Extra parameter

Turns out that `sum` has an extra parameter.

```scala
trait Seq[A] {
  ...

  def sum(implicit num: Numeric[A]): A = ...

  ...
}
```

Q: What role would `num` play inside the `sum` function?

---

# Extra parameter

```scala
trait Seq[A] {
  ...

  def sum(implicit num: Numeric[A]): A = ...

  ...
}
```

> Q: What role would `num` play inside the `sum` function?

It's defining mathematical concepts for this type,

e.g. how to "add" two A's together

`sum` repeatedly applies this to fold the sequence down to a single value.

---

# Success vs Failure

Before we had:

## Works

- Int


- Long


- BigInt

## Doesn't compile

- Person


- String

## Question

Why is it working for some and not for others?

---

# Implicit scope resolution

> Why is it working for some and not for others?

The compiler searches far and wide to find an instance of `Numeric[A]`

for different `A`'s.

For `Int`, `Long` and `BigInt`, it found one.

For `String` and `Person` it couldn't find one, hence the error.

---

# Addings strings?

Suppose we wanted to "add" strings via concatenation.

```scala
// Input
val strings = List(
  "abc",
  "def",
  "ghi"
)

// Output
strings.sum // "abcdefghi"
```

How would we make this compile?

---

# Adding strings

> Q: How would we make this compile?

We'd need to:

- define a `Numeric[String]` instance


- mark it as implicit


- put it somewhere the compiler can "see" it

---

# Problems defining `Numeric`

`Numeric` is an abstraction not just for addition,

but other numeric concepts like multiplication.

You'll need to also define those for strings (and that might not make sense!)

---

# We've actually already seen this

`flatten`

Flattening only makes if your type is complex.

You can flatten a `List[Option[Int]]`.

You can't flatten a `List[Int]`.

---

# flatten

```scala
@ List(1, 2, 3).flatten 
cmd38.sc:1: No implicit view available from Int => scala.collection.IterableOnce[B].

@ List(None, Some(3), Some(4)).flatten 
res38: List[Int] = List(3, 4)
```

---

# Summing that up

We can make methods selectively appear and disappear by clever use of implicits.

---

# Back to folding!

Q: Okay smarty pants, how would you _multiply_ these numbers together?

```scala
// Input
val input = List(1, 2, 3, 4)

// Output: 24 (ie. 4 factorial)
```

---

# foldLeft

```scala
input.product
```

There's one for multiplying too...

---

# Smarty pants

Q: How would you do it from scratch in an iterative way

with a var and a loop.

For example adding `1 to 4`.

---

# foldLeft example - sum

> Add the numbers 1 to 4

Imperatively:

```scala
var acc = 0

for (i <- 1 to 4) {
  acc = acc + i
}

acc
```

---

# foldLeft example - product

> Multiply the numbers 1 to 4

Imperatively:

```scala
var acc = 1

for (i <- 1 to 4) {
  acc = acc * i
}

acc
```

---

# Comparing the two

sum

```scala
var acc = 0

for (i <- 1 to 4) {
  acc = acc + i
}

acc
```

product

```scala
var acc = 1

for (i <- 1 to 4) {
  acc = acc * i
}

acc
```

Differences?

- different seed


- different "combine" logic

---

# Abstraction

Q: What should our brains think when we see the same scaffolding,

but different bits of logic inside.

---

# Abstraction

> Q: What should our brains think when we see the same scaffolding,
> 
> but different bits of logic inside.

Higher order function!

We pass in the seed as a value.

We pass in combine logic as a parameter.

```scala
def fold[A](seq: Seq[A], seed: A, combine: (A, A) => A): A
//                                          |  |     |
//                                        old  next  new
//                                        acc  A     acc
```

Q: What small scalary change would we make to this signature and why?

---

# Improvement

> Q: What small scalary change would we make to this signature and why?

Put the function parameter into its own group!

```diff
-def fold[A](seq: Seq[A], seed: A, combine: (A, A) => A): A
+def fold[A](seq: Seq[A], seed: A)(combine: (A, A) => A): A
```

Lets people use block syntax and improves the type inference.

```seq
fold(seed) {
  case (...) => ...
}
```

Can destructure `A`'s and put complex logic in.

---

# Implementation

```scala
def fold[A](seq: Seq[A], seed: A)(combine: (A, A) => A): A = {
  var acc = seed

  for (a <- seq) {
    acc = combine(acc, a)
  }

  acc
}
```

We've abstracted it out and hidden away the dirty imperative loop.

---

# Alternative implementation

```scala
def fold[A](seq: Seq[A], seed: A)(combine: (A, A) => A): A = {
  var acc = seed

  for (a <- seq) {
    acc = combine(acc, a)
  }

  acc
}
```

Q: What's a more functional approach we could have used to avoid

a dirty loop and `var`?

---

# Alternative implementation

> Q: What's a more functional approach we could have used to avoid
>
> a dirty loop and `var`?

A tail recursive inner function.

```scala
def fold[A](seq: Seq[A], seed: A)(combine: (A, A) => A): A = {

  @tailrec
  def foldTail(remaining: Seq[A], acc: A): A = {
    if (remaining.isEmpty) acc
    else foldTail(remaining.slice(1, remaining.size), combine(acc, remaining(0))
  }

  foldTail(seq, seed)
}
```

Q: What potential performance issue can you see here?

---

# Seq performance issue

```scala
def fold[A](seq: Seq[A], seed: A)(combine: (A, A) => A): A = {

  @tailrec
  def foldTail(remaining: Seq[A], acc: A): A = {
    if (remaining.isEmpty) acc
    else foldTail(remaining.slice(1, remaining.size), combine(acc, remaining(0))
    //            ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
  }

  foldTail(seq, seed)
}
```

On each recursion it's slicing out the "tail" of the collection for the next iteration.

How does `slice` work for a `Seq`?

Depends on the concrete collection being passed at runtime.

`List`: O(1) (assuming it uses `tail`)

`Array`: O(n) as it has to copy the entirety of the array into a new array

---

# Remark

Another example where coding against an abstraction doesn't work well in practice.

You need to know the concrete type to avoid performance issues.

We can see how `List` is a great structure for these kinds of algorithms,

but `Array` wouldn't be.

---

# Standard library

Turns out `Seq` already has a built in function `foldLeft` that works

the same way as our `fold`:

```scala
// Sum
(1 to 4).foldLeft(0) { (acc, next) => acc + next }

// Product
(1 to 4).foldLeft(1) { (acc, next) => acc * next }
```

Q: How can we pretty this up a bit?

---

# Double underscore!

> Q: How can we pretty this up a bit?

Our combine logic is a two parameter function and we're using them in correct order:

A few variations:

```scala
// Sum
(1 to 4).foldLeft(0) { (acc, next) => acc + next }
(1 to 4).foldLeft(0) { _ + _ }
(1 to 4).foldLeft(0)(_ + _)

// Product
(1 to 4).foldLeft(1) { (acc, next) => acc * next }
(1 to 4).foldLeft(1) { _ + _ }
(1 to 4).foldLeft(1)(_ + _)
```

---

# fold "left"?

Q: Why is it called that?

Is there a left wing agenda?

---

# fold "left"?

> Q: Why is it called that?

It moves from left to right across the collection.

---

# foldLeft and foldRight

There is also `foldRight` which goes right to left.

---

# foldRight

The semantics for `foldRight` are a little different:

```scala
(1 to 4).foldRight(0) { (next, acc) => next * acc }
//                        switched
```

This is because visually the accumulator is moving right to left

```
   1  2  3  4
            <- 0

   1  2  3
         <- 4

   1  2
      <- 7

   1
   <- 9

   10
```

---

# foldRight

Be careful with double underscore!

```scala
(1 to 4).foldRight(0) { (next, acc) => acc / next }

// Not the same!
(1 to 4).foldRight(0) { (next, acc) => _ / _ }
```

The first underscore now represents `next` and the second `acc`.

---

# foldLeft and foldRight

Does it matter which direction we iterate over?

Q: Come up with an example where it does,

ie. something of the form:

```scala
val seq = ...

val seed = ...

def combine(x, y) = ...

val a = seq.foldLeft(seed)  { (acc, next) => combine(acc, next) }
val b = seq.foldRight(seed) { (next, acc) => combine(acc, next) }

a != b
```

---

# Example

Concatenating strings.

```scala
@ List("a", "b", "c").foldLeft("")(_ + _)
// "abc"

@ List("a", "b", "c").foldRight("") { (next, acc) => acc + next }
// "cba"
```

String concatenation is not "commutative" the way integer addition and multiplication are:

```
"boban" + "jones" != "jones" + "boban"
```

---

# Performance

Some structures are better for a particular direction.

e.g. `List` is designed for left to right.

---

# Handling commutativity issues

Q: Back to our concatenation example.

Suppose we had a `Seq[String]` whose runtime type is designed for right to left iteration.

How could we use `foldRight` to still concatenate our strings "left to right":

```scala
// Input
val seq = RightToLeftSeq("a", "b", "c")

seq.foldRight(???) { ??? }

// Output
"abc"
```

---

# Handling commutativity issues

_Prepend_ each new element:

```scala
RightToLeftSeq("a", "b", "c").foldRight("") { case (next, acc) => next + acc }

// or just
RightToLeftSeq("a", "b", "c").foldRight("")(_ + _)
```

```
  "a"  "b"  "c"
                <- ""

  "a"  "b"
         <- "c"

  "a"
    <- "bc"

  "abc"
```

---

# Another helpful property

Think about the order of operations here:

```
// Left folding
((("" + "a") + "b") + "c")

// Right folding
("a" + ("b" + ("c" + "")))
```

String concatenation has some nice properties that meant we got the same answer.

Q: What are they?

---

# Helpful properties

## Associativity

```
a + (b + c) = (a + b) + c, for all strings a, b, c
```

## Identity

The empty string is "neutral"

```
"" + a = a + "" = a, for all strings a
```

---

# Maths creep!

Maths is creeping in...

:whip-crack: Get back maths!

But this kind of thinking is useful.

Q: Where would we typically use identity elements when we're folding?

---

# Identity elements

> Q: Where would we typically use identity elements when we're folding?

Seeds. Good neutral starting point for an operation.

Works for left and right folding.

---

# Associativity

Good for parallelism.

```
a + b + c + d + e + f
-----   -----   -----
```

Can add chunks independently and recombine.

---

# Commutativity

```
a + b + c + d + e + f
-----   -----   -----
```

Don't even need to keep track of the original order.

```
c + d + e + f + a + b
-----   -----   -----
```

Allows for more optimizations.

---

# Two type parameters

> I looked at the source code for `foldLeft`,
>
> and there's another type parameter `B`.

---

# Two type parameters

In all our examples we were doing: `Seq[A] --> A`.

e.g. summing ints into a int.

You can fold into a different type.

e.g. "Add the lengths of all these strings"

```scala
List(
  "Boban", // 5
  "Enxhell", // 7
  "Zij" // 3
)
```

would fold to 15 (an `Int`, not a `String`).

```scala
names.foldLeft(0) { (acc, next) => acc + next.length }
```

---

# fold complexity

We try not to use fold too much in the analytics codebase.

It can become quite complex compared to the other combinators.

---

# reduce

There is also `reduceLeft` and `reduceRight`...

Q: What's the difference between `fold*` and `reduce*` methods?

---

# reduce

> Q: What's the difference between `fold*` and `reduce*` methods?

They fold, but use the first element of the `Seq` as the seed.

e.g.

```scala
(1 to 4).reduceLeft(_ * _) // 4!

// equivalent to:

(2 to 4).foldLeft(1)(_ * _)
```

---

# reduce

> Q: Why is reduce dangerous?

---

# reduce

> Q: Why is reduce dangerous?

It implicitly assumes there is a first element.

Will blow up if the `Seq` is empty.

Q: What does `fold*` do when the `Seq` is empty?

---

# fold vs reduce

> Q: What does `fold*` do when the `Seq` is empty?

Just returns the seed.

It's a trivial fold over no elements.

Generally use `fold`-based methods.

I haven't seen a good justification for `reduce` in a long time.

---

# That's it!

There's lots of other neat things you can do,

but today we covered the main ones you'll use a lot.

---

# MVP award?

---

# Code Uncles

Any thoughts/reflections?

---

# QnA?
