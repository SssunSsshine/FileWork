package com.vsu.exeption;

public class InvalidFormatException extends RuntimeException{
    public InvalidFormatException(String message) {
        super(message);
    }
}
