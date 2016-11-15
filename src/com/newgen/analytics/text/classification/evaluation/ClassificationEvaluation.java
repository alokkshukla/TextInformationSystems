package com.newgen.analytics.text.classification.evaluation;

import java.util.Map;

/**
 * Created by alok.shukla on 11/15/2016.
 */
public class ClassificationEvaluation {
    public double[] getEvaluationParameter(ConfusionMatrix c){
        double[] parameters = new double[2];
        double macroAveragePrecision = 0.0;
        double microAveragePrecision = 0.0;
        Integer pooledNum=0,pooledDenom=0;
        Map<String, Map<String,Integer>> matrix = c.getConfusionMatrix();
        for (Map.Entry<String, Map<String,Integer>> entry : matrix.entrySet())
        {

            Integer num = matrix.get(entry.getKey()).get(entry.getKey());
            pooledNum+=num;
            Integer denom = 0;
            Map<String,Integer> entries =  matrix.get(entry.getKey());
            for (Map.Entry<String,Integer> value : entries.entrySet()) {

                denom+=entries.get(value.getKey());

            }
            pooledDenom+=denom;
            macroAveragePrecision+=(double)num/denom;

        }
        macroAveragePrecision/=matrix.size();
        microAveragePrecision=(double)pooledNum/pooledDenom;
        parameters[0] = macroAveragePrecision;
        parameters[1] = microAveragePrecision;
        return parameters;
    }
}
