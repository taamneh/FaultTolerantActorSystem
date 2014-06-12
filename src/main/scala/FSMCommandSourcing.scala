/**
 * Created by Salah on 6/12/2014.
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


sealed trait State
case object Idle extends State
case object Active extends State

sealed trait Data
case object Uninitialized extends Data
case class Datastore (ctr : Int) extends Data


class TestFsm2 extends Processor with FSM[State, Data] {
  startWith(Idle, Uninitialized)
  when(Idle) {
    case Event( Persistent(Begin,_), Uninitialized) =>
      println("We move to Active State")
      goto(Active) using Datastore(0)
    case Event(Persistent(tods(value),_),_) =>
      println("We cant accept your Message now please try later")
      stay()
  }
  // transition elided ...
  when(Active) {
    case Event(Persistent(tods(value),_), t @ Datastore(ctr)) =>
      stay() using  t.copy(ctr = ctr + value)
  }
  whenUnhandled {
    case Event(Persistent(Request,_), s) =>
      sender ! stateData
      stay
    case Event("Throw", _) =>
      throw new Exception_Msgs.Exception_Msg("CounterService not available, lack of initial value")
    case Event(e, s) => println("Nothing happen")
      stay

  }

  override def preRestart(reason: Throwable, message: Option[Any]) {
    message match {
      case Some(p: Persistent) if !recoveryRunning => deleteMessage(p.sequenceNr) // mark failing message as deleted
      case _                                       => // ignore
    }
    super.preRestart(reason, message)
  }
  // unhandled elided ...
  initialize()
}

class sndr2 extends Actor {

  // Stop the CounterService child if it throws ServiceUnavailable

  override val supervisorStrategy = OneForOneStrategy() {
    case _: Exception_Msgs.Exception_Msg => Restart
  }

  def receive = {
    case d @ Datastore(ctr) => println(ctr)
    case _ => println("Stop Working......")
  }

  val test = context.actorOf(Props[TestFsm2],"Test")

  // with command sourcing
  test ! Persistent(Begin)
  test ! Persistent(tods(17))
  test ! Persistent(tods(18))
  test ! Persistent(tods(19))
  test ! Persistent(tods(6))
  test ! Persistent(Request)
  test ! "Throw"
  test ! Persistent(tods(6)) // 6 will be added to the previous save value
  test ! Persistent(Request)

}

object FSMCommandSourcing extends App{
  val system = ActorSystem("Mysystem")
  val act = system.actorOf(Props[sndr2],"Sender")

}
