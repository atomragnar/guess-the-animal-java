package animals;

import animals.languagerules.AnimalNLPService;
import animals.ui.App;
import animals.utils.CommandLineUtils;

import java.util.Map;
import java.util.regex.Pattern;


public class Main {

    public static void main(String[] args)  {
        new App(
                CommandLineUtils.handleCommandLineArgs.apply(args)
        ).run();


    }

}
