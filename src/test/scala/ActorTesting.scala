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


class ActorTesting extends TestKit(ActorSystem("ActorEventSourcing"))
with DefaultTimeout with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll {

  println("Start Testing................")
  val actorSndr = TestActorRef[sndr]
  val actor = actorSndr.underlyingActor

  actorSndr ! "Add5"
  actorSndr ! "Add5"
  actorSndr ! "Add5"
  actorSndr ! "Add5"
  expectNoMsg
  println("Restarting the actor counter became 20")
  actorSndr ! "Add6"
  actorSndr ! "Request"
  expectMsg(21)

}