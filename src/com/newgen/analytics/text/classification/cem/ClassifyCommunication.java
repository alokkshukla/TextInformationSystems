//package com.newgen.analytics.text.classification.cem;
//
//import com.newgen.analytics.text.utils.PreProcessing;
//import java.io.*;
//
///**
// * Created by alok.shukla on 9/28/2016.
// */
//public class ClassifyCommunication {
//    SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer();
//    OpenNLPClassify classifier = new OpenNLPClassify();
//    public String getCategory(String content) throws IOException {
//        String label = "";
////        float score = this.sentimentAnalyzer.getSentiment(content);
////        if(score<2){
////            label = "COMPLAINT";
////        }
////        else if(score>=3){
////            label="COMPLIMENT";
////        }
////        else{
//            label = classifier.classify(content);
//        //}
//        return label;
//    }
//
//    public static void main(String[] args) throws Exception{
//        File file = new File("results/results.csv");
//        PreProcessing p = new PreProcessing();
//
//        // if file doesnt exists, then create it
//        if (!file.exists()) {
//            file.createNewFile();
//        }
//
//        FileWriter fw = new FileWriter(file.getAbsoluteFile());
//        BufferedWriter bw = new BufferedWriter(fw);
//        ClassifyCommunication test = new ClassifyCommunication();
//        BufferedReader br = null;
//        String line;
//        bw.write("Expected Category, Predicted Category, Content\n");
//        try {
//
//            br = new BufferedReader(new FileReader("data/Data.test"));
//            while ((line = br.readLine()) != null) {
//                String[] data = line.split("\t");
//               bw.write(data[0]);
//                bw.write(",");
//
//                bw.write(test.getCategory(p.preProcess(data[1].toLowerCase())));
//                bw.write(",");
//                bw.write(data[1]);
//                bw.write("\n");
//
//            }
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            bw.close();
//            if (br != null) {
//                try {
//                    br.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//}
