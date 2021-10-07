object ForeachDemo {
  def main(args: Array[String]): Unit = {
    args.foreach { name =>
        if (name.toLowerCase == "boban") throw new Exception("No bobans!")
        else println(name)
      }
  }
}
