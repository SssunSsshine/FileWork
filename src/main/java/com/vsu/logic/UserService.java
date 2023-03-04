package com.vsu.logic;

import com.vsu.exeption.InvalidFormatException;
import com.vsu.info.UserInfo;
import org.apache.commons.lang3.ObjectUtils;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

public class UserService {
    public static final int MIN_AGE = 0;
    public static final String SEPARATOR = ";";
    private Map<String, UserInfo> users;

    public UserService() {
        this.users = new HashMap<>();
    }

    public UserService(Map<String, UserInfo> users) {
        this.users = users;
    }
    public void clearUsers(){
        users = new HashMap<>();
    }

    public Map<String, UserInfo> getUsers() {
        return users;
    }

    public void add(UserInfo user) throws InvalidParameterException {
        if (!isValidUser(user)) {
            throw new InvalidFormatException(String.format("Invalid data format %s", user));
        }
        users.put(user.getFullName().toUpperCase(), user);
    }

    private static boolean isValidUser(UserInfo user) {
        return isNotNull(user)  &&
                !isBlank(user)&&
                user.getAge() > MIN_AGE &&
                !hasSemicolon(user);
    }

    private static boolean hasSemicolon(UserInfo user) {
        return user.getFullName().contains(SEPARATOR)||
                user.getAddress().contains(SEPARATOR) ||
                user.getPhoneNumber().contains(SEPARATOR);
    }

    private static boolean isBlank(UserInfo user) {
        return user.getPhoneNumber().isBlank() ||
                user.getFullName().isBlank() ||
                user.getAddress().isBlank();
    }

    private static boolean isNotNull(UserInfo user) {
        return ObjectUtils.allNotNull(user.getPhoneNumber(),
                                        user.getFullName(),
                                        user.getAddress(),
                                        user.getAge(),
                                        user.getSex());
    }

    public boolean remove(String name) {
        return users.remove(name.toUpperCase()) != null;
    }

    public UserInfo find(String fullName) {
        return users.get(fullName.toUpperCase());
    }

}
