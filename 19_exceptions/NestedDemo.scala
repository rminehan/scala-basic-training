object NestedDemo {
  def function1(): Unit = {
    try {
      Lib.doDangerousStuff()
    }
    catch {
      case ex: Exception =>
        throw new Exception("Our dangerous stuff failed", ex)
    }
  }

  def main(args: Array[String]): Unit = {
    function1()
  }
}

object Lib {
  def doDangerousStuff(): Unit = {
    throw new Exception("Boban encountered")
  }
}
