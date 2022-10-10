package com.example.fruitfall.exceptions;

public class IncorrectStringException extends RuntimeException {

    private final String wrongString;
    private final String justification;

    public IncorrectStringException(String wrongString, String justification) {
        this.wrongString = wrongString;
        this.justification = justification;
    }

    public IncorrectStringException(String wrongString) {
        this(wrongString, "");
    }

    @Override
    public String getMessage() {
        return "String incorrectly parsed for level initialization : " + wrongString + (justification.isEmpty() ? "" : " (" + justification + ")");
    }
}
