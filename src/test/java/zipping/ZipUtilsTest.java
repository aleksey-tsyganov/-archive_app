package zipping;

import net.lingala.zip4j.ZipFile;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Тестирование вспомогательного класса ZipUtils
 * */
public class ZipUtilsTest {
    Zip zip = new Zip();
    private ZipFile zipFile;
    private File directory;
    private File file;

    /**
     * Правило для создания временной папки
     * */
    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    /**
     * Инициализация тестирования
     * */
    @Before
    public void setUp() throws IOException {
        String dirPath = "javaTestDir";
        String filePath = "javaTestFile.txt";
        file = folder.newFile(filePath);
        directory = folder.newFolder(dirPath);
        zipFile = zip.zipInFile(file.getPath(), folder.getRoot().getPath(),false);
    }

    /**
     * Тестирование метода для zip файла
     * */
    @Test
    public void testZipFileName() {
        String actual = ZipUtils.zipFileName(folder.getRoot().getPath());
        assertTrue(actual.endsWith(".zip"));
    }

    /**
     * Тестирование метода создающего имя для файла контрольной суммы
     * */
    @Test
    public void testFileNameForChecksum() {
        String actual = ZipUtils.fileNameForChecksum(zipFile);
        assertTrue(actual.endsWith(".txt"));
    }

    /**
     * Тестирование метода получения имени файла для валидации контрольной суммы
     * */
    @Test
    public void testFileNameForValidate() {
        String actual = ZipUtils.fileNameForValidate(zipFile.getFile().getName());
        assertTrue(actual.endsWith(".txt"));
    }

    /**
     * Тестирование метода получения имени для реархивированного файла
     * */
    @Test
    public void testGetDirNameForReZip() {
        String expected = zipFile.getFile().getName().replace(".zip", "");
        String actual = ZipUtils.getDirNameForReZip(zipFile.getFile().getName());
        assertEquals(expected, actual);
    }

    /**
     * Тестирование метода получения родительской директории
     * */
    @Test
    public void testGetParentDirName() {
        String expected = folder.getRoot().getPath();
        String actual = ZipUtils.getParentDirName(directory.getPath());
        assertEquals(expected, actual);
    }

    /**
     * Тестирование метода переименовывающего файл
     * */
    @Test
    public void testRenameFile() throws IOException {
        File newFile = folder.newFile("newFile.txt");
        ZipUtils.renameFile(newFile, file);
        assertFalse(newFile.exists());
        assertTrue(file.exists());
    }

    /**
     * Тестирование удаления директории
     * */
    @Test
    public void testDeleteDir() {
        ZipUtils.deleteDir(directory);
        assertFalse(directory.exists());
    }

    /**
     * Тестирование метода получения дочерней директории
     * */
    @Test
    public void testGetChildDirectoryNameForReZip() {
        String expected = file.getName();
        String actual = ZipUtils.getChildDirectoryNameForReZip(zipFile.getFile().getPath());
        assertEquals(expected, actual);
    }
}