import $ivy.`org.typelevel::cats-core:2.6.1`

import cats.data.{NonEmptyList, Validated}
import cats.data.Validated.{Valid, Invalid}
import cats.data.ValidatedNel

type ParseError = String

def parse(digits: String): ValidatedNel[ParseError, Int] = {
  if (digits.forall(_.isDigit)) Valid(digits.toInt)
  else Invalid(NonEmptyList.of(s"Non-digits found in input '$digits'"))
}
