package zipping;

import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Класс для архивации
 * */
@Slf4j
public class Zip {

    /**
     * Пустой конструктор
     * */
    public Zip(){
        //Empty constructor
    }

    /**
     * Метод для архивации
     * @param sourceDirectory директория поступления данных
     * @param targetDirectory директория для размещения за архивированного файла
     * @param csum если true производиться расчет контрольной суммы
     * @return ZipFile
     * */
    public ZipFile zipInFile (String sourceDirectory, String targetDirectory, boolean csum) {

        File addToZipFile = new File(sourceDirectory);
        if (targetDirectory.equals("")) {
            targetDirectory = addToZipFile.getParent();
        }
        String fullPath = ZipUtils.zipFileName(targetDirectory);
        ZipFile zipFile = new ZipFile(fullPath);

        try {
            if (addToZipFile.isDirectory()) {
                zipFile.addFolder(addToZipFile);
            }
            else {
                zipFile.addFile(addToZipFile);
            }
        }
        catch (ZipException err) {
            log.error("Ошибка при архивации, метод zipInFile", err);
        }

        if (csum) {
            CheckSum.writeCheckSum(zipFile);
        }
        return zipFile;
    }

    /**
     * Метод для архивации с паролем
     * @param sourceDirectory директория поступления данных
     * @param targetDirectory директория для размещения за архивированного файла
     * @param csum если true производиться расчет контрольной суммы
     * @param password пароль
     * @return ZipFile
     * */
    public ZipFile zipInFile (String sourceDirectory, String targetDirectory, String password, boolean csum) {

        File addToZipFile = new File(sourceDirectory);
        String fullPath = ZipUtils.zipFileName(targetDirectory);
        ZipFile zipFile = new ZipFile(fullPath, password.toCharArray());
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setEncryptFiles(true);
        zipParameters.setEncryptionMethod(EncryptionMethod.AES);

        try {
            if (addToZipFile.isDirectory()) {
                zipFile.addFolder(addToZipFile, zipParameters);
            }
            else {
                zipFile.addFile(addToZipFile, zipParameters);
            }
        }
        catch (ZipException err) {
            log.error("Ошибка при архивации, метод zipInFile", err);
        }

        if (csum) {
            CheckSum.writeCheckSum(zipFile);
        }
        return zipFile;
    }

    /**
     * Метод для реархивации
     * @param sourceDirectory директория поступления данных
     * */
    public void reZip(String sourceDirectory) {

        String directoryName = ZipUtils.getDirNameForReZip(sourceDirectory);
        String parentDirName = ZipUtils.getParentDirName(sourceDirectory);
        String fullPath = parentDirName + File.separator + directoryName;
        String childDirectory = ZipUtils.getChildDirectoryNameForReZip(sourceDirectory);
        String ultimatePathForReZip = fullPath + File.separator + childDirectory;

        ExtractZip.extract(sourceDirectory, fullPath,false);

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException err) {
            log.error("Thread interrupted", err);
            Thread.currentThread().interrupt();
        }

        ZipFile zipFile = new ZipFile(String.valueOf(zipInFile(ultimatePathForReZip, parentDirName, false)));
        ZipUtils.renameFile(new File(zipFile.getFile().getPath()), new File(sourceDirectory));
        ZipUtils.deleteDir(new File(fullPath));
    }

    /**
     * Метод для реархивации с паролем
     * @param sourceDirectory директория поступления данных
     * @param password строка с паролем
     * */
    public void reZip(String sourceDirectory, String password){

        String directoryName = ZipUtils.getDirNameForReZip(sourceDirectory);
        String parentDirName = ZipUtils.getParentDirName(sourceDirectory);
        String fullPath = parentDirName + File.separator + directoryName;
        String childDirectory = ZipUtils.getChildDirectoryNameForReZip(sourceDirectory);
        String ultimatePathForReZip = fullPath + File.separator + childDirectory;

        ExtractZip.extract(sourceDirectory, fullPath,false);
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException err) {
            log.error("Thread interrupted", err);
            Thread.currentThread().interrupt();
        }
        ZipFile zipFile = new ZipFile(String.valueOf(zipInFile(ultimatePathForReZip, parentDirName, password, false)));
        ZipUtils.renameFile(new File(zipFile.getFile().getPath()), new File(sourceDirectory));
        ZipUtils.deleteDir(new File(fullPath));
    }
}
