package akka.devoxx2017.actors;

import akka.actor.*;
import akka.devoxx2017.actors.Director.MovieException;
import scala.concurrent.duration.Duration;

import static scala.concurrent.duration.Duration.Zero;

public class Producer extends AbstractLoggingActor {

    private final ActorRef scenarist;
    private final ActorRef billMurrayAnswerPhone;

    public static CreateMovie CreateMovie = new CreateMovie();

    public static Props props(ActorRef scenarist, ActorRef billMurrayAnswerPhone) {
        return Props.create(Producer.class, () -> new Producer(scenarist, billMurrayAnswerPhone));
    }

    private Producer(ActorRef scenarist, ActorRef billMurrayAnswerPhone) {
        this.scenarist = scenarist;
        this.billMurrayAnswerPhone = billMurrayAnswerPhone;
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return new OneForOneStrategy(1, Zero(), e -> {
            if (e instanceof MovieException) {
                log().error(e, "Arggg");
                return SupervisorStrategy.restart();
            } else {
                return SupervisorStrategy.stop();
            }
        });
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(CreateMovie.class, msg -> {
                    ActorRef movie = context().actorOf(Director.props(sender(), scenarist, billMurrayAnswerPhone));
                    context().watch(movie);
                })
                .match(Terminated.class, msg -> {
                    log().info("movie terminated");
                })
                .build();
    }

    public static class CreateMovie {}
}
