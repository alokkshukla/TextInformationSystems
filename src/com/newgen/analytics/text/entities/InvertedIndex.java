package com.newgen.analytics.text.entities;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by alok.shukla on 11/8/2016.
 */
public class InvertedIndex {

    public Corpus getCorpus() {
        return corpus;
    }

    public void setCorpus(Corpus c) {
        this.corpus = c;
    }

    private Corpus corpus;

    public InvertedIndex(){
        this.invertedIndex = new TreeMap<String,Posting>();
    }

    public InvertedIndex(Corpus c){
        this.corpus = c;
        this.invertedIndex = new TreeMap<String,Posting>();
        Vocabulary v = new Vocabulary();
        v.populateVocab(c);
        Iterator iter = v.getVocab().iterator();
        while (iter.hasNext()) {
            String word = (String) iter.next();
            this.getInvertedIndex().put(word,c.getPosting(word));
        }
        System.out.print("Index created");
    }

    public void printIndex(){
        for(Map.Entry<String,Posting> entry : this.getInvertedIndex().entrySet()) {
            System.out.println(entry.getKey() + " => " + entry.getValue().getPosting());
        }
    }

    private Map<String,Posting> invertedIndex;
    public Map<String, Posting> getInvertedIndex() {
        return invertedIndex;
    }

    public void setInvertedIndex(Map<String, Posting> invertedIndex) {
        this.invertedIndex = invertedIndex;
    }

    public static void main(String[] args) throws Exception{
        Document d1 = new Document();
        d1.createDocument("Alok Kumar ALok","");

        Document d2 = new Document();
        d2.createDocument("Shukla alok","");

        Corpus c = new Corpus();
        c.getCorpus().add(d1);
        c.getCorpus().add(d2);

        Vocabulary v = new Vocabulary();
        v.populateVocab(c);

        System.out.println(v.getVocab());

        InvertedIndex idx = new InvertedIndex(c);
        idx.printIndex();
    }

}
