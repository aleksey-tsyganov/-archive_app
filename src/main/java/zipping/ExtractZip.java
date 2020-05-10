package zipping;

import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

/**
 * Класс для извлечения файлов из архива
 * */
@Slf4j
public class ExtractZip {

    /**
     * Конструктор
     * */
    private ExtractZip(){}

    /**
     * Метод для извлечения файлов из архива
     * @param sourceDirectory директория поступления файла
     * @param targetDirectory директория размещения извлеченных файлов
     * @param csum если true производится валидация контрольной суммы
     * */
    public static void extract(String sourceDirectory, String targetDirectory, boolean csum) {

        try {
            new ZipFile(sourceDirectory).extractAll(targetDirectory);
        }
        catch (ZipException err ) {
            log.error("Попытка разархивировать файл, защищенный паролем, без указания пароля ", err);
            System.out.println(Message.PASSWORD_FAIL.getMessage() + "\n" + Message.PASSWORD_PARAM_DESCRIPTION.getMessage());
            System.exit(1);
        }

        if (csum) {
            CheckSum.validateChecksum(sourceDirectory);
        }
    }

    /**
     * Метод для извлечения файлов из архива с паролем
     * @param sourceDirectory директория поступления файла
     * @param targetDirectory директория размещения извлеченных файлов
     * @param csum если true производится валидация контрольной суммы
     * @param password строка с паролем
     * */
    public static void extract(String sourceDirectory, String targetDirectory, String password, boolean csum) {
        try {
            new ZipFile(sourceDirectory, password.toCharArray()).extractAll(targetDirectory);
        }
        catch (ZipException err) {
            log.error("Пароль указан неверно", err);
            System.out.println(Message.PASSWORD_FAIL.getMessage() + "\n" + Message.PASSWORD_PARAM_DESCRIPTION.getMessage());
            System.exit(1);
        }

        if (csum) {
            CheckSum.validateChecksum(sourceDirectory);
        }
    }
}
