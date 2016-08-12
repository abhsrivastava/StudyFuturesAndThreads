package com.abhi
import scala.concurrent._

object FutureNesting extends App {


   def createFuture : Future[Unit] = {
      import scala.concurrent.ExecutionContext.Implicits.global
      Future {
         Thread.sleep(10000)
         println("+++ executed Future ++++")
      }
   }

   def testOnComplete(f: => Future[Unit])(implicit ec: ExecutionContext) : Unit = {
      f.onComplete{_ => Thread.sleep(1000); println("oncomplete1");}
      f.onComplete{_ =>  Thread.sleep(1000); println("oncomplete2");}
      f.onComplete{_ =>  Thread.sleep(1000); println("oncomplete3");}
      f.onComplete{_ =>  Thread.sleep(1000); println("oncomplete4");}
      f.onComplete{_ =>  Thread.sleep(1000); println("oncomplete5");}
      f.onComplete{_ =>  Thread.sleep(1000); println("oncomplete6");}
      f.onComplete{_ =>  Thread.sleep(1000); println("oncomplete7");}
      f.onComplete{_ =>  Thread.sleep(1000); println("oncomplete8");}
      f
   }

   def measureSingle(future: => Future[Unit])(implicit ec: ExecutionContext) : Future[Long] = {
      val start = System.currentTimeMillis
      future map  { _ =>
         val end = System.currentTimeMillis
         end - start
      }
   }

   def measureNested(future: => Future[Unit])(implicit ec: ExecutionContext) : Future[Long] = {
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
         lazy val x1 = createFuture
         import MyExecutionContext._
         x1 onSuccess {case _ => println("completed with direct invocation to future")}
         scala.io.StdIn.readLine()
      case 2 =>
         scala.io.StdIn.readLine()
         lazy val x2 = createFuture
         import MyExecutionContext._
         measureSingle(x2)(myEc) map {case t => println(s"time taken $t"); println ("completed with function wrapping")}
         scala.io.StdIn.readLine()
      case 3 =>
         scala.io.StdIn.readLine()
         lazy val x3 = createFuture
         import MyExecutionContext._
         measureNested(x3)(myEc) map {case t => println(s"time taken $t"); println("completed with future nesting")}
         scala.io.StdIn.readLine()
      case 4 =>
         scala.io.StdIn.readLine()
         lazy val x4 = createFuture
         import MyExecutionContext._
         testOnComplete(x4)(myEc)
         scala.io.StdIn.readLine()
   }
}