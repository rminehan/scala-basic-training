import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.data.Validated.{Invalid, Valid}

object AddValidatedNel {

  def main(args: Array[String]): Unit = {
    // Note how errors are modelled with a NonEmptyList
    // This is just an alias for Validated[NonEmptyList[String], (Int, Int)]
    // Could also use NonEmptyChain/ValidatedNec
    val inputV: ValidatedNel[String, (Int, Int)] = args match {
      case Array(str1, str2) =>
        (parse(str1), parse(str2)) match {
          case (Valid(int1), Valid(int2)) => Valid((int1, int2))

          case (Valid(_), Invalid(error)) => Invalid(NonEmptyList.one(error))

          case (Invalid(error), Valid(_)) => Invalid(NonEmptyList.one(error))

          case (Invalid(error1), Invalid(error2)) => Invalid(NonEmptyList.of(error1, error2))
        }

      case _ => Invalid(NonEmptyList.of("Exactly two integer parameters expected"))
    }

    inputV match {
      case Valid((int1, int2)) => println(int1 + int2)

      case Invalid(errorsNel) => errorsNel.toList.foreach(println)
    }
  }

  type ParseError = String

  def parse(digits: String): Validated[ParseError, Int] = {
    if (digits.forall(_.isDigit)) Valid(digits.toInt)
    else Invalid(s"Non-digits found in input '$digits'")
  }
}
