package com.newgen.analytics.text.classification.evaluation;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alok.shukla on 11/15/2016.
 */
public class ConfusionMatrix {
    public Map<String, Map<String, Integer>> getConfusionMatrix() {
        return confusionMatrix;
    }

    public ConfusionMatrix(String[] categories) {
        this.confusionMatrix = new HashMap<>();
        for(int i=0;i<categories.length;i++){
            HashMap<String,Integer> entries = new HashMap<>();

            for(int j=0;j<categories.length;j++){
                entries.put(categories[j],0);
            }
            this.confusionMatrix.put(categories[i],entries);
        }
    }

    public ConfusionMatrix(Map<String, Map<String, Integer>> confusionMatrix) {
        this.confusionMatrix = confusionMatrix;
    }

    public void setConfusionMatrix(Map<String, Map<String, Integer>> confusionMatrix) {
        this.confusionMatrix = confusionMatrix;
    }

    Map<String,Map<String,Integer>> confusionMatrix;



}
