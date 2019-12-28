package com.company;

/**
 * extends the RuntimeException in order to check error during runtime
 * this class used to detect a wrong extension of a file
 */
public class IncorrectFileExtensionException extends RuntimeException{
    public IncorrectFileExtensionException(String errorMessage){
        super(errorMessage);
    }
}
