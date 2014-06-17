
import akka.actor.{ActorSystem, Props}
import akka.testkit.DefaultTimeout
import akka.testkit.ImplicitSender
import akka.testkit.TestKit
import scala.concurrent.duration._
import scala.collection.immutable
import akka.testkit.{ImplicitSender, DefaultTimeout, TestKit}
import akka.actor.{Props, ActorSystem}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import scala.util.Random
import org.scalatest.BeforeAndAfterAll
import org.scalatest.WordSpecLike
import org.scalatest.Matchers
import com.typesafe.config.ConfigFactory
import akka.actor.Actor
import akka.actor.ActorRef
import akka.testkit.TestActorRef
import akka.persistence._
import akka.testkit.TestFSMRef
import akka.actor.FSM




class FSMTester extends TestKit(ActorSystem("ActorEventSourcing"))
with DefaultTimeout with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll {

  println("Start Testing FSM................")

  val fsm = TestFSMRef(new GPA_Calulation)
  val mustBeTypedProperly: TestActorRef[GPA_Calulation] = fsm

  assert(fsm.stateName == Excellent, "Start in the Init state")
  assert(fsm.stateData == Uninitialized, "No initial state data")
  fsm ! "Throw"

  assert(fsm.stateName == Excellent, "Start in the Init state")
  //fsm.stateName should be(Excellent)
  //assert(fsm.stateName == None, "Start in the Init state")
  //assert(fsm.stateData == Uninitialized, "No initial state data")

  //fsm ! "sdfads"
  //fsm ! Grade(90, 3)
  //assert(fsm.stateName == Excellent, "Start in the Init state")
 // assert(fsm.stateData == Grade(90,3), "hhh  ")


  //assert(fsm.stateName == Good)
  //assert(fsm.stateData == Uninitialized)
  /*fsm ! "go" // being a TestActorRef, this runs also on the CallingThreadDispatcher
  assert(fsm.stateName == 2)
  assert(fsm.stateData == "go")
  fsm.setState(stateName = 1)
  assert(fsm.stateName == 1)
  assert(fsm.isTimerActive("test") == false)
  fsm.setTimer("test", 12, 10 millis, true)
  assert(fsm.isTimerActive("test") == true)
  fsm.cancelTimer("test")
  assert(fsm.isTimerActive("test") == false)

  actorSndr ! Grade(100, 3)
  actorSndr ! "Request"
  expectMsg(100)
  actorSndr ! Grade(50, 3)
  actorSndr ! "Request"
  expectMsg(75)
  actorSndr ! Grade(90, 3)
  actorSndr ! "Request"
  expectMsg(80)
  actorSndr ! "Throw"
  Thread.sleep(100)
  actorSndr ! Grade(60, 3)
  actorSndr ! "Request"
  expectMsg(75)
*/
}