package animals.languagerules;

import animals.utils.QuadFunction;
import animals.utils.TriFunction;
import animals.languagerules.localization.MessageBundle;
import animals.languagerules.localization.PatternBundle;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static animals.languagerules.AnimalNLPService.StringFormatter.patterns;

public class AnimalNLPService {

    private final MessageBundle messages;
    private final String DELIMITER = "\\|";


    private static final BiFunction<String, Pattern, ValidationResult> validate = (input, pattern) ->
            pattern.matcher(input).find() ? ValidationResult.VALID : ValidationResult.INVALID;



    public AnimalNLPService(Locale locale) {
        this.messages = new MessageBundle(locale);
    }

    public String getString(String key) {
        return switch (key) {
            case "hi" -> messages.getGreeting();
            case "cheer" -> messages.getRandomMessage("cheerfulExpression", DELIMITER);
            case "clarification" -> messages.getRandomMessage("clarification", DELIMITER);
            case "bye" -> messages.getRandomMessage("goodbye", DELIMITER);
            case "playAgain" -> messages.getRandomMessage("playAgain", DELIMITER);
            case "gameThanks" -> messages.getRandomMessage("gameThanks", DELIMITER);
            default -> messages.getMessage(key);
        };
    }

    public ValidationResult validate(String input, String key) {
        return validate.apply(input, patterns.getPattern(key));
    }

    public static class StringFormatter  {

        protected static final PatternBundle patterns;
        private static final Map<Pattern, String> NEGATIVE_STATEMENT_PATTERNS;
        private static final Map<Pattern, String> QUESTION_PATTERNS;
        static {
                patterns = new PatternBundle(Locale.getDefault());
                NEGATIVE_STATEMENT_PATTERNS = patterns.getMapPatternReplacements(
                        "^fact\\.\\d+\\.pattern",
                        ".pattern",
                        ".neg.replace");
                QUESTION_PATTERNS = patterns.getMapPatternReplacements(
                        "^fact\\.\\d+\\.pattern",
                        ".pattern",
                        ".q.replace");
        }

        /*
                Methods fetching from PatternBundle
         */

        public static Map<Pattern, String> getQuestionPatterns() {
            return QUESTION_PATTERNS;
        }

        public static Map<Pattern, String> getNegativeStatementPatterns() {
            return NEGATIVE_STATEMENT_PATTERNS;
        }

        private static Matcher getMatcher(String input, String patternKey) {
            return patterns.getPattern(patternKey).matcher(input);
        }

        private static Pattern getPattern(String key) {
            return patterns.getPattern(key);
        }

        private static String getString(String key) {
            return patterns.getString(key);
        }

        private static String getArticle(int n) {
            return n == 1
                    ? getPattern("article.undefined.1.replace").toString()
                    : getPattern("article.undefined.2.replace").toString();
        }

        /*
                String formatting utility methods
         */

        private static final Predicate<String> isVowels = str -> getMatcher(str.substring(0, 1), "pattern.vowels").matches();
        private static final UnaryOperator<String> lowerCase = String::toLowerCase;
        private static final TriFunction<String, String, String, String> getReplacement = String::replaceAll;
        public static UnaryOperator<String> capitalizeFirstLetter = input -> input.substring(0, 1).toUpperCase() + input.substring(1);
        public static final UnaryOperator<String> UNDEFINED_ARTICLE = str -> isVowels.test(str)
                ? getArticle(1).formatted(str)
                : getArticle(2).formatted(str);

        public static final UnaryOperator<String> DEFINITE_ARTICLE = input -> getString("article.definite.insert").formatted(input);
        public static final UnaryOperator<String> REPLACE_PRONOUN = input -> getMatcher(input, "singular.pronoun").replaceAll("");
        private static final UnaryOperator<String> format = String::formatted;
        private static final BiFunction<String, String, String> biFormat = String::formatted;
        private static final TriFunction<String, String, Boolean, String> triFormat = String::formatted;
        public static final QuadFunction<String, String, String, String, String> quadFormat = String::formatted;


        /*
                Helper methods for validation and formatting
         */

        private static boolean isPositive(String input) {
            return getMatcher(input, "answer.positive").find();
        }

        public static Predicate<String> isPositiveBinary() {
            return StringFormatter::isPositive;
        }

        /*
                Helper method for formatting statements and questions
         */

       /* private static String replace(String target, Map<Pattern, String> patternToReplacements) {
            return patternToReplacements.entrySet().stream()
                    .filter(entry -> entry.getKey().matcher(target).find())
                    .map(entry -> getReplacement.apply(REPLACE_PRONOUN.apply(target), entry.getKey().pattern(), entry.getValue()))
                    .findFirst()
                    .orElse(target);
        }*/

        private static String replace(String target, Map<Pattern, String> patternStringMap) {
            Optional<Map.Entry<Pattern, String>> entry = patternStringMap.entrySet().stream()
                    .filter(e -> e.getKey().matcher(target).find())
                    .findFirst();
            return entry.isPresent()
                    ? getReplacement.apply(REPLACE_PRONOUN.apply(target), entry.get().getKey().pattern(), entry.get().getValue())
                    : target;
        }

        /*
                Formatting questions
         */

        public static UnaryOperator<String> QUESTION = input -> capitalizeFirstLetter.apply(
                replace(lowerCase.apply(REPLACE_PRONOUN.apply(input))
                , QUESTION_PATTERNS) + "?"
        );

        /*
                Formatting statements
         */


        private static final UnaryOperator<String> negativeStatement = input -> replace(lowerCase.apply(input), NEGATIVE_STATEMENT_PATTERNS);

        public static TriFunction<String, String, Boolean, String> ANIMAL_STATEMENT = (animal, fact, isTrue) ->
                DEFINITE_ARTICLE.apply(animal) + " " + (isTrue
                       ? REPLACE_PRONOUN.apply(fact) : REPLACE_PRONOUN.apply(negativeStatement.apply(fact)));

        public static final BiFunction<String, Boolean, String> FACT_STATEMENT = (fact, isTrue)  ->
                isTrue
                        ? capitalizeFirstLetter.apply(fact)
                        : capitalizeFirstLetter.apply(negativeStatement.apply(fact));

        /*
                Prompt the user for a distinct fact separating the two animals
         */

        public static final TriFunction<String, String, String, String> ASK_FOR_DISTINCT_FACT = (prompt, a, b) -> prompt
                .formatted(UNDEFINED_ARTICLE.apply(a), UNDEFINED_ARTICLE.apply(b));



         /*
               Function for displaying animal search results
         */

        private static final Function<List<Map.Entry<String, Boolean>>, List<String>> FACTS_ABOUT_ANIMAL_LIST = facts ->
                facts.stream().map(f ->
                        ("- " + (FACT_STATEMENT.apply(f.getKey(), f.getValue()) + "."))
                ).toList();

        public static final QuadFunction<Optional<List<Map.Entry<String, Boolean>>>, String, String, String, String> FACTS_ABOUT_ANIMAL =
                (facts, animal, prompt, notFound) -> facts.map(entries ->
                                prompt
                                        .formatted(DEFINITE_ARTICLE.apply(animal), String.join("\n", FACTS_ABOUT_ANIMAL_LIST.apply(entries))))
                        .orElseGet(() -> notFound.formatted(animal));

        /*
               Function for printing the tree stats
         */
        public static final QuadFunction<String, int[], Double, String, String> TREE_STATS = (statement, stats, depth, prompt) ->
                prompt.formatted(
                        statement,
                        stats[0],
                        stats[1],
                        stats[2],
                        stats[3],
                        stats[4],
                        depth
                );

        /*
                Method for formatting prompt displaying the learned facts about the animals
         */


        public static final QuadFunction<String, String, String, String, String> LEARNED_FACT = (rightChild, leftChild, fact, prompt) -> prompt
                .formatted(
                        ANIMAL_STATEMENT.apply(rightChild, fact, true),
                        ANIMAL_STATEMENT.apply(leftChild, fact, false),
                        QUESTION.apply(fact)
                );


    }




}
