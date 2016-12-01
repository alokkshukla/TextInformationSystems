package com.newgen.analytics.text.classification.naivebayes;


import com.newgen.analytics.text.classification.evaluation.ClassificationEvaluation;
import com.newgen.analytics.text.classification.evaluation.ConfusionMatrix;
import com.newgen.analytics.text.classification.svm.SVMClassify;
import com.newgen.analytics.text.utils.PreProcessing;
import com.newgen.utils.MapUtils;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by alok.shukla on 9/30/2016.
 */
public class NaiveBayesModel implements Serializable  {
	private static final long serialVersionUID = 1L;
	String path;

    public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private Map<String, Map<String, Integer>> nbModel;
    private Map<String, Integer> catCounts;
    private Set<String> labels = new HashSet<String>();

    public Set<String> getLabels() {
		return labels;
	}

	public void setLabels(Set<String> labels) {
		this.labels = labels;
	}

	public Map<String, Integer> getCatDocCounts() {
        return catDocCounts;
    }

    public void setCatDocCounts(Map<String, Integer> catDocCounts) {
        this.catDocCounts = catDocCounts;
    }

    private Map<String, Integer> catDocCounts;
    private PreProcessing preProcessing;


    private int noOfDocs;

    private Map<String, BigDecimal> catProb;


    int vocabLength;

    public Map<String, Map<String, Integer>> getNbModel() {
        return nbModel;
    }

    public void setNbModel(Map<String, Map<String, Integer>> nbModel) {
        this.nbModel = nbModel;
    }

    public PreProcessing getPreProcessing() {
        return preProcessing;
    }

    public void setPreProcessing(PreProcessing preProcessing) {
        this.preProcessing = preProcessing;
    }

    public NaiveBayesModel() {
        nbModel = new HashMap<String, Map<String, Integer>>();
        preProcessing = new PreProcessing();
        catCounts = new HashMap<String, Integer>();
        vocabLength = 0;
        catProb = new HashMap<String, BigDecimal>();
        noOfDocs = 0;
        catDocCounts = new HashMap<String, Integer>();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public int getVocabLength() {
        return vocabLength;
    }

    public Map<String, BigDecimal> getCatProb() {
        return catProb;
    }

    public void setCatProb(Map<String, BigDecimal> catProb) {
        this.catProb = catProb;
    }


    public void setVocabLength(int vocabLength) {
        this.vocabLength = vocabLength;
    }

    public NaiveBayesModel(Map<String, Map<String, Integer>> nbModel, PreProcessing preProcessing) {

        this.nbModel = nbModel;
        this.preProcessing = preProcessing;
    }

    public Map<String, Integer> getCatCounts() {
        return catCounts;
    }

    public void setCatCounts(Map<String, Integer> catCounts) {
        this.catCounts = catCounts;
    }

    public int getNoOfDocs() {
        return noOfDocs;
    }

    public void setNoOfDocs(int noOfDocs) {
        this.noOfDocs = noOfDocs;
    }

    public void addToModel(String content, String cat) throws Exception {
        setNoOfDocs(getNoOfDocs() + 1);
        Map<String, Integer> tempCounts = getCatDocCounts();
        if (null == tempCounts.get(cat)) {
            tempCounts.put(cat, 1);
        } else {
            tempCounts.put(cat, tempCounts.get(cat) + 1);
        }
        setCatDocCounts(tempCounts);


        if (null == nbModel) {
            nbModel = new HashMap<String, Map<String, Integer>>();
        }
        List<String> features = PreProcessing.removeStopWords(PreProcessing.tokenize(content));
        int length = features.size();
        if (null == catCounts) {
            catCounts = new HashMap<String, Integer>();
        }
        if (null == catCounts.get(cat)) {
            catCounts.put(cat, length);
        } else {
            catCounts.put(cat, catCounts.get(cat) + length);
        }
        for (int i = 0; i < length; i++) {
            String word = features.get(i);
            if (null == nbModel.get(word)) {
                setVocabLength(getVocabLength() + 1);
                Map<String, Integer> wordMap = new HashMap<String, Integer>();
                wordMap.put(cat, 1);
                nbModel.put(word, wordMap);
            } else {
                if (null == nbModel.get(word).get(cat)) {
                    Map<String, Integer> wordMap = nbModel.get(word);
                    wordMap.put(cat, 1);
                    nbModel.put(word, wordMap);
                } else {
                    int count = nbModel.get(word).get(cat);
                    Map<String, Integer> wordMap = nbModel.get(word);
                    wordMap.put(cat, count + 1);
                    nbModel.put(word, wordMap);
                }


            }
            Map<String, Integer> wordMap = nbModel.get(word);
            if(null==wordMap.get("DOCS")){
                wordMap.put("DOCS",1);
            }else{
                wordMap.put("DOCS",wordMap.get("DOCS")+1);
            }
            nbModel.put(word, wordMap);
        }
        Map<String, BigDecimal> temp = getCatProb();
        for (Map.Entry<String, BigDecimal> entry : temp.entrySet()) {
            BigDecimal prob = BigDecimal.valueOf(getCatDocCounts().get(entry.getKey()).doubleValue() / getNoOfDocs());
            temp.put(entry.getKey(), prob);

        }
        setCatProb(temp);
        temp = getCatProb();
        BigDecimal prob = BigDecimal.valueOf(getCatDocCounts().get(cat).doubleValue() / getNoOfDocs());
        temp.put(cat, prob);
        setCatProb(temp);

        Map<String, Map<String, Integer>> map = this.getNbModel();


    }


    public String trainModel(String trainFilePath, String modelOut) {
        NaiveBayesModel model = new NaiveBayesModel();
        model.setPath(modelOut);
        String onlpModelPath=modelOut;
        OutputStream onlpModelOutput = null;
        BufferedReader br = null;
        String line;
        try {

            br = new BufferedReader(new FileReader(trainFilePath));
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                {
                    if (data.length > 1) {
                        model.addToModel(data[1], data[0].trim());
                        model.labels.add(data[0]);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

           this.serialize(model,onlpModelPath);
            return onlpModelPath;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            if (onlpModelOutput != null) {
                try {
                    onlpModelOutput.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return null;
    }




    public BigDecimal getCategoryProbability(String content, String cat) throws Exception {
        BigDecimal totalProb = BigDecimal.valueOf(1.0);
        if (getCatCounts().get(cat) == null) {
            System.out.print("Invalid cat "+cat);
            return BigDecimal.valueOf(0.0);
        }
        int catCount = getCatCounts().get(cat);
        int vocabLength = getVocabLength();
        BigDecimal catProb = (getCatProb().get(cat));
        List<String> features = PreProcessing.removeStopWords(PreProcessing.tokenize((content)));
        for (int i = 0; i < features.size(); i++) {
            String word = features.get(i);
            BigDecimal prob = BigDecimal.valueOf(0.0);
            int wordCountCat = 0;
            if (null != getNbModel().get(word)) {
                if (null == getNbModel().get(word).get(cat)) {
                    wordCountCat = 0;
                } else {
                    wordCountCat = getNbModel().get(word).get(cat);
                }
            }
            prob = BigDecimal.valueOf((wordCountCat + 0.45) / (double) (catCount + vocabLength));

            totalProb = totalProb.multiply(prob);
        }
        return totalProb.multiply(catProb);
    }

    public ConfusionMatrix testModel(String testFilePath, String modelPath, String outoutFile) throws Exception {

        NaiveBayesModel model = (NaiveBayesModel)this.deserialize(modelPath);
        String[] cats=model.labels.toArray(new String[model.labels.size()]); 
        ConfusionMatrix results = new ConfusionMatrix(cats);
        Map<String, Map<String, Integer>> confusionMatrix = results.getConfusionMatrix();
        Map<String, BigDecimal> scores = new HashMap<String, BigDecimal>();
        File resfile = new File(outoutFile);


        // if file doesnt exists, then create it
        if (!resfile.exists()) {
            resfile.createNewFile();
        }
        String line = "";
        FileWriter fw = new FileWriter(resfile.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        BufferedReader br = null;
        bw.write("Expected Category, Predicted Category,Content\n");
        try {

            br = new BufferedReader(new FileReader(testFilePath));
            while ((line = br.readLine()) != null) {

                String[] data = line.split("\t");
                for(int i=0;i<cats.length;i++){
                	 scores.put(cats[i], model.getCategoryProbability(data[1], cats[i]));
                }
               

                Map<String, BigDecimal> map = scores;
                		for (Map.Entry<String, BigDecimal> e : map.entrySet())
                		{
                		    System.out.print(e.getKey() + "\t" + ((BigDecimal)e.getValue()).toString()+"\t");
                		}
                		System.out.println();
                Iterator it = MapUtils.sortByValue(scores).entrySet().iterator();
                Map.Entry<String, BigDecimal> entry = (Map.Entry<String, BigDecimal>) it.next();
                String expected = data[0];
                String predicted = entry.getKey();
                bw.write(expected + ",");
                bw.write(predicted + ",");
                bw.write(data[1] + "\n");
                if (confusionMatrix.get(predicted) != null) {
                    Map<String, Integer> entries = confusionMatrix.get(predicted);
                    if (entries.get(expected) != null) {
                        entries.put(expected, entries.get(expected) + 1);
                    } else {
                        entries.put(expected, 1);
                    }
                } else {
                    Map<String, Integer> entries = new HashMap<>();
                    entries.put(expected, 1);
                    confusionMatrix.put(predicted, entries);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        bw.close();
        br.close();
        return results;
    }
    
    public String getLabel(String content,String modelPath) throws Exception{
    	
    	  Map<String, BigDecimal> scores = new HashMap<String, BigDecimal>();
    	NaiveBayesModel model = (NaiveBayesModel)this.deserialize(modelPath);
    	String[] cats=model.labels.toArray(new String[model.labels.size()]); 
    	for(int i=0;i<cats.length;i++){
       	 scores.put(cats[i], model.getCategoryProbability(content, cats[i]));
       }
    	Iterator it = MapUtils.sortByValue(scores).entrySet().iterator();
        Map.Entry<String, BigDecimal> entry = (Map.Entry<String, BigDecimal>) it.next();
    	return entry.getKey();
    }

    private void getMutualInfo(String featFile, int n) throws IOException {
        Map<String, Map<String, Integer>> map = this.getNbModel();
        File file = new File(featFile);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);

        for (Map.Entry<String, Map<String, Integer>> entry : map.entrySet()) {
            Map<String, Integer> innerMap = entry.getValue();
            double dw=0.0;
            if(null!=entry.getValue().get("DOCS")) {
                dw = entry.getValue().get("DOCS") / (double) 2320;
            }else{
                System.out.print("VOilla");
            }

            for (Map.Entry<String, Integer> innerEntry : innerMap.entrySet()) {
                double piw=0,pi=0,mi=0,chi=0;

                if (innerEntry.getKey() != "DOCS") {
                    bw.write(entry.getKey() + ",");
                    bw.write(innerEntry.getKey() + ",");
                    pi = this.getCatDocCounts().get(innerEntry.getKey()) / (double) 2320;
                    piw = innerEntry.getValue() / dw;


                    mi = Math.log(piw / pi);

                    chi = ((n * (dw * dw) * ((piw - pi) * (piw - pi))) / (dw * (1 - dw) * pi * (1 - pi)));

                    bw.write(mi +",");
                    bw.write(chi +"\n");
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String trainFile = "data/TestNew";
        String testFile = "data/TrainNew";
        String resFile = "results/ResLinux.CSV";


        NaiveBayesModel helper = new NaiveBayesModel();
       
        String modelPath = helper.trainModel(trainFile,"model/NBLinux.ser");
        String[] cat = {"COMPLAINT","COMPLIMENT","MISCELLANEOUS","REQUEST"};


        ConfusionMatrix mat = helper.testModel(testFile,modelPath,resFile);
        mat.print();
        ClassificationEvaluation e = new ClassificationEvaluation();
        System.out.println("Macro Precision:\t"+e.getEvaluationParameter(mat)[0]);
        System.out.println("Micro Precision:\t"+e.getEvaluationParameter(mat)[1]);

    }



   

    /**
     * deserialize to Object from given file. We use the general Object so as
     * that it can work for any Java Class.
     */
    public static Object deserialize(String fileName) throws IOException,
            ClassNotFoundException {
        FileInputStream fis = new FileInputStream(fileName);
        BufferedInputStream bis = new BufferedInputStream(fis);
        ObjectInputStream ois = new ObjectInputStream(bis);
        Object obj = ois.readObject();
        ois.close();
        return obj;
    }

    /**
     * serialize the given object and save it to given file
     */
    public static void serialize(Object obj, String fileName)
            throws IOException {
        FileOutputStream fos = new FileOutputStream(fileName);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.close();
    }
    
    public String labelDataset(String testFilePath, String modelPath, String outputFile) throws ClassNotFoundException, IOException{
    	 NaiveBayesModel model = (NaiveBayesModel)this.deserialize(modelPath);
         
        
         Map<String, BigDecimal> scores = new HashMap<String, BigDecimal>();
         File resfile = new File(outputFile);
         String[] cat=this.labels.toArray(new String[this.labels.size()]); 

         // if file doesnt exists, then create it
         if (!resfile.exists()) {
             resfile.createNewFile();
         }
         String line = "";
         FileWriter fw = new FileWriter(resfile.getAbsoluteFile());
         BufferedWriter bw = new BufferedWriter(fw);
         BufferedReader br = null;
//         bw.write("Label\tContent\n");
         try {

             br = new BufferedReader(new FileReader(testFilePath));
             while ((line = br.readLine()) != null) {

                 String[] data = line.split("\t");
                 for(int i=0;i<cat.length;i++){
                	 scores.put(cat[i], model.getCategoryProbability(data[1], cat[i]));
                 }
                 
                 Iterator it = MapUtils.sortByValue(scores).entrySet().iterator();
                 Map.Entry<String, BigDecimal> entry = (Map.Entry<String, BigDecimal>) it.next();
                 String expected = data[0];
                 String predicted = entry.getKey();
               
                 bw.write(predicted + "\t");
                 bw.write(data[1] + "\n");
                 

             }

         } catch (Exception e) {
             e.printStackTrace();
         }
         bw.close();
         br.close();
        return outputFile;
    }
    
    public void coLabelDataset(String testFilePath, String modelPath1, String modelPath2, String outoutFile) throws ClassNotFoundException, IOException{
    	NaiveBayesModel model1 = (NaiveBayesModel)this.deserialize(modelPath1);
        
    	NaiveBayesModel model2 = (NaiveBayesModel)this.deserialize(modelPath2);
        Map<String, BigDecimal> scores1 = new HashMap<String, BigDecimal>();
        Map<String, BigDecimal> scores2 = new HashMap<String, BigDecimal>();
        File resfile = new File(outoutFile);


        // if file doesnt exists, then create it
        if (!resfile.exists()) {
            resfile.createNewFile();
        }
        String line = "";
        FileWriter fw = new FileWriter(resfile.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        BufferedReader br = null;
//        bw.write("Label\tContent\n");
        try {

            br = new BufferedReader(new FileReader(testFilePath));
            while ((line = br.readLine()) != null) {

                String[] data = line.split("\t");
                scores1.put("YES", model1.getCategoryProbability(data[1], "YES"));

                scores1.put("NO", model1.getCategoryProbability(data[1], "NO"));
                
                Iterator it1 = MapUtils.sortByValue(scores1).entrySet().iterator();
                Map.Entry<String, BigDecimal> entry1 = (Map.Entry<String, BigDecimal>) it1.next();
                
                scores2.put("YES", model2.getCategoryProbability(data[1], "YES"));

                scores2.put("NO", model2.getCategoryProbability(data[1], "NO"));
                
                Iterator it2 = MapUtils.sortByValue(scores2).entrySet().iterator();
                Map.Entry<String, BigDecimal> entry2 = (Map.Entry<String, BigDecimal>) it2.next();
                String expected = data[0];
                String predicted = "NO";
                if(entry1.getKey()=="YES"&&entry2.getKey()=="YES"){
                	predicted="YES";
                }
               
              
                bw.write(predicted + "\t");
                bw.write(data[1] + "\n");
                

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        bw.close();
        br.close();
       
    }
    
    public static void main5(String[] args) throws Exception{
		NaiveBayesModel test = new NaiveBayesModel();
		System.out.println(test.getLabel("Disgusted by your services","model/NBLinux.ser"));
	}

}
