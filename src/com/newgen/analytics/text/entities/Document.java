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
    private int length;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    private String label;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String content;
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

    public void createDocument(String content,String id) throws Exception{


        int length = 0;
        Map<String,Integer> docMap = new HashMap<String,Integer>();
        List<String> tokens = PreProcessing.tokenize(content);
        for(int i=0;i<tokens.size();i++){
            length++;
            if(null==docMap.get(tokens.get(i))){
                docMap.put(tokens.get(i),1);
            }else{
                int count = docMap.get(tokens.get(i));
                docMap.put(tokens.get(i),count+1);
            }

        }
        if(null!=id && ""!=id){
            this.setDocID(id);
        }
        else{
            this.setDocID(Integer.toString(content.hashCode()));
        }

        this.length = length;
        this.setContent(content);
        this.setContentMap(docMap);


    }

    public int getDocWordCount(String word){
        if(null!=this.getContentMap().get(word)){
            return this.getContentMap().get(word);
        }else{
            return 0;
        }
    }


    public static void main(String[] args) throws Exception{
        Document doc = new Document();
        doc.createDocument("Alok Kumar Shukl is Okay Okay Alok","");
        System.out.print(doc.getContentMap());
        System.out.print(doc.getDocID());
    }

}
