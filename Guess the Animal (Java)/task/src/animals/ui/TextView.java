package animals.ui;

import animals.languagerules.AnimalNLPService;
import animals.languagerules.ValidationResult;

import static animals.languagerules.AnimalNLPService.*;

import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public class TextView {

    private static final Scanner scanner = new Scanner(System.in);
    private static final AnimalNLPService service = new AnimalNLPService(Locale.getDefault());
    public void display(String... prompts) {
        Arrays.stream(prompts).forEach(System.out::println);
    }
    public void print(String... prompts) {
        Arrays.stream(prompts).forEach(System.out::print);
    }

    protected String in(String prompt) {
        out(prompt);
        return scanner.nextLine().trim().toLowerCase();
    }

    protected void out(String prompt) {
        System.out.print(prompt);
    }


    protected String getString(String key) {
        return service.getString(key);
    }

    protected boolean askYesOrNo(String prompt) {
        String yesOrNo = in(prompt);
        while (service.validate(yesOrNo, "answer.yesOrNo") == ValidationResult.INVALID) {
            yesOrNo = in(getString("clarification"));
        }
        return StringFormatter.isPositiveBinary().test(yesOrNo);
    }

    // TODO add validation through passing function to ask method
    protected String ask(String prompt) {
        return in(prompt);
    }

    protected String askForFact(String prompt) {
        String fact = in(prompt);
        while (service.validate(fact, "fact.isCorrect") == ValidationResult.INVALID) {
            fact = in( prompt +"\n"+ getString("game.input.examples"));
        }
        return fact;

    }



}
