package animals.languagerules;

import java.util.function.BiPredicate;

public class ValidationHelper {

    @FunctionalInterface
    public interface TriPredicate<T, U, V> {
        boolean test(T t, U u, V v);
    }

    protected static final BiPredicate<String, String> prefix = String::startsWith;
    protected static final BiPredicate<String, String> suffix = String::endsWith;
    protected static final TriPredicate<String, String, String> endsWithEither = (input, first, second) -> suffix.test(input, first) || suffix.test(input, second);


}
