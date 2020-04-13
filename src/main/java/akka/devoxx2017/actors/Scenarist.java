package akka.devoxx2017.actors;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;
import akka.devoxx2017.utils.ScenariiGenerator;

import static akka.actor.Props.create;
import static akka.devoxx2017.utils.ScenariiGenerator.nextScenario;

public class Scenarist extends AbstractLoggingActor {

    public static CreateScenario CreateScenario = new CreateScenario();

    public static Props props() {
        return Props.create(Scenarist.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(CreateScenario.class, msg -> {
                    String scenario = nextScenario();
                    log().info("I wrote a new scenario: {}", scenario);
                    sender().tell(Director.Scenario(scenario), self());
                })
                .build();
    }

    public static class CreateScenario {}
}
