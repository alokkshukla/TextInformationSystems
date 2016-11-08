package com.newgen.analytics.text.entities;

import java.util.*;

/**
 * Created by alok on 09/11/16.
 */
public class Corpus {

    private Set<Document> corpus;

    public int getSize() {
        return this.getCorpus().size();
    }

    public void setSize(int size) {
        this.size = size;
    }

    private int size;

    public Corpus(Set<Document> corpus) {
        this.corpus = corpus;
    }

    public Corpus() {
        this.corpus = new HashSet<Document>();
    }

    public Set<Document> getCorpus() {
        return corpus;
    }

    public void setCorpus(Set<Document> corpus) {
        this.corpus = corpus;
    }



    public Posting getPosting(String word){
        Posting post = new Posting();
        Iterator iter = this.getCorpus().iterator();
        while (iter.hasNext()) {
            System.out.println();
            Document d = (Document) iter.next();
            int count = d.getDocWordCount(word);
            if(count>0){
                post.getPosting().put(d.getDocID(),count);
            }
        }
        for(int i=0;i<corpus.size();i++){

        }
        return post;
    }

    public static void main(String[] args) throws Exception{
        Document d1 = new Document();
        d1.createDocument("Alok Kumar Alok","");
        System.out.println(d1.getDocID());
        Document d2 = new Document();
        d2.createDocument("Shukla Alok","");
        System.out.println(d2.getDocID());
        Corpus c = new Corpus();
        c.getCorpus().add(d1);
        c.getCorpus().add(d2);
        Posting p = c.getPosting("Alok");
        System.out.println(p.getPosting());

    }
}
