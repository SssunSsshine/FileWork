package com.vsu.repository;

import com.vsu.exeption.InvalidFormatException;
import com.vsu.info.UserInfo;
import com.vsu.logic.*;
import com.vsu.ui.ConsoleUserInterface;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileRepository {

    private static final Logger LOGGER = Logger.getLogger(ConsoleUserInterface.class.getName());
    public static final int PARAMETERS_COUNT = 5;
    public static final int USER_AGE = 1;
    public static final String STR_END = "\r\n";
    public static final int USER_NAME = 0;
    public static final int USER_PHONE = 2;
    public static final int USER_SEX = 3;
    public static final int USER_ADDRESS = 4;
    private String path = "";
    private Integer hash;
    private final CheckSumService checkSumService;

    public FileRepository() {
        checkSumService = new CheckSumService();
    }

    public String getPath() {
        return path;
    }

    public UserService readFromFile(String path) throws IOException {
        Map<String, UserInfo> users;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {
            setHash(bufferedReader.readLine());
            users = parseStrFromFile(bufferedReader);
        }

        checkSumService.checkHashCodes(hash, users);
        this.path = path;

        return new UserService(users);
    }

    private static Map<String, UserInfo> parseStrFromFile(BufferedReader bufferedReader) throws IOException {
        String userStr;
        Map<String, UserInfo> users = new HashMap<>();
        while ((userStr = bufferedReader.readLine()) != null) {
            UserInfo user;
            String[] arrInf = userStr.split(";");
            if (arrInf.length != PARAMETERS_COUNT) {
                LOGGER.log(Level.WARNING, String.format("User %s has bad format", userStr));
                throw new InvalidFormatException("File is damaged");
            }
            user = new UserInfo(arrInf[USER_NAME].trim(), Integer.parseInt(arrInf[USER_AGE].trim()),
                    arrInf[USER_PHONE].trim(), arrInf[USER_SEX].trim(), arrInf[USER_ADDRESS].trim());

            if (!users.containsKey(user.getFullName().toUpperCase())) {
                users.put(user.getFullName().toUpperCase(), user);
            } else {
                LOGGER.log(Level.WARNING, "Doubling full name");
            }
        }
        return users;
    }

    private void setHash(String hash) {
        if (hash == null) {
            LOGGER.log(Level.WARNING, "Something wrong with hash");
            throw new InvalidFormatException("File is damaged, Hash NOT found");
        }

        try {
            this.hash = Integer.parseInt(hash.trim());
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Something wrong with hash");
            throw new InvalidFormatException("File is damaged, hash has bad format");
        }
    }

    public void saveFile(Map<String ,UserInfo> users) throws IOException {
        save(users, this.path);
    }

    public void save(Map<String ,UserInfo> users, String path) throws IOException {
        try (Writer bufferedWriter = new BufferedWriter(new FileWriter(path))) {
            int hash = checkSumService.hashCode(users);
            bufferedWriter.write(hash + STR_END);
            for (UserInfo user : users.values()) {
                bufferedWriter.write(user.toStringForFile() + STR_END);
            }
        }
        this.path = path;
    }

    //создание файла только если он еще не существует
    public boolean newFile(String path) throws IOException {
        File file = new File(path);
        try {
            if (file.createNewFile()) {
                this.path = path;
                return true;
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING,String.format("Something wrong with file %s", path));
            throw new IOException(e);
        }
        return false;
    }

    /*создание(перезапись) файла даже если он уже существует*/
    public void createFile(String path) throws IOException {
        FileOutputStream fout = new FileOutputStream(path);
        fout.close();
        this.path = path;
    }
}
