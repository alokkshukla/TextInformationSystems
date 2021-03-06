package com.newgen.analytics.text.utils;

import com.newgen.analytics.text.classification.naivebayes.VocabularyModel;
import com.newgen.analytics.text.classification.sentiment.SentimentAnalyzer;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
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
public class PreProcessing implements Serializable{
    public PreProcessing() {
        lemmatizer=null;
        token_model=null;
        tokenizer=null;
        stemmer=null;
        pos_model=null;
        stopwordsFile="lib/stop.txt";


    }

    static SimpleLemmatizer lemmatizer;
    static TokenizerModel token_model;
    static Tokenizer tokenizer;
    static SnowballStemmer stemmer;
    String stopwordsFile;
    static StanfordCoreNLP pipeline;
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
            InputStream is = new FileInputStream("lib/en-token.bin");

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

            br = new BufferedReader(new FileReader("lib/stop.txt"));
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

    public static String getPOSTag(String content){
        String tag="";
        InputStream modelIn = null;


        try {
            if(pos_model == null) {
                modelIn = new FileInputStream("lib/en-pos-maxent.bin");
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

    public static List<String> lemmatize(List<String> words) throws Exception{

        List<String> res = new ArrayList<>();
        if (lemmatizer == null) {
            InputStream is = new FileInputStream("lib/en-lemmatizer.dict");
            lemmatizer = new SimpleLemmatizer(is);
            is.close();
        }
        for(int i=0;i<words.size();i++){
            res.add(lemmatizer.lemmatize(words.get(i), PreProcessing.getPOSTag(words.get(i))));
        }

        return res;
    }

    public static List<String> lemmatize(String documentText)
    {
        List<String> lemmas = new LinkedList<String>();

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(documentText);

        // run all Annotators on this text

        if(PreProcessing.pipeline==null){
            Properties props;
            props = new Properties();
            props.put("annotators", "tokenize, ssplit, pos, lemma");

            // StanfordCoreNLP loads a lot of models, so you probably
            // only want to do this once per execution
            PreProcessing.pipeline = new StanfordCoreNLP(props);
        }
        PreProcessing.pipeline.annotate(document);

        // Iterate over all of the sentences found
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for(CoreMap sentence: sentences) {
            // Iterate over all tokens in a sentence
            for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // Retrieve and add the lemma for each word into the list of lemmas
                lemmas.add(token.get(CoreAnnotations.LemmaAnnotation.class));
            }
        }

        return lemmas;
    }

    public static void main7(String[] args) throws Exception{
        PreProcessing helper = new PreProcessing();
        SentimentAnalyzer an = new SentimentAnalyzer();
        File file = new File("data/ScoredData.tsv");

        // if file doesnt exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);



        String csvFile = "data/Train_";
        BufferedReader br = null;
        String line = "";
        List<String> modifiedContent = new ArrayList<String>();

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                String[] data = line.split("\t");
                if (data.length > 1) {
//                bw.write(data[0]);
//
//                // bw.write("\t");
//                bw.write(",");
//                bw.write(an.getSentiment(data[1]).toString());
//                bw.write(","+data[1]+",");

                    List<String> tokens = helper.tokenize(data[1].toLowerCase());
//
//                List<String> stems =  new ArrayList<String>();
//
//                for(int i=0;i<tokens.size();i++){
//                    stems.add(helper.lemmatize(tokens.get(i)));
//                }
                    List<String> features = (helper.removeDuplicates(tokens));
                    if (vmod.getVocabModel().get(data[0]) == null) {
                        vmod.getVocabModel().put(data[0], new HashSet<String>(features));
                    } else {
                        Set<String> temp = vmod.getVocabModel().get(data[0]);
                        temp.addAll(features);
                        vmod.getVocabModel().put(data[0], temp);
                    }
//                for(int i=0;i<features.size();i++){
//                    bw.write(features.get(i));
//                    bw.write(" ");
//                }

//                bw.write("\n");
                }
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


       for(int j=0;j<500;j++){
        bw.write("REQUEST");
        bw.write("\t");

        // In real life, the Random object should be rather more shared than this
        int i = 0;
        List<String> words = new ArrayList<String>();
        words.addAll(vmod.getVocabModel().get("REQUEST"));
        for(int k=0;k<50;k++) {
            bw.write(words.get( new Random().nextInt(words.size())));
            bw.write(" ");
        }
        bw.write("\n");
    }
        for(int j=0;j<700;j++){
        bw.write("SUGGESTION");
        bw.write("\t");

        // In real life, the Random object should be rather more shared than this
        int i = 0;
        List<String> words = new ArrayList<String>();
        words.addAll(vmod.getVocabModel().get("SUGGESTION"));
        for(int k=0;k<50;k++) {
            bw.write(words.get( new Random().nextInt(words.size())));
            bw.write(" ");
        }
        bw.write("\n");
    }
        for(int j=0;j<800;j++){
        bw.write("COMPLIMENT");
        bw.write("\t");

        // In real life, the Random object should be rather more shared than this
        int i = 0;
        List<String> words = new ArrayList<String>();
        words.addAll(vmod.getVocabModel().get("COMPLIMENT"));
        for(int k=0;k<50;k++) {
            bw.write(words.get( new Random().nextInt(words.size())));
            bw.write(" ");
        }
        bw.write("\n");
    }
        for(int j=0;j<500;j++){
        bw.write("MISCEALLANEOUS");
        bw.write("\t");

        // In real life, the Random object should be rather more shared than this
        int i = 0;
        List<String> words = new ArrayList<String>();
        words.addAll(vmod.getVocabModel().get("MISCEALLANEOUS"));
        for(int k=0;k<50;k++) {
            bw.write(words.get( new Random().nextInt(words.size())));
            bw.write(" ");
        }
        bw.write("\n");
    }
        bw.close();

    File file2 = new File("data/vocab.csv");

    // if file doesnt exists, then create it
        if (!file2.exists()) {
        file2.createNewFile();
    }

    FileWriter fw2 = new FileWriter(file2.getAbsoluteFile());
    BufferedWriter bw2 = new BufferedWriter(fw2);


        for (Map.Entry<String, Set<String>> entry : vmod.getVocabModel().entrySet())
    {
        bw2.write(entry.getKey() + "\n" + entry.getValue());
        bw2.write("\n");
    }
        bw2.close();
    }

    public static String preProcess(String content) throws Exception {
        List<String> features = PreProcessing.removeStopWords(PreProcessing.tokenize(content.toLowerCase()));
        String res="";
        for(int i=0;i<features.size();i++){
            res+=features.get(i);
           res+=" ";
        }
        return res;
    }

//    public static List<String> tokeniseAndRemoveDuplicates(String content) throws Exception{
//
//
////     return removeDuplicates((tokenize(dealWithNegation(content))));
//    }

    public boolean checkIfNegative(String content){

        return false;
    }
    public static String dealWithNegation(String content){
//        String[] negations ={"not","isnt","aint","arent","wasnt","werent","hasnt","havnt","hadnt","wont","cant","couldnt","darent","didnt","doesnt","dont","hasnt","havent","hadnt","isnt","maynt","mightnt","mustnt","neednt","oughtnt","shant","shouldnt","wasnt","werent","wont","wouldnt",               ,"aren't","can't","couldn't","daren't","didn't","doesn't","don't","hasn't","haven't","hadn't","isn't","mayn't","mightn't","mustn't","needn't","oughtn't","shan't","shouldn't","wasn't","weren't","won't","wouldn't"};
//        String[] punctuations = {",",".",";","!"};
//        List<String> negationsList = new ArrayList<String>(Arrays.asList(negations));
//        String[] sentences = content.split(".");
//        int p=0;
//        while(p<content.length()){
//            for(int j=0;j<negationsList.size();j++){
//                p = content.indexOf(negationsList.get(j));
//                String prev = content.substring(0,p);
////                int firstPunctation =
////                String treatmentNeeded = content.substring(p,content.in)
//            }
//            p=content.indexOf("not");
//        }
        return content;
    }

    public static void main(String[] args) throws Exception{
       List<String> test = new ArrayList<>();
        test.add("cancel");
        test.add("cancelling");
        test.add("cancelled");
        System.out.print(PreProcessing.lemmatize("cancel cancelling cancelled"));
    }

//    private int getIndexOfFirstPunctuation(String content){
//
//    }
}
