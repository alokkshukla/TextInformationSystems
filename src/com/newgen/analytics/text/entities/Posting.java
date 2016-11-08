package com.newgen.analytics.text.entities;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by alok.shukla on 11/8/2016.
 */
public class Posting {

    public Posting(){
        this.posting = new HashMap<String,Integer>();
    }
    private Map<String,Integer> posting;

    public Map<String, Integer> getPosting() {
        return posting;
    }

    public void setPosting(Map<String, Integer> posting) {
        this.posting = posting;
    }
}
