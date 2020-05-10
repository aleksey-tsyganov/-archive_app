package zipping;


import net.lingala.zip4j.ZipFile;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.TemporaryFolder;

import java.io.*;

import static org.junit.Assert.*;


/**
 * Тестирование класса контрольной суммы
 * */
public class CheckSumTest {
    Zip zip = new Zip();
    ZipFile zipFile;
    private File file;
    private File notFile;

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
     * Инициализация тестирования
     * */
    @Before
    public void setUp() throws IOException {
        notFile = new File("javaTestNotFile.txt");
        file = folder.newFile("javaTestFile.txt");
        zipFile = zip.zipInFile(file.getPath(), folder.getRoot().getPath(),false);
    }

    /**
     * Тестировние метода расчета контрольной суммы
     * */
    @Test
    public void testCreateCheckSum() {
        String test = CheckSum.createCheckSum(file.getPath());
        assertFalse(test.isEmpty());
        assertEquals("", CheckSum.createCheckSum(notFile.getPath()));
    }

    /**
     * Тестирование метода записывающего контрольную сумму в файл
     * */
    @Test
    public void testWriteCheckSum() {
        CheckSum.writeCheckSum(zipFile);
        String fileName = ZipUtils.fileNameForChecksum(zipFile);
        String fullPath = folder.getRoot() + File.separator + fileName;
        boolean test = new File(fullPath).exists();
        assertTrue(test);
    }

    /**
     * Тест метода валидации контрольной суммы. Валидация не пройдена.
     * */
    @Test
    public void testFailValidateChecksum() {
        CheckSum.validateChecksum(zipFile.getFile().getPath());
        assertEquals(Message.CSUM_FAIL.getMessage() + "\n" + Message.SUM_PARAM_DESCRIPTION.getMessage(),
                systemOutRule.getLog().trim());
    }

    /**
     * Тест метода валидации контрольной суммы. Валидация прошла успешно.
     * */
    @Test
    public void testSuccessValidateChecksum() {
        zipFile = zip.zipInFile(file.getPath(), folder.getRoot().getPath(),true);
        CheckSum.validateChecksum(zipFile.getFile().getPath());
        assertEquals(Message.CSUM_SUCCESS.getMessage(), systemOutRule.getLog().trim());
    }

    /**
     * Удаление не нужных файлов/папок
     * */
    @After
    public void tearDown() {
        folder.delete();
    }
}