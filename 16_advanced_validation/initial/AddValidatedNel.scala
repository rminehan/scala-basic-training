import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.data.Validated.{Invalid, Valid}

object AddValidatedNel {

  def main(args: Array[String]): Unit = {

    // Note: In the case of a validation failure,
    // there should be at least one error message
    val inputV: Validated[List[String], (Int, Int)] = args match {
      case Array(str1, str2) =>
        (parse(str1), parse(str2)) match {
          case (Valid(int1), Valid(int2)) => Valid((int1, int2))

          case (Valid(_), Invalid(error)) => Invalid(List(error))

          case (Invalid(error), Valid(_)) => Invalid(List(error))

          case (Invalid(error1), Invalid(error2)) => Invalid(List(error1, error2))
        }

      case _ => Invalid(List("Exactly two integer inputs expected"))
    }

    inputV match {
      case Valid((int1, int2)) => println(int1 + int2)

      case Invalid(errors) => errors.foreach(println)
    }
  }

  type ParseError = String

  def parse(digits: String): Validated[ParseError, Int] = {
    if (digits.forall(_.isDigit)) Valid(digits.toInt)
    else Invalid(s"Non-digits found in input '$digits'")
  }
}

// TODO - change error type to NonEmptyList
// TODO - change Validated[NonEmptyList...] to ValidatedNel
// TODO - change ValidatedNel to ValidatedNec
