package serial

import akka.actor._
import scala.concurrent.duration._
import scala.language.postfixOps

import play.api.libs.iteratee._
import play.api.libs.concurrent._

import akka.util.Timeout
import akka.pattern.ask

import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Logger


object SerialActor {

  implicit val timeout = Timeout(1 second)

  val roomActor = Akka.system.actorOf(Props[SerialActor]);



  def attach(): scala.concurrent.Future[(Iteratee[String, _], Enumerator[String])] = {
    // try to add the player to the room
    (roomActor ? Join()).map {
      case Connected(enumerator) =>
        // Create an Iteratee to consume the feed
        val iteratee = Iteratee.foreach[String] {
          event =>
            roomActor ! SendMessage(event);
        }
        (iteratee, enumerator)
    }
  }
}

/**
 * Created by tuxburner on 4/14/14.
 */
class SerialActor extends Actor {

  val (chatEnumerator, chatChannel) = Concurrent.broadcast[String]

  override def receive: Actor.Receive = {
    case Join() => {
      sender ! Connected(chatEnumerator)
    }
    case SendMessage(message: String) => {
      chatChannel.push(message)
    }
  }

}


case class Connected(enumerator: Enumerator[String])

case class SendMessage(message: String);

case class Join()

