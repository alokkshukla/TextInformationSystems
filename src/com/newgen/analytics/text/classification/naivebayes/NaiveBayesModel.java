package com.newgen.analytics.text.classification.naivebayes;


import com.newgen.analytics.text.utils.PreProcessing;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by alok.shukla on 9/30/2016.
 */
public class NaiveBayesModel {

    private Map<String, Map<String, Integer>> nbModel;
    private Map<String, Integer> catCounts;

    public Map<String, Integer> getCatDocCounts() {
        return catDocCounts;
    }

    public void setCatDocCounts(Map<String, Integer> catDocCounts) {
        this.catDocCounts = catDocCounts;
    }

    private Map<String, Integer> catDocCounts;
    private PreProcessing preProcessing;


    private int noOfDocs;

    private Map<String, BigDecimal> catProb;


    int vocabLength;

    public Map<String, Map<String, Integer>> getNbModel() {
        return nbModel;
    }

    public void setNbModel(Map<String, Map<String, Integer>> nbModel) {
        this.nbModel = nbModel;
    }

    public PreProcessing getPreProcessing() {
        return preProcessing;
    }

    public void setPreProcessing(PreProcessing preProcessing) {
        this.preProcessing = preProcessing;
    }

    public NaiveBayesModel() {
        nbModel = new HashMap<String, Map<String, Integer>>();
        preProcessing = new PreProcessing();
        catCounts = new HashMap<String, Integer>();
        vocabLength = 0;
        catProb = new HashMap<String, BigDecimal>();
        noOfDocs = 0;
        catDocCounts = new HashMap<String, Integer>();
    }

    public int getVocabLength() {
        return vocabLength;
    }

    public Map<String, BigDecimal> getCatProb() {
        return catProb;
    }

    public void setCatProb(Map<String, BigDecimal> catProb) {
        this.catProb = catProb;
    }


    public void setVocabLength(int vocabLength) {
        this.vocabLength = vocabLength;
    }

    public NaiveBayesModel(Map<String, Map<String, Integer>> nbModel, PreProcessing preProcessing) {

        this.nbModel = nbModel;
        this.preProcessing = preProcessing;
    }

    public Map<String, Integer> getCatCounts() {
        return catCounts;
    }

    public void setCatCounts(Map<String, Integer> catCounts) {
        this.catCounts = catCounts;
    }

    public int getNoOfDocs() {
        return noOfDocs;
    }

    public void setNoOfDocs(int noOfDocs) {
        this.noOfDocs = noOfDocs;
    }

    public void addToModel(String content, String cat) throws Exception {
        setNoOfDocs(getNoOfDocs() + 1);
        Map<String, Integer> tempCounts = getCatDocCounts();
        if (null == tempCounts.get(cat)) {
            tempCounts.put(cat, 1);
        } else {
            tempCounts.put(cat, tempCounts.get(cat) + 1);
        }
        setCatDocCounts(tempCounts);


        if (null == nbModel) {
            nbModel = new HashMap<String, Map<String, Integer>>();
        }
        List<String> features = (preProcessing.tokenize(preProcessing.dealWithNegation(content)));
        int length = features.size();
        if (null == catCounts) {
            catCounts = new HashMap<String, Integer>();
        }
        if (null == catCounts.get(cat)) {
            catCounts.put(cat, length);
        } else {
            catCounts.put(cat, catCounts.get(cat) + length);
        }
        for (int i = 0; i < length; i++) {
            String word = features.get(i);
            if (null == nbModel.get(word)) {
                setVocabLength(getVocabLength() + 1);
                Map<String, Integer> wordMap = new HashMap<String, Integer>();
                wordMap.put(cat, 1);
                nbModel.put(word, wordMap);
            } else {
                if (null == nbModel.get(word).get(cat)) {
                    Map<String, Integer> wordMap = nbModel.get(word);
                    wordMap.put(cat, 1);
                    nbModel.put(word, wordMap);
                } else {
                    int count = nbModel.get(word).get(cat);
                    Map<String, Integer> wordMap = nbModel.get(word);
                    wordMap.put(cat, count + 1);
                    nbModel.put(word, wordMap);
                }
            }
        }
        Map<String, BigDecimal> temp = getCatProb();
        for (Map.Entry<String, BigDecimal> entry : temp.entrySet()) {
            BigDecimal prob = BigDecimal.valueOf(getCatDocCounts().get(entry.getKey()).doubleValue() / getNoOfDocs());
            temp.put(entry.getKey(), prob);

        }
        setCatProb(temp);
        temp = getCatProb();
        BigDecimal prob = BigDecimal.valueOf(getCatDocCounts().get(cat).doubleValue() / getNoOfDocs());
        temp.put(cat, prob);
        setCatProb(temp);

    }


    public static void main(String[] args) throws Exception {
        String content = "just plain boring";
        String cat = "NEG";
        NaiveBayesModel model = new NaiveBayesModel();
        model.addToModel(content, cat);
        System.out.println(model.getNbModel());
        content = "entirely predictable and lacks energy";
        model.addToModel(content, cat);
        System.out.println(model.getNbModel());
        content = "no surprises and very few laughs";
        model.addToModel(content, cat);
        System.out.println(model.getNbModel());
        cat = "POS";
        content = "very powerful";
        model.addToModel(content, cat);
        System.out.println(model.getNbModel());
        content = "the most fun film of the summer";
        model.addToModel(content, cat);
        System.out.println(model.getNbModel());
        System.out.println(model.getCatCounts());
        System.out.println(model.getVocabLength());
        System.out.println(model.getNoOfDocs());
        System.out.println(model.getCatProb());
        System.out.println(model.getCatDocCounts());
        System.out.println(model.getCategoryProbability("predictable with no originality", "POS").toPlainString());
        System.out.println(model.getCategoryProbability("predictable with no originality", "NEG").toString());
    }

    public BigDecimal getCategoryProbability(String content, String cat) throws Exception {
        BigDecimal totalProb = BigDecimal.valueOf(1.0);
        int catCount = getCatCounts().get(cat);
        int vocabLength = getVocabLength();
        BigDecimal catProb = (getCatProb().get(cat));
        List<String> features =(preProcessing.tokenize(preProcessing.dealWithNegation(content)));
        for (int i = 0; i < features.size(); i++) {
            String word = features.get(i);
            BigDecimal prob = BigDecimal.valueOf(0.0);
            int wordCountCat = 0;
            if (null != getNbModel().get(word)) {
                if (null == getNbModel().get(word).get(cat)) {
                    wordCountCat = 0;
                } else {
                    wordCountCat = getNbModel().get(word).get(cat);
                }
            }
            prob = BigDecimal.valueOf((wordCountCat + 1) / (double) (catCount + vocabLength));

            totalProb = totalProb.multiply(prob);
        }
        return totalProb.multiply(catProb);
    }
    public static void main1(String[] args) throws Exception {
//        SentimentAnalyzer sent = new SentimentAnalyzer();
        NaiveBayesModel model = new NaiveBayesModel();
        NaiveBayesModel model2 = new NaiveBayesModel();
        BufferedReader br = null;
        String line;
        try {

            br = new BufferedReader(new FileReader("data/TrainingData.csv"));
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                    {
                        model.addToModel(data[1], data[0]);
                    }

            }
        }catch(Exception e){

        }


        System.out.println(model.getCatCounts());
        System.out.println(model.getVocabLength());
        System.out.println(model.getNoOfDocs());
        System.out.println(model.getCatProb());
        System.out.println(model.getCatDocCounts());
        Map<String,BigDecimal> scores = new HashMap<String,BigDecimal>();

        File file = new File("results/ResulsNB5.csv");


        // if file doesnt exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("Expected Category, Predicted Category, Prob, Content\n");
        try {

            br = new BufferedReader(new FileReader("data/Data.test"));
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");

//                    System.out.println("-----------------------------------------------------------------------------------------------------");
                bw.write(data[0]+",");


                    scores.put("COMPLAINT",model.getCategoryProbability(data[1], "COMPLAINT"));

                    scores.put("REQUEST", model.getCategoryProbability(data[1], "REQUEST"));
                    scores.put("LEAD", model.getCategoryProbability(data[1], "LEAD"));
                    scores.put("SUGGESTION", model.getCategoryProbability(data[1], "SUGGESTION"));
                    scores.put("COMPLIMENT",model.getCategoryProbability(data[1], "COMPLIMENT"));
                    Iterator it = model.sortByValue(scores).entrySet().iterator();
                    Map.Entry<String,BigDecimal> entry= (Map.Entry<String, BigDecimal>) it.next();



                        bw.write(entry.getKey()+entry.getValue().toEngineeringString() + ",");
                Map.Entry<String,BigDecimal> entry2= (Map.Entry<String, BigDecimal>) it.next();
                bw.write(entry2.getKey()+entry2.getValue().toString() + ",");

                bw.write(data[1]+"\n");
                    }




//                System.out.println("-----------------------------------------------------------------------------------------------------");





        }catch(Exception e){

        }
        bw.close();
        br.close();
//        System.out.println("Req: "+model.getCategoryProbability("can you help me with fixing this", "REQUEST").toPlainString());
//    //    System.out.println(model.getCategoryProbability("Looking for a credit card", "COMPLAINT").toPlainString());
//        System.out.println("Lead: "+model.getCategoryProbability("can you help me with fixing this", "LEAD").toPlainString());
//        System.out.println("Sugg: "+model.getCategoryProbability("can you help me with fixing this", "SUGGESTION").toPlainString());
//        System.out.println("Comp: "+model.getCategoryProbability("can you help me with fixing this", "COMPLIMENT").toPlainString());
    }
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

}
