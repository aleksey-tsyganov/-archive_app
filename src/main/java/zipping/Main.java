package zipping;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;

import java.util.Scanner;


/**
 * Класс для подготовки и запуска выполнения требуемой функции
 * */
@Slf4j
public class Main {

    final CommandLineParameters mainArgs = new CommandLineParameters();

    /**
     * Метод Main
     * */
    public static void main(String [] args) {
        Main mainInit = new Main();
        mainInit.needHelp();
        if (args.length == 0) {
            System.exit(0);
        }
        else {
            if (mainInit.handleInputArgs(args)) {
                mainInit.run();
                System.out.println("\nВыполнено!");
            }
        }
    }

    /**
     * Метод для вызова подсказки
     * */
    public void needHelp(){
        JCommander helpCommander = new JCommander(mainArgs);
        System.out.println(Message.NEED_HELP.getMessage());

        Scanner scanner = new Scanner(System.in);
        String answer = scanner.nextLine();
        try {
            answer = answer.toLowerCase();
            if(answer.equals("y")){
                showUsage(helpCommander);
            }
            else if (answer.equals("n")) {
                return;
            }
            else {
                System.out.println(Message.YES_NO_REQUIRED.getMessage());
                needHelp();
            }
        }
        catch (Exception err) {
            System.out.println(Message.YES_NO_REQUIRED.getMessage());
            needHelp();
        }
    }

    /**
     * Метод обработки аргументов командной строки
     * @param args список аргументов
     * @return boolean
     * */
    public boolean handleInputArgs(String [] args) {
        JCommander jCommander = new JCommander(mainArgs);
        jCommander.setProgramName("Archive App");
        boolean validation = false;

        try {
            jCommander.parse(args);
            ArgsValidator argsValidator = new ArgsValidator();
            String validationResult = argsValidator.argsDispatcher(mainArgs);
            if (validationResult.isEmpty()){
                validation = true;
            }
            else {
                System.out.println(validationResult);
                showUsage(jCommander);
            }
        }
        catch (ParameterException err) {
            log.error(Message.PARAMS_ERROR.getMessage(), err);
            System.out.println(Message.PARAMS_ERROR.getMessage());
            showUsage(jCommander);
        }
        if (mainArgs.isHelp()) {
            showUsage(jCommander);
        }

        return validation;
    }

    /**
     * Метод запуска выполнения запрошенной функции
     * */
    public void run() {
        System.out.println("Running program with...");
        System.out.println("Функция: " + mainArgs.action);
        System.out.println("Объект: " + mainArgs.sourceDir);
        System.out.println("Директория размещения: " + mainArgs.targetDir);
        System.out.println("Производить расчет\\валидацию контрольной суммы: " + mainArgs.csum);

        Zip zip = new Zip();

        if (mainArgs.action.equals("save")) {
            if (mainArgs.password != null) {
                zip.zipInFile(mainArgs.sourceDir, mainArgs.targetDir, mainArgs.password, mainArgs.csum);
            }
            else {
                zip.zipInFile(mainArgs.sourceDir, mainArgs.targetDir, mainArgs.csum);
            }
        }
        else if (mainArgs.action.equals("retrieve")) {
            if (mainArgs.password != null) {
                ExtractZip.extract(mainArgs.sourceDir, mainArgs.targetDir, mainArgs.password, mainArgs.csum);
            }
            else {
                ExtractZip.extract(mainArgs.sourceDir, mainArgs.targetDir, mainArgs.csum);
            }
        }
        else if (mainArgs.action.equals("csum")) {
            ZipFile zipFile = new ZipFile(mainArgs.sourceDir);
            CheckSum.writeCheckSum(zipFile);
        }
        else if (mainArgs.action.equals("repas")) {

            if (mainArgs.password != null) {
                zip.reZip(mainArgs.sourceDir, mainArgs.password);
            }
            else {
                zip.reZip(mainArgs.sourceDir);
            }

        }
        else {
            mainArgs.isHelp();
        }
    }

    /**
     * Метод для вызова подсказки
     * @param jCommander JCommander
     * */
    public void showUsage (JCommander jCommander) {
        jCommander.usage();
    }

}
