package com.newgen.analytics.text.indexing;

import com.newgen.analytics.text.entities.InvertedIndex;
import com.newgen.analytics.text.utils.PreProcessing;

import java.util.*;

/**
 * Created by alok.shukla on 11/8/2016.
 */
public class Indexer {
    PreProcessing preProcessing= new PreProcessing();


    public InvertedIndex createInvertedIndex(List<String> content, String tag) throws Exception {

        InvertedIndex idx = new InvertedIndex();
//        if (tag == "STRING") {
//            for(int i=0;i<content.size();i++){
//                List<String> tokens = preProcessing.tokenize(content.get(i));
//                for(int j=0;j<tokens.size();j++){
//                    Posting post = idx.getInvertedIndex().get(j);
//                    int count = 0;
//                    if(null!=post){
//                        count = post.getPosting().get(i);
//                    } else{
//                        post = new Posting();
//                    }
//
//                    post.getPosting().put(j, count+1);
//                    idx.getInvertedIndex().put(tokens.get(j),post);
//                }
//
//            }
//
//        }
        return idx;
    }

    private Map<Integer,String> assignIDs(List<String> content){
        Map<Integer,String> contentMap = new HashMap<Integer,String>();
        for(int i=0;i<content.size();i++){
            contentMap.put(i,content.get(i));
        }
        return contentMap;
    }

    public static void main(String[] args) throws Exception {
        Indexer idxr = new Indexer();
        List<String> content = new ArrayList<String>();
        content.add("Test String");
        InvertedIndex idx = idxr.createInvertedIndex(content,"STRING");
        System.out.print(idx.getInvertedIndex());
    }
}
