package zipping;

import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Класс валидирующий аргументы командной строки
 * */
@Slf4j
public class ArgsValidator {

    List<String> errorMsgList = new ArrayList<>();
    String retrieve = "retrieve";
    String save = "save";
    String repas = "repas";

    /**
     * Метод распределяющий параметры по валидирующим методам
     * @param mainArgs Аргументы командной строки
     * @return Строку с сообщением или пустую строку
     * */
    public String argsDispatcher(CommandLineParameters mainArgs) {
        File sourceFile = new File(mainArgs.sourceDir);
        String targetDir = mainArgs.targetDir;
        String password = mainArgs.password;
        String action = mainArgs.action;

        boolean sourceDirValidation = sourceDirValidator(sourceFile, action);
        boolean targetDirValidation = true;
        boolean passwordValidation = true;
        boolean csumValidation = true;

        if (targetDir == null && (action.equals(save) || action.equals(retrieve))) {
            targetDirValidation = false;
            String targetDirErrorMsg = Message.TARGET_PARAM_REQUIRED_IN_METHOD.getMessage();
            errorMsgList.add(targetDirErrorMsg);
            errorMsgList.add(Message.TARGET_PARAM_DESCRIPTION.getMessage());
        }
        if (targetDir != null) {
            targetDirValidation = targetDirValidator(new File(mainArgs.targetDir));
        }
        if (password != null) {
            passwordValidation = passwordValidator(password);
        }
        if (password == null && ((action.equals(retrieve) || action.equals(repas)) && checkEncryption(sourceFile))) {
            passwordValidation = false;
            String passwordErrorMsg = Message.PASSWORD_REQUIRED.getMessage();
            errorMsgList.add(passwordErrorMsg);
            errorMsgList.add(Message.PASSWORD_PARAM_DESCRIPTION.getMessage());
        }
        if (mainArgs.csum && action.equals(retrieve)){
            csumValidation = csumValidator(sourceFile);
        }

        if (sourceDirValidation && targetDirValidation && passwordValidation && csumValidation) {
            return "";
        }
        else {
            return String.join("\n", errorMsgList);
        }
    }

    /**
     * Метод валидации файла/директории поступления данных
     * @param action исполняемая функция
     * @param sourceFile файл
     * @return boolean
     * */
    public boolean sourceDirValidator (File sourceFile, String action) {
        String sourceDirErrorMsg = "";
        String extension = FilenameUtils.getExtension(sourceFile.getName());
        if (!sourceFile.exists()) {
            sourceDirErrorMsg = Message.DIR_FOR_ZIP_NOT_EXISTS.getMessage();
        }
        if (!sourceFile.exists() && sourceFile.isFile()) {
            sourceDirErrorMsg = Message.DIR_FOR_ZIP_NOT_EXISTS.getMessage();
        }
        if(action.equals(retrieve) && !extension.equals("zip")){
            sourceDirErrorMsg = Message.FILE_IS_NOT_ZIP.getMessage();
        }
        errorMsgList.add(sourceDirErrorMsg);

        if(!sourceDirErrorMsg.isEmpty()){
            errorMsgList.add(Message.SOURCE_PARAM_DESCRIPTION.getMessage());
        }
        return sourceDirErrorMsg.isEmpty();
    }

    /**
     * Метод валидации директории размещения данные
     * @param targetFile файл
     * @return boolean
     * */
    public boolean targetDirValidator (File targetFile) {
        String targetDirErrorMsg = "";
        if (!targetFile.isDirectory()) {
            targetDirErrorMsg = Message.TARGET_PATH_NOT_DIR.getMessage();
        }
        errorMsgList.add(targetDirErrorMsg);
        if (!targetDirErrorMsg.isEmpty()){
            errorMsgList.add(Message.TARGET_PARAM_DESCRIPTION.getMessage());
        }
        return targetDirErrorMsg.isEmpty();
    }

    /**
     * Метод валидации пароля
     * @param password Строка с паролем
     * @return boolean
     * */
    public boolean passwordValidator (String password) {
        String passwordErrorMsg = "";

        if (password.isEmpty() || password.isBlank()) {
            passwordErrorMsg = Message.EMPTY_PASSWORD.getMessage();
        }
        errorMsgList.add(passwordErrorMsg);
        if(!passwordErrorMsg.isEmpty()){
            errorMsgList.add(Message.PASSWORD_PARAM_DESCRIPTION.getMessage());
        }
        return passwordErrorMsg.isEmpty();
    }

    /**
     * Метод валидации параметра проверки контрольной суммы
     * @param sourceFile файл
     * @return boolean
     * */
    public boolean csumValidator (File sourceFile) {
        String csumErrorMsg = "";
        String fileName = ZipUtils.fileNameForValidate(sourceFile.getPath());
        File checkExist = new File(fileName);

        if (!checkExist.isFile()) {
            csumErrorMsg = Message.NO_CSUM_FILE.getMessage();
        }
        errorMsgList.add(csumErrorMsg);
        if(!csumErrorMsg.isEmpty()) {
            errorMsgList.add(Message.SUM_PARAM_DESCRIPTION.getMessage());
        }
        return csumErrorMsg.isEmpty();
    }

    /**
     * Метод проверки наличия пароля на файле
     * @param sourceFile файл
     * @return boolean
     * */
    public boolean checkEncryption(File sourceFile) {
        boolean encryption = false;
        try {
            ZipFile zipFile = new ZipFile(sourceFile);
            encryption = zipFile.isEncrypted();
        }
        catch (ZipException err){
            log.error("Архив не найден", err);
        }
        return encryption;
    }
}
