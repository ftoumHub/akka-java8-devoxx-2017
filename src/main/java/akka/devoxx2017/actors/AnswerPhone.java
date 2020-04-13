package akka.devoxx2017.actors;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;
import akka.devoxx2017.messages.Messages.PhoneMessage;
import io.vavr.Tuple2;
import io.vavr.collection.List;

import static akka.devoxx2017.messages.Messages.NoMessage;
import static io.vavr.collection.List.empty;

public class AnswerPhone extends AbstractLoggingActor {

    private List<PhoneMessage> messages = empty();

    public static GiveMeLastMessage GiveMeLastMessage = new GiveMeLastMessage();

    public static LeaveAMessage LeaveAMessage(PhoneMessage phoneMessage) {
        return new LeaveAMessage(phoneMessage);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(LeaveAMessage.class, msg -> {
                    messages = messages.append(msg.message);
                })
                .match(GiveMeLastMessage.class, msg -> {
                    if(!messages.isEmpty()) {
                        Tuple2<PhoneMessage, List<PhoneMessage>> pair = messages.pop2();
                        messages = pair._2;
                        sender().tell(pair._1, self());
                    } else {
                        sender().tell(NoMessage, self());
                    }
                })
                .build();
    }

    public static Props props() {
        return Props.create(AnswerPhone.class);
    }

    public static class LeaveAMessage {
        public final PhoneMessage message;

        public LeaveAMessage(PhoneMessage message) {
            this.message = message;
        }
    }

    public static class GiveMeLastMessage {}

}
