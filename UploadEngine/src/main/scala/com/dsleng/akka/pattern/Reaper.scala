package com.dsleng.akka.pattern

import akka.actor.{Actor, ActorRef,ActorLogging, Terminated}
import scala.collection.mutable.ArrayBuffer

object Reaper {
  val name = "reaper"
  // Used by others to register an Actor for watching
  case class WatchMe(ref: ActorRef)
}

class Reaper extends Actor with ActorLogging {
  import Reaper._

  // Keep track of what we're watching
  val watched = ArrayBuffer.empty[ActorRef]

  // Derivations need to implement this method.  It's the
  // hook that's called when everything's dead
  def allSoulsReaped() = {
    println("SYSTEM SHUTDOWN START")
    context.system.terminate()
    println("SYSTEM SHUTDOWN END")
  }

  // Watch and check for termination
  final def receive = {
    case WatchMe(ref) =>
      log.info("Watching: " + sender())
      context.watch(ref)
      watched += ref
    case Terminated(ref) =>
      log.info("Removed: " + sender())
      watched -= ref
      log.info("No watched: " + watched.length)
      if (watched.isEmpty) allSoulsReaped()
  }
}

trait ReaperWatched { this: Actor => 
  override def preStart() {
    println("IN PRESTART" + self)
    context.actorSelection("/user/" + Reaper.name) ! Reaper.WatchMe(self)
  }
}