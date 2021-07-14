import cats.data.{NonEmptyChain, NonEmptyList, Validated, ValidatedNel}
import cats.data.Validated.{Invalid, Valid}
import cats.syntax.all._

object AddMapN {

  def main(args: Array[String]): Unit = {
    // Expect three arguments that parse to Ints
    val inputV: Validated[NonEmptyList[String], (Int, Int, Int)] = args match {
      case Array(str1, str2, str3) =>
        (parse(str1), parse(str2), parse(str3)).mapN {
          (int1, int2, int3) => (int1, int2, int3)
        }

      case _ => Invalid(NonEmptyList.of("Exactly three parameters expected representing ints"))
    }

    inputV match {
      case Valid((int1, int2, int3)) => println(int1 + int2 + int3)

      case Invalid(errorsNel) => errorsNel.toList.foreach(println)
    }
  }

  type ParseError = String

  def parse(digits: String): Validated[NonEmptyList[ParseError], Int] = {
    if (digits.forall(_.isDigit)) Valid(digits.toInt)
    else Invalid(NonEmptyList.of(s"Non-digits found in input '$digits'"))
  }
}
