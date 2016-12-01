package com.newgen.analytics.text.classification.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.newgen.analytics.text.classification.evaluation.ClassificationEvaluation;
import com.newgen.analytics.text.classification.evaluation.ConfusionMatrix;
import com.newgen.analytics.text.classification.maxent.OpenNLPClassify;
import com.newgen.analytics.text.classification.naivebayes.NaiveBayesModel;

public class CoClassification {

	public String createCoClassificationModel(String trainFile,String testFile,String[] cat,String additional,String modelPath, String resFile,String algo) throws Exception{
		String line="";
		switch(algo){
		case "NB":
			// Init model
			NaiveBayesModel helper = new NaiveBayesModel();
			modelPath = helper.trainModel(trainFile,modelPath);
			ConfusionMatrix mat = helper.testModel(testFile, modelPath, resFile);
			ClassificationEvaluation eval = new ClassificationEvaluation();
			double mi = eval.getEvaluationParameter(mat)[1];
			double ma = eval.getEvaluationParameter(mat)[0];
			System.out.println("Initial Macro Precision:\t"+ma);
			System.out.println("Initial Micro Precision:\t"+mi);

			double diff = 5;
			double threshold = 0.001;
			while(Math.abs(diff)>threshold&&diff!=0){
				// Predict unlabeled
				String labelledFile = helper.labelDataset(additional, modelPath, "temp/TempLabels");

				// Concatenate dataset
				String updatedDataset = "temp/TempDataSet";
				File tempDataSet = new File(updatedDataset);
				if (!tempDataSet.exists()) {
					tempDataSet.createNewFile();
				}

				FileWriter fw = new FileWriter(tempDataSet);
				File file1 = new File(trainFile);
				File file2 = new File(labelledFile);
				FileInputStream fis = new FileInputStream(file1);
				byte[] data = new byte[(int) file1.length()];
				fis.read(data);
				fis.close();

				String str = new String(data, "UTF-8");

				fis = new FileInputStream(file2);
				data = new byte[(int) file2.length()];
				fis.read(data);
				fis.close();

				str+="\n"+new String(data, "UTF-8");
				fw.write(str);
				fw.close();


				// RebuildModel
				modelPath = helper.trainModel(updatedDataset,modelPath);
				mat = helper.testModel(testFile, modelPath, resFile);
				eval = new ClassificationEvaluation();

				double newMa=eval.getEvaluationParameter(mat)[0];
				double newMi=eval.getEvaluationParameter(mat)[1];

				diff=newMa-ma;
				System.out.println("Gain: "+diff);
				ma=newMa;
				mi=newMi;
			}
			System.out.println("Final Macro Precision:\t"+ma);
			System.out.println("Final Micro Precision:\t"+mi);
			return modelPath;

		case "ME":
			// Init model
			OpenNLPClassify maxent = new OpenNLPClassify();
			modelPath = maxent.trainModel(trainFile,500,1,modelPath);
			ConfusionMatrix c = maxent.testModel(testFile, modelPath, resFile);
			ClassificationEvaluation e = new ClassificationEvaluation();
			double mip = e.getEvaluationParameter(c)[1];
			double map = e.getEvaluationParameter(c)[0];
			System.out.println("Initial Macro Precision:\t"+map);
			System.out.println("Initial Micro Precision:\t"+mip);
			c.print();

			double difference = 5;
			double thres = 0.001;
			while(Math.abs(difference)>thres&&difference!=0.0){
				// Predict unlabeled
				String labelledFile = maxent.labelDataset(additional, modelPath, "temp/TempMaxentLabels");

				// Concatenate dataset
				String updatedDataset = "temp/TempMaxentDataSet";
				File tempDataSet = new File(updatedDataset);
				if (!tempDataSet.exists()) {
					tempDataSet.createNewFile();
				}

				FileWriter fw = new FileWriter(tempDataSet);
				File file1 = new File(trainFile);
				File file2 = new File(labelledFile);
				FileInputStream fis = new FileInputStream(file1);
				byte[] data = new byte[(int) file1.length()];
				fis.read(data);
				fis.close();

				String str = new String(data, "UTF-8");

				fis = new FileInputStream(file2);
				data = new byte[(int) file2.length()];
				fis.read(data);
				fis.close();

				str+="\n"+new String(data, "UTF-8");
				fw.write(str);
				fw.close();


				// RebuildModel
				modelPath = maxent.trainModel(updatedDataset,500,1,modelPath);
				mat = maxent.testModel(testFile, modelPath, resFile);
				eval = new ClassificationEvaluation();
				mat.print();
				double newMa=eval.getEvaluationParameter(mat)[0];
				double newMi=eval.getEvaluationParameter(mat)[1];

				difference=newMa-map;
				System.out.println("Gain: "+difference);
				map=newMa;
				mip=newMi;
			}
			System.out.println("Final Macro Precision:\t"+map);
			System.out.println("Final Micro Precision:\t"+mip);
			return modelPath;
			
		default:
			break;
		}
		return modelPath;
	}
	public static void main(String[] args) throws Exception{

		String trainFile = "data/TrainNew";
		String testFile = "data/TestNew";
		String resFile = "results/CoClassRes.CSV";
		String additional ="data/Additional";
		String modelPath = "model/NBCoClass.ser";
		String[] cat = {"COMPLAINT","COMPLIMENT","MISCELLANEOUS","REQUEST"};
		CoClassification c = new CoClassification();
		c.createCoClassificationModel(trainFile, testFile, cat, additional, modelPath, resFile, "ME");
	}

}
