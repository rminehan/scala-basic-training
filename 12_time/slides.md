---
author: Rohan
date: 2021-06-25
title: Time
---

```
 _____ _                
|_   _(_)_ __ ___   ___ 
  | | | | '_ ` _ \ / _ \
  | | | | | | | | |  __/
  |_| |_|_| |_| |_|\___|
                        
```

---

# Motivation

Time libraries are often misused and misunderstood (particularly by juniors)

I see this a lot in code review (and have been victim to it myself!)

---

# Over-engineered?

> "Why doesn't it just do what I want?"
>
> "Why is it so complex?"

---

# Over-engineered?

> "Why doesn't it just do what I want?"
>
> "Why is it so complex?"

Devs get frustrated and reach for the closest tool at hand to solve their problem,

without thinking harder about the nature of the problem

---

# Today

Get the mental model correct (how to think about time)

Understand the way java.time was intended to be used

---

# Today

> Get the mental model correct (how to think about time)

ie. become time lords

(play Dr Who music)

---

# Agenda

- a brief history of java.time


- mental model


- machine representations


- structured representations


- stringy nonsense


- ISO-8601


- bug hunt 

---

```
 ____  _                   
/ ___|| |_ ___  _ __ _   _ 
\___ \| __/ _ \| '__| | | |
 ___) | || (_) | |  | |_| |
|____/ \__\___/|_|   \__, |
                     |___/ 
 _   _                
| |_(_)_ __ ___   ___ 
| __| | '_ ` _ \ / _ \
| |_| | | | | | |  __/
 \__|_|_| |_| |_|\___|
                      
```

A brief history of java.time

---

# The early days of java

We'd use `java.util.Date`

---

# Issues with it

- mutable


- weak api

---

# joda time

Third party library

Fixed those issues

---

# Java 8

Joda time integrated into the standard library.

Changed a little

Rebranded as `java.time`

---

# Summary - 3 time libraries

- java.util.Date


- joda time


- java.time

---

# Backwards compatibility

Each better than the last

But java does not remove things

You'll find legacy docs, SO answers, code etc... for older ones

---

# The rule

Use java.time where possible

For example:

- new logic should use java.time


- migrate older logic to use java.time where practical

---

# java.sql.Timestamp

One place we have to deal with `java.util.Date`

```java
/**
 * A thin wrapper around java.util.Date that allows
 * the JDBC API to identify this as an SQL TIMESTAMP value.
 * ...
 */
public class Timestamp extends java.util.Date {
  ...
}
```

:scream-cat:

---

```
 __  __            _        _ 
|  \/  | ___ _ __ | |_ __ _| |
| |\/| |/ _ \ '_ \| __/ _` | |
| |  | |  __/ | | | || (_| | |
|_|  |_|\___|_| |_|\__\__,_|_|
                              
 __  __           _      _ 
|  \/  | ___   __| | ___| |
| |\/| |/ _ \ / _` |/ _ \ |
| |  | | (_) | (_| |  __/ |
|_|  |_|\___/ \__,_|\___|_|
                           
```

Time is more complex than you might first think

---

# Concepts for this section

- absolute vs contextual time


- precision


- what is a second?

---

# Confusion

We have a strong but fuzzy intuition for time built into us.

When we think/talk about time, we don't clearly differentiate between these concepts.

---

# Examples

Absolute:

> The meteorite hit Earth on July 3rd 2020 at 3:45pm SGT

Contextual:

> Boban's birthday this year is April 20th 2021

---

# Contextual

> Enxhell's 15th birthday is June 26th 2021

## Scenario 1

Imagine you're wanting to wish Enxhell happy birthday.

When do you officially consider it to be his birthday?

## Scenario 2

Or maybe you're a bouncer at an MA-15+ movie theatre.

When is it okay for you to let Enxhell in to see Taken 3 (still showing in Kosovo)?

(His cousin is starring in it)

---

# Contextual

> Enxhell's 15th birthday is June 26th 2021

There's an implied timezone needed to convert that to an instant in time.

If the bouncer is working at the Kosovo movie theatre,

it will be based on the timezone of Kosovo (UTC+2).

---

# Absolute terms

> Enxhell's 15th birthday is June 26th 2021
>
> it will be based on the timezone of Kosovo (UTC+2).

In absolute terms that would be: 2021-06-26T00:00+02:00

---

# Precision

The bouncer cares about day-level precision

Doesn't care what time of day Enxhell was born

---

# Other examples of contextual dates

- new years day (1st)


- end of financial year (June 30th)


There is often an implicit context to these

---

# Thinking more about resolution

"New years day" vs "A new years day"

"Birthday" vs "Date of birth"

---

# Conversation

> Clement: Cynthia, when is your birthday?

---

# Conversation

> Clement: Cynthia, when is your birthday?

How would Cynthia respond?

> (A) "May 31st"
>
> (B) "It's improper to ask me that!"

---

# Subtle difference

"Birthday" vs "date of birth"

---

# Birthday

The day of the year when we eat cake on your behalf

Model with a month and a day

e.g.

```scala
case class MonthDay(month: Int, day: Int)

val cynthiaBirthday = MonthDay(5, 31) // 31st May
```

Note how there's no year

It's a more abstract concept

---

# Date of birth

An exact date

Model with a year, month and day (and sometimes a time)

Suits a `LocalDate` well (later)

---

# Danger

Sometimes the implicit context is clear to us, but not to others

---

# Example

A Kosovo-an company has an api for transactions:

```
GET /transactions/enxhell
```

```json
{
  {
    "description": "High volume hair gel XL",
    "cost": 25.30,
    "date": "2021-04-03",
    "time": "15:30:10"
  },
  {
    "description": "Australian slang phrase book",
    "cost": 10.00,
    "date": "2021-09-03",
    "time": "16:35:11"
  }
}
```

Api isn't explicit about the timezone

Easy to see someone interpreting it as UTC (typical for devs)

or subconsciously think it's their own timezone (non-devs)

---

# What is an absolute timestamp?

How would you represent it?

---

# What is a timestamp?

> How would you represent it?

Depends how precise you want to be.

Time is a continuum with no gaps (like the real numbers):

```
<------------------------------------>
```

Introduces some difficulties

---

# Precision

We can't measure time to infinite precision

You have to pick a level of precision, e.g. days, seconds, millis, nanoseconds

---

# Implicit assumptions

"universal time" (no relativity stuff)

This lets us have a benchmark.

---

```
 __  __            _     _            
|  \/  | __ _  ___| |__ (_)_ __   ___ 
| |\/| |/ _` |/ __| '_ \| | '_ \ / _ \
| |  | | (_| | (__| | | | | | | |  __/
|_|  |_|\__,_|\___|_| |_|_|_| |_|\___|
                                      
 ____                                     _        _   _                 
|  _ \ ___ _ __  _ __ ___  ___  ___ _ __ | |_ __ _| |_(_) ___  _ __  ___ 
| |_) / _ \ '_ \| '__/ _ \/ __|/ _ \ '_ \| __/ _` | __| |/ _ \| '_ \/ __|
|  _ <  __/ |_) | | |  __/\__ \  __/ | | | || (_| | |_| | (_) | | | \__ \
|_| \_\___| .__/|_|  \___||___/\___|_| |_|\__\__,_|\__|_|\___/|_| |_|___/
          |_|                                                            
```

Representations designed for a machine (not a human) to process

---

# Example

Represent time as the number of milliseconds since the epoch

(Jan 1st 1970 UTC - western calendar)

---

# Representation

`Int`?

`Int.MaxValue` is 2,147,483,647

So 2,147,483 seconds.

596 hours

24 days :sad-parrot:

---

# Representation

Won't get much mileage out of that.

More memory!

---

# Long

`Long.MaxValue` is 2^63 - 1

ie. about 2^32 bigger than `Int`

About 6 billion years.

Should be big enough for most purposes...

---

# Just an example

Just an example.

Could represent it in different ways.

Each way is a convention and users of that representation need to understand the convention.

---

# java.time.Instant

A representation of an instant in time from the java standard library.

---

# java.time.Instant

Peeking inside:

```java
/**
 * An instantaneous point on the time-line.
 * ...
 *
 * @since 1.8
 */
public final class Instant ... {

    ...

    /**
     * The number of seconds from the epoch of 1970-01-01T00:00:00Z.
     */
    private final long seconds;
    /**
     * The number of nanoseconds, later along the time-line, from the seconds field.
     * This is always positive, and never exceeds 999,999,999.
     */
    private final int nanos;
}
```

---

# Notes

- times before 1970 would use negative seconds


- nano-second precision (10^-9)

(Nano ~ 1 billion'th, an Int goes to 2.1B)

---

# Working with instants

To the repl!

---

# Summary

```scala
@ val instant = Instant.now 
// 2021-06-23T06:15:05.272Z

@ instant.getNano 
// 272000000

@ instant.toEpochMilli 
// 1624428905272L

@ Instant.MAX 
// +1000000000-12-31T23:59:59.999999999Z

@ Instant.MAX.toEpochMilli 
// java.lang.ArithmeticException: long overflow

@ Instant.ofEpochSecond(100, 3) 
// 1970-01-01T00:01:40.000000003Z

```

---

# Postgres?

Machine representations in postgres

(sql generally)

---

# Postgres

`timestamp` type:

> stored as an 8 byte integer representing microseconds since midnight on Jan 1 2000

ie. a Long

---

# Imperfect mapping

## java.time.Instant

- internal representation: `Long` (seconds) + `Int` (nanoseconds) since the epoch


- precision: nanosecond precision (10^-9)


- size: 12 bytes

## postgres timestamp

- internal representation: `Long` microseconds since 2000


- precision: microseconds (10^-6)


- size: 8 bytes

## Conclusion

They're not 1-1.

e.g. `Instant.ofEpochSecond(0, nano = 1)` is too precise for sql

Would need to approximate (e.g. round off)

---

```
 ____  _                   _                      _ 
/ ___|| |_ _ __ _   _  ___| |_ _   _ _ __ ___  __| |
\___ \| __| '__| | | |/ __| __| | | | '__/ _ \/ _` |
 ___) | |_| |  | |_| | (__| |_| |_| | | |  __/ (_| |
|____/ \__|_|   \__,_|\___|\__|\__,_|_|  \___|\__,_|
                                                    
 ____                                     _        _   _                 
|  _ \ ___ _ __  _ __ ___  ___  ___ _ __ | |_ __ _| |_(_) ___  _ __  ___ 
| |_) / _ \ '_ \| '__/ _ \/ __|/ _ \ '_ \| __/ _` | __| |/ _ \| '_ \/ __|
|  _ <  __/ |_) | | |  __/\__ \  __/ | | | || (_| | |_| | (_) | | | \__ \
|_| \_\___| .__/|_|  \___||___/\___|_| |_|\__\__,_|\__|_|\___/|_| |_|___/
          |_|                                                            
```

Back to java.time

---

# Machine representation

Hopefully the point has been made that absolute times are usually

represented using simple primitives

---

# Human friendly representations of time

> Nanoseconds since the epoch

Very precise and unambiguous

Not how humans think about time

---

# Example

> Q: What time is the meeting today?
>
> A: 11203200000000000000 nanoseconds since the epoch

(Good chance to talk about Star Trek)

---

# How humans think of time

"Next Wednesday"

"2021-03-10"

"5 minutes ago"

(This is what you'll see in UI's)

---

# A structured representation

Like we saw before, we think of a date more like:

```scala
case class LocalDate(year: Int, month: Int, day: Int)
```

---

# Concepts we need to be clear on

- absolute vs contextual (local)


- precision (day vs time)

---

# LocalDate

```java
public final class LocalDate ... {
    ...

    private final int year;

    private final short month;

    private final short day;

    ...
}
```

Day precision

More structured

Not an offset like `Instant`

---

# LocalTime

```java
public final class LocalTime ... {
    ...

    private final byte hour;

    private final byte minute;

    private final byte second;

    private final int nano;

    ...
}
```

Nanosecond precision

Again more structured

No explicit day information encoded

Good for daily schedules, e.g. "run everyday at 12:30:00"

---

# LocalDateTime

LocalDateTime = LocalDate + LocalTime

```java
public final class LocalDateTime ... {

    ...

    private final LocalDate date;

    private final LocalTime time;

    ...
}
```

Can represent a point in time on a particular day

---

# Context dependent

```scala
val meteoriteLanding = LocalDateTime.of(LocalDate.of(2021, 4, 21), LocalTime.of(13, 2, 15))
```

Information needed to convert this to an `Instant` is not explicitly encoded

Implicit knowledge required

---

# Explicitly encoding

```scala
val meteoriteLanding = LocalDateTime.of(LocalDate.of(2021, 4, 21), LocalTime.of(13, 2, 15))
```

We need something like a timezone or zone offset

---

# Timezone (e.g. zone id)

Examples:

- Australia/Sydney


- Asia/Singapore

To the repl!

---

# Summary

```scala
import java.time.ZoneId

ZoneId.of("Australia/Sydney")

ZoneId.of("Asia/Singapore")
```

See more [here](https://en.wikipedia.org/wiki/List_of_tz_database_time_zones)

---

# Timezone

The role of a timezone is to convert a local date into an absolute instant.

ie. work out our "offset" from UTC.

---

# DST

Don't forget daylight savings!

Sydney is UTC+10 sometimes and UTC+11 other times.

(Singaporeans aren't cursed with daylight savings)

---

# ZoneOffset

A representation of the time difference between somewhere and Greenwich/UTC.

To the repl!

---

# Summary

```scala
@ import java.time.ZoneOffset 

@ ZoneOffset.of("+02:00") 
// +02:00

@ ZoneOffset.UTC 
// Z

@ ZoneOffset.UTC.getTotalSeconds 
// 0

@ val singaporeOffset = ZoneOffset.of("+08:00") 
// +08:00

@ singaporeOffset.getTotalSeconds / 3600 
// 8

@ singaporeOffset.getId 
// "+08:00"

@ singaporeOffset.toString 
// "+08:00"
```

From the source:

```java
    @Override
    public String toString() {
        return id;
    }
```

---

# ZoneId vs ZoneOffset

Are they interchangeable?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# ZoneId vs ZoneOffset

> Are they interchangeable?

No

`ZoneId` > `ZoneOffset` ?

e.g. daylight savings

---

# Recapping so far

- many `Local-` style classes (`LocalDate`, `LocalTime`, `LocalDateTime`)


- can convert these to an `Instant` directly


- seen `ZoneId` and `ZoneOffset` as mechanisms to pin these local things down

---

# ZonedDateTime

```java
public final class ZonedDateTime ... {

    ...

    private final LocalDateTime dateTime;

    private final ZoneOffset offset;

    private final ZoneId zone;

    ...

}
```

Basically `LocalDateTime` plus the extra info needed to zone it

---

# OffsetDateTime

```java
public final class OffsetDateTime ... {

    ...

    private final LocalDateTime dateTime;

    private final ZoneOffset offset;

    ...
}
```

(Just `ZonedDateTime` without a `ZoneId`)

```scala
@ import java.time.OffsetDateTime 

@ OffsetDateTime.now 
// 2021-06-24T15:08:40.659+10:00
```

---

# Recapping ZonedDateTime

`ZonedDateTime` is essentially a `LocalDateTime` with extra timezone information

(offset + zone)

---

# Recap

```
      Machine           |       Structured
   Representations      |    Representations (java.time)
                        |
  ----------------------|--------------------------
                        |      Local    |  Absolute
    java.time.Instant   | ----------------------
                        |               |
    postgres timestamp  | LocalDate     |
             timestampz |               |  ZonedDateTime
                        | LocalTime     |
                        |               |  OffsetDateTime
                        | LocalDateTime |
  
```

## Machine representations

Very precise representation of a moment in time

Cheap and simple

Great for computers

Not user friendly

## Local-

Intuitive representations of time concepts

Assumes an implicit "local" context (e.g. birthday) 

Not absolute (unable to convert to an Instant)

## Absolute

A `LocalDateTime` plus timezone information

Enough information to convert to an `Instant`

---

```
 _____ _                
|_   _(_)_ __ ___   ___ 
  | | | | '_ ` _ \ / _ \
  | | | | | | | | |  __/
  |_| |_|_| |_| |_|\___|
                        
 _     _    __         __              
| |   (_)  / /  _ __   \ \    ___  ___ 
| |   | | | |  | '_ \   | |  / _ \/ __|
| |___| | | |  | | | |  | | |  __/\__ \
|_____|_| | |  |_| |_|  | |  \___||___/
           \_\         /_/             
```

Time Lies

Extra info to make the point that time keeping is complex

What is a second?

---

# How would you define a second?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# One way

Define it to fit cleanly into a day

e.g. a second is defined so that we have 60 * 60 * 24 of them in a day

Issues?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Issues

- practical difficulties implementing it


- the Earth is slowing down

---

# Practical issues

How would you build a simple machine to measure time?

You need to know when the Earth has rotated on its axis.

```scala
ZIO[... with EarthRotationMeasurementThing, ..., ...]
```

---

# The spin of the Earth is slowing down

Dinosaurs worked shorter days than us

Our great*10^6 grandkids will be enduring 25 hour days

---

# Better way?

Find some natural phenomenon that you can measure.

Something that you can measure anywhere and won't change overtime.

Ideally something that happens to divide nicely into a solar day.

---

# SI seconds

From [wikipedia](https://en.wikipedia.org/wiki/Second):

> The second is defined as being equal to the time duration of 9 192 631 770
>
> periods of the radiation corresponding to the transition between the two hyperfine
>
> levels of the fundamental unperturbed ground-state of the caesium-133 atom

---

# Problems with this approach?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Problems with this approach?

> Ideally something that happens to divide nicely into a solar day.

It's not going to divide perfectly.

---

# Error

From the `java.time.Instant` docs:

> The length of the solar day is the standard way that humans measure time.
>
> This has traditionally been subdivided into 24 hours of 60 minutes of 60 seconds,
>
> forming a 86400 second day.

then:

> Unfortunately, as the Earth rotates the length of the day varies.
>
> In addition, over time the average length of the day is getting longer as the Earth slows.
>
> As a result, the length of a solar day in 2012 is slightly longer than 86400 SI seconds.

---

# Further

> The actual length of any given day and the amount by which the Earth is slowing
>
> are not predictable and can only be determined by measurement.
>
> The UT1 time-scale captures the accurate length of day,
>
> but is only available some time after the day has completed.

ie. we can't even predict the number of SI seconds in a day,

we can only measure after the fact

---

# java.time's approach

> The Java Time-Scale divides each calendar day into exactly 86400 subdivisions, known as seconds.
>
> These seconds may differ from the SI second.

---

# java.time Second

A java.time "second" is an abstraction

The _actual_ time that passes will vary depending on context

```

<------------------------------------------------------------------------------------------>
      dinosaurs                         now                                 star trek

actual |--|                           |----|                               |--------|
time                                 (average)
elapsed
```

---

# Other time keeping systems

TAI - International Atomic Time

37 seconds ahead of UTC

(SI seconds are slightly shorter, more have elapsed)

[More info](https://en.wikipedia.org/wiki/International_Atomic_Time)

---

# Does this affect us?

Not really.

If our leadiq code is still around in 100 million years, it will have been rewritten

(except the actors package)

No need to switch to a Caesium pocket watch

Just mentioning it to make you appreciate that time keeping is more complex than you might think.

---

```
 ____  _        _             
/ ___|| |_ _ __(_)_ __   __ _ 
\___ \| __| '__| | '_ \ / _` |
 ___) | |_| |  | | | | | (_| |
|____/ \__|_|  |_|_| |_|\__, |
                        |___/ 
                                           
 _ __   ___  _ __  ___  ___ _ __  ___  ___ 
| '_ \ / _ \| '_ \/ __|/ _ \ '_ \/ __|/ _ \
| | | | (_) | | | \__ \  __/ | | \__ \  __/
|_| |_|\___/|_| |_|___/\___|_| |_|___/\___|
                                           
```

---

# First off

Hopefully now it's clear:

> Timestamps are strings

Don't use strings to represent timestamps in your internal models

Sometimes they are transported to us from outside our app as strings,

but we would aggressively validate them and use a more appropriate representation

---

# Another area for conflation

```scala
// Group A
logger.debug(s"User with name: '$name'")
println(s"Found user with age: $age")
println(s"User created at: $timestamp")

// Group B
val id  = s"${hash(linkedinId)}--${timestamp.getEpochMillis}"
val sqlQuery = s"SELECT * FROM leads WHERE created_at >= $formattedTime"
```

What's the difference between the two groups?

---

# Another area for conflation

```scala
// Group A
logger.debug(s"User with name: '$name'")
println(s"Found user with age: $age")
println(s"User created at: $timestamp")

// Group B
val id  = s"${hash(linkedinId)}--${timestamp.getEpochMillis}"
val sqlQuery = s"SELECT * FROM leads WHERE created_at >= $formattedTime"
```

> What's the difference between the two groups?

A is for humans

B is for machines/logic

Unfortunately we use the same type for both (`String`)

---

# Human audience

Highly intuitive

Doesn't require a precise representation

---

# Floats

```scala
val score: Float = ...
println("Boban scored: $score")
```

Usually would print something like:

```
Boban scored 4.0
```

---

# Floats

```scala
val score: Float = ...
println("Boban scored: $score")
```

What about a really big score though?

```
Boban scored 1.23E12
```

Whoops! It switched to exponential notation.

---

# Floats

Imagine if you had stuck that in your sql query:

```scala
val query = s"SELECT * FROM results WHERE score = $score"
```

---

# Parsing

Or later you were trying to parse the score back out of some text:

```
Boban scored 1.3
Bobanita scored 1.4
Enxhell scored 1.3E12
```

```scala
val scorePattern = "scored (\\d+\\.\\d)".r
```

Whoopsie - Enxhell will tie with Boban

The text was designed for humans and is now being parsed by an unintuitive machine

---

# Timestamps

```scala
println(s"User created at: $timestamp")

// Zij's computer:
//   User created at 2020-03-05T11:00+08:00[Asia/Singapore]

// Rohan's computer:
//   User created at 2020-03-05T11:00+11:00[Australia/Sydney]
```

---

# String building

How do these code samples work?

```scala
val score: Float = ...

val result = "He scored " + score + " on the test"

val result = s"He scored $score on the test"
```

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# String building

> How do these code samples work?

```scala
val score: Float = ...

val result = "He scored " + score + " on the test"

val result = s"He scored $score on the test"
```

`.toString`

---

# .toString

Everything can be converted to a `String`

It's usually intended as a _human_ readable representation of a type

---

# The "convenience" of toString

It's _too_ close at hand

We conflate human vs machine reader because it's the same type

```scala
val query = s"SELECT * FROM leads WHERE created_at > '$timestamp'"
```

---

# Objection

```scala
val query = s"SELECT * FROM leads WHERE created_at > '$timestamp'"
```

> But Rohan!
>
> I've done exactly this and it works! What's the issue?

---

# Objection

```scala
val query = s"SELECT * FROM leads WHERE created_at > '$timestamp'"
```

> But Rohan!
>
> I've done exactly this and it works! What's the issue?

Yes it might happen to work

Have you tested it on the edge cases though (analogous to 1.23E12)?

Still a bad habit - encourages this bad conflation

---

# Good habit

When you are using strings to represent data to be processed by a machine,

use an explicit controlled encoding

(don't rely on `toString`)

At the serialization and deserialization location, use that encoding

---

# Formatters

To the repl!

---

# Summary

```scala
@ val now = ZonedDateTime.now 
// 2021-06-25T15:33:49.997+10:00[Australia/Sydney]

// Just some random pattern I made up - don't use this
@ val rohanFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS Z") 

@ val formatted = rohanFormat.format(now) 
// "2021-06-25 15:33:49 997 +1000"

@ ZonedDateTime.parse(formatted, rohanFormat) 
// 2021-06-25T15:33:49.997+10:00

@ res129 == now 
// false  (differ on ZoneId info)

@ res129.toInstant == now.toInstant 
// true   (but still represent the same point in time)
```

---

# Aside

Beware of `==` on `ZonedDateTime`

Doesn't mean:

> "Represents the same point in time"

More like:

> "Represents the same description of a point in time (incl. zone offset and id)"

(You should already be suspicious of `==` btw)

---

```
 ___ ____   ___         ___   __    ___  _ 
|_ _/ ___| / _ \       ( _ ) / /_  / _ \/ |
 | |\___ \| | | |_____ / _ \| '_ \| | | | |
 | | ___) | |_| |_____| (_) | (_) | |_| | |
|___|____/ \___/       \___/ \___/ \___/|_|
                                           
```

The "standard" for time

See [wikipedia](https://en.wikipedia.org/wiki/ISO_8601)

java.time follows this

---

# ISO-8601 calendar

The "standard" calendar (ie. Gregorian calendar)

(there are others!)

Matters when you do:

```scala
zonedDateTime.toInstant
```

---

# Textual time representations

Sometimes we need to transfer dates "over the wire" to another app

But there's no native representation in that transfer format

Examples?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Json!

Json has built in types like:

- object


- array


- numeric


- string


- boolean


- null

No native format for timestamps :sad-parrot:

---

# How to represent it in json?

## Machine format

Use the numeric type

e.g. millis since the epoch

11024238423000

## Textual format

Use the string type

Format the string in some human readable way

"10/11/2021 14:33:09.119 +10:00"

---

# Pro's and con's of approaches?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Pro's and con's - machine format

## Pro's

Cheap

Fairly unambiguous

Fairly simple to deserialize into something like an `Instant`

```scala
val nanos = (millisSinceEpoch % 1000) * 1_000_000
val seconds = millisSinceEpoch / 1000 
```

## Con's

Makes json hard to debug

Data is not self-describing

More fiddly to mess around with curl

## Summary

Good for machines, hard for humans

---

# Pro's and con's - textual format

"10/11/2021 14:33:09.119 +10:00"

## Pro's

Self-describing

(but potentially ambiguous and misleading)

## Con's

Expensive

(Extra bytes on the wire and parsing)

---

# Diversity

You can understand people using different formats

Inconsistencies, confusion etc...

---

# My recommendation

Use the textual representation spelled out by the ISO-8601 standard:

```scala
@ import java.time.format.DateTimeFormatter 

@ val secondsZDT = ZonedDateTime.parse("2021-06-24T00:00Z") 

@ secondsZDT.format(DateTimeFormatter.ISO_INSTANT) 
// "2021-06-24T00:00:00Z"

@ val millisZDT = ZonedDateTime.parse("2021-06-24T00:00:00.12Z") 

@ millisZDT.format(DateTimeFormatter.ISO_INSTANT) 
// "2021-06-24T00:00:00.120Z"

@ val microsZDT = ZonedDateTime.parse("2021-06-24T00:00:00.1234Z") 

@ microsZDT.format(DateTimeFormatter.ISO_INSTANT) 
// "2021-06-24T00:00:00.123400Z"

@ val nanosZDT = ZonedDateTime.parse("2021-06-24T00:00:00.12345678Z") 

@ nanosZDT.format(DateTimeFormatter.ISO_INSTANT) 
// "2021-06-24T00:00:00.123456780Z"
```

(Tends to cluster sub-second to 3's)

There are other `ISO-*` style formatters in the library

(Note this is the format I've been passing into the `parse` function)

---

# Why use it?

Fairly standard now

Unambiguous (e.g. month vs day of month)

Encourages explicit encoding of timezone

Most languages will have tools to parse this as ISO-8601 is well known

---

```
 ____  _        _             
/ ___|| |_ _ __(_)_ __   __ _ 
\___ \| __| '__| | '_ \ / _` |
 ___) | |_| |  | | | | | (_| |
|____/ \__|_|  |_|_| |_|\__, |
                        |___/ 
                                           
 _ __   ___  _ __  ___  ___ _ __  ___  ___ 
| '_ \ / _ \| '_ \/ __|/ _ \ '_ \/ __|/ _ \
| | | | (_) | | | \__ \  __/ | | \__ \  __/
|_| |_|\___/|_| |_|___/\___|_| |_|___/\___|
                                           
```

---

# Another area for conflation

```scala
// Group A
logger.debug(s"User with name: '$name'")
println(s"Found user with age: $age")
println(s"User created at: $timestamp")

// Group B
val id  = s"${hash(linkedinId)}--${timestamp.getEpochMillis}"
val sqlQuery = s"SELECT * FROM leads WHERE created_at >= $formattedTime"
```

What's the difference between the two groups?

---

# Another area for conflation

```scala
// Group A
logger.debug(s"User with name: '$name'")
println(s"Found user with age: $age")
println(s"User created at: $timestamp")

// Group B
val id  = s"${hash(linkedinId)}--${timestamp.getEpochMillis}"
val sqlQuery = s"SELECT * FROM leads WHERE created_at >= $formattedTime"
```

> What's the difference between the two groups?

A is for humans

B is for machines/logic

Unfortunately we use the same type for both (`String`)

---

# Human audience

Highly intuitive

Doesn't require a precise representation

---

# Floats

```scala
val score: Float = ...
println("Boban scored: $score")
```

Usually would print something like:

```
Boban scored 4.0
```

---

# Floats

```scala
val score: Float = ...
println("Boban scored: $score")
```

What about a really big score though?

```
Boban scored 1.23E12
```

Whoops! It switched to exponential notation.

---

# Floats

Imagine if you had stuck that in your sql query:

```scala
val query = s"SELECT * FROM results WHERE score = $score"
```

---

# Parsing

Or later you were trying to parse the score back out of some text:

```
Boban scored 1.3
Bobanita scored 1.4
Enxhell scored 1.3E12
```

```scala
val scorePattern = "scored (\\d+\\.\\d)".r
```

Whoopsie - Enxhell will tie with Boban

The text was designed for humans and is now being parsed by an unintuitive machine

---

# Timestamps

```scala
println(s"User created at: $timestamp")

// Zij's computer:
//   User created at 2020-03-05T11:00+08:00[Asia/Singapore]

// Rohan's computer:
//   User created at 2020-03-05T11:00+11:00[Australia/Sydney]
```

---

# String building

How do these code samples work?

```scala
val score: Float = ...

val result = "He scored " + score + " on the test"

val result = s"He scored $score on the test"
```

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# String building

> How do these code samples work?

```scala
val score: Float = ...

val result = "He scored " + score + " on the test"

val result = s"He scored $score on the test"
```

`.toString`

---

# .toString

Everything can be converted to a `String`

It's usually intended as a _human_ readable representation of a type

---

# The "convenience" of toString

It's _too_ close at hand

We conflate human vs machine reader because it's the same type

```scala
val query = s"SELECT * FROM leads WHERE created_at > '$timestamp'"
```

---

# Objection

```scala
val query = s"SELECT * FROM leads WHERE created_at > '$timestamp'"
```

> But Rohan!
>
> I've done exactly this and it works! What's the issue?

---

# Objection

```scala
val query = s"SELECT * FROM leads WHERE created_at > '$timestamp'"
```

> But Rohan!
>
> I've done exactly this and it works! What's the issue?

Yes it might happen to work

Have you tested it on the edge cases though (analogous to 1.23E12)?

Still a bad habit - encourages this bad conflation

---

# Good habit

When you are using strings to represent data,

use an explicit controlled representation

(don't rely on `toString` and `parse`)

---

```
 ____              
| __ ) _   _  __ _ 
|  _ \| | | |/ _` |
| |_) | |_| | (_| |
|____/ \__,_|\__, |
             |___/ 
 _   _             _   
| | | |_   _ _ __ | |_ 
| |_| | | | | '_ \| __|
|  _  | |_| | | | | |_ 
|_| |_|\__,_|_| |_|\__|
                       
```

(A bit like a quiz)

---

# Bug Hunt

I'll show you a code sample

Try to spot something about it that smells or is wrong

---

# Example 1

```scala
// Get today's date as a day
val today: LocalDate = ZonedDateTime.now.toLocalDate
```

---

# Example 1

```scala
// Get today's date as a day
val today: LocalDate = ZonedDateTime.now.toLocalDate
```

What timezone?

`java.time` is using the timezone configuration of the machine it's on

Suppose it's 11pm in Singapore on the 3rd when we run this

For Zij, it will produce the 3rd

For Rohan, it will produce the 4th

Not deterministic!

Core issue is the subtle conflation between absolute and contextual times

---

# Example 2

```scala
// We know one of our customers signed up at this time
val searchTimestamp: ZonedDateTime = ...

// Find them!
customers.find(_.timestamp == searchTimestamp).getOrElse(
  throw new Exception("Couldn't find our customer")
)
```

---

# Example 2

```scala
// We know one of our customers signed up at this time
val searchTimestamp: ZonedDateTime = ...

// Find them!
customers.find(_.timestamp == searchTimestamp).getOrElse(
  throw new Exception("Couldn't find our customer")
)
```

The `searchTimestamp` might have different zone information

to the ones our customers have even though it's the same point in time

---

# Example 3

```scala
// We want to represent the months where we received pay from a customer
val months = List(
  LocalDate(2021, 11, 1),
  LocalDate(2021, 12, 1),
  LocalDate(2022, 2, 1),
)
```

---

# Example 3

```scala
// We want to represent the months where we received pay from a customer
val months = List(
  LocalDate(2021, 11, 1),
  LocalDate(2021, 12, 1),
  LocalDate(2022, 2, 1),
)
```

This is overly precise

You can tell is smells because we've established a "convention" of using 1 for the day

Would be easy for someone to accidentally do this:

```scala
// Did we get paid in Feb?
months.contains(LocalDate(2021, 2, 2))
```

Why have this extra dangling field if it has no meaning?

`YearMonth` is a more suitable type

---

# Example 4

```scala
// We are writing tests for a driver for a simple key-value store
// Keys and values are always kept as strings
object KeyValueStore {
  def write(key: String, value: String): Unit = ...
  def read(key: String): String = ...
}

// We convert timestamps to strings for storage
val formatter = DateTimeFormatter.ofPattern("yyyy-MM-ddTHH:mm:ss.SSS")

// Test that after writing a timestamp and reading it back we get the same thing
val key = "test"
val sent: Instant = ZonedDateTime.parse("2021-05-04T12:30:00Z").toInstant

KeyValueStore.write(key, formatter.format(sent))

val retrieved: Instant = formatter.parse(KeyValueStore.read(key)).toInstant

sent mustEqual retrieved
```

---

# Example 4

```scala
// We are writing tests for a driver for a simple key-value store
// Keys and values are always kept as strings
object KeyValueStore {
  def write(key: String, value: String): Unit = ...
  def read(key: String): String = ...
}

// We convert timestamps to strings for storage
val formatter = DateTimeFormatter.ofPattern("yyyy-MM-ddTHH:mm:ss.SSS")

// Test that after writing a timestamp and reading it back we get the same thing
val key = "test"
val sent: Instant = ZonedDateTime.parse("2021-05-04T12:30:00Z").toInstant

KeyValueStore.write(key, formatter.format(sent))

val retrieved: Instant = formatter.parse(KeyValueStore.read(key)).toInstant

sent mustEqual retrieved
```

(1) (smell) forgot to specify the timezone in the formatter

(2) (bug in the test) the formatter is chopping it off to 3 d.p before sending it

When the string comes back and gets deserialized, it will be to 3 d.p

Our test will pass because our test case happens to be on a timestamp that is all 0's

after 3 d.p

Mix up your data in your tests and don't use "simple" timestamps all the time

---

# Wrapping up

Time is tricky

We have a strong, but flawed intuition for it

That can subconsciously make its way into our software

---

# Wrapping up

java.time is pretty good

If it feels awkward or cumbersome to you, then strong chance there is a fundamental misunderstanding

e.g. "why can't I convert from X to Y?" (maybe because it doesn't make sense)

---

# QnA
