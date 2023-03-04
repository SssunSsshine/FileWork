package com.vsu.info;

import com.vsu.enumeration.Sex;

public class UserInfo {
    private final String fullName;
    private final Integer age;
    private final String phoneNumber;
    private final Sex sex;
    private final String address;

    public UserInfo(String fullName, Integer age, String phoneNumber, String sex, String address) {
        this.fullName = fullName;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.sex = Sex.valueOf(sex.toUpperCase());
        this.address = address;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAddress() {
        return address;
    }

    public String getSex() {
        return sex.name();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Integer getAge() {
        return age;
    }

    public String toStringForFile(){
        return fullName + ';' +
                age + ";" +
                phoneNumber + ';' +
                sex + ";" +
                address + ";";
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "fullName='" + fullName + '\'' +
                ", age=" + age +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", sex=" + sex +
                ", address='" + address + '\'' +
                '}';
    }
}
