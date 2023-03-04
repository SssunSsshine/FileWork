package com.vsu.ui;

import com.vsu.enumeration.Operation;
import com.vsu.exeption.ConsoleNotWorkingException;
import com.vsu.exeption.HashCodeException;
import com.vsu.exeption.InvalidFormatException;
import com.vsu.info.UserInfo;
import com.vsu.logic.*;
import com.vsu.repository.FileRepository;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsoleUserInterface {
    private static final Logger LOGGER = Logger.getLogger(ConsoleUserInterface.class.getName());
    private static final String END_LINE = "\n";
    private static final String SPACE = " ";
    private static final Integer MAX_ERRORS = 3;
    public static final String AGREEMENT = "YES";
    private int errorsCount = 0;
    private FileRepository fIleRepository;
    private UserService userService;

    public ConsoleUserInterface() {
        userService = new UserService();
        fIleRepository = new FileRepository();
    }

    private void printMenu() {
        StringBuilder menu = new StringBuilder("Hello!");
        menu.append(END_LINE);
        menu.append("Use ").append(Operation.EXIT).append(SPACE).append("to exit").append(END_LINE);
        menu.append("Use ").append(Operation.LOAD).append(SPACE).append("to read file").append(END_LINE);
        menu.append("Use ").append(Operation.SEARCH).append(SPACE).append("to search user").append(END_LINE);
        menu.append("Use ").append(Operation.ADD).append(SPACE).append("to add user").append(END_LINE);
        menu.append("Use ").append(Operation.REMOVE_USER).append(SPACE).append("to remove user").append(END_LINE);
        menu.append("Use ").append(Operation.SAVE).append(SPACE).append("to save file").append(END_LINE);
        menu.append("Use ").append(Operation.SAVE_AS).append(SPACE).append("to save file as").append(END_LINE);
        menu.append("Use ").append(Operation.NEW_FILE).append(SPACE).append("to create new file").append(END_LINE);
        System.out.println(menu);
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        Operation choice = null;
        fIleRepository = new FileRepository();
        userService = new UserService();
        while (choice != Operation.EXIT) {
            printMenu();
            try {
                choice = Operation.valueOf(scanner.nextLine().trim().toUpperCase());
                processCommand(choice);
            } catch (IOException ioe) {
                LOGGER.log(Level.WARNING, "Something is wrong with opening the file", ioe);
            } catch (HashCodeException hce) {
                LOGGER.log(Level.WARNING, "Something is wrong with hash codes", hce);
            } catch (InvalidFormatException ife) {
                LOGGER.log(Level.WARNING, "Something is wrong with input data", ife);
            } catch (IllegalArgumentException e) {
                handleError(e);
            }
        }
        System.out.println("Goodbye");
    }

    private void processCommand(Operation choice) throws IOException {
        switch (choice) {
            case LOAD:
                loadFile();
                break;
            case SEARCH:
                search();
                break;
            case NEW_FILE:
                newFile();
                break;
            case ADD:
                addUser();
                break;
            case REMOVE_USER:
                removeUser();
                break;
            case SAVE:
                saveFile();
                break;
            case SAVE_AS:
                saveFileAs();
                break;
            case EXIT:
                break;
        }
    }

    private void saveFile() throws IOException {
        if (fIleRepository.getPath().isEmpty()) {
            LOGGER.log(Level.WARNING, "Please load data first");
            return;
        }

        LOGGER.log(Level.INFO, "Saving file {0}", fIleRepository.getPath());

        fIleRepository.saveFile(userService.getUsers());

        LOGGER.log(Level.INFO, "File {0} successfully saved", fIleRepository.getPath());
        System.out.println(String.format("File %s successfully saved", fIleRepository.getPath()));
        System.out.println();
    }

    private void newFile() throws IOException {
        String answer = "";
        Scanner scanner = new Scanner(System.in);
        if (!fIleRepository.getPath().isEmpty()) {
            System.out.println("Are you sure? There may be data loss in an already loaded file (YES / NO)");
            answer = scanner.nextLine().trim().toUpperCase();
            if (!answer.equals(AGREEMENT)) {
                return;
            }
        }
        System.out.println("Enter file name");
        String filename = scanner.nextLine();
        LOGGER.log(Level.INFO, "Creating file {0}", filename);
        if (fIleRepository.newFile(filename)) {
            userService.clearUsers();
            LOGGER.log(Level.INFO, "File {0} successfully created", fIleRepository.getPath());
        } else {
            System.out.println("File already exists. Do you want to replace it? (YES / NO)");
            answer = scanner.nextLine().trim().toUpperCase();
            if (answer.equals(AGREEMENT)) {
                fIleRepository.createFile(filename);
                userService.clearUsers();
                LOGGER.log(Level.INFO, "File {0} successfully created", fIleRepository.getPath());
                System.out.println(String.format("File %s successfully created", fIleRepository.getPath()));
                System.out.println();
            }
        }
    }

    private void saveFileAs() throws IOException {
        if (fIleRepository.getPath().isEmpty()) {
            LOGGER.log(Level.WARNING, "Please load data first");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter file name");
        String filename = scanner.nextLine();

        LOGGER.log(Level.INFO, "Saving file {0}", filename);

        File file = new File(filename);
        if (file.exists()) {
            System.out.println("File already exists. Do you want to replace it? (YES / NO)");
            String answer = scanner.nextLine().trim().toUpperCase();
            if (!answer.equals(AGREEMENT)) {
                return;
            }
        }

        fIleRepository.save(userService.getUsers(),filename);

        LOGGER.log(Level.INFO, "File {0} successfully saved", fIleRepository.getPath());
        System.out.println(String.format("File %s successfully saved", fIleRepository.getPath()));
        System.out.println();
    }

    private void removeUser() {
        if (userService.getUsers().isEmpty()) {
            LOGGER.log(Level.WARNING, "Please load data first");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter full name");
        String name = scanner.nextLine();

        LOGGER.log(Level.INFO, "Removing user {0}", name);

        if (userService.remove(name)) {
            LOGGER.log(Level.INFO, "User {0} successfully removed", name);
            System.out.println(String.format("User %s successfully removed", name));
            System.out.println();
        } else {
            LOGGER.log(Level.WARNING, "User {0} does not exist", name);
            System.out.println();
        }
    }

    private void addUser() {
        if (fIleRepository.getPath().isEmpty()) {
            LOGGER.log(Level.WARNING, "Please load data first");
            return;
        }

        UserInfo user = getUserInfo();
        if (user == null) return;
        LOGGER.log(Level.INFO, "Adding user {0}", user);
        userService.add(user);
        LOGGER.log(Level.INFO, "User successfully added");
        System.out.println(String.format("User %s successfully added", user));
        System.out.println();
    }

    private UserInfo getUserInfo() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter full name");
        String name = scanner.nextLine();
        if (userService.getUsers().containsKey(name.toUpperCase())) {
            LOGGER.log(Level.WARNING, "Repetition of full name");
            return null;
        }
        System.out.println("Enter age");
        Integer age = 0;
        try {
            age = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Age must be numeric");
            return null;
        }
        System.out.println("Enter phone number");
        String phone = scanner.nextLine();
        System.out.println("Enter sex: MALE or FEMALE");
        String sex = scanner.nextLine();
        System.out.println("Enter address");
        String address = scanner.nextLine();

        return new UserInfo(name, age, phone, sex, address);
    }

    private void search() {
        if (userService.getUsers().isEmpty()) {
            LOGGER.log(Level.WARNING, "Please load data first");
            System.out.println("Please load data first");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter full name");
        String name = scanner.nextLine();

        LOGGER.log(Level.INFO, "Searching {0}", name);

        UserInfo user = userService.find(name);

        if (user == null) {
            LOGGER.log(Level.WARNING, "User is not found");
        } else {
            LOGGER.log(Level.INFO, "User {0} is found", name);
            System.out.println(String.format("User %s is found", name));
            System.out.println(user);
            System.out.println();
        }
    }

    private void loadFile() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter filename");
        String filename = scanner.nextLine();

        LOGGER.log(Level.INFO, "Reading file {0}", filename);

        userService = fIleRepository.readFromFile(filename);

        LOGGER.log(Level.INFO, "File {0} successfully loaded", filename);
        System.out.println(String.format("File %s successfully loaded", filename));
        System.out.println();
    }

    private void handleError(IllegalArgumentException e) {
        LOGGER.log(Level.WARNING, "Incorrect command entry", e);
        errorsCount++;
        if (errorsCount > MAX_ERRORS) {
            throw new ConsoleNotWorkingException(e);
        }
    }
}
