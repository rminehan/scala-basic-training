import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}

object Double {
  def main(args: Array[String]): Unit = {

    val inputV: Validated[String, Int] = args match {
      case Array(digits) => parse(digits)
      case _ => Invalid("Invalid args passed. Exactly one arg of digits is expected")
    }

    inputV match {
      case Valid(int) => println(int * 2)
      case Invalid(error) => println(error)
    }

  }

  type ParseError = String

  def parse(digits: String): Validated[ParseError, Int] = {
    if (digits.forall(_.isDigit)) Valid(digits.toInt)
    else Invalid(s"Input invalid. '$digits' Non-digits found in input string")
  }
}

