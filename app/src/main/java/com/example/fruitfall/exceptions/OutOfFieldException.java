package com.example.fruitfall.exceptions;

public class OutOfFieldException extends RuntimeException {

    private String wrongString;
    private int xConflict;
    private int yConflict;

    public OutOfFieldException(String wrongString, int xConflict, int yConflict) {
        this.wrongString = wrongString;
        this.xConflict = xConflict;
        this.yConflict = yConflict;
    }

    @Override
    public String getMessage() {
        return "String that led to a message : " + wrongString + " ; conflictual coordinates : " + this.xConflict + "," + this.yConflict;
    }
}
