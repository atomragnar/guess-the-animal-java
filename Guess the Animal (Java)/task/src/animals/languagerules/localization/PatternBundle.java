package animals.languagerules.localization;

import animals.utils.TriFunction;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class PatternBundle {

    private final ResourceBundle bundle;


    public PatternBundle(Locale locale) {
        bundle = ResourceBundle.getBundle("patterns", locale);
    }

    private String get(String key) {
        return bundle.getString(key);
    }

    private static final TriFunction<String, String, String, String> getReplacement = String::replace;

    public Map<Pattern, String> getMapPatternReplacements(String keyPattern, String target, String replacement) {
        Map<Pattern, String> patternToReplacements = new HashMap<>();
        for (String key : bundle.keySet()) {
            if (key.matches(keyPattern)) {
                patternToReplacements.put(
                        Pattern.compile(bundle.getString(key)),
                        bundle.getString(key.replace(target, replacement))
                );
            }
        }
        return patternToReplacements;
    }


    public Pattern getPattern(String s) {
        return Pattern.compile(get(s));
    }

    public String getString(String key) {
        return get(key);
    }
}
