object DemoScala {

  def function3(): Unit = {
    throw new RuntimeException("Boban's Everywhere!")
  }

  def function2(): Unit = {
    function3()
  }

  def function1(): Unit = {
    function2()
  }

  def main(args: Array[String]): Unit = {
    function1()
  }
}
