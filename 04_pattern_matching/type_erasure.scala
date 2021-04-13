object Demo {
  def main(args: Array[String]): Unit = {
    // Will print "0" three times...
    println(getInt(List(0, 1, 2)))
    println(getInt(List("Abc", "Def")))
    println(getInt(List(1.0, 2.0, 3.0)))
  }

  def getInt(any: Any): Int = any match {
    case _: List[Int] => 0
    case _: List[String] => 1
    case _: List[Double] => 2
    case _ => 3
  }

}
