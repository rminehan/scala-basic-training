import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.data.Validated.{Invalid, Valid}
import cats.syntax.all._

object AddMapN {

  def main(args: Array[String]): Unit = {
    // Expect three arguments that parse to Ints
    val inputV: Validated[NonEmptyList[String], (Int, Int, Int)] = args match {
      case Array(str1, str2, str3) =>
        ???

      case _ => Invalid(NonEmptyList.one("Expected exactly 3 integer inputs"))
    }

    inputV match {
      case Valid((int1, int2, int3)) => println(int1 + int2 + int3)

      case Invalid(errors) => errors.toList.foreach(println)
    }
  }

  type ParseError = String

  // NOTE: Returns ValidatedNel now - necessary to make mapN work
  def parse(digits: String): ValidatedNel[ParseError, Int] = {
    if (digits.forall(_.isDigit)) Valid(digits.toInt)
    else Invalid(NonEmptyList.of(s"Non-digits found in input '$digits'"))
  }
}
