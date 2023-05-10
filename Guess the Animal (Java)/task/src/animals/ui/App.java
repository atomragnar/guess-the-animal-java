package animals.ui;

import animals.languagerules.AnimalNLPService;
import animals.tree.AnimalFactTree;
import animals.storage.TreeStorage;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class App extends TextView implements Runnable {

    private final AnimalFactTree tree = new AnimalFactTree();
    private final TreeStorage storage;
    private final Menu menu;
    private TreeState treeState;
    private final Runnable printTree = () -> out(tree.toString());
    private final Runnable printStats = () -> out(tree.getAnimalTreeStats(getString("tree.stats")) + "\n");
    private final Runnable printNoAnimalsFound = () -> out(getString("tree.list.noAnimals") + "\n");
    private final Consumer<List<String>> printAnimalsList = animals -> {
        out(getString("tree.list.animals.headline") + "\n");
        animals.forEach(animal -> out(getString("tree.list.animals.animal").formatted(animal) + "\n"));
    };
    private final Consumer<List<String>> printAnimalsHandler = animals -> {
        if (animals.isEmpty()) {
            printNoAnimalsFound.run();
        } else {
            printAnimalsList.accept(animals);
        }
    };
    private final Runnable printAnimals = () -> {
        printAnimalsHandler.accept(tree.getAnimals());
    };

    private final Supplier<String> SEARCH_PROMPT = () -> ask(getString("tree.search.prompt") + "\n");

    private final Runnable PRINT_SEARCH_RESULT = () -> {
        String animal = SEARCH_PROMPT.get();
        out(AnimalNLPService.StringFormatter.FACTS_ABOUT_ANIMAL.apply(
                tree.findAnimal(animal),
                animal,
                getString("tree.search.facts"),
                getString("tree.search.noFacts")

            ) + "\n"
        );
    };

    public App(TreeStorage storage) {
        this.menu = new Menu(getString("menu.title"), getString("menu.error"));
        this.storage = storage;
        loadTree();
    }


    private void loadTree()  {
        storage.loadTree(tree);
        this.treeState = tree.isFresh()
                ? TreeState.EMPTY
                : TreeState.LOADED_KNOWLEDGE;
    }

    private String menuPrompt(MenuAction menuAction) {
        return switch (menuAction) {
            case GUESSING_GAME -> getString("menuItem.1");
            case LIST_ANIMALS -> getString("menuItem.2");
            case SEARCH_ANIMAL -> getString("menuItem.3");
            case CALCULATE_STATS -> getString("menuItem.4");
            case PRINT_KNOWLEDGE_TREE -> getString("menuItem.5");
            case EXIT -> getString("menuItem.0");
        };
    }


    @Override
    public void run() {

        loadTree();



        if (treeState == TreeState.EMPTY) {
            startWithEmptyTree();
            out(getString("welcome") + "\n");
        } else {
            out(getString("welcome") + "\n");
        }

        // TODO add check if the user has a preloaded tree or not.

        menu
                .add(MenuAction.GUESSING_GAME, menuPrompt(MenuAction.GUESSING_GAME),new Game(tree))
                .add(MenuAction.LIST_ANIMALS, menuPrompt(MenuAction.LIST_ANIMALS), printAnimals)
                .add(MenuAction.SEARCH_ANIMAL, menuPrompt(MenuAction.SEARCH_ANIMAL), PRINT_SEARCH_RESULT)
                .add(MenuAction.CALCULATE_STATS, menuPrompt(MenuAction.CALCULATE_STATS), printStats)
                .add(MenuAction.PRINT_KNOWLEDGE_TREE, menuPrompt(MenuAction.PRINT_KNOWLEDGE_TREE), printTree)
                .add(MenuAction.EXIT, menuPrompt(MenuAction.EXIT), () -> menu.stopRunning().run())
                .run();

        saveTree();
        out(getString("bye") + "\n");

    }


    private void startWithEmptyTree() {
        String animal = in(getString("animal.askFavorite") + "\n");
        while (animal.isEmpty()) {
            animal = in(getString("animal.askFavorite") + "\n");
        }
        tree.getCurrent().setData(animal);
    }

    private void saveTree() {
        storage.saveTree(tree);
    }


    enum TreeState {
        EMPTY,
        LOADED_KNOWLEDGE,
    }


}
