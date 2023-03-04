package com.vsu.exeption;

import java.io.IOException;

public class ConsoleNotWorkingException extends RuntimeException {
    public ConsoleNotWorkingException(Throwable cause) {
        super(cause);
    }
}
