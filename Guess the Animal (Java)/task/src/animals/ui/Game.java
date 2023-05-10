package animals.ui;

import animals.tree.AnimalFactTree;

import static animals.languagerules.AnimalNLPService.*;

import java.util.function.*;

public class Game extends TextView implements Runnable {

    private final AnimalFactTree gameTree;
    private boolean gameOn = true;
    private GameState gameState = GameState.NEW_GAME;
    private final Function<AnimalFactTree, String> GUESS = tree -> getString("game.guess")
            .formatted(StringFormatter.UNDEFINED_ARTICLE.apply(tree.getCurrData()));
    private final Predicate<String> IS_TRUE_FOR = animal -> askYesOrNo(getString("game.isCorrect")
            .formatted(StringFormatter.UNDEFINED_ARTICLE.apply(animal)));

    public Game(AnimalFactTree gameTree) {
        this.gameTree = gameTree;
    }


    @Override
    public void run() {
        gameLoop();
    }

    private void startGame() {
        in(getString("game.start") + "\n");
    }

    private void gameRestart() {
        in(getString("game.restart") + "\n");
    }

    private void gameLoop() {

        while (gameOn) {

            switch (gameState) {
                case NEW_GAME -> startGame();
                case RESTART_GAME -> gameRestart();
            }

            while (gameTree.isFact()) {
                gameTree.next(askYesOrNo(question()));
            }

            if (askYesOrNo(GUESS.apply(gameTree))) {
                out(getString("game.win") + "\n");
            } else {
                String newAnimal = lose();
                String newFact = askForFact(specifyFact(newAnimal));
                gameTree.insertFact(newFact, newAnimal, IS_TRUE_FOR.test(newAnimal));
                displayLearnedFacts();
            }
            playAgain();
        }


        reset();
    }

    private String question() {
        return StringFormatter.QUESTION.apply(gameTree.getCurrData());
    }

    private String lose() {
        return ask(getString("game.lose") + "\n");
    }

    private String specifyFact(String newAnimal) {
        return StringFormatter.ASK_FOR_DISTINCT_FACT
                .apply(
                        getString("game.specify.fact"),
                        gameTree.getCurrData(),
                        newAnimal
                );
    }

    private void displayLearnedFacts() {
        out(StringFormatter.LEARNED_FACT.apply(
                gameTree.getRightData(),
                gameTree.getLeftData(),
                gameTree.getCurrData(),
                getString("game.learned") + "\n")
        );
        out(getString("game.learnedMuch").formatted(getString("cheer")) + "\n");
    }

    private void playAgain() {
        gameOn = askYesOrNo(getString("playAgain") + "\n");
        if (gameOn) {
            reset();
            gameState = GameState.RESTART_GAME;
        }
    }

    private void reset() {
        gameTree.reset();
    }

    enum GameState {
        NEW_GAME,
        RESTART_GAME,
    }

}
