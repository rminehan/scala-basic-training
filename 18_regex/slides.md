---
author: Clohan
date: 2021-08-31
title: Regex
---

```
 ____
|  _ \ ___  __ _  _____  __
| |_) / _ \/ _` |/ _ \ \/ /
|  _ <  __/ (_| |  __/>  <
|_| \_\___|\__, |\___/_/\_\
           |___/
```

---

# Regex

Why learn about regex?

---

# Regex

> Why learn about regex?

It's used everywhere

Gets abused and misused too

---

# Audience

Probably used regex here and there

Learning on the job

---

# Goals

- cement the basic syntax


- understand good and bad practices


- create general familiarity

---

# Goals

> cement the basic syntax

Regex syntax is quite complex

In the wild you can get by with the basics

---

# Complex regexes

> In the wild you can get by with the basics

In fact!

If you're using very complex regexes

maybe you're using the wrong tool

---

# Agenda

- basic syntax


- best practices


- performance notes


- regex with scala


- regex on the cli


- regex in vim


- regex in sql/mongo

---

# Split sessions?

See how far we get

---

```
 ____            _
| __ )  __ _ ___(_) ___
|  _ \ / _` / __| |/ __|
| |_) | (_| \__ \ | (__
|____/ \__,_|___/_|\___|

 ____              _
/ ___| _   _ _ __ | |_ __ ___  __
\___ \| | | | '_ \| __/ _` \ \/ /
 ___) | |_| | | | | || (_| |>  <
|____/ \__, |_| |_|\__\__,_/_/\_\
       |___/
```

---

# Start with examples

Will show you a regex

You tell me what strings it would match or what it means

---

# Interactive

Use a regex tester to follow along

---

# Example 1 - question mark

Regex: `boba?n`

What strings would it match?

```
 ___
|__ \
  / /
 |_|
 (_)

```

---

# Example 1 - question mark

Regex: `boba?n`

- "boban"

- "bobn"

---

# Example 2 - plus

Regex: `0+`

What strings would it match?

```
 ___
|__ \
  / /
 |_|
 (_)

```

---

# Example 2 - plus

Regex: `0+`

- "0"


- "00"


- "000"

etc...

Doesn't match the empty string as it needs "1 or more"

---

# Example 3 - digit

Note: `\d` means "a digit"

Regex: `x\d+`

Which of these would get matched completely?

```
0: "x"

1: "y1"

2: "x012"

3: "x0011p"

4: "x9"

 ___
|__ \
  / /
 |_|
 (_)

```

---

# Example 3 - digit

Note: `\d` means "a digit"

Regex: `x\d+`

Means: "An 'x' followed by one or more digits"

```
0: "x"

1: "y1"

2: "x012" <--

3: "x0011p" <-- wouldn't be completely matched

4: "x9" <--
```

---

# Example 4 - whitespace

Note: `\s` means "whitespace" (e.g. space, tab, etc...)

Note: `*` means "0 or more"

Regex: `\s*boban\s+jones\s*`

What does this regex mean?

```
 ___
|__ \
  / /
 |_|
 (_)

```

---

# Example 4 - whitespace

Note: `\s` means "whitespace" (e.g. space, tab, etc...)

Note: `*` means "0 or more"

Regex: `\s*boban\s+jones\s*`

> 0 or more whitespaces,
>
> then "boban",
>
> then 1 or more whitespaces,
>
> then "jones",
>
> then 0 or more whitespaces

---

# Example 4 - whitespace

Regex: `\s*boban\s+jones\s*`

> 0 or more whitespaces,
>
> then "boban",
>
> then 1 or more whitespaces,
>
> then "jones",
>
> then 0 or more whitespaces

ie.

> Searching for the name "boban jones"
>
> and we're not fussy about boundary whitespace
>
> or interior whitespace

---

# Example 4 - whitespace

Regex: `\s*boban\s+jones\s*`

Will match all of these

```
"boban jones"

"boban      jones"

" boban \tjones"
```

(where \t represents an actual tab character)

---

# Example 5 - character class

Note: `[xyz]` means "x or y or z"

Regex: `x[ABCDEF\d]+`

What does it mean?

```
 ___
|__ \
  / /
 |_|
 (_)

```

---

# Example 5 - character class

Regex: `x[ABCDEF\d]+`

> 'x' followed by 1 or more A-F or digit characters

ie. a hexidecimal number

Examples:

```
"x0AB103"

"xF03192"
```

---

# Observations

Regex: flexible rules to match text

Can also extract text

---

# More formally

We combine "atoms" with "quantifiers"

## Atoms

Examples:

- `\d` - digit


- `\s` - whitespace


- `\w` - "word" (number or letter or underscore)


- `[xyz]` - character class, x or y or z where x,y,z are atoms

## Quantifiers

- `?` - 0 or 1


- `+` - 1 or more


- `*` - 0 or more


- `{n}` - exactly n

---

```
  ___
 / _ \ _ __   ___
| | | | '_ \ / _ \
| |_| | | | |  __/
 \___/|_| |_|\___|

 ____
|  _ \ ___  __ _  _____  __
| |_) / _ \/ _` |/ _ \ \/ /
|  _ <  __/ (_| |  __/>  <
|_| \_\___|\__, |\___/_/\_\
           |___/
 ___
|__ \
  / /
 |_|
 (_)

```

---

# Many regex's

We talk about "regex" as if it's a universal language

---

# Reality

The sad reality is that it's more like sql:

- many implementations


- overlap on common stuff


- behave differently at edge cases and advanced features

---

# Dialects of regex

- perl


- java


- python


- vim

etc...

---

# Common usage

Generally we keep in the safe zone

Most regex is transferable

---

```
 ____            _
| __ )  ___  ___| |_
|  _ \ / _ \/ __| __|
| |_) |  __/\__ \ |_
|____/ \___||___/\__|

 ____                 _   _
|  _ \ _ __ __ _  ___| |_(_) ___ ___  ___
| |_) | '__/ _` |/ __| __| |/ __/ _ \/ __|
|  __/| | | (_| | (__| |_| | (_|  __/\__ \
|_|   |_|  \__,_|\___|\__|_|\___\___||___/

```

---

# The problem with regex

- hard to read


- used where they shouldn't be


- brittle

---

# Case study: parsing html at Quantium

Give Grandaddy Rohan the mic

```
#rant
#indoctrintation
#back-in-my-day
```

---

# Hard to read

Example: regex to validate email

```
[A-Z0-9._%+-]+@(?:[A-Z0-9-]+\.)+[A-Z]{2,6}
```

---

# Suffers from the python effect

## During development

Building interactively

Context loaded into your brain

You understand it and the weird edge cases

## 6 months later

All context lost

Looking at a random soup of characters

---

# Used where it shouldn't be

- html


- json


- csv


- yaml

See the [best SO post ever written](https://stackoverflow.com/a/1732454/15607965)

---

# Brittle

Very easy to forget about certain characters

e.g. regex for a western first name:

```
[a-zA-Z]+
```

Who are we forgetting?

```
 ___
|__ \
  / /
 |_|
 (_)

```

---

# Günther

```
[a-zA-Z]+
```

> Who are we forgetting?

Poor old Günther

---

# Günther

```
[a-zA-Zü]+
       ^
```

:fix-parrot:

Who are we forgetting?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Ünter

```
[a-zA-Zü]+
       ^
```

Günther's Bruder Ünter

(capital Ü)

---

# Ünter

```
[a-zA-ZüÜ]+
        ^
```

:fix-parrot:

Who are we forgetting?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Italians?

```
[a-zA-ZüÜ]+
```

They're Italian cousin Mário

> ééééh! You forget about-á me-á!?

(Clement: please say with Italian accent and hand gestures)

---

# Example from analytics-scripts

Code wants to keep just lines whale lines:

```scala
val whalesEmailList = whaleCustomersJSON
  .filter(_.matches("""\{  "_id" : "Whales".*"""))
  //                     ^^     ^ ^
  ...
```

Whitespace logic is needlessly brittle

Couples us to specific implementation details about how it's produced

Could be silently dropping a lot of data

```
#regex-shaming
```

---

# Alternatives

Use `\s` with an appropriate quantifier

```scala
.filter(_.matches("""\{\s*"_id"\s*:\s*"Whales".*"""))
```

A bit harder to read though :sad-regex-parrot:

---

# Alternatives

> A bit harder to read though :sad-regex-parrot:

Parse a cleaned version of the string

```scala
.filter(_.replaceAll("\\s+", "").matches("""\{"_id":"Whales".*""")))
//        ^^^^^^^^^^^^^^^^^^^^^^            ^^^^^^^^^^^^^^^^^^^^^
//        Temp copy with spaces removed     Regex can assume no whitespace
```

Easier to read but a bit slower

---

# Observations of this in the wild

The "regex" development cycle:

Hacky regex written to validate something

Loop:

- Breaks on some input

- Gets patched to fill the gap

- Repeat

(Often reinventing the wheel when a better parser exists)

---

```
 ____            __
|  _ \ ___ _ __ / _| ___  _ __ _ __ ___   __ _ _ __   ___ ___
| |_) / _ \ '__| |_ / _ \| '__| '_ ` _ \ / _` | '_ \ / __/ _ \
|  __/  __/ |  |  _| (_) | |  | | | | | | (_| | | | | (_|  __/
|_|   \___|_|  |_|  \___/|_|  |_| |_| |_|\__,_|_| |_|\___\___|

 _   _       _
| \ | | ___ | |_ ___  ___
|  \| |/ _ \| __/ _ \/ __|
| |\  | (_) | ||  __/\__ \
|_| \_|\___/ \__\___||___/

```

---

# Example

What text will this match?

Regex: `boban.+jones`

Text:

```
boban jones of family jones, son of boban jones senior
```

```
 ___
|__ \
  / /
 |_|
 (_)

```

---

# Greedy

Regex: `boban.+jones`

Text:

```
boban jones of family jones, son of boban jones senior
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
```

Maybe not what you were expecting

```
boban jones of family jones, son of boban jones senior
^^^^^^^^^^^                         ^^^^^^^^^^^
```

---

# Understanding this

Regex: `boban.+jones`

> 'boban' then
>
> 1 or more of any character (greedily - the biggest you can find) then
>
> 'jones'

Text and match:

```
boban jones of family jones, son of boban jones senior
---->                                     ---->
boban                                     jones
     ------------------------------------>
                       .+
```

---

# Backtracking

Regex: `boban.+jones`

> 1 or more of any character (greedily) then

Text and match:

```
boban jones of family jones, son of boban jones senior
---->
boban
     ------------------------------------------------> oops, can't match 'j' from 'jones'
                     .+                   <----------- backtrack until I can
                                          ----> try again
```

---

# Performance

Imagine you have a long string and you have a unique match near the front

Regex: `boban.+jones`

```
boban jones 00000 11111 22222 33333 ....
---->
boban
     -----------------------------> ... oops, can't match 'j' from 'jones'
      <---------------------------- ... backtrack until I can
      ---->
```

Two round trips of the search text

---

# Lazy?

What we want is a non-greedy or "lazy" approach.

> 'boban' then
>
> 1 or more of any character (lazily) looking ahead then
>
> 'jones'

```
boban peter jones son of babandore jones
---->
boban
     ------>
     any                    <- cautiously looking ahead
            ---->
            jones
```

---

# Lazy syntax

Different in different dialects

## Java/perl

```
boban.+?jones
       ^
```

## Vim

```
\vboban.{-1,}jones
```

See [cheat sheet](https://github.com/rminehan/vim-kata/blob/main/advanced_vim_regex_cheat_sheet.md)

---

# Compiling regexes

Developers express regexes as strings, e.g. "boban.+jones"

Internally regex engines compile them into a more optimized format

(Similar mindset to interpreted vs compiled languages)

---

# Scala

Can explicitly compile regexes with `.r`

```scala
val regex: Regex = "boban.+jones".r
```

---

# Compilation

Compilation takes time

```scala
// Recompiles the regex on every iteration
strings.filter(_.matches("boban.+jones"))
```

---

# Caching it

If you are using the same regex many times consider compiling it once and caching that

```scala
// Recompiles the regex on every iteration
strings.filter(_.matches("boban.+jones"))

// Compile it once
val compiled = "boban.+jones".r
strings.collect { case s@compiled(_*) => s }
```

(Example is a little awkward,
see also [SO post](https://stackoverflow.com/questions/3021813/how-to-check-whether-a-string-fully-matches-a-regex-in-scala))

---

# Back to our example

```scala
val whalesEmailList = whaleCustomersJSON
  .filter(_.matches("""\{  "_id" : "Whales".*"""))
  ...
```

Being run over every line in this input file (potentially massive file)

```
#more-regex-shaming
```

---

# Some benchmarks

To the code!

---

# Results

Using scalameter on robox:

```
::Benchmark (greedy vs lazy).greedy::
-  20000: 0.004096 ms
-  40000: 0.004089 ms
-  60000: 0.004136 ms
-  80000: 0.00418 ms
- 100000: 0.004211 ms

::Benchmark (greedy vs lazy).lazy::
-  20000: 0.003785 ms
-  40000: 0.001396 ms
-  60000: 0.001339 ms
-  80000: 0.001359 ms
- 100000: 0.001197 ms

::Benchmark (greedy vs lazy).lazy2::
-  20000: 0.002078 ms
-  40000: 0.002177 ms
-  60000: 0.002095 ms
-  80000: 0.001088 ms
- 100000: 0.001103 ms

::Benchmark (compiled vs uncompiled).compiling::
- 10: 0.00065 ms
- 20: 0.000933 ms
- 30: 0.001297 ms
- 40: 0.001768 ms
- 50: 0.00216 ms

::Benchmark (compiled vs uncompiled).searching compiled::
- 10: 0.000444 ms
- 20: 0.000443 ms
- 30: 0.000436 ms
- 40: 0.000427 ms
- 50: 0.000432 ms

::Benchmark (compiled vs uncompiled).searching uncompiled::
- 10: 0.000905 ms
- 20: 0.001199 ms
- 30: 0.001539 ms
- 40: 0.001915 ms
- 50: 0.002405 ms
```

---

```
 ____
|  _ \ ___  __ _  _____  __
| |_) / _ \/ _` |/ _ \ \/ /
|  _ <  __/ (_| |  __/>  <
|_| \_\___|\__, |\___/_/\_\
           |___/
 _
(_)_ __
| | '_ \
| | | | |
|_|_| |_|

 ____            _
/ ___|  ___ __ _| | __ _
\___ \ / __/ _` | |/ _` |
 ___) | (_| (_| | | (_| |
|____/ \___\__,_|_|\__,_|

```

scala.util.matching.Regex

---

# scala.util.matching.Regex

Uses the java regex engine under the hood

Same dialect of regex

---

# Integration with pattern matching

Capture groups in your regex can be destructured

```scala
@ val idRegex = "([0-9a-z]+)-([0-9a-z]+)".r

@ "123-456" match {
    case idRegex(first, second) => s"Got '$first' and '$second'"
    case _ => "Couldn't match"
  }
// "Got '123' and '456'"
```

---

# Annoying backslashes

Many regex atoms use backslash, e.g.

- `\d`


- `\w`

In most languages (scala included) that's already the escape character for string literals

```scala
@ val regex = "\w+"
// (console):1:15 expected ([btnfr'\\\\\"]] | OctalEscape | UnicodeEscape)
// val regex = "\w+"
//              ^
```

---

# Annoying backslashes

> In most languages (scala included) that's already the escape character for string literals

So we escape the backslashes themselves:

```scala
@ val regex = "\\w+"
```

At runtime, this is a string with 3 characters "\w+",

even though it took 4 characters to represent it

---

# Alternatives

Can use `raw` strings or triple quoted strings

Works well for backslash-heavy regexes

```scala
val bigRegex1 = "(\\w+)\\s+(\\d+)"

val bigRegex2 = raw"(\w+)\s+(\d+)"

val bigRegex3 = """(\w+)\s+(\d+)"""
```

---

# String interpolation

If a big regex has a repeated sub-pattern,

you can make it a bit more readable with string interpolation

---

# Example

```scala
// Example text: "928F-1A*3--C338-37A*"

val bigRegex1 = raw"([A-F\d*]{4})-([A-F\d*]{4})--([A-F\d*]{4})-([A-F\d*]{4})".r

val bigRegex2 = {
  val token = raw"([A-F\d*]+)"
  s"$token-$token--$token-$token".r
}
```

High level structure is a bit clearer now

(particularly the `--`)

---

# Commenting inside regexes

You can put documentation _inside_ a regex to make it more readable

It gets compiled away later

---

# Example

```scala
@ val complexRegex = """(?x)
                       |\w+   # First name
                       |\s+   # Space between
                       |\w+   # Last name
                       |--    # Delimeter between name and id
                       |\d{6} # First 6 digits of id
                       |-     # Delimeter within id
                       |\d{4} # Last 6 digits of id""".stripMargin.r

@ complexRegex.findFirstIn("Boban Jones--001934-1173")
// Some(value = "Boban Jones--001934-1173")
```

Ooooooooh

---

# Example - (?x)

```scala
@ val complexRegex = """(?x)  <---------------
                       |\w+   # First name
                       |\s+   # Space between
                       |\w+   # Last name
                       |--    # Delimeter between name and id
                       |\d{6} # First 6 digits of id
                       |-     # Delimeter within id
                       |\d{4} # Last 6 digits of id""".stripMargin.r
```

Note the `(?x)` at the start of the regex which tells the engine comments are enabled

[See oracle docs](https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html#COMMENTS)

---

# Whitespace ignored

```scala
@ val complexRegex = """(?x)
                       |\w+   # First name
                           ^^^ IGNORED
                       ...
```

From the docs:

> In this mode, whitespace is ignored,

Might be confusing if you wanted to use the literal space character in your pattern

---

```
 ____
|  _ \ ___  __ _  _____  __
| |_) / _ \/ _` |/ _ \ \/ /
|  _ <  __/ (_| |  __/>  <
|_| \_\___|\__, |\___/_/\_\
           |___/

  ___  _ __
 / _ \| '_ \
| (_) | | | |
 \___/|_| |_|

 _   _
| |_| |__   ___
| __| '_ \ / _ \
| |_| | | |  __/
 \__|_| |_|\___|

      _ _
  ___| (_)
 / __| | |
| (__| | |
 \___|_|_|

```

ie. grep

---

# grep

A tool which which drops all lines from standard input that don't match the pattern

```
                 grep -i boban
Standard Input                Standard Output
---------------------------------------------
boban                         boban
jones    (x)                  Boban
bobn     (x)                  the boban
Boban
jimmy    (x)
the boban
```

The match can be anywhere on the line

---

# Demo

To the repl!

---

# grep dialect

`grep` is old school

Depending on your grep installation,

`+` and `?` will be treated as literal characters, not quantifiers

---

# Other special characters

These are all treated as literal characters:

- `+`


- `?`


- `|`


- `(` and `)`


- `[` and `]`


- `{` and `}`

---

# "Standard" regex

Chars like `+` and `?` were not part of the original regex standard

Your grep might default to "standard" mode

---

# "Extended" regex

What we're used to

Chars like `+` and `?` have their special meanings

Most modern regex engines use "extended" regex

---

# Fixing grep

The `-E` flag makes it use "extended" regex

e.g.

```bash
grep -E "[a-z]+"
```

---

# egrep

Or you can use egrep ("extended grep")

---

# ripgrep

grep is a bit old and slow

ripgrep is much faster and has more modern features

In particular, it integrates nicely with git

---

```
 ____
|  _ \ ___  __ _  _____  __
| |_) / _ \/ _` |/ _ \ \/ /
|  _ <  __/ (_| |  __/>  <
|_| \_\___|\__, |\___/_/\_\
           |___/
 _
(_)_ __
| | '_ \
| | | | |
|_|_| |_|

       _
__   _(_)_ __ ___
\ \ / / | '_ ` _ \
 \ V /| | | | | | |
  \_/ |_|_| |_| |_|

```

---

# Buffer search

Regex is all over vim like white on rice

You can search your buffer with a regex using `/`

To the vim!

---

# Vim

Vim's regex dialect is a bit different

It also has a bunch of special regex features designed for text editors

See [our kata](https://github.com/rminehan/vim-kata) for more details

---

```
 ____
|  _ \ ___  __ _  _____  __
| |_) / _ \/ _` |/ _ \ \/ /
|  _ <  __/ (_| |  __/>  <
|_| \_\___|\__, |\___/_/\_\
           |___/
 _
(_)_ __
| | '_ \
| | | | |
|_|_| |_|

           _
 ___  __ _| |
/ __|/ _` | |
\__ \ (_| | |
|___/\__, |_|
        |_|
```

(Really postgres)

---

# Example

You want to do:

> Find all people with name "boban jones" where there could be boundary whitespace
>
> or multiple whitespace characters between "boban" and "jones"

---

# Guess

Something like:

```sql
SELECT *
FROM people
WHERE name matches '\s*boban\s+jones\s*'
```

(Made up code)

---

# Scale

Before diving in with regex,

realize that it can be expensive

due to the _scale_ of your data

---

# Docs

Pretty good overview [here](https://www.postgresql.org/docs/9.3/functions-matching.html)

We'll look at:

- `LIKE`


- `~`

---

# LIKE operator

A simplified pattern matching format

- `_` is the wildcard character (like `.` in regex)



- `%` is equivalent to `.*` in regex

---

# Example

```sql
SELECT *
FROM people
WHERE name LIKE '%boban%jones%'
```

Going to produce a few false positives like "bobanita jones"

Maybe that's okay

---

# Performance

LIKE is fairly simple

Not converted to a regex internally

Implemented in C with its own ad-hoc specialized implementation

See [also](https://stackoverflow.com/a/29474797/15607965)

---

# `~` operator

Takes a regular expression (POSIX standard, ie. egrep)

More powerful than `LIKE`

```sql
SELECT *
FROM people
WHERE name ~ '\s*boban\s+jones\s*'
```

No false positives here, but a bit slower

---

# Indexes

It's hard for a regex engine to make use of an index

---

# Our example

Suppose we had indexed the name field

```sql
SELECT *
FROM people
WHERE name ~ '\s*boban\s+jones\s*'
```

The engine can't really use that here because of the wildcard at the start

---

# Using POSITION

```sql
SELECT *
FROM employees
WHERE POSITION('boban' IN name) > 0 AND POSITION('jones' IN name) > 0
```

Checking literal strings are contained in a string is much faster than a regex/LIKE search

See [docs](https://www.postgresql.org/docs/9.1/functions-string.html)

---

# Using POSITION

```sql
SELECT *
FROM employees
WHERE POSITION('boban' IN name) > 0 AND POSITION('jones' IN name) > 0
```

Will find additional false positives like "jones boban" but often that's fine

---

# Mongo?

Also supports searching by regex

But the principle is the same

> Data can be at a massive scale
>
> Regex engines are slow and have trouble utilizing indexes
>
> There may be a quicker more specialized operations
>
> particularly if a few false positives are acceptable

---

```
  ____                 _           _
 / ___|___  _ __   ___| |_   _ ___(_) ___  _ __
| |   / _ \| '_ \ / __| | | | / __| |/ _ \| '_ \
| |__| (_) | | | | (__| | |_| \__ \ | (_) | | | |
 \____\___/|_| |_|\___|_|\__,_|___/_|\___/|_| |_|

```

---

# Conclusion

Regex pops up everywhere

Hopefully you feel more comfortable

---

# If you remember one thing...

Use regex with care

Sometimes it _isn't_ the best tool for the job

---

```
  ___                  _   _                ___
 / _ \ _   _  ___  ___| |_(_) ___  _ __  __|__ \
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __|/ /
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \_|
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___(_)

  ____                                     _
 / ___|___  _ __ ___  _ __ ___   ___ _ __ | |_ ___
| |   / _ \| '_ ` _ \| '_ ` _ \ / _ \ '_ \| __/ __|
| |__| (_) | | | | | | | | | | |  __/ | | | |_\__ \
 \____\___/|_| |_| |_|_| |_| |_|\___|_| |_|\__|___/

```

Or maybe you want to hear Clement do more foreign accents (:aussie-parrot:)?
