package akka.devoxx2017.actors;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.devoxx2017.messages.Messages;
import akka.devoxx2017.messages.Messages.NoMessage;
import akka.devoxx2017.messages.Messages.PhoneMessage;
import scala.concurrent.duration.FiniteDuration;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static akka.actor.Props.create;
import static akka.devoxx2017.actors.AnswerPhone.GiveMeLastMessage;
import static akka.devoxx2017.actors.Director.IAmIn;

public class BillMurray extends AbstractLoggingActor {

    private final ActorRef answerPhone;

    private Random random = new Random();

    public static Props props(ActorRef answerPhone) {
        return create(BillMurray.class, () -> new BillMurray(answerPhone));
    }

    private BillMurray(ActorRef answerPhone) {
        this.answerPhone = answerPhone;
    }

    @Override
    public Receive createReceive() {
        return nextBehavior();
    }

    private Receive happy() {
        return receiveBuilder()
                .match(PhoneMessage.class, msg -> {
                    log().info("I have a new message: {}", msg.message);
                    msg.number.tell(IAmIn, self());
                    getContext().become(nextBehavior());
                    this.answerPhone.tell(GiveMeLastMessage, self());
                })
                .match(NoMessage.class, this::handleNoMessage)
                .build();
    }


    private Receive grouchy() {
        return receiveBuilder()
                .match(PhoneMessage.class, msg -> {
                    log().info("I have a new message: {}", msg.message);
                    getContext().become(nextBehavior());
                    this.answerPhone.tell(GiveMeLastMessage, self());
                })
                .match(NoMessage.class, this::handleNoMessage)
                .build();
    }

    private Receive provocative() {
        return receiveBuilder()
                .match(PhoneMessage.class, msg -> {
                    log().info("I have a new message: {}", msg.message);
                    msg.number.tell(Director.FuckYou, self());
                    getContext().become(nextBehavior());
                    this.answerPhone.tell(GiveMeLastMessage, self());
                })
                .match(NoMessage.class, this::handleNoMessage)
                .build();
    }

    private Receive nextBehavior() {
        int i = random.nextInt(3);
        if(i == 0) {
            log().info("Becoming happy");
            return happy();
        } else if (i == 1) {
            log().info("Becoming grouchy");
            return grouchy();
        } else {
            log().info("Becoming provocative");
            return provocative();
        }
    }

    private void handleNoMessage(NoMessage noMessage) {
        context().system().scheduler().scheduleOnce(
                FiniteDuration.create(1, TimeUnit.SECONDS),
                this.answerPhone,
                GiveMeLastMessage,
                context().dispatcher(),
                self()
        );
    }

    @Override
    public void preStart() throws Exception {
        context().system().scheduler().scheduleOnce(
                FiniteDuration.create(1, TimeUnit.SECONDS),
                this.answerPhone,
                GiveMeLastMessage,
                context().dispatcher(),
                self()
        );
    }

}
