package com.newgen.analytics.text.classification.ensemble;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.newgen.analytics.text.classification.evaluation.ClassificationEvaluation;
import com.newgen.analytics.text.classification.evaluation.ConfusionMatrix;
import com.newgen.analytics.text.classification.maxent.OpenNLPClassify;
import com.newgen.analytics.text.classification.naivebayes.NaiveBayesModel;
import com.newgen.analytics.text.classification.svm.SVMClassify;

/**
 * Created by alok.shukla on 11/16/2016.
 */
public class EnsembleClassifier {

public EnsembleClassifier(){
	this.nb = new NaiveBayesModel();
	this.maxent = new OpenNLPClassify();
	this.svm = new SVMClassify();
}
	
NaiveBayesModel nb = new NaiveBayesModel();
OpenNLPClassify maxent = new OpenNLPClassify();
SVMClassify svm = new SVMClassify();

public void trainModels(String trainFile,String testFile) throws Exception{

   
    


  String maxentResultsPath = "results/MaxentLinux.csv";
  String nbResultsPath = "results/NBLinux.csv";
   
//    String nbModelPath = this.nb.trainModel(trainFile,"model/NBLinux.ser");
    String maxentModelPath = this.maxent.trainModel(trainFile,5000,1,"model/MaxentLinux.ser");
//   
//    this.maxent.testModel(testFile,maxentModelPath,maxentResultsPath);
//    this.nb.testModel(testFile, nbModelPath, nbResultsPath);
//    this.svm.trainModel("/root/workspace/TextInformationSystem/Train.sh");
//    this.svm.testModel("/root/workspace/TextInformationSystem/Test.sh");

    
}

public void evaluateModels(String nb,String mx,String svm,String finalRes) throws IOException{
	
	String[] cats=this.nb.getLabels().toArray(new String[this.nb.getLabels().size()]); 
	ConfusionMatrix results = new ConfusionMatrix(cats);
    Map<String, Map<String, Integer>> confusionMatrix = results.getConfusionMatrix(); 
	File resfile = new File(finalRes);


     // if file doesnt exists, then create it
     if (!resfile.exists()) {
         resfile.createNewFile();
     }
     String line = "";
     FileWriter fw = new FileWriter(resfile.getAbsoluteFile());
     BufferedWriter bw = new BufferedWriter(fw);
     BufferedReader br1=null,br2=null,br3 = null;
     List<String> nbCats = new ArrayList<>();
     List<String> mxCats = new ArrayList<>();
     List<String> svmCats = new ArrayList<>();
     List<String> actCats = new ArrayList<>();
     
     try {

         br1 = new BufferedReader(new FileReader(nb));
         br2 = new BufferedReader(new FileReader(mx));
         br3 = new BufferedReader(new FileReader(svm));
         for(int i=1;i<=1;i++)
         {
             br1.readLine();
             br2.readLine();
         }
         
         for(int i=1;i<=6;i++)
         {
             br3.readLine();
         }
         while ((line = br1.readLine()) != null) {
         String[] data = line.split(",");
         nbCats.add(data[1]);
         actCats.add(data[0]);
         }
         }catch(Exception e){
        	 e.printStackTrace();
         }
     while ((line = br2.readLine()) != null) {
         String[] data = line.split(",");
         mxCats.add(data[1]);
         }
     while ((line = br3.readLine()) != null) {
         String[] data = line.split("\t");
         svmCats.add(data[0]);
         }
     
     br1.close();
     br2.close();
     br3.close();
     bw.write("Expected Category, Predicted Category,Content\n");
     try {
    	 br1 = new BufferedReader(new FileReader(nb));
         br1.readLine();
    	 String expected = "";
    	 String predicted = "";
         for(int i=0;i<actCats.size();i++){
        	 expected = actCats.get(i);
        	 String content = br1.readLine().split(",")[2];
        	 if(svmCats.get(i)==mxCats.get(i)){
        		 predicted = mxCats.get(i);
        	 }
        	 else if(svmCats.get(i)==nbCats.get(i)){
        		 predicted = nbCats.get(i);
        	 }
        	 else if(nbCats.get(i)==mxCats.get(i)){
        		 predicted = mxCats.get(i);
        	 }else{
        		 predicted = svmCats.get(i);
        	 }
        	 bw.write(expected + ",");
             bw.write(predicted + ",");
             bw.write(content + "\n");
             
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
     br1.close();
    ClassificationEvaluation eval = new ClassificationEvaluation();
    results.setConfusionMatrix(confusionMatrix);
    double[] res = eval.getEvaluationParameter(results);
     System.out.println("Macro: "+res[0]);
     System.out.println("Micro: "+res[1]);
         
}

public String getClass(String content) throws Exception{
	
	String nb = this.nb.getLabel(content, "model/NBLinux.ser");
	String mx = this.maxent.getLabel(content, "model/MaxentLinux.ser");
	String svm = this.svm.getLabel(content);
	String label="";
	if(nb==mx){
		label = nb;
	}else if(nb==svm){
		label=svm;
	}else if(mx==svm){
		label=svm;
	}else{
		label=svm;
	}
	return label;
}


	 public static void main (String[] args) throws Exception
	  {
		 
		 EnsembleClassifier c = new EnsembleClassifier();
	    String content = "START";
	    while(true){
		 // create a scanner so we can read the command-line input
	    
		 Scanner scanner = new Scanner(System.in);

	    //  prompt for the user's name
	    System.out.print("Tweet (EXIT to quit):  ");

	    // get their input as a String
	    content = scanner.next();
	    if(content.contains("EXIT")){
	    	break;
	    }

	   

	    System.out.println(String.format("Prediction: "+c.getClass(content)));
	    }

	  

}
}
