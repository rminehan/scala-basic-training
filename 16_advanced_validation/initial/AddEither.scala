object AddEither {

  def main(args: Array[String]): Unit = {
    // Expect two argument that parse to Ints
    val inputV: Either[String, (Int, Int)] = ???

    inputV match {
      case Right((left, right)) => println(left + right)
      case Left(error) => println(s"Encountered error: $error")
    }
  }

  type ParseError = String

  // Rewritten to use Either
  def parse(digits: String): Either[ParseError, Int] = {
    if (digits.forall(_.isDigit)) Right(digits.toInt)
    else Left(s"Non-digits found in input '$digits'")
  }
}
