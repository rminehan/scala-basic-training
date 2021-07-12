import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.data.Validated.{Invalid, Valid}

object AddValidatedList {

  def main(args: Array[String]): Unit = {

    // Note the List here - modelling multiple errors
    // No short-circuiting like with Either
    val inputV: Validated[List[String], (Int, Int)] = ???

    inputV match {
      case Valid((left, right)) => println(left + right)

      // Many errors to print now
      case Invalid(errors) => errors.foreach(println)
    }
  }

  type ParseError = String

  def parse(digits: String): Validated[ParseError, Int] = {
    if (digits.forall(_.isDigit)) Valid(digits.toInt)
    else Invalid(s"Non-digits found in input '$digits'")
  }
}


