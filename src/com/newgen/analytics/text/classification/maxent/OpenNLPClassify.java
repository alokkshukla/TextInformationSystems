package com.newgen.analytics.text.classification.maxent;

/**
 * Created by alok.shukla on 9/28/2016.
 */
import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.newgen.analytics.text.classification.evaluation.ClassificationEvaluation;
import com.newgen.analytics.text.classification.evaluation.ConfusionMatrix;
import com.newgen.analytics.text.classification.naivebayes.NaiveBayesModel;
import com.newgen.analytics.text.entities.Document;
import opennlp.tools.doccat.*;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.featuregen.BigramNameFeatureGenerator;

public class OpenNLPClassify {

    public static void main(String[] args) throws Exception {

        OpenNLPClassify maxent = new OpenNLPClassify();
        String modelPath = maxent.trainModel("data/TrainData",5000,1,"model/MaxentComplete.ser");
        String[] cat = {"COMPLAINT","COMPLIMENT","MISCELLANEOUS","REQUEST"};
        ConfusionMatrix mat = maxent.testModel("data/TestData","model/MaxentComplete.ser","results/MaxentComplete.csv",cat);
        //mat.print();
        ClassificationEvaluation e = new ClassificationEvaluation();
        System.out.println("Macro Precision:\t"+e.getEvaluationParameter(mat)[0]);
        System.out.println("Micro Precision:\t"+e.getEvaluationParameter(mat)[1]);

    }




    public String trainModel(String trainFilepath, int iter,int cutoff,String modelPath){
        String onlpModelPath = modelPath;

        DoccatModel model = null;
        InputStream dataInputStream = null;
        OutputStream onlpModelOutput = null;
        try {

            // Read training data file
            dataInputStream = new FileInputStream(trainFilepath);
            // Read each training instance
            ObjectStream<String> lineStream = new PlainTextByLineStream(
                    dataInputStream, "UTF-8");
            ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(
                    lineStream);
            // Calculate the training model
            TrainingParameters par = new TrainingParameters();
            par.put("Cutoff",Integer.toString(cutoff));
            par.put("Iterations",Integer.toString(iter));
            DoccatFactory doccatFactory = new DoccatFactory();
            model = DocumentCategorizerME.train("en", sampleStream, par, doccatFactory);

        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            if (dataInputStream != null) {
                try {
                    dataInputStream.close();
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
  /*
   * Now we are writing the calculated model to a file in order to use the
   * trained classifier in production
   */
        try {
            onlpModelOutput = new BufferedOutputStream(new FileOutputStream(
                    onlpModelPath));
            model.serialize(onlpModelOutput);
            return onlpModelPath;
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            if (onlpModelOutput != null) {
                try {
                    onlpModelOutput.close();
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
       return null;
    }
    public ConfusionMatrix testModel(String testFilePath, String modelPath, String outoutFile, String[] labels) throws Exception {
        String classificationModelFilePath = modelPath;
        InputStream is = new FileInputStream(classificationModelFilePath);
        DoccatModel classificationModel = new DoccatModel(is);
        DocumentCategorizerME classificationME = new DocumentCategorizerME(classificationModel,
                new NGramFeatureGenerator(),
                new BagOfWordsFeatureGenerator());
        DocumentCategorizerEvaluator modelEvaluator = new DocumentCategorizerEvaluator(
                classificationME);



        ConfusionMatrix results = new ConfusionMatrix(labels);
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

                String expected = data[0];
                double[] classDistribution = classificationME.categorize(data[1]);


                String predicted = classificationME.getBestCategory(classDistribution);
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




	public String labelDataset(String additional, String modelPath, String tempPath, String[] cat) throws IOException {
		// TODO Auto-generated method stub
		String classificationModelFilePath = modelPath;
        InputStream is = new FileInputStream(classificationModelFilePath);
        DoccatModel classificationModel = new DoccatModel(is);
        DocumentCategorizerME classificationME = new DocumentCategorizerME(classificationModel,
                new NGramFeatureGenerator(),
                new BagOfWordsFeatureGenerator());
        DocumentCategorizerEvaluator modelEvaluator = new DocumentCategorizerEvaluator(
                classificationME);



        
        File resfile = new File(tempPath);


        // if file doesnt exists, then create it
        if (!resfile.exists()) {
            resfile.createNewFile();
        }
        String line = "";
        FileWriter fw = new FileWriter(resfile.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        BufferedReader br = null;
        //bw.write("Expected Category, Predicted Category,Content\n");
        try {

            br = new BufferedReader(new FileReader(additional));
            while ((line = br.readLine()) != null) {

                String[] data = line.split("\t");

           
                double[] classDistribution = classificationME.categorize(data[1]);


                String predicted = classificationME.getBestCategory(classDistribution);
        
                bw.write(predicted + "\t");
                bw.write(data[1] + "\n");
               

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
       bw.close();
       
		return tempPath;
	}




}
