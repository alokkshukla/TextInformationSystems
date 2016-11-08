//package com.newgen.analytics.text.classification.sentiment;
//
///**
// * Created by alok.shukla on 9/27/2016.
// */
//import java.util.ArrayList;
//import java.util.Properties;
//
//import edu.stanford.nlp.ling.CoreAnnotations;
//import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
//import edu.stanford.nlp.pipeline.Annotation;
//import edu.stanford.nlp.pipeline.StanfordCoreNLP;
//import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
//import edu.stanford.nlp.trees.Tree;
//import edu.stanford.nlp.util.CoreMap;
//
//public class SentimentAnalyzer {
//    public static StanfordCoreNLP getPipeline() {
//        return pipeline;
//    }
//
//    public static void setPipeline(StanfordCoreNLP pipeline) {
//        SentimentAnalyzer.pipeline = pipeline;
//    }
//
//    static StanfordCoreNLP pipeline = null;
//    public ArrayList<Integer> getSentiment(String lines) {
//        ArrayList<Integer> scores = new ArrayList<Integer>();
//
//        Annotation document = new Annotation(lines);
//        Properties props = new Properties();
//        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
//        if(null==pipeline) {
//            pipeline = new StanfordCoreNLP(props);
//        }
//        pipeline.annotate(document);
//        int count =0;
//        float totalSentiment = 100;
//        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
//
//            count++;
//
//            Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
//            int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
//            scores.add(sentiment);
//
//        }
//
//        return scores;
//    }
//
//    public String getSentimentCategory(String lines) {
//
//        Annotation document = new Annotation(lines);
//        Properties props = new Properties();
//        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
//        if(null==pipeline) {
//            pipeline = new StanfordCoreNLP(props);
//        }
//        pipeline.annotate(document);
//        int count =0;
//        String totalSentiment = "Neutral";
//        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
//
//            count++;
//
//            Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
//            int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
//
//            if(sentiment<1){
//                totalSentiment = "Negative";
//            }
//
//            if(sentiment>2){
//                totalSentiment = "Positive";
//            }
//        }
//
//        return totalSentiment;
//    }
//
//
//
//
//    public static void main(String[] args) {
//        SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer();
//
//        System.out.println(sentimentAnalyzer
//                .getSentiment("worst service ever. Too good."));
//    }
//}