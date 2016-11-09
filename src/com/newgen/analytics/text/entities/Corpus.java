package com.newgen.analytics.text.entities;

import javax.print.Doc;
import java.util.*;

/**
 * Created by alok on 09/11/16.
 */
public class Corpus {

    private Set<Document> corpus;

    public double getAvdl() {


        Set<Document> docs = this.getCorpus();
        Iterator it = docs.iterator();
        double totalLength = 0.0;
        while(it.hasNext()){
           Document d = (Document)it.next();
            totalLength+=d.getLength();
        }
       avdl = (double)totalLength/docs.size();
        return avdl;
    }

    public void setAvdl(double avdl) {
        this.avdl = avdl;
    }

    private double avdl;

    public int getSize() {
        return this.getCorpus().size();
    }

    public void setSize(int size) {
        this.size = size;
    }

    private int size;

    public Corpus(Set<Document> corpus) {
        this.corpus = corpus;
        this.avdl = getAvdl();
        this.size = corpus.size();
    }

    public Corpus() {
        this.corpus = new HashSet<Document>();
        this.avdl =0.0;
        this.size = 0;
    }

    public Set<Document> getCorpus() {
        return corpus;
    }

    public void setCorpus(Set<Document> corpus) {
        this.corpus = corpus;
        this.avdl = getAvdl();
        this.size = corpus.size();
    }


    public Document getDocByID(String ID){
        Iterator it = this.getCorpus().iterator();
        while(it.hasNext()){
            Document d = (Document) it.next();
            if(d.getDocID().equals(ID)){
                return d;
            }
        }
    return null;
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

    public void addToCorpus(Document d){
        this.getCorpus().add(d);
        this.avdl = getAvdl();
        this.size+=1;
    }
}
