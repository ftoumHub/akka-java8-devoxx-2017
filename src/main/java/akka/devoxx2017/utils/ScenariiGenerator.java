package akka.devoxx2017.utils;

import javaslang.collection.List;

import java.util.Random;

import static javaslang.collection.List.of;

public class ScenariiGenerator {

    private static List<String> list1 = of("Un taxi", "Un chinois", "Un yamakazi");
    private static List<String> list2 = of("protege", "pête la gueule à");
    private static List<String> list3 = of("un flic", "un yamakazi", "une pute");
    private static List<String> list4 = of("en banlieu", "en audi");

    private static Random random = new Random();

    public static String nextScenario() {
        return list1.get(random.nextInt(3))
                + " "
                + list2.get(random.nextInt(2))
                + " "
                + list3.get(random.nextInt(3))
                + " "
                + list4.get(random.nextInt(2));
    }

}
