package zipping;

import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Вспомогательный класс для классов Zip, CheckSum, ExtractZip
 * */
@Slf4j
public class ZipUtils {

    private ZipUtils(){}

    /**
     * Метод для формирования имени файла
     * @param targetDirectory путь для хранения созданного файла
     * @return строку с именем файла
     * */
    public static String zipFileName (String targetDirectory) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss");
        LocalDateTime dateTime = LocalDateTime.now();
        return targetDirectory + File.separator + dateFormat.format(dateTime) + ".zip";
    }

    /**
     * Метод для формирования имени файла содержащего контрольную сумму
     * @param zipFile сформированный zipfile
     * @return строку с именем файла в формате ".txt"
     * */
    public static String fileNameForChecksum (ZipFile zipFile) {
        return zipFile.getFile().getName().replace(".zip", ".txt");
    }

    /**
     * Метод для формирования имени файла который содержит контрольную сумму
     * @param sourceFile файл для которого была рассчитана контрольная сумма
     * @return имя файла в формате .txt, который содержит контрольную сумму
     * */
    public static String fileNameForValidate (String sourceFile) {
        File file = new File(sourceFile);
        return file.getParent() + File.separator + file.getName().replace(".zip", ".txt");
    }

    /**
     * Метод для получения имени директории
     * @param sourceDirectory полное имя файла
     * @return имя директории
     * */
    public static String getDirNameForReZip(String sourceDirectory) {
        ZipFile zipFile = new ZipFile(sourceDirectory);
        String directoryName = zipFile.getFile().getName();
        directoryName = directoryName.replace(".zip", "");
        return directoryName;
    }

    /**
     * Метод для получения имени родительской директории файла
     * @param sourceDirectory полный путь к файлу
     * @return путь родительской директории
     * */
    public static String getParentDirName(String sourceDirectory) {
        ZipFile zipFile = new ZipFile(sourceDirectory);
        return zipFile.getFile().getParent();
    }

    /**
     * Метод переименования файла
     * @param newFile файл, который необходимо переименовать
     * @param oldFile файл в который необходимо переименовать
     * @return boolean
     * */
    public static boolean renameFile(File newFile, File oldFile) {
        File tempFile = new File(oldFile.getPath());
        try {
            Files.delete(Path.of(oldFile.getPath()));
        } catch (IOException err) {
            log.error("Ошибка при попытке удалить файл", err);
        }
        return newFile.renameTo(tempFile);
    }

    /**
     * Метод для удаления директории
     * @param directoryToDelete директория для удаления
     * */
    public static void deleteDir(File directoryToDelete) {
        File[] dirContent = directoryToDelete.listFiles();
        if (dirContent != null) {
            for(File file : dirContent) {
                deleteDir(file);
            }
        }
        try {
            Files.delete(Path.of(directoryToDelete.getPath()));
        } catch (IOException err) {
            log.error("Ошибка при попытке удалить файл", err);
        }
    }

    /**
     * Метод для получения имя дочерней директории.
     * Директория является дочерней по отношению к директории
     * в которую происходит предварительная распаковка архива при его реархивации.
     * @param sourceDirectory родительская директория
     * @return имя дочерней директории
     * */
    public static String getChildDirectoryNameForReZip(String sourceDirectory){
        String childDirectoryName = "";
        try {
            ZipFile zipFile = new ZipFile(sourceDirectory);
            int lastIndex = zipFile.getFileHeaders().size() - 1;
            Object lastHeader = zipFile.getFileHeaders().get(lastIndex);
            return childDirectoryName + lastHeader.toString();
        }
        catch (ZipException err) {
            log.error("Ошибка при попытке получить имя дочерней директории", err);

        }
        return childDirectoryName;
    }
}
