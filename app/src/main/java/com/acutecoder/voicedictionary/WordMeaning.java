package com.acutecoder.voicedictionary;

/*
 *Created by Bhuvaneshwaran
 *on 11:31 PM, 12/5/2022
 *AcuteCoder
 */

public class WordMeaning {

    private final String meaning;
    private final String example;

    public WordMeaning(String meaning, String example) {
        this.meaning = meaning;
        this.example = example;
    }

    public String getMeaning() {
        return meaning;
    }

    public String getExample() {
        return example;
    }

}
