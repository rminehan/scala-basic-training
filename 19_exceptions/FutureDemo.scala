import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object FutureDemo {
  def doWork(): Int = {
    // Do some hard work
    // ...
    Thread.currentThread().getStackTrace().foreach(println)
    3
  }

  def main(args: Array[String]): Unit = {
    val future = Future(doWork())

    Await.result(future, 5.seconds)
  }
}
