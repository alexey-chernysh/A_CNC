package ru.android_cnc.acnc.Interpreter.Exceptions;

/**
 * Created by Sales on 27.04.2015.
 */
public class EvolutionException extends Exception {

    private	String message_ = null;

    public EvolutionException(String msg){
        message_ = msg;
    }

}
