package animals.utils;

import animals.storage.TreeStorage;

import java.util.Set;
import java.util.function.Function;

public class CommandLineUtils {

    static class Constants {

        private Constants() {
        }
        private static final String KEY_WORD = "-type";
        private static final Set<String> validArgs = Set.of("json", "xml", "yaml");

    }

    public static Function<String[], TreeStorage> handleCommandLineArgs = args -> {
        if (args.length == 2 && args[0].equals(Constants.KEY_WORD) && Constants.validArgs.contains(args[1])) {
            return new TreeStorage(args[1]);
        } else {
            return new TreeStorage("json");
        }
    };


}

