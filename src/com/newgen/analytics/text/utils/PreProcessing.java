package com.newgen.analytics.text.utils;

import com.newgen.analytics.text.classification.naivebayes.VocabularyModel;
import opennlp.tools.lemmatizer.SimpleLemmatizer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import java.io.*;
import java.util.*;

/**
 * Created by alok.shukla on 9/27/2016.
 */
public class PreProcessing {
    public PreProcessing() {
        lemmatizer=null;
        token_model=null;
        tokenizer=null;
        stemmer=null;
        pos_model=null;
        stopwordsFile="keywords.txt";

    }

    static SimpleLemmatizer lemmatizer;
    static TokenizerModel token_model;
    static Tokenizer tokenizer;
    static SnowballStemmer stemmer;
    String stopwordsFile;
    static POSModel pos_model;
    static VocabularyModel vmod = new VocabularyModel();
    public SimpleLemmatizer getLemmatizer() {
        return lemmatizer;
    }

    public void setLemmatizer(SimpleLemmatizer lemmatizer) {
        this.lemmatizer = lemmatizer;
    }

    public TokenizerModel getToken_model() {
        return token_model;
    }

    public void setToken_model(TokenizerModel token_model) {
        this.token_model = token_model;
    }

    public Tokenizer getTokenizer() {
        return tokenizer;
    }

    public void setTokenizer(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public SnowballStemmer getStemmer() {
        return stemmer;
    }

    public void setStemmer(SnowballStemmer stemmer) {
        this.stemmer = stemmer;
    }

    public String getStopwordsFile() {
        return stopwordsFile;
    }

    public void setStopwordsFile(String stopwordsFile) {
        this.stopwordsFile = stopwordsFile;
    }

    public POSModel getPos_model() {
        return pos_model;
    }

    public void setPos_model(POSModel pos_model) {
        this.pos_model = pos_model;
    }


    public static List<String> tokenize(String content) throws Exception{
        if(null==token_model) {
            InputStream is = new FileInputStream("bin/en-token.bin");

            token_model = new TokenizerModel(is);
        }
        if(null==tokenizer) {
            tokenizer = new TokenizerME(token_model);
        }

        List<String> temp = Arrays.asList(tokenizer.tokenize(content));
        List<String> res = new ArrayList<String>();
        for(int i=0;i<temp.size();i++){
            res.add(i,temp.get(i).toLowerCase());
        }

       return res;
    }

    public String stem(String word){
        if(null == stemmer) {
            stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
        }
        return (String) stemmer.stem(word);

    }

    public static List<String> removeDuplicates(List<String> content){


        Set<String> hs = new HashSet<>();
        hs.addAll(content);
        content.clear();
        content.addAll(hs);
        return content;
    }

    public static List<String> removeStopWords(List<String> content) throws Exception{
        Set<String> stopwords = new HashSet<String>();

        BufferedReader br = null;
        String line = "";
        List<String> modifiedContent = new ArrayList<String>();

        try {

            br = new BufferedReader(new FileReader("bin/stop.txt"));
            while ((line = br.readLine()) != null) {
              stopwords.add(line.toLowerCase());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        for(int i=0;i<content.size();i++){
            String token = content.get(i);
            if(!stopwords.contains(token)){
                modifiedContent.add(token);
            }
        }
    return modifiedContent;
    }

    public String getPOSTag(String content){
        String tag="";
        InputStream modelIn = null;


        try {
            if(pos_model == null) {
                modelIn = new FileInputStream("model/en-pos-maxent.bin");
                pos_model = new POSModel(modelIn);
            }
        }
        catch (IOException e) {
            // Model loading failed, handle the error
            e.printStackTrace();
        }
        finally {
            if (modelIn != null) {
                try {
                    modelIn.close();
                }
                catch (IOException e) {
                }
            }
        }
        POSTaggerME tagger = new POSTaggerME(pos_model);
        return tagger.tag(content);
    }

    public String lemmatize(String word) throws Exception{

        if (lemmatizer == null) {
            InputStream is = new FileInputStream("model/en-lemmatizer.dict");
            lemmatizer = new SimpleLemmatizer(is);
            is.close();
        }
        String lemma = lemmatizer.lemmatize(word, this.getPOSTag(word));
        return lemma;
    }

//    public static void main(String[] args) throws Exception{
//        PreProcessing helper = new PreProcessing();
//        SentimentAnalyzer an = new SentimentAnalyzer();
//        File file = new File("data/ScoredData2.csv");
//
//        // if file doesnt exists, then create it
//        if (!file.exists()) {
//            file.createNewFile();
//        }
//
//        FileWriter fw = new FileWriter(file.getAbsoluteFile());
//        BufferedWriter bw = new BufferedWriter(fw);
//
//
//
//        String csvFile = "data/Data.tsv";
//        BufferedReader br = null;
//        String line = "";
//        List<String> modifiedContent = new ArrayList<String>();
//
//        try {
//
//            br = new BufferedReader(new FileReader(csvFile));
//            while ((line = br.readLine()) != null) {
//                String[] data = line.split("\t");
//                bw.write(data[0]);
//
//               // bw.write("\t");
//                bw.write(",");
//                bw.write(an.getSentiment(data[1]).toString());
//                bw.write(","+data[1]+",");
//                List<String> tokens = helper.tokenize(data[1].toLowerCase());
//
//                List<String> stems =  new ArrayList<String>();
//
//                for(int i=0;i<tokens.size();i++){
//                    stems.add(helper.lemmatize(tokens.get(i)));
//                }
//                List<String> features = helper.removeStopWords(helper.removeDuplicates(stems));
//                if(vmod.getVocabModel().get(data[0])==null){
//                    vmod.getVocabModel().put(data[0],new HashSet<String>(features));
//                }else{
//                    Set<String> temp = vmod.getVocabModel().get(data[0]);
//                    temp.addAll(features);
//                    vmod.getVocabModel().put(data[0],temp);
//                }
//                for(int i=0;i<features.size();i++){
//                    bw.write(features.get(i));
//                    bw.write(" ");
//                }
//
//                bw.write("\n");
//            }
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//
//            if (br != null) {
//                try {
//                    br.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
////        }
////       for(int j=0;j<300;j++){
////           bw.write("REQUEST");
////           bw.write("\t");
////
////         // In real life, the Random object should be rather more shared than this
////           int i = 0;
////           List<String> words = new ArrayList<String>();
////           words.addAll(vmod.getVocabModel().get("REQUEST"));
////           for(int k=0;k<50;k++) {
////                bw.write(words.get( new Random().nextInt(words.size())));
////               bw.write(" ");
////           }
////           bw.write("\n");
////       }
////        for(int j=0;j<300;j++){
////            bw.write("LEAD");
////            bw.write("\t");
////
////            // In real life, the Random object should be rather more shared than this
////            int i = 0;
////            List<String> words = new ArrayList<String>();
////            words.addAll(vmod.getVocabModel().get("LEAD"));
////            for(int k=0;k<50;k++) {
////                bw.write(words.get( new Random().nextInt(words.size())));
////                bw.write(" ");
////            }
////            bw.write("\n");
////        }
////        for(int j=0;j<300;j++){
////            bw.write("SUGGESTION");
////            bw.write("\t");
////
////            // In real life, the Random object should be rather more shared than this
////            int i = 0;
////            List<String> words = new ArrayList<String>();
////            words.addAll(vmod.getVocabModel().get("SUGGESTION"));
////            for(int k=0;k<50;k++) {
////                bw.write(words.get( new Random().nextInt(words.size())));
////                bw.write(" ");
////            }
////            bw.write("\n");
////        }
////        for(int j=0;j<300;j++){
////            bw.write("COMPLIMENT");
////            bw.write("\t");
////
////            // In real life, the Random object should be rather more shared than this
////            int i = 0;
////            List<String> words = new ArrayList<String>();
////            words.addAll(vmod.getVocabModel().get("COMPLIMENT"));
////            for(int k=0;k<50;k++) {
////                bw.write(words.get( new Random().nextInt(words.size())));
////                bw.write(" ");
////            }
////            bw.write("\n");
////        }
////        bw.close();
//
////        File file2 = new File("data/vocab.csv");
////
////        // if file doesnt exists, then create it
////        if (!file2.exists()) {
////            file2.createNewFile();
////        }
////
////        FileWriter fw2 = new FileWriter(file2.getAbsoluteFile());
////        BufferedWriter bw2 = new BufferedWriter(fw2);
////
////
////        for (Map.Entry<String, Set<String>> entry : vmod.getVocabModel().entrySet())
////        {
////           bw2.write(entry.getKey() + "\n" + entry.getValue());
////            bw2.write("\n");
////        }
////        bw2.close();
//    }

    public String preProcess(String content) throws Exception {
        List<String> tokens = this.tokenize(content.toLowerCase());
        List<String> stems =  new ArrayList<String>();

        for(int i=0;i<tokens.size();i++){
            stems.add(this.lemmatize(tokens.get(i)));
        }
        String res="";
        List<String> features = this.removeStopWords(this.removeDuplicates(stems));
        for(int i=0;i<features.size();i++){
            res+=features.get(i);
           res+=" ";
        }
        return res;
    }

    public static List<String> tokeniseAndRemoveDuplicates(String content) throws Exception{


     return removeDuplicates((tokenize(dealWithNegation(content))));
    }

    public boolean checkIfNegative(String content){

        return false;
    }
    public static String dealWithNegation(String content){
        return content;
    }

    public static void main(String[] args) throws Exception{
        String test = "Alok K Alok alok alok Shukla";
        System.out.print(PreProcessing.removeDuplicates(PreProcessing.tokenize(test)));
    }
}
