package com.newgen.analytics.text.classification.naivebayes;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by alok.shukla on 9/27/2016.
 */
public class VocabularyModel {

    Map<String,Set<String>>vocabModel;
    int noOfWords;


    public Map<String, Set<String>> getVocabModel() {
        return vocabModel;
    }

    public void setVocabModel(Map<String, Set<String>> vocabModel) {
        this.vocabModel = vocabModel;
    }

    public int getNoOfWords() {
        return noOfWords;
    }

    public void setNoOfWords(int noOfWords) {
        this.noOfWords = noOfWords;
    }

    public VocabularyModel() {
        this.vocabModel = new HashMap<String,Set<String>>();
    }

    public VocabularyModel(Map<String, Set<String>> vocabModel) {
        this.vocabModel = vocabModel;
    }

    public static void main(String[] args){

    }
}
