package com.abhi
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

object FutureNesting extends App {


   def measureSingle(future: => Future[Unit]) : Future[Long] = {
      val start = System.currentTimeMillis
      future map { _ =>
         val end = System.currentTimeMillis
         end - start
      }
   }

   def measureNested(future: => Future[Unit]) : Future[Long] = {
      val future2 = Future {
         println("going to sleep")
         Thread.sleep(10000)
         println("wake up")
         Future {
            println("going to sleep")
            Thread.sleep(10000)
            println("wake up")
            Future {
               println("going to sleep")
               Thread.sleep(10000)
               println("wakeup")
               measureSingle(future)
            }
         }
      }
      future2 flatMap { case x1 =>
         x1 flatMap {case x2 =>
            x2 flatMap { case x3 =>
               x3
            }
         }
      }
   }

   args(0).toInt match {
      case 1 =>
         scala.io.StdIn.readLine()
         lazy val x1 = Future{ println("+++ executing Future"); Thread.sleep(10000); println("woke up"); }
         x1 onSuccess {case _ => println("completed with direct invocation to future")}
         scala.io.StdIn.readLine()
      case 2 =>
         scala.io.StdIn.readLine()
         lazy val x2 = Future{ println("+++ executing Future"); Thread.sleep(10000); println("woke up"); }
         measureSingle(x2) map {case t => println(s"time taken $t"); println ("completed with function wrapping")}
         scala.io.StdIn.readLine()
      case 3 =>
         scala.io.StdIn.readLine()
         lazy val x3 = Future{ println("+++ executing Future"); Thread.sleep(10000); println("woke up"); }
         measureNested(x3) map {case t => println(s"time taken $t"); println("completed with future nesting")}
         scala.io.StdIn.readLine()
   }
}