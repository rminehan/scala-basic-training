import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}

object Double {
  def main(args: Array[String]): Unit = {
    // Generate validation
    val inputV: Validated[String, Int] = ???

    // Either fail with the error,
    // or proceed with the good data
    inputV match {
      ???
    }
  }

  type ParseError = String

  def parse(digits: String): Validated[ParseError, Int] = {
    if (digits.forall(_.isDigit)) Valid(digits.toInt)
    else Invalid(s"Non-digits found in input: '$digits'")
  }
}
