package com.example.models;

public class Question {
    private final int id;
    private final String text;
    private final String optionA;
    private final String optionB;
    private final String optionC;
    private final String optionD;
    private final Option correctOption;

    public Question(int id, String text, String optionA, String optionB, String optionC, String optionD,
            Option correctOption) {
        this.id = id;
        this.text = text;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getOptionA() {
        return optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public Option getCorrectOption() {
        return correctOption;
    }

    // GameController-ийн Record маягийн бичиглэлтэй тааруулах туслах аргууд
    public String text() {
        return text;
    }

    public String optionA() {
        return optionA;
    }

    public String optionB() {
        return optionB;
    }

    public String optionC() {
        return optionC;
    }

    public String optionD() {
        return optionD;
    }

    public Option correctOption() {
        return correctOption;
    }
}