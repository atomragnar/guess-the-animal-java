package animals.ui;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum MenuAction {

    GUESSING_GAME(1),
    LIST_ANIMALS(2),
    SEARCH_ANIMAL(3),
    CALCULATE_STATS(4),
    PRINT_KNOWLEDGE_TREE(5),
    EXIT(0);

    private final int n;

    MenuAction(int n) {
        this.n = n;
    }

    public static MenuAction fromInt(int n) {
        for (MenuAction action : MenuAction.values()) {
            if (action.n == n) {
                return action;
            }
        }
        throw new IllegalArgumentException("No such game mode");
    }

    public static Set<Integer> getValidOptions() {
        return Arrays.stream(MenuAction.values()).map(action -> action.n).collect(Collectors.toSet());
    }

    public String getN() {
        return String.valueOf(n);
    }


}
