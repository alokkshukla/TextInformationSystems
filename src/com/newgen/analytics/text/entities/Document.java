package com.newgen.analytics.text.entities;

import com.newgen.analytics.text.indexing.Indexer;
import com.newgen.analytics.text.utils.PreProcessing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alok on 08/11/16.
 */
public class Document {

    private String docID;
    private Map<String,Integer> contentMap;

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public Map<String, Integer> getContentMap() {
        return contentMap;
    }

    public void setContentMap(Map<String, Integer> contentMap) {
        this.contentMap = contentMap;
    }

    public Document(String docID, Map<String, Integer> contentMap) {
        this.docID = docID;
        this.contentMap = contentMap;
    }

    public Document() {
        this.docID = "";
        this.contentMap = new HashMap<String,Integer>();
    }

    public Document createDocument(String content,String id) throws Exception{
        Document doc = new Document();
        Map<String,Integer> docMap = new HashMap<String,Integer>();
        List<String> tokens = PreProcessing.tokenize(content);
        for(int i=0;i<tokens.size();i++){
            if(null==docMap.get(tokens.get(i))){
                docMap.put(tokens.get(i),1);
            }else{
                int count = docMap.get(tokens.get(i));
                docMap.put(tokens.get(i),count+1);
            }

        }
        if(null!=id && ""!=id){
            doc.setDocID(id);
        }
        else{
            doc.setDocID(Integer.toString(content.hashCode()));
        }
        doc.setContentMap(docMap);
        return doc;

    }

    public int getDocWordCount(Document doc, String word){
        if(null!=doc.getContentMap().get(word)){
            return doc.getContentMap().get(word);
        }else{
            return 0;
        }
    }


    public static void main(String[] args) throws Exception{
        Document doc = new Document();
        System.out.print(doc.createDocument("Alok Kumar Shukl is Okay Okay Alok","").getDocID());
    }

}
