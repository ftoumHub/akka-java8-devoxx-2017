package akka.devoxx2017;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.devoxx2017.actors.AnswerPhone;
import akka.devoxx2017.actors.BillMurray;
import akka.devoxx2017.actors.Producer;
import akka.devoxx2017.actors.Scenarist;
import akka.devoxx2017.messages.Messages;
import akka.devoxx2017.messages.Messages.AMovie;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import java.util.List;
import java.util.concurrent.CompletionStage;

import static akka.devoxx2017.actors.BillMurray.props;
import static akka.devoxx2017.actors.Producer.CreateMovie;
import static akka.devoxx2017.actors.Producer.props;
import static akka.pattern.PatternsCS.ask;
import static akka.stream.ActorMaterializer.create;
import static akka.stream.javadsl.Sink.seq;
import static akka.stream.javadsl.Source.range;
import static java.lang.System.out;

public class ApplicationMain {

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("MyActorSystem");

        ActorRef scenarist = system.actorOf(Scenarist.props(), "scenarist");
        ActorRef answerPhone = system.actorOf(AnswerPhone.props(), "answerPhone");
        system.actorOf(props(answerPhone), "billMurray");

        ActorRef producer = system.actorOf(props(scenarist, answerPhone), "producer");

        CompletionStage<List<AMovie>> movies = range(0, 20)
                .mapAsyncUnordered(10,
                        i -> ask(producer, CreateMovie, 5000)
                                .thenApply(AMovie.class::cast)
                )
                .runWith(seq(), create(system));

        movies.whenComplete((l, e ) ->
            io.vavr.collection.List.ofAll(l).forEach(m ->
                    out.println(m.actor + " in " + m.scenario))
        );
    }

}