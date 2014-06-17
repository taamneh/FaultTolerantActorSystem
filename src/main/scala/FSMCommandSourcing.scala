/**
 * Created by Salah on 6/12/2014.
 */
import akka.actor._
import akka.actor.SupervisorStrategy._
import akka.pattern.{ ask, pipe }
import scala.collection._
import akka.persistence._
import scala.concurrent.{Future, Await}
import akka.util.Timeout
import scala.concurrent.duration._


//
sealed trait State
case object None extends State
case object Poor extends State
case object Satisfactory extends State
case object Good extends State
case object Excellent extends State
case object Idle extends State
case object Active extends State

sealed trait Data
case object Uninitialized extends Data
case class Datastore(ctr : Int) extends Data
case class GPA (ctr : Double, tot_hr: Int) extends Data
case class Grade (grd : Int, hr : Int) extends Data


class GPA_Calulation extends Processor with FSM[State, Data] {

  def Rank(cls: Any)= {
    cls match {
      case x: Int if(x >=90 )  => Excellent
      case x: Int if(x >=80 )  => Good
      case x: Int if(x >=70 )  => Satisfactory
      case _  => Poor
    }
  }
  startWith(Excellent, Uninitialized)
  when(None) {
    case Event(Persistent(Grade(grd, hr),_),_) =>
      println("What")
      goto(Rank(grd)) using GPA(ctr = grd, tot_hr = hr)
  }
   when(Excellent) {
      case Event(Persistent( Grade(grd, hr),_),t @ GPA(ctr, tot_hr)) =>
      goto(Rank(grd)) using t.copy(ctr = (ctr*tot_hr + grd*hr)/(hr+tot_hr),tot_hr+hr )

  }
  when(Good) {
    case Event(Persistent( Grade(grd, hr),_),t @ GPA(ctr, tot_hr)) =>
      goto(Rank(grd)) using t.copy(ctr = (ctr*tot_hr + grd*hr)/(hr+tot_hr),tot_hr+hr )
  }
  when(Satisfactory) {
    case Event(Persistent( Grade(grd, hr),_),t @ GPA(ctr, tot_hr)) =>
      goto(Rank(grd)) using t.copy(ctr = (ctr*tot_hr + grd*hr)/(hr+tot_hr),tot_hr+hr )
  }
  when(Poor) {
      case Event(Persistent( Grade(grd, hr),_),t @ GPA(ctr, tot_hr)) =>
      goto(Rank(grd)) using t.copy(ctr = (ctr*tot_hr + grd*hr)/(hr+tot_hr),tot_hr+hr )
  }
  whenUnhandled {
    case Event("Request", s) =>
      sender ! stateData
      stay
    case Event("Throw", _) =>
      throw new Exception_Msgs.Exception_Msg("Exception..We are going to restart")
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
  initialize()


}

class sndr2 extends Actor {

  // Stop the CounterService child if it throws ServiceUnavailable

  override val supervisorStrategy = OneForOneStrategy() {
    case _: Exception_Msgs.Exception_Msg => Restart
  }

  val test = context.actorOf(Props[GPA_Calulation],"Test")
  def receive = {
    case Grade(grd, hr)=>  test ! Persistent(Grade(grd, hr))
    case "Request" =>
      implicit val timeout = Timeout(5 seconds)
      val future: Future[Any] = test.ask("Request")(5 seconds) // enabled by the “ask” import
      val result = Await.result(future, timeout.duration).asInstanceOf[GPA]
      println(result.ctr)
      sender() ! result.ctr
    case "Throw" => test ! "Throw"
    case _ => println("Stop Working......")
  }
}
object FSMCommandSourcing extends App{
  val system = ActorSystem("Mysystem")
  val act = system.actorOf(Props[sndr2],"Sender")
}
