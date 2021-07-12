object AddEither {

  def main(args: Array[String]): Unit = {

    val inputV: Either[String, (Int, Int)] = args match {
      case Array(str1, str2) =>
        for {
          int1 <- parse(str1)
          int2 <- parse(str2)
        } yield (int1, int2)

        // Equivalent to:
        // parse(str1).flatMap { int1 =>
        //   parse(str2).map { int2 =>
        //     (int1, int2)
        //   }
        // }

      case _ => Left("Expected exactly two arguments composed of digits")
    }

    inputV match {
      case Right((int1, int2)) => println(int1 + int2)
      case Left(error) => println(s"Encountered error: $error")
    }
  }

  type ParseError = String

  def parse(digits: String): Either[ParseError, Int] = {
    if (digits.forall(_.isDigit)) Right(digits.toInt)
    else Left(s"Non-digits found in input '$digits'")
  }
}

