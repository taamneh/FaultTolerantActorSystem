import akka.actor.ActorSystem
import akka.testkit.{DefaultTimeout, ImplicitSender, TestActorRef, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}


class FSM_Fault_Tolerant_Tester extends TestKit(ActorSystem("ActorEventSourcing"))
with DefaultTimeout with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll {

  println("Start Testing FSM................")
  val actorSndr = TestActorRef[sndr2]
  val actor = actorSndr.underlyingActor

  /*val fsm = TestFSMRef(actorSndr.acto)
  val mustBeTypedProperly: TestActorRef[TestFsmActor] = fsm*/

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

}