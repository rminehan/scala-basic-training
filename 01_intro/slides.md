---
author: Rohan
title: Session 1 - Intro to scala and the series
date: 2021-04-15
---

```
__        __   _                          
\ \      / /__| | ___ ___  _ __ ___   ___ 
 \ \ /\ / / _ \ |/ __/ _ \| '_ ` _ \ / _ \
  \ V  V /  __/ | (_| (_) | | | | | |  __/ !!
   \_/\_/ \___|_|\___\___/|_| |_| |_|\___|
                                          
```

Welcome to this series of trainings!

---

# Why are we all meeting?

Train as a group.

Staggered joining times makes that tricky.

---

# Context

2020 batch of junior developers:

- Zij (software)


- Jonathan (software)


- Ritchie (software)


- Lulu (aka Yon Lu) (software)


- James (ML)


- Clement (ML)

Trained as a group.

Juniors presented on their experiences.

---

# Lessons learnt

- invest heavily in the onboarding


- keep juniors together


- spend more time on the basics before getting into the advanced stuff


- keep it practical


- try not to go overtime

---

# Methodology

- 2 sessions per week


- roughly 1 hour


- designed for our analytics codebase


- no homework (but you can peek ahead if you want)


- try to get Uncles Zij and Clement involved here and there

---

# Resources

Slides and demo scripts on github: https://github.com/rminehan/scala-basic-training

For now recordings won't be accessible publicly.

---

# The analytics codebase

Training is designed to prepare you for the analytics code base.

What's it like?

- 95% scala code


- deliberately simple


- no concurrency


- small number of dependencies, mostly the standard library


- uses basic FP concepts but isn't trying to be advanced


- uses spark a bit


- sql and mongo

---

# Beyond analytics

The analytics codebase is a good training area. Gentle learning curve.

Software juniors will spend a few months in that. Analysts will stay there long term.

Later might move to other teams, or switch to other code bases.

Much more complex stack (kafka, graphql, aws, zio, type classes...).

---

# Time in lieu

This training time counts as work.

There's no homework because you're already very busy.

If you _do_ want extra exercises, we can point you to things.

Keep track of that time spent and we'll add it to your time in lieu.

---

# What about product orientation?

Easier to do that when you've joined and can access our code, docs etc...

---

# About me?

Christian, Husband, Dad.

Aussie living in the greatest country on Earth.

Hybrid engineer/manager/trainer/interviewer jack of all trades for the data team.

Love scala, but I'm aware of its flaws.

For some reason people get the impression I don't like python.

Love vim, terminals, cli tools, only recently discovered non-terminal software...

Old man vibe.

Tend to roast people (Enxhell, Thilo, interns), don't take it personally. Roast me back.

Trying to learn Singlish to keep up with the young folk.

Like party parrots. That's how you can get on my good side.

---

# Let's get going!

```
 ____            _       
/ ___|  ___ __ _| | __ _ 
\___ \ / __/ _` | |/ _` |
 ___) | (_| (_| | | (_| |  !
|____/ \___\__,_|_|\__,_|
                         
```

---

# Scala

With the remaining time, introduce scala broadly and the tools we'll use.

---

# Scala - key points

- cousin of java


- statically typed


- designed for FP


- great for concurrency


- still a bit experimental

---

# Cousin of java

You can use familiar things from the java standard library.

---

# Sample

## Java:

```java
import java.time.ZonedDateTime;

int a = 1;

String s = "abc".toUpperCase();
```

## Scala:

```scala
import java.time.ZonedDateTime

val a = 1

val s = "abc".toUpperCase
```

## Initial differences?

- packages are the same


- familiar friends are the same: `int`, `String` etc...


- drop the semi-colons


- can sometimes drop brackets when calling functions with no parameters


- type inference

---

# Application: Copying off stack overflow

You're trying to figure out how to do something in scala, e.g. you google:

> How to make gif of Rohan using scala?

Might get no matches.

---

# Try it with java

> How to make gif of Rohan using java?

Java is much more popular. Lots of libraries, blog posts, answers.

You can translate them to scala fairly directly.

---

# Caution though

Java code often uses an "imperative" style.

Idiomatic scala code will often feel quite different.

Once you understand how the java code works, try to convert it to idiomatic scala code.

---

```
 _____           _     
|_   _|__   ___ | |___ 
  | |/ _ \ / _ \| / __| ?
  | | (_) | (_) | \__ \
  |_|\___/ \___/|_|___/
                       
```

---

# Text editor

Probably start out with intellij or VS code.

Most beginner friendly. Best scala support.

90% of our scala devs use intellj.

Code completion and other IDE features helps newbies navigate complex scala features.

(Then later of course you'll move to vim)

---

# Running code?

Scala statically typed => compiler

For big projects: sbt

For demo's and tiny prototypes: ammonite - https://ammonite.io/

---

# Mucking around

Ammonite repl.

Scala worksheets (for intellij users).

Codi worksheets (for vimbeciles).

---

# Quick repl demo

---

# Quick codi demo

`:Codi amm`

```vim
let g:codi#interpreters = {
                   \ 'amm': {
                       \ 'bin': 'amm',
                       \ 'prompt': '^@ ',
                       \ },
                   \ }
```

(Can hook up to any repl, e.g. python)

---

# Scala versions

So many versions.

In 2021, most likely you'll encounter

- 2.11


- 2.12 (we use)


- 2.13

For our purposes they're all the same.

---

# Installing stuff

If you want to play around:

- JDK (java 8+)


- Ammonite (probably for scala 2.12). Just copy paste this into your shell:

```bash
sudo sh -c '(echo "#!/usr/bin/env sh" && curl -L https://github.com/com-lihaoyi/Ammonite/releases/download/2.3.8/2.12-2.3.8-65-0f0d597f) > /usr/local/bin/amm && chmod +x /usr/local/bin/amm' && amm
```

- SBT (later on)


- Intellij/VS Code with scala plugin

---

# If you get stuck...

... reach out to Zij, Clement, Willy or myself.

---

# Running the slides

- install `lookatme`: https://github.com/d0c-s4vage/lookatme


- clone the github repo: https://github.com/rminehan/scala-basic-training


- `cd` into the directory for the lesson


- run `lookatme slides.md`

(Or just read them in a text editor, they're just markdown files after all)

---

```
  ____          _        _   _            _
 / ___|___   __| | ___  | | | |_ __   ___| | ___
| |   / _ \ / _` |/ _ \ | | | | '_ \ / __| |/ _ \
| |__| (_) | (_| |  __/ | |_| | | | | (__| |  __/
 \____\___/ \__,_|\___|  \___/|_| |_|\___|_|\___|

 _____ _
|_   _(_)_ __ ___   ___
  | | | | '_ ` _ \ / _ \
  | | | | | | | | |  __/
  |_| |_|_| |_| |_|\___|

```

Time for our code uncles to reflect on their experiences learning scala.

---

# Uncle Zij

Get hands dirty:

- ammonite repl


- scala-exercises website (interactive)

---

# Uncle Clement

- importance of reading and writing code


- asking lots of questions in the process.


- building a whole project from scratch (build.sbt, dependencies.scala, main files)


- read the sbt documentations step by step


- intellij tips

---

# That's it for today!

```
  ___                  _   _                 
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___ 
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
                                             
```
