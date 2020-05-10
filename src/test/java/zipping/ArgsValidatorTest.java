package zipping;

import com.beust.jcommander.JCommander;
import net.lingala.zip4j.ZipFile;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;


/**
 * Тестирование валидации параметров
 * */
public class ArgsValidatorTest {
    Zip zip = new Zip();
    ZipFile zipFile;
    CommandLineParameters cmdParams = new CommandLineParameters();
    JCommander jCommander = new JCommander(cmdParams);
    private String password = "123";
    private ArgsValidator argsValidator = new ArgsValidator();
    private File notDirectory = new File("\\test");
    private File file;
    private File directory;
    private File notFile = new File("javaTestFile.txt");
    private String saveAction = "save";

    /**
     * Правило
     * */
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    /**
     * Инициализация тестирования
     * */
    @Before
    public void setUp() throws IOException{
        String filePath = "javaTestFile.txt";
        String directoryPath = "javaTestDir";
        directory = folder.newFolder(directoryPath);
        file = folder.newFile(filePath);
        zipFile = zip.zipInFile(file.getPath(), directory.getParent(), password, false);
    }

    /**
     * Тестирование положительного результата отработки метода
     * */
    @Test
    public void testArgsDispatcherTrue() {
        String[] argv = {saveAction, "-f", file.getPath(), "-t", directory.getPath(), "-p", "1", "-s"};
        jCommander.parse(argv);
        assertEquals("", argsValidator.argsDispatcher(cmdParams));
    }

    /**
     * Тестирование если директория назначения равна null
     * */
    @Test
    public void testTargetSourceNull() {
        String[] argv = {saveAction, "-f", file.getPath(), "-p", "1", "-s"};
        jCommander.parse(argv);
        assertEquals(Message.TARGET_PARAM_REQUIRED_IN_METHOD.getMessage() + "\n" + Message.TARGET_PARAM_DESCRIPTION.getMessage(),
                argsValidator.argsDispatcher(cmdParams).trim());
    }

    /**
     * Тестирование что директория поступления данных является директорией
     * */
    @Test
    public void testSourceDirIsDirectory() {
        assertTrue(argsValidator.sourceDirValidator(directory, saveAction));
        assertFalse(argsValidator.sourceDirValidator(notDirectory, saveAction));
    }

    /**
     * Тестирование что директория поступления данных является файлом
     * */
    @Test
    public void testSourceDirIsFile() {
        assertTrue(argsValidator.sourceDirValidator(file, saveAction));
        assertFalse(argsValidator.sourceDirValidator(notFile, saveAction));
    }

    /**
     * Тестирование что поступивший файл не является zip архивом
     * */
    @Test
    public void testSourceFileNotZipFile(){
        String retrieveAction = "retrieve";
        assertFalse(argsValidator.sourceDirValidator(file, retrieveAction));
    }

    /**
     * Тестирование что директория назначения является директорией
     * */
    @Test
    public void testTargetDirIsDirectory() {
        assertTrue(argsValidator.sourceDirValidator(directory, saveAction));
        assertFalse(argsValidator.sourceDirValidator(notDirectory, saveAction));
        assertTrue(argsValidator.sourceDirValidator(file, saveAction));
    }

    /**
     * Тестирование метода валидирующего пароль
     * */
    @Test
    public void testPasswordValidator() {
        String blankPassword = " ";
        String notPassword = "";
        assertTrue(argsValidator.passwordValidator(password));
        assertFalse(argsValidator.passwordValidator(blankPassword));
        assertFalse(argsValidator.passwordValidator(notPassword));
    }

    /**
     * Тестирование необходимости указания пароля
     * */
    @Test
    public void testPasswordRequired() {
        String[] argv = {"repas", "-f", zipFile.getFile().getPath()};
        jCommander.parse(argv);
        assertEquals(Message.PASSWORD_REQUIRED.getMessage() + "\n" + Message.PASSWORD_PARAM_DESCRIPTION.getMessage(),
                argsValidator.argsDispatcher(cmdParams).trim());
    }

    /**
     * Тестирование метода определяющего установлен ли пароль
     * */
    @Test
    public void testFileNotEncrypted(){
        assertTrue(argsValidator.checkEncryption(new File(zipFile.getFile().getPath())));
        assertFalse(argsValidator.checkEncryption(file));
    }

    /**
     * Тестирование метода валидации контрольной суммы
     * */
    @Test
    public void testCSumIfNoCheckSumFile() {
        assertFalse(argsValidator.csumValidator(new File(zipFile.getFile().getPath())));
    }
}