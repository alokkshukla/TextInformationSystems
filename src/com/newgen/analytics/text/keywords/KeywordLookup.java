package com.newgen.analytics.text.keywords;

import com.newgen.analytics.text.classification.evaluation.ConfusionMatrix;
import com.newgen.analytics.text.utils.PreProcessing;

import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by alok.shukla on 11/22/2016.
 */
public class KeywordLookup {
    Map<String,Integer> keywords = new HashMap<>();
    public static void main(String[] args) throws Exception {
        KeywordLookup key = new KeywordLookup();

        BufferedReader br = null;
        String line="";
        try {

            br = new BufferedReader(new FileReader("model/neg.txt"));
            while ((line = br.readLine()) != null) {
               key.keywords.put(line.trim(), 1);
            }
        }catch(Exception e){

        }



        File resfile = new File("results/keywords.csv");


        // if file doesnt exists, then create it
        if (!resfile.exists()) {
            resfile.createNewFile();
        }

        FileWriter fw = new FileWriter(resfile.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write("Expected Category, Predicted Category,Content\n");
        try {

            br = new BufferedReader(new FileReader("data/Test"));
            while ((line = br.readLine()) != null) {

                String[] data = line.split("\t");
                bw.write(data[0]+",");
                String toWrite = "DEFAULT,";
                List<String> tokens = PreProcessing.tokenize(data[1]);
                for(int i=0;i<tokens.size();i++){
                    if(key.keywords.get(tokens.get(i))!=null){
                        toWrite="COMPLAINT,";
                    }
                }
                bw.write(toWrite);
                bw.write(data[1]+"\n");

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        bw.close();
        br.close();



    }
}
