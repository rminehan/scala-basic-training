package regex

import spire.syntax.literals.si._
import spire.implicits.cfor
import org.scalameter.api._

import scala.util.matching.Regex

class RegexBench extends Bench.LocalTime {

  val rng = new scala.util.Random(279)

  performance of "(greedy vs lazy)" in {

    val stringLengths: Gen[Int] = Gen.range("size")(
      from = i"20 000",
      upto = i"100 000",
      hop = i"20 000"
    )

    // Search strings with a "boban jones" at the start and trailing '0's
    // They are setup to exploit the worst case for the greedy search where
    // it will go all the way to the end of the string then backtrack making
    // two passes over the string
    val bobanJonesSearchStrings: Gen[String] = for {
      length <- stringLengths
    } yield {
      s"boban jones${'0' * length}"
    }

    // NOTE: To get a performance difference between lazy and greedy,
    // it's important to make sure it short-circuits after the first match.
    // Otherwise the lazy one will end up walking the whole string as well looking
    // for its next match.
    // I found that `String.matches(regex)` actually doesn't _seem_ to do this
    // (haven't checked in detail)
    // so went with `findFirstIn`
    measure method "greedy" in {
      using(bobanJonesSearchStrings) in { text =>
        val regex = "boban.+jones".r
        regex.findFirstIn(text)
      }
    }

    measure method "lazy" in {
      using(bobanJonesSearchStrings) in { text =>
        val regex = "boban.+?jones".r
        regex.findFirstIn(text)
      }
    }

    measure method "lazy2" in {
      using(bobanJonesSearchStrings) in { text =>
        val regex = "boban[^j]+jones".r
        regex.findFirstIn(text)
      }
    }
  }

  performance of "(compiled vs uncompiled)" in {
    // Testing to understand the time it takes to compile a regex
    // We'd expect this time to increase as the regex itself gets longer
    // When a regex is used many times over the life of the app,
    // there might be benefit it compiling it once and storing that

    val regexLengths: Gen[Int] = Gen.range("size")(
      from = 10,
      upto = 50,
      hop = 10
    )

    // Just builds simple "aaaa..." style regexes
    // A better test would include other aspects of regex syntax
    val regexesUncompiled: Gen[String] = regexLengths.map { length =>
      "a" * length
    }

    measure method "compiling" in {
      using(regexesUncompiled) in { uncompiled =>
        uncompiled.r
      }
    }

    val searchString = "a" * 1000

    measure method "searching uncompiled" in {
      using(regexesUncompiled) in { uncompiled =>
        searchString.matches(uncompiled)
      }
    }

    val regexesCompiled: Gen[Regex] = regexesUncompiled.map(_.r)

    measure method "searching compiled" in {
      using(regexesCompiled) in { compiled =>
        compiled.findAllIn(searchString)
      }
    }

  }

}
