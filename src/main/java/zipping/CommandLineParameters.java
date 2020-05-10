package zipping;


import com.beust.jcommander.Parameter;

/**
 * Класс для формирования параметров возможных для указания в командной строке
 * */
public class CommandLineParameters {

    /**
     * Помощь
     * */
    @Parameter(names = {"-h", "--help"}, help = true, description = "Меню подсказок.\n", order = 0)
    public boolean help;

    /**
     * Действие
     * */
    @Parameter(
            description = "\n\nНапечатайте 'save' для создания zip архива. " +
                    "Напечатайте 'retrieve' для извлечения данных из zip архива. " +
                    "Напечатайте 'csum' для расчета или проверки контрольной суммы. " +
                    "Напечатайте 'repas' для пересоздания zip архива с паролем.\n",
            required = true,
            order = 1)
    public String action;

    /**
     * Путь для получения файлов
     * */
    @Parameter(
            names = {"-f", "--full_path"},
            description =
                    "Файл/Директория для архивации, извлечения, расчета контрольной суммы, пересоздания с паролем. " +
                    "Необходимо указать 'абсолютный путь'.\n",
            required = true,
            order = 2)
    public String sourceDir;

    /**
     * Путь для размещения файлов
     * */
    @Parameter(
            names = {"-t", "--target_directory"},
            description =
                    "Директория для размещения готового архива или извлечения файлов из архива. " +
                    "Параметр является обязательным для функции 'save' и 'retrieve'. " +
                    "Необходимо указать 'абсолютный путь'.\n",
            order = 3)
    public String targetDir;

    /**
     * Пароль
     * */
    @Parameter(names = {"-p", "--password"}, description = "Пароль.\n", order = 4, help = true)
    public String password;

    /**
     * Расчет контрольной суммы
     * */
    @Parameter(
            names = {"-s", "--sum"},
            description =
                    "Расчет/проверка контрольной суммы. " +
                    "Для 'расчета' контрольной суммы параметр следует установить при использовании функции 'save'. " +
                    "Для 'проверки' контрольной суммы параметр следует установить при использовании функции 'retrieve'.\n",
            order = 5)
    public boolean csum;

    /**
     * Метод вызова помощи
     * @return Помощь
     * */
    public boolean isHelp() {
        return help;
    }
}
