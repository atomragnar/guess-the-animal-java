package animals.utils;

import java.util.List;
import java.util.Random;

public class RandomIndexGenerator {
    public static <T> int getRandomIndex(List<T> list) {
        Random random = new Random();
        return random.nextInt(list.size());
    }

    public static <T> int getRandomIndex(T[] array) {
        Random random = new Random();
        return random.nextInt(array.length);
    }
}