package zipping;

import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Класс для расчета/валидации контрольной суммы
 * */
@Slf4j
public class CheckSum {

    /**
     * Конструктор
     * */
    private CheckSum(){}

    /**
     * Метод для расчета контрольной суммы
     * @param sourceFile файл для которого следует рассчитать контрольную сумму
     * */
    public static String createCheckSum(String sourceFile) {

        String checkSum = "";
        try (FileInputStream fis = new FileInputStream(sourceFile)){
            checkSum = DigestUtils.sha1Hex(fis);
        }
        catch (FileNotFoundException err) {
            log.error("Файл не найден. Метод createCheckSum", err);
        }
        catch (IOException err) {
            log.error("Ошибка получения доступа к файлу", err);
        }

        return checkSum;
    }

    /**
     * Метод для записи контрольной суммы в файл
     * @param zipFile zip файл
     * */
    public static void writeCheckSum(ZipFile zipFile){

        String fileSource = zipFile.getFile().getAbsolutePath();
        String fileName = ZipUtils.fileNameForChecksum(zipFile);
        File file = new File(fileName);
        String parentPath = zipFile.getFile().getParent() + File.separator + file;

        try (FileOutputStream fileOutputStream = new FileOutputStream(parentPath)) {
            byte[] checksum = createCheckSum(fileSource).getBytes();
            fileOutputStream.write(checksum);
        }
        catch (FileNotFoundException err) {
            log.error("Файл не найден. Метод writeCheckSum", err);
        }
        catch (IOException err) {
            log.error("Ошибка получения доступа к файлу.", err);
        }
    }

    /**
     * Метод проверки контрольной суммы
     * @param sourceFile файл у которого следует проверить контрольную сумму
     * */
    public static void validateChecksum(String sourceFile) {
        String expectedSum = "";

        try {
            Path path = Paths.get(ZipUtils.fileNameForValidate(sourceFile));
            expectedSum = new String(Files.readAllBytes(path));
        }
        catch (IOException err) {
            log.error("Ошибка получения доступа к файлу. Метод validateChecksum", err);
        }

        if (expectedSum.equals(createCheckSum(sourceFile))){
            System.out.println(Message.CSUM_SUCCESS.getMessage());
        }
        else {
            System.out.println(Message.CSUM_FAIL.getMessage() + "\n" + Message.SUM_PARAM_DESCRIPTION.getMessage());
        }
    }
}
