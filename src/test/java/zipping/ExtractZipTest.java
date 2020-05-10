package zipping;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class ExtractZipTest {
    Zip zip = new Zip();
    ZipFile zipFile;
    ZipFile zipFilePswdProtect;
    ZipFile zipFileCsum;
    private File file;
    private String password;
    private String targetDir;

    /**
     * Правило создающее темповую директорию
     * */
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    /**
     * Правило для тестирования сообщений System.out.println
     * */
    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    /**
     * Правило для тестирования System.exit
     * */
    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    /**
     * Инициализация тестирования
     * */
    @Before
    public void setUp() throws IOException, InterruptedException {
        file = folder.newFile("javaTestFile.txt");
        password = "1";
        String sourceDir = file.getPath();
        targetDir = folder.getRoot().getPath();
        zipFile = zip.zipInFile(sourceDir, targetDir, false);
        TimeUnit.SECONDS.sleep(2);
        zipFilePswdProtect = zip.zipInFile(sourceDir, targetDir, password,false);
        TimeUnit.SECONDS.sleep(2);
        zipFileCsum = zip.zipInFile(sourceDir, targetDir, password,true);
    }

    /**
     * Тестирование метода извлекающего файлы
     * */
    @Test
    public void testExtract() throws ZipException {
        String fileName = zipFile.getFileHeaders().get(0).toString();
        file.delete();
        ExtractZip.extract(zipFile.getFile().getPath(), targetDir, false);
        File expectedFileName = new File(targetDir + File.separator + fileName);
        assertEquals("javaTestFile.txt", expectedFileName.getName());
    }

    /**
     * Тестирование извлечения файлов из запароленного архива
     * */
    @Test
    public void testExtractPswdProtected() throws ZipException {
        String fileName = zipFilePswdProtect.getFileHeaders().get(0).toString();
        file.delete();
        ExtractZip.extract(zipFilePswdProtect.getFile().getPath(), targetDir, password,false);
        File expectedFileName = new File(targetDir + File.separator + fileName);
        assertEquals("javaTestFile.txt", expectedFileName.getName());
    }

    /**
     * Тестирование извлечения файлов из запароленного архива, без указания пароля
     * */
    @Test
    public void testFailExtractPswdProtected() {
        exit.expectSystemExit();
        ExtractZip.extract(zipFilePswdProtect.getFile().getPath(), targetDir,false);
    }

    /**
     * Тестирование извлечения файлов из запароленного архива, c указанием неверного пароля
     * */
    @Test
    public void testWrongPswdExtractPswdProtected() {
        String wrongPswd = "qwe123";
        exit.expectSystemExit();
        ExtractZip.extract(zipFilePswdProtect.getFile().getPath(), targetDir, wrongPswd, false);
    }

    /**
     * Тестирование извлечение файлов с валидацией контрольной суммы
     * */
    @Test
    public void testExtractWithCsumValidation() {
        ExtractZip.extract(zipFileCsum.getFile().getPath(), targetDir, password, true);
        assertEquals(Message.CSUM_SUCCESS.getMessage(), systemOutRule.getLog().trim());
    }
}