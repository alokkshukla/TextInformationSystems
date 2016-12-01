//package com.newgen.analytics.text.retrieval;
//
//import com.newgen.analytics.text.classification.evaluation.ConfusionMatrix;
//import com.newgen.analytics.text.classification.naivebayes.NaiveBayesModel;
//import com.newgen.analytics.text.entities.*;
//import com.newgen.analytics.text.utils.PreProcessing;
//
//import javax.print.Doc;
//import javax.print.attribute.IntegerSyntax;
//import java.io.*;
//import java.math.BigDecimal;
//import java.math.BigInteger;
//import java.util.*;
//import java.util.stream.Collectors;
//
//
//public class BM25 {
//    public Map<String, BigDecimal> getBM25Score(InvertedIndex idx, Document query, int k, double b) throws Exception {
//        Map<String, BigDecimal> scores = new HashMap<String, BigDecimal>();
//
//        Corpus c = idx.getCorpus();
//        double avdl = c.getAvdl();
//        Set<Document> docs = c.getCorpus();
//        int size = c.getSize();
//        Iterator it = docs.iterator();
//        while (it.hasNext()) {
//            Document d = (Document) it.next();
//            List<String> tokens = PreProcessing.removeStopWords(PreProcessing.removeDuplicates(PreProcessing.tokenize(query.getContent())));
//            for (int j = 0; j < tokens.size(); j++) {
//                String word = tokens.get(j);
//                int wq = query.getContentMap().get(word);
//                Map<String, Integer> p = c.getPosting(word).getPosting();
//                int wd = 0;
//                if (null != p.get(Integer.toString(d.getContent().hashCode()))) {
//                    wd = p.get(Integer.toString(d.getContent().hashCode()));
//                }
//
//                int df = p.size();
//                BigDecimal IDF = BigDecimal.valueOf(0);
//                if (df != 0) {
//                    IDF = BigDecimal.valueOf(Math.log((size + 1) / (double) df));
//
//                }
//                BigDecimal TF = BigDecimal.valueOf(((k + 1) * wd) / (wd + (k * (1 - b + b * ((double) d.getLength() / avdl)))));
//                TF = TF.multiply(IDF);
//                TF = TF.multiply(BigDecimal.valueOf(wq));
//                scores.put(Integer.toString(d.getContent().hashCode()), TF);
//            }
//        }
//        return scores;
//    }
//
//    public void setTFIDF(InvertedIndex idx) throws Exception {
//        File file = new File("results/TFIDFRes.csv");
//
//
//        // if file doesnt exists, then create it
//        if (!file.exists()) file.createNewFile();
//
//        FileWriter fw = new FileWriter(file.getAbsoluteFile());
//        BufferedWriter bw = new BufferedWriter(fw);
//
//        bw.write("Word,Score\n");
//        Map<String, BigDecimal> scores = new HashMap<>();
//        Corpus c = idx.getCorpus();
//        Set<Document> documents = c.getCorpus();
//        double avdl = c.getAvdl();
//        int size = c.getSize();
//        Vocabulary v = new Vocabulary(c);
//        Set<String> words = v.getVocab();
//        Iterator it = words.iterator();
//
//        while (it.hasNext()) {
//            String word = (String) it.next();
//            BigDecimal score = BigDecimal.valueOf(0);
//            Iterator i = documents.iterator();
//            while (i.hasNext()) {
//                Document d = (Document) i.next();
//
//                int wd = 0;
//
//                Map<String, Integer> p = c.getPosting(word).getPosting();
//                if (null != p.get(Integer.toString(d.getContent().hashCode()))) {
//                    wd = p.get(Integer.toString(d.getContent().hashCode()));
//                }
//                int df = p.size();
//                BigDecimal IDF = BigDecimal.valueOf(0);
//                if (df != 0) {
//                    IDF = BigDecimal.valueOf(Math.log((size + 1) / (double) df));
//
//                }
//                BigDecimal TF = BigDecimal.valueOf(((250 + 1) * wd) / (wd + (250 * (1 - 0.7 + 0.7 * ((double) d.getLength() / avdl)))));
//                TF = TF.multiply(IDF);
//                score = score.add(TF);
//
//            }
//            scores.put(word, score);
//            bw.write(word + ",");
//            bw.write(score.toString() + "\n");
//        }
//
//
//        bw.close();
//    }
//
//
//
//    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
//        return map.entrySet()
//                .stream()
//                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
//                .collect(Collectors.toMap(
//                        Map.Entry::getKey,
//                        Map.Entry::getValue,
//                        (e1, e2) -> e1,
//                        LinkedHashMap::new
//                ));
//    }
//
//
//    public InvertedIndex trainModel(String trainFilePath){
//        Corpus c = new Corpus();
//
//        BufferedReader br = null;
//        String[] data = new String[2];
//        String line;
//        try {
//
//            br = new BufferedReader(new FileReader(trainFilePath));
//            while ((line = br.readLine()) != null) {
//                data = line.split("\t");
//                {
//                    Document d = new Document();
//                    d.createDocument(data[1], "");
//                    d.setLabel(data[0]);
//                    c.addToCorpus(d);
//                }
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        }
//
//
//        InvertedIndex idx = new InvertedIndex(c);
//        return idx;
//    }
//
//    public ConfusionMatrix testModel(String testFilePath, InvertedIndex idx, String outoutFile, String[] labels) throws Exception {
//
//        ConfusionMatrix results = new ConfusionMatrix(labels);
//        Map<String, Map<String, Integer>> confusionMatrix = results.getConfusionMatrix();
//
//        File resfile = new File(outoutFile);
//
//        Corpus c = idx.getCorpus();
//        // if file doesnt exists, then create it
//        if (!resfile.exists()) {
//            resfile.createNewFile();
//        }
//        String line = "";
//        FileWriter fw = new FileWriter(resfile.getAbsoluteFile());
//        BufferedWriter bw = new BufferedWriter(fw);
//        BufferedReader br = null;
//        bw.write("Expected Category, Predicted Category,Content\n");
//        try {
//
//            br = new BufferedReader(new FileReader(testFilePath));
//            while ((line = br.readLine()) != null) {
//                String[] data = line.split("\t");
//                Document query = new Document();
//                query.createDocument(data[1], "");
//                query.setLabel(data[0]);
//                double b = 0.7;
//                int k = 5;
//                Iterator it = this.sortByValue(this.getBM25Score(idx, query, k, b)).entrySet().iterator();
//                Map.Entry<String, BigDecimal> entry = (Map.Entry<String, BigDecimal>) it.next();
//                String expected = data[0];
//                Document d = c.getDocByID(entry.getKey());
//                String predicted = d.getLabel();
//                bw.write(expected + ",");
//                bw.write(predicted + ",");
//                bw.write(data[1] + "\n");
//                if (confusionMatrix.get(predicted) != null) {
//                    Map<String, Integer> entries = confusionMatrix.get(predicted);
//                    if (entries.get(expected) != null) {
//                        entries.put(expected, entries.get(expected) + 1);
//                    } else {
//                        entries.put(expected, 1);
//                    }
//                } else {
//                    Map<String, Integer> entries = new HashMap<>();
//                    entries.put(expected, 1);
//                    confusionMatrix.put(predicted, entries);
//                }
//
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        bw.close();
//        br.close();
//        return results;
//    }
//
//
//    public static void main(String[] args) throws Exception {
//        BM25 bm = new BM25();
//        InvertedIndex idx = bm.trainModel("data/Train");
//        String[] cat = {"COMPLAINT","COMPLIMENT","MISCELLANEOUS","REQUEST","SUGGESTION"};
//        ConfusionMatrix res = bm.testModel("data/Test",idx,"results/kNN.csv",cat);
//        System.out.println(res.getConfusionMatrix());
//    }
//
//
//}