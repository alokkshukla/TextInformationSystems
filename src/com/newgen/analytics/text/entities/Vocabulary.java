package com.newgen.analytics.text.entities;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by alok on 09/11/16.
 */
public class Vocabulary {
    public Set<String> getVocab() {
        return vocab;
    }

    public void setVocab(Set<String> vocab) {
        this.vocab = vocab;
    }

    private Set<String> vocab;

    public Vocabulary() {
        this.vocab = new HashSet<String>();
    }

    public Vocabulary(Set<String> vocab) {
        this.vocab = vocab;
    }

    public Vocabulary(Corpus c){
        this.vocab = new HashSet<String>();
        Iterator iter = c.getCorpus().iterator();
        while (iter.hasNext()) {
            Document doc = (Document)iter.next();
            Set<String> words = doc.getContentMap().keySet();
            if(null!=words) {
                this.getVocab().addAll(words);
            }
;        }
    }

    public static void main(String[] args) throws Exception {
        Document d1 = new Document();
        d1.createDocument("Alok Kumar ALok","");

        Document d2 = new Document();
        d2.createDocument("Shukla alok","");

        Corpus c = new Corpus();
        c.getCorpus().add(d1);
        c.getCorpus().add(d2);

        Vocabulary v = new Vocabulary(c);

        System.out.println(v.getVocab());

        System.out.print(v.getVocab());
    }
}
