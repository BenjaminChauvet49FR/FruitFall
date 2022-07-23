package com.example.fruitfall.exceptions;

public class IncorrectStringException extends RuntimeException {

    private String wrongString;

    public IncorrectStringException(String wrongString) {
        this.wrongString = wrongString;
    }

    @Override
    public String getMessage() {
        return "String incorrectly parsed for level initialization : " + wrongString;
    }
}
