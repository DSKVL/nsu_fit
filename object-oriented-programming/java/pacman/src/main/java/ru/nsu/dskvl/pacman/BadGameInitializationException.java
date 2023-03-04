package ru.nsu.dskvl.pacman;

public class BadGameInitializationException extends Exception {
    private final String message;
    public BadGameInitializationException(String mes) {
        message = mes;
    }
    @Override
    public String getMessage(){
        return message;
    }
}
