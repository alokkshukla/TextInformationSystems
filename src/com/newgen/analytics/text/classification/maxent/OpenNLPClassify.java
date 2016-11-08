//package com.newgen.analytics.text.classification.maxent;
//
///**
// * Created by alok.shukla on 9/28/2016.
// */
//import java.io.BufferedOutputStream;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//
//import opennlp.tools.doccat.DoccatModel;
//import opennlp.tools.doccat.DocumentCategorizerEvaluator;
//import opennlp.tools.doccat.DocumentCategorizerME;
//import opennlp.tools.doccat.DocumentSample;
//import opennlp.tools.doccat.DocumentSampleStream;
//import opennlp.tools.util.InvalidFormatException;
//import opennlp.tools.util.ObjectStream;
//import opennlp.tools.util.PlainTextByLineStream;
//
//public class OpenNLPClassify {
//
//    public static void main(String[] args) throws InvalidFormatException,
//            IOException {
//        new OpenNLPClassify().train();
//        String cat = "Lead";
//        String content = "looking for a new credit card";
//        new OpenNLPClassify().test(cat, content,"model/maxent.ser");
//    }
//
//    public void train() {
//        String onlpModelPath = "model/maxent.ser";
//        String trainingDataFilePath = "data/Features.csv";
//        DoccatModel model = null;
//        InputStream dataInputStream = null;
//        OutputStream onlpModelOutput = null;
//        try {
//
//
//            // Read training data file
//            dataInputStream = new FileInputStream(trainingDataFilePath);
//            // Read each training instance
//            ObjectStream<String> lineStream = new PlainTextByLineStream(
//                    dataInputStream, "UTF-8");
//            ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(
//                    lineStream);
//            // Calculate the training model
//            model = DocumentCategorizerME.train("en", sampleStream);
//        } catch (IOException e) {
//            System.err.println(e.getMessage());
//        } finally {
//            if (dataInputStream != null) {
//                try {
//                    dataInputStream.close();
//                } catch (IOException e) {
//                    System.err.println(e.getMessage());
//                }
//            }
//        }
//  /*
//   * Now we are writing the calculated model to a file in order to use the
//   * trained classifier in production
//   */
//        try {
//            onlpModelOutput = new BufferedOutputStream(new FileOutputStream(
//                    onlpModelPath));
//            model.serialize(onlpModelOutput);
//        } catch (IOException e) {
//            System.err.println(e.getMessage());
//        } finally {
//            if (onlpModelOutput != null) {
//                try {
//                    onlpModelOutput.close();
//                } catch (IOException e) {
//                    System.err.println(e.getMessage());
//                }
//            }
//        }
//    }
//
//    /*
//     * Now we call the saved model and test it
//     * Give it a new text document and the expected category
//     */
//    public void test(String cat, String text, String path) throws InvalidFormatException,
//            IOException {
//        String classificationModelFilePath = path;
//        InputStream is = new FileInputStream(classificationModelFilePath);
//        DoccatModel classificationModel = new DoccatModel(is);
//        DocumentCategorizerME classificationME = new DocumentCategorizerME(classificationModel);
//        DocumentCategorizerEvaluator modelEvaluator = new DocumentCategorizerEvaluator(
//                classificationME);
//        String expectedDocumentCategory = cat;
//        String documentContent = text;
//        DocumentSample sample = new DocumentSample(expectedDocumentCategory,
//                documentContent);
//        double[] classDistribution = classificationME.categorize(documentContent);
//      String res = classificationME.getAllResults(classDistribution);
//        String predictedCategory = classificationME.getBestCategory(classDistribution);
//        modelEvaluator.evaluateSample(sample);
//        double result = modelEvaluator.getAccuracy();
//        System.out.println("Model prediction : " + predictedCategory);
//        System.out.println("All : " + res);
//    }
//
//    public String classify(String text) throws InvalidFormatException,
//            IOException {
//        String classificationModelFilePath = "model/maxent.ser";
//        InputStream is = new FileInputStream(classificationModelFilePath);
//        DoccatModel classificationModel = new DoccatModel(is);
//        DocumentCategorizerME classificationME = new DocumentCategorizerME(classificationModel);
//        DocumentCategorizerEvaluator modelEvaluator = new DocumentCategorizerEvaluator(
//                classificationME);
//
//        String documentContent = text;
//        DocumentSample sample = new DocumentSample("DEFAULT",documentContent);
//        double[] classDistribution = classificationME.categorize(documentContent);
//        String predictedCategory = classificationME.getBestCategory(classDistribution);
//        modelEvaluator.evaluateSample(sample);
//
//        return predictedCategory;
//
//    }
//}
