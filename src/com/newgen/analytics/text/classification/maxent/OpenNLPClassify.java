package com.newgen.analytics.text.classification.maxent;

/**
 * Created by alok.shukla on 9/28/2016.
 */
import java.io.*;

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

    public static void main5(String[] args) throws InvalidFormatException,
            IOException {

        OpenNLPClassify maxent = new OpenNLPClassify();
        maxent.train();

        File file = new File("results/MaxentRes20k.csv");


        // if file doesnt exists, then create it
        if (!file.exists()) file.createNewFile();

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write("Expected,Predicted,Content\n");
        String[] data = new String[2];
        BufferedReader br = null;
        String line;
        try {

            br = new BufferedReader(new FileReader("data/Test"));
            while ((line = br.readLine()) != null) {
                data = line.split("\t");
                {

                    if(data.length>1) {
                        String tag = maxent.classify(data[1]);
                        if(!tag.equalsIgnoreCase(data[0])){
                            bw.write(data[0]+",");
                            bw.write(tag+",");
                            bw.write(data[1]+"\n");
                        }

                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            // System.out.print(data);
        }
//        new OpenNLPClassify().test(cat, content,"model/maxent.ser");
        bw.close();
        br.close();
    }


    public static void main(String[] args) throws Exception {
        new OpenNLPClassify().test("","need a credit card","model/maxent.ser");
    }
    public void train() {
        String onlpModelPath = "model/maxent.ser";
        String trainingDataFilePath = "data/Train";
        DoccatModel model = null;
        InputStream dataInputStream = null;
        OutputStream onlpModelOutput = null;
        try {


            // Read training data file
            dataInputStream = new FileInputStream(trainingDataFilePath);
            // Read each training instance
            ObjectStream<String> lineStream = new PlainTextByLineStream(
                    dataInputStream, "UTF-8");
            ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(
                    lineStream);
            // Calculate the training model
            TrainingParameters par = new TrainingParameters();
            par.put("Cutoff","1");
            par.put("Iterations","50000");
//            FeatureGenerator feat = (FeatureGenerator) new BigramNameFeatureGenerator();
//            TokenizerModel mod = new TokenizerModel();
//            Tokenizer tok = new TokenizerME();
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
    }

    /*
     * Now we call the saved model and test it
     * Give it a new text document and the expected category
     */
    public void test(String cat, String text, String path) throws InvalidFormatException,
            IOException {
        String classificationModelFilePath = path;
        InputStream is = new FileInputStream(classificationModelFilePath);
        DoccatModel classificationModel = new DoccatModel(is);
        DocumentCategorizerME classificationME = new DocumentCategorizerME(classificationModel);
        DocumentCategorizerEvaluator modelEvaluator = new DocumentCategorizerEvaluator(
                classificationME);
        String expectedDocumentCategory = cat;
        String documentContent = text;
        DocumentSample sample = new DocumentSample(expectedDocumentCategory,
                documentContent);
        double[] classDistribution = classificationME.categorize(documentContent);
      String res = classificationME.getAllResults(classDistribution);
        String predictedCategory = classificationME.getBestCategory(classDistribution);
        modelEvaluator.evaluateSample(sample);
        double result = modelEvaluator.getAccuracy();
        System.out.println("Model prediction : " + predictedCategory);
        System.out.println("All : " + res);
    }

    public String classify(String text) throws InvalidFormatException,
            IOException {
        String classificationModelFilePath = "model/maxent.ser";
        InputStream is = new FileInputStream(classificationModelFilePath);
        DoccatModel classificationModel = new DoccatModel(is);
        DocumentCategorizerME classificationME = new DocumentCategorizerME(classificationModel);
        DocumentCategorizerEvaluator modelEvaluator = new DocumentCategorizerEvaluator(
                classificationME);

        String documentContent = text;
        DocumentSample sample = new DocumentSample("DEFAULT",documentContent);
        double[] classDistribution = classificationME.categorize(documentContent);
        String predictedCategory = classificationME.getBestCategory(classDistribution);
        modelEvaluator.evaluateSample(sample);

        return predictedCategory;

    }
}
