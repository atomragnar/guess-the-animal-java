package animals.languagerules.localization;

import animals.utils.RandomIndexGenerator;

import java.time.LocalTime;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.BiFunction;

public class MessageBundle {

    private final ResourceBundle bundle;

    public MessageBundle(Locale locale) {
        bundle = ResourceBundle.getBundle("messages", locale);
    }

    private static final BiFunction<Integer, ResourceBundle, String> greeting = (hour, bundle) -> switch (hour) {
        case 5, 6, 7, 8, 9, 10, 11 -> bundle.getString("greeting.morning");
        case 12, 13, 14, 15, 16, 17 -> bundle.getString("greeting.afternoon");
        case 18, 19, 20, 21 -> bundle.getString("greeting.evening");
        default -> {
            yield hour >= 22 ? bundle.getString("greeting.night") : bundle.getString("greeting.early");
        }
    };

    private String get(String key) {
        return bundle.getString(key);
    }

    public String getMessage(String key) {
        return bundle.getString(key);
    }

    public String getGreeting() {
        return greeting.apply(LocalTime.now().getHour(), bundle);
    }

    public String getRandomMessage(String type, String delimiter) {
        String[] randomMessages = get(type + ".messages").split(delimiter);
        return randomMessages[RandomIndexGenerator.getRandomIndex(randomMessages)];

    }

}
