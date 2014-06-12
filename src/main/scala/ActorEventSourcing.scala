/**
 * Created by Salah on 6/10/2014.
 */

import akka.actor._
import akka.actor.SupervisorStrategy._
import com.typesafe.config.ConfigFactory
import akka.event.LoggingReceive
import akka.pattern.{ ask, pipe }
import scala.collection.parallel.immutable
import scala.collection._
import scala.concurrent.duration._
import akka.persistence._


object Exception_Msgs
{
  class Exception_Msg(msg: String) extends RuntimeException(msg)
}
case class tods(value: Int)
case object Begin
case object Request

case class Evt (sal: Int)
case class Cmd (step: Int)


class rsvr extends EventsourcedProcessor {
  //context.setReceiveTimeout(10 seconds)

  println("Intilaization")
  var counter  =0;
  def updateState(event: Evt): Unit =
    counter = event.sal

  val receiveRecover: Receive = {
    case evt: Evt                                 => updateState(evt)
    //case SnapshotOffer(_, snapshot: ExampleState) => state = snapshot
  }
  val receiveCommand: Receive = {
    case a @ Cmd(step) =>
      //TODO switch to immutable
      counter = counter + a.step
      println("AAAA " + a.step + "   " + counter)
      persist(Evt(counter)) {
        event => updateState(event)
          context.system.eventStream.publish(event)}
      // the message the cause the error will not be persisted..
      if(counter.equals(20) ) {
        sender() ! "Bye"
        throw new Exception_Msgs.Exception_Msg("CounterService not available, lack of initial value")
      }
      else if(counter > 40)
      {
        sender () ! "Stop"
      }
      else
      {
        println(counter + " " )
        sender () ! "Again"
      }
    /*case ReceiveTimeout =>
      context.setReceiveTimeout(Duration.Undefined)
      println("Done")*/
    //throw new RuntimeException("Receive timed out")

    case _ => sender() ! println("I do not understand")
  }
}
class sndr extends Actor {

  // Stop the CounterService child if it throws ServiceUnavailable
  val child = context.actorOf(Props[rsvr],"EventSorucer")

  override val supervisorStrategy = OneForOneStrategy() {
    case _: Exception_Msgs.Exception_Msg => Restart
  }
  // increase by one
  child ! Cmd(step = 1)
  def receive = {
    case "Again" => child ! Cmd(step = 1)
    case "Bye" => child ! Cmd(step = 2) // This shows how the events will be restored after restart.....
    case _ => println("Stop Working......")
  }
}
object ActorEventSourcing extends App {

  val system = ActorSystem("Mysystem")
  val act = system.actorOf(Props[sndr],"Sender")


  //Thread.sleep(500)
  //system.shutdown()
  //context.system.scheduler.schedule(Duration.Zero, 1 second, self, Do)

}

/*
override val supervisorStrategy =
OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
case _: ArithmeticException => Resume
case _: NullPointerException => Restart
case _: IllegalArgumentException => Stop
case _: Exception => Escalate
}

Just to check that ...

 */