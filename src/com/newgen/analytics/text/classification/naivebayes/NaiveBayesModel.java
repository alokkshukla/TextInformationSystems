package com.newgen.analytics.text.classification.naivebayes;


import com.newgen.analytics.text.classification.evaluation.ClassificationEvaluation;
import com.newgen.analytics.text.classification.evaluation.ConfusionMatrix;
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
        List<String> features = PreProcessing.lemmatize(PreProcessing.tokenize(content));
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
            Map<String, Integer> wordMap = nbModel.get(word);
            if(null==wordMap.get("DOCS")){
                wordMap.put("DOCS",1);
            }else{
                wordMap.put("DOCS",wordMap.get("DOCS")+1);
            }
            nbModel.put(word, wordMap);
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

        Map<String, Map<String, Integer>> map = this.getNbModel();


    }


    public NaiveBayesModel trainModel(String trainFilePath) {
        NaiveBayesModel model = new NaiveBayesModel();
        BufferedReader br = null;
        String line;
        try {

            br = new BufferedReader(new FileReader(trainFilePath));
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                {
                    if (data.length > 1) {
                        model.addToModel(data[1], data[0].trim());
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return model;
    }


    public static void main1(String[] args) throws Exception {
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
        if (getCatCounts().get(cat) == null) {
            System.out.print("Inavlid cat");
        }
        int catCount = getCatCounts().get(cat);
        int vocabLength = getVocabLength();
        BigDecimal catProb = (getCatProb().get(cat));
        List<String> features = PreProcessing.lemmatize(PreProcessing.tokenize((content)));
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
            prob = BigDecimal.valueOf((wordCountCat + 0.45) / (double) (catCount + vocabLength));

            totalProb = totalProb.multiply(prob);
        }
        return totalProb.multiply(catProb);
    }

    public ConfusionMatrix testModel(String testFilePath, NaiveBayesModel model, String outoutFile, String[] labels) throws Exception {


        ConfusionMatrix results = new ConfusionMatrix(labels);
        Map<String, Map<String, Integer>> confusionMatrix = results.getConfusionMatrix();
        Map<String, BigDecimal> scores = new HashMap<String, BigDecimal>();
        File resfile = new File(outoutFile);


        // if file doesnt exists, then create it
        if (!resfile.exists()) {
            resfile.createNewFile();
        }
        String line = "";
        FileWriter fw = new FileWriter(resfile.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        BufferedReader br = null;
        bw.write("Expected Category, Predicted Category,Content\n");
        try {

            br = new BufferedReader(new FileReader(testFilePath));
            while ((line = br.readLine()) != null) {

                String[] data = line.split("\t");
                scores.put("COMPLAINT", model.getCategoryProbability(data[1], "COMPLAINT"));
                scores.put("REQUEST", model.getCategoryProbability(data[1], "REQUEST"));
                scores.put("MISCELLANEOUS", model.getCategoryProbability(data[1], "MISCELLANEOUS"));
                scores.put("SUGGESTION", model.getCategoryProbability(data[1], "SUGGESTION"));
                scores.put("COMPLIMENT", model.getCategoryProbability(data[1], "COMPLIMENT"));
                Iterator it = model.sortByValue(scores).entrySet().iterator();
                Map.Entry<String, BigDecimal> entry = (Map.Entry<String, BigDecimal>) it.next();
                String expected = data[0];
                String predicted = entry.getKey();
                bw.write(expected + ",");
                bw.write(predicted + ",");
                bw.write(data[1] + "\n");
                if (confusionMatrix.get(predicted) != null) {
                    Map<String, Integer> entries = confusionMatrix.get(predicted);
                    if (entries.get(expected) != null) {
                        entries.put(expected, entries.get(expected) + 1);
                    } else {
                        entries.put(expected, 1);
                    }
                } else {
                    Map<String, Integer> entries = new HashMap<>();
                    entries.put(expected, 1);
                    confusionMatrix.put(predicted, entries);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        bw.close();
        br.close();
        return results;
    }

    private void getMutualInfo(String featFile, int n) throws IOException {
        Map<String, Map<String, Integer>> map = this.getNbModel();
        File file = new File(featFile);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);

        for (Map.Entry<String, Map<String, Integer>> entry : map.entrySet()) {
            Map<String, Integer> innerMap = entry.getValue();
            double dw=0.0;
            if(null!=entry.getValue().get("DOCS")) {
                dw = entry.getValue().get("DOCS") / (double) 2320;
            }else{
                System.out.print("VOilla");
            }

            for (Map.Entry<String, Integer> innerEntry : innerMap.entrySet()) {
                double piw=0,pi=0,mi=0,chi=0;

                if (innerEntry.getKey() != "DOCS") {
                    bw.write(entry.getKey() + ",");
                    bw.write(innerEntry.getKey() + ",");
                    pi = this.getCatDocCounts().get(innerEntry.getKey()) / (double) 2320;
                    piw = innerEntry.getValue() / dw;


                    mi = Math.log(piw / pi);

                    chi = ((n * (dw * dw) * ((piw - pi) * (piw - pi))) / (dw * (1 - dw) * pi * (1 - pi)));

                    bw.write(mi +",");
                    bw.write(chi +"\n");
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String trainFile = "data/Train";
        String testFile = "data/Test";
        String resFile = "results/NBResults17Nov";
        String MIfile = "features/MutualInfo.csv";

        NaiveBayesModel helper = new NaiveBayesModel();
        ClassificationEvaluation evaluation = new ClassificationEvaluation();
        NaiveBayesModel model = helper.trainModel(trainFile);
        String[] labels = {"REQUEST", "MISCEALLANEOUS", "COMPLIMENT", "COMPLAINT", "SUGGESTION"};
        //  ConfusionMatrix mat = helper.testModel(testFile,model,resFile,labels);
        model.getMutualInfo("features/Features.csv",2320);
//        System.out.print(model.getCatDocCounts());
//
//        System.out.print(d.toPlainString());

    }

    public static void main10(String[] args) throws Exception {
//        SentimentAnalyzer sent = new SentimentAnalyzer();
        NaiveBayesModel model = new NaiveBayesModel();
        NaiveBayesModel model2 = new NaiveBayesModel();
        BufferedReader br = null;
        String line;
        try {

            br = new BufferedReader(new FileReader("data/Training"));
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                {
                    if (data.length > 1) {
                        model.addToModel(data[1], data[0].trim());
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        System.out.println(model.getCatCounts());
        System.out.println(model.getVocabLength());
        System.out.println(model.getNoOfDocs());
        System.out.println(model.getCatProb());
        System.out.println(model.getCatDocCounts());
        Map<String, BigDecimal> scores = new HashMap<String, BigDecimal>();

        File file = new File("results/ResulsNB17Nov.csv");


        // if file doesnt exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("Expected Category, Predicted Category, Next, Diff,Content\n");
        try {

            br = new BufferedReader(new FileReader("data/Test"));
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                scores.put("COMPLAINT", model.getCategoryProbability(data[1], "COMPLAINT"));
                scores.put("REQUEST", model.getCategoryProbability(data[1], "REQUEST"));
                scores.put("MISCEALLANEOUS", model.getCategoryProbability(data[1], "MISCEALLANEOUS"));
                scores.put("SUGGESTION", model.getCategoryProbability(data[1], "SUGGESTION"));
                scores.put("COMPLIMENT", model.getCategoryProbability(data[1], "COMPLIMENT"));
                Iterator it = model.sortByValue(scores).entrySet().iterator();
                Map.Entry<String, BigDecimal> entry = (Map.Entry<String, BigDecimal>) it.next();

                if (true) {

                    bw.write(data[0] + ",");
                    bw.write(entry.getKey() + ",");
//                    Map.Entry<String, BigDecimal> entry2 = (Map.Entry<String, BigDecimal>) it.next();
//                    bw.write(entry2.getKey() + "," + entry2.getValue().subtract(entry.getValue()).toPlainString() + ",");

                    bw.write(data[1] + "\n");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        bw.close();
        br.close();
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
