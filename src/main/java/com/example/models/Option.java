package com.example.models;

public enum Option {
    A, B, C, D;

    public static Option fromString(String str) {
        if (str == null || str.isBlank())
            return null;
        try {
            return Option.valueOf(str.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}