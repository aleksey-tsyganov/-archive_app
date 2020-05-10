package zipping;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.junit.*;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Тестирование класс создающего архив
 * */
public class ZipTest {
    Zip zip = new Zip();
    ZipFile zipFile;
    ZipFile zipFilePswdProtect;
    private File file;
    private String password;

    /**
     * Правило создающее темповую директорию
     * */
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

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
        String filePath = "javaTestFile.txt";
        file = folder.newFile(filePath);
        password = "1";
        zipFile = zip.zipInFile(file.getPath(), folder.getRoot().getPath(), false);
        TimeUnit.SECONDS.sleep(2);
        zipFilePswdProtect = zip.zipInFile(file.getPath(), folder.getRoot().getPath(), password,false);
    }

    /**
     * Тестирование архивирующего метода
     * */
    @Test
    public void testZipInFileCreate() throws ZipException {
        assertTrue(zipFile.getFile().exists());
        assertFalse(zipFile.isEncrypted());
    }

    /**
     * Тестирование метода архивирующего с паролем
     * */
    @Test
    public void testZipInFilePasswordProtected() throws ZipException {
        assertTrue(zipFilePswdProtect.isEncrypted());
    }

    /**
     * Тестирование метода реархивации с паролем
     * */
    @Test
    public void testReZipWithPassword() throws IOException {
        ZipFile newZipFile = new ZipFile(zipFile.getFile().getPath());
        zip.reZip(newZipFile.getFile().getPath(), password);
        assertTrue(newZipFile.isEncrypted());
    }

    /**
     * Тестирование метода реархивации файла на который установлен пароль
     * */
    @Test
    public void testTryReZipPasswordProtected() {
        ZipFile newZipFile = new ZipFile(zipFilePswdProtect.getFile().getPath());
        exit.expectSystemExit();
        zip.reZip(newZipFile.getFile().getPath(), password);
    }
}