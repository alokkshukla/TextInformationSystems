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


public class BM25{
    public Map<String,BigDecimal>  getBM25Score(InvertedIndex idx, Document query, int k, double b) throws Exception{
        Map<String,BigDecimal> scores = new HashMap<String,BigDecimal>();

        Corpus c = idx.getCorpus();
        Set<Document> docs = c.getCorpus();
        Iterator it = docs.iterator();
        while (it.hasNext()){
            Document d = (Document)it.next();
            List<String> tokens = PreProcessing.removeDuplicates(PreProcessing.tokenize(query.getContent()));
            for(int j=0;j<tokens.size();j++){
                String word = tokens.get(j);
                int wq = query.getContentMap().get(word);
                int wd=0;
                if(null!=c.getPosting(word).getPosting().get(Integer.toString(d.getContent().hashCode()))){
                    wd = c.getPosting(word).getPosting().get(Integer.toString(d.getContent().hashCode()));
                }

                int df = c.getPosting(word).getPosting().size();
                BigDecimal IDF = BigDecimal.valueOf(0);
                if (df!=0) {
                    IDF = BigDecimal.valueOf(Math.log((c.getSize() + 1) / (double) df));

                }
                BigDecimal TF = BigDecimal.valueOf(((k+1)*wd)/(double)(wd+(k*(1-b+b*((double)d.getLength()/(double)c.getAvdl())))));
                TF =TF.multiply(IDF);
                TF = TF.multiply(BigDecimal.valueOf(wq));
                scores.put(Integer.toString(d.getContent().hashCode()),TF);
            }
        }
        return scores;
    }

    public static void main1(String[] args) throws Exception{
        Document d1 = new Document();
        d1.createDocument("Alok Kumar ALok","");

        Document d2 = new Document();
        d2.createDocument("Shukla alok","");

        Corpus c = new Corpus();
        c.addToCorpus(d1);
        c.addToCorpus(d2);

//        Vocabulary v = new Vocabulary();
//        v.populateVocab(c);
//
//        System.out.println(v.getVocab());

        InvertedIndex idx = new InvertedIndex(c);
        BM25 scorer = new BM25();
        Document query = new Document();
        query.createDocument("alok","");
        System.out.println(scorer.getBM25Score(idx,query,25,0.5));
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

    public static void main(String[] args) throws Exception {
        Corpus c = new Corpus();
        BM25 scorer = new BM25();
        BufferedReader br = null;
        String line;
        try {

            br = new BufferedReader(new FileReader("data/Data.train"));
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                {
                    Document d = new Document();
                    d.createDocument(data[1],"");
                    d.setLabel(data[0]);
                    c.addToCorpus(d);
                }

            }
        }catch(Exception e){

        }


        InvertedIndex idx = new InvertedIndex(c);


        File file = new File("results/ResulsBM25.csv");


        // if file doesnt exists, then create it
        if (!file.exists()) file.createNewFile();

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("Expected Cat, Query, Doc Cat, Doc, Doc Score\n");
        try {

            br = new BufferedReader(new FileReader("data/Data.test"));
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");



                Document query = new Document();
                query.createDocument(data[1],"");
                query.setLabel(data[0]);

                Iterator it = scorer.sortByValue(scorer.getBM25Score(idx,query,25,0.5)).entrySet().iterator();
                int count = 0;
                while(count++<5){
                    bw.write(data[0]+",");
                    bw.write(data[1]+",");
                    Map.Entry<String,BigDecimal> entry= (Map.Entry<String, BigDecimal>) it.next();
                    Document d = c.getDocByID(entry.getKey());
                    bw.write(d.getLabel()+",");
                    bw.write(d.getContent()+",");
                    bw.write(entry.getValue().toString()+"\n");
                }
            }










        }catch(Exception e){

        }
        bw.close();
        br.close();
    }

}