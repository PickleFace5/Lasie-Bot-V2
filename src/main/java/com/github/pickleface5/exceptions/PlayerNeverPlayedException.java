package com.github.pickleface5.exceptions;

public class PlayerNeverPlayedException extends Exception {
    public PlayerNeverPlayedException(String errorMessage) {
        super(errorMessage);
    }
}
