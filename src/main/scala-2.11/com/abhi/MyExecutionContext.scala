package com.abhi

import java.util.concurrent.{Executors, ThreadFactory}
import java.util.concurrent.atomic.AtomicLong
import scala.concurrent.ExecutionContext

/**
  * Created by abhsrivastava on 8/12/16.
  */
object MyExecutionContext {
   implicit val myEc = new ExecutionContext {
      val threadPool = Executors.newCachedThreadPool(new ThreadFactory{
         private val counter = new AtomicLong(0L)
         def newThread(r: Runnable) = {
            val thread = new Thread(r)
            thread.setName("abhishek-" + counter.getAndIncrement().toString)
            thread.setDaemon(true)
            thread
         }
      })
      def execute(runnable: Runnable): Unit = {
         println("runnign abhishek thread")
         threadPool.submit(runnable)
      }
      def reportFailure(t : Throwable): Unit = {
         println("my thread pool has error")
      }
   }
}
