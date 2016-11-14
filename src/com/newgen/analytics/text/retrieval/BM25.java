package com.newgen.analytics.text.retrieval;

import com.newgen.analytics.text.classification.naivebayes.NaiveBayesModel;
import com.newgen.analytics.text.entities.*;
import com.newgen.analytics.text.utils.PreProcessing;

import javax.print.Doc;
import javax.print.attribute.IntegerSyntax;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;


public class BM25 {
    public Map<String, BigDecimal> getBM25Score(InvertedIndex idx, Document query, int k, double b) throws Exception {
        Map<String, BigDecimal> scores = new HashMap<String, BigDecimal>();

        Corpus c = idx.getCorpus();
        double avdl = c.getAvdl();
        Set<Document> docs = c.getCorpus();
        int size = c.getSize();
        Iterator it = docs.iterator();
        while (it.hasNext()) {
            Document d = (Document) it.next();
            List<String> tokens = PreProcessing.removeStopWords(PreProcessing.removeDuplicates(PreProcessing.tokenize(query.getContent())));
            for (int j = 0; j < tokens.size(); j++) {
                String word = tokens.get(j);
                int wq = query.getContentMap().get(word);
                Map<String, Integer> p = c.getPosting(word).getPosting();
                int wd = 0;
                if (null != p.get(Integer.toString(d.getContent().hashCode()))) {
                    wd = p.get(Integer.toString(d.getContent().hashCode()));
                }

                int df = p.size();
                BigDecimal IDF = BigDecimal.valueOf(0);
                if (df != 0) {
                    IDF = BigDecimal.valueOf(Math.log((size + 1) / (double) df));

                }
                BigDecimal TF = BigDecimal.valueOf(((k + 1) * wd) / (wd + (k * (1 - b + b * ((double) d.getLength() / avdl)))));
                TF = TF.multiply(IDF);
                TF = TF.multiply(BigDecimal.valueOf(wq));
                scores.put(Integer.toString(d.getContent().hashCode()), TF);
            }
        }
        return scores;
    }

    public void setTFIDF(InvertedIndex idx) throws Exception {
        File file = new File("results/TFIDFRes.csv");


        // if file doesnt exists, then create it
        if (!file.exists()) file.createNewFile();

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write("Word,Score\n");
        Map<String, BigDecimal> scores = new HashMap<>();
        Corpus c = idx.getCorpus();
        Set<Document> documents = c.getCorpus();
        double avdl = c.getAvdl();
        int size = c.getSize();
        Vocabulary v = new Vocabulary(c);
        Set<String> words = v.getVocab();
        Iterator it = words.iterator();

        while (it.hasNext()) {
            String word = (String) it.next();
            BigDecimal score = BigDecimal.valueOf(0);
            Iterator i = documents.iterator();
            while (i.hasNext()) {
                Document d = (Document) i.next();

                int wd = 0;

                Map<String, Integer> p = c.getPosting(word).getPosting();
                if (null != p.get(Integer.toString(d.getContent().hashCode()))) {
                    wd = p.get(Integer.toString(d.getContent().hashCode()));
                }
                int df = p.size();
                BigDecimal IDF = BigDecimal.valueOf(0);
                if (df != 0) {
                    IDF = BigDecimal.valueOf(Math.log((size + 1) / (double) df));

                }
                BigDecimal TF = BigDecimal.valueOf(((250 + 1) * wd) / (wd + (250 * (1 - 0.7 + 0.7 * ((double) d.getLength() / avdl)))));
                TF = TF.multiply(IDF);
                score = score.add(TF);

            }
            scores.put(word, score);
            bw.write(word + ",");
            bw.write(score.toString() + "\n");
        }


        bw.close();
    }

    public static void main1(String[] args) throws Exception {
        Document d1 = new Document();
        d1.createDocument("Alok Kumar ALok", "");

        Document d2 = new Document();
        d2.createDocument("Shukla alok", "");

        Corpus c = new Corpus();
        c.addToCorpus(d1);
        c.addToCorpus(d2);

        Vocabulary v = new Vocabulary(c);

//
//        System.out.println(v.getVocab());

        InvertedIndex idx = new InvertedIndex(c);
        BM25 scorer = new BM25();
        Document query = new Document();
        query.createDocument("alok", "");
        System.out.println(scorer.getBM25Score(idx, query, 25, 0.5));
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


    public static void main7(String[] args) throws Exception {
        Corpus c = new Corpus();
        BM25 scorer = new BM25();
        String[] data = new String[2];
        BufferedReader br = null;
        String line;
        try {

            br = new BufferedReader(new FileReader("data/Data.tsv"));
            while ((line = br.readLine()) != null) {
                data = line.split("\t");
                {
                    Document d = new Document();
                    if (data.length > 1) {
                        d.createDocument(data[1], "");
                        d.setLabel(data[0]);
                        c.addToCorpus(d);
                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            // System.out.print(data);
        }

        //Vocabulary v = new Vocabulary(c);

//        System.out.println(v.getVocab());
        InvertedIndex idx = new InvertedIndex(c);
        scorer.setTFIDF(idx);
    }

    public static void main(String[] args) throws Exception {
        Corpus c = new Corpus();
        BM25 scorer = new BM25();
        BufferedReader br = null;
        String[] data = new String[2];
        String line;
        try {

            br = new BufferedReader(new FileReader("data/Train"));
            while ((line = br.readLine()) != null) {
                data = line.split("\t");
                {
                    Document d = new Document();
                    d.createDocument(data[1], "");
                    d.setLabel(data[0]);
                    c.addToCorpus(d);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();

        }

//        Vocabulary v = new Vocabulary();
//        v.populateVocab(c);
//        System.out.println(v.getVocab());
        InvertedIndex idx = new InvertedIndex(c);


        File file = new File("results/ResulsBM25Final.csv");


        // if file doesnt exists, then create it
        if (!file.exists()) file.createNewFile();

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("Expected Cat, Pred Cat, Content\n");
        try {

            br = new BufferedReader(new FileReader("data/Test"));
            while ((line = br.readLine()) != null) {
                data = line.split("\t");


                Document query = new Document();
                query.createDocument(data[1], "");
                query.setLabel(data[0]);

                double b = 0.7;

                Map<String, Integer> results = new HashMap<>();

                int k = 50;

                Iterator it = scorer.sortByValue(scorer.getBM25Score(idx, query, k, b)).entrySet().iterator();
                int count = 0;
//                bw.write(Integer.toString(k) + ",= K," + Double.toString(b) + ",= b," + "SEP\n");

                Map.Entry<String, BigDecimal> entry = (Map.Entry<String, BigDecimal>) it.next();

                if(!data[0].equalsIgnoreCase(entry.getKey())) {
                    bw.write(data[0] + "," + (String) entry.getKey() + "," + data[1] + "\n");
                }

//                while (count++ < 10) {
//
//
//                    Map.Entry<String, BigDecimal> entry = (Map.Entry<String, BigDecimal>) it.next();
//                    Document d = c.getDocByID(entry.getKey());
//
//                    String cat = d.getLabel();
//                    if (results.get(cat) != null) {
//                        results.put(cat, results.get(cat) + 1);
//                    } else {
//                        results.put(cat, 1);
//                    }
//                }
//
//                Iterator i = scorer.sortByValue(results).entrySet().iterator();
//                Map.Entry<String, Integer> e = (Map.Entry<String, Integer>) i.next();//i.next()
//                if(!data[0].equalsIgnoreCase(e.getKey())) {
//                    bw.write(data[0] + "," + (String) e.getKey() + "," + data[1] + "\n");
//                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        bw.close();
        br.close();
    }

//    public List<Double> optimise(InvertedIndex idx,String testFile){
//
//    }

}