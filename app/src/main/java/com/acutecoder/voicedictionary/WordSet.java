package com.acutecoder.voicedictionary;

/*
 *Created by Bhuvaneshwaran
 *on 11:19 AM, 12/6/2022
 *AcuteCoder
 */

public class WordSet {

    private final String word;
    private final WordMeaning noun, verb, adj, adv;

    public WordSet(String word, WordMeaning noun, WordMeaning verb, WordMeaning adj, WordMeaning adv) {
        this.word = word;
        this.noun = noun;
        this.verb = verb;
        this.adj = adj;
        this.adv = adv;
    }

    public String getWord() {
        return word;
    }

    public WordMeaning getNoun() {
        return noun;
    }

    public WordMeaning getVerb() {
        return verb;
    }

    public WordMeaning getAdj() {
        return adj;
    }

    public WordMeaning getAdv() {
        return adv;
    }
}
