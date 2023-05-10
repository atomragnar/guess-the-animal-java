package animals.ui;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Menu extends TextView implements Runnable {
    private final Map<MenuAction, MenuItem> menuItems = new LinkedHashMap<>();
    private final Set<Integer> validOptions = MenuAction.getValidOptions();
    private final String headline;
    private final String errorMessage;
    private boolean keepRunning = true;

    public Menu(String headline, String errorMessage) {
        this.headline = headline;
        this.errorMessage = errorMessage;
    }

    @Override
    public void run() {

        while (keepRunning) {
            MenuAction currentAction = MenuAction.fromInt(getOption());
            menuItems.get(currentAction).run();
        }


    }

    public Menu add(MenuAction action, String prompt, Runnable actionToRun) {
        menuItems.put(action, new MenuItem(prompt, actionToRun));
        return this;
    }

    @Override
    public String toString() {
        StringBuilder menu = new StringBuilder();
        menu.append(headline).append("\n");
        menuItems.forEach((action, item) -> {
            menu.append(item.toString()).append("\n");
        });
        return menu.toString();
    }

    private int getOption() {
        while (true) {
            try {
                int option = Integer.parseInt(in(toString()));
                if (validOptions.contains(option)) {
                    return option;
                } else {
                    out(errorMessage);
                }
            } catch (NumberFormatException e) {
                out(errorMessage);
            }
        }
    }

    protected Runnable stopRunning() {
        return () -> keepRunning = false;
    }

    static final class MenuItem implements Runnable {
        private final String prompt;
        private final Runnable action;

        MenuItem(String prompt, Runnable action) {
            this.prompt = prompt;
            this.action = action;
        }

        @Override
        public String toString() {
            return prompt;
        }

        @Override
        public void run() {
            action.run();
        }


    }

}

