object RedHerringDemo {

  def main(args: Array[String]): Unit = {
    try {
      println(Lib.computeRatio(args.head.toInt))
    }
    catch {
      case ex: Exception =>
        throw new Exception("User error. Input is too large for library", ex)
    }
  }
}

object Lib {
  /** Throws an exception if the input is too big */
  def computeRatio(i: Int): Int = {
    if (i > 100)
      throw new Exception(s"Input is too big: $i")

    100/i
  }
}
