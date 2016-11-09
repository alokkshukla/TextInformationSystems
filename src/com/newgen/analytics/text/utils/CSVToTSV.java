package com.newgen.analytics.text.utils;

/**
 * Created by alok.shukla on 11/9/2016.
 */


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CSVToTSV
{
    public static void main(String[] args)
    {
        CSVToTSV tester = new CSVToTSV();
        tester.cleanUp("data/Data.csv", "data/Data.tsv");
    }

    public void cleanUp(String input, String output)
    {
        String csvFile = input;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        try
        {
            File file = new File(output);
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null)
            {
                while (!line.contains("END")) {
                    line = line + br.readLine();
                }
                String[] data = line.split(cvsSplitBy);
                String label = data[0];
                if (label.length() >= 3)
                {
                    String value = "";
                    for (int i = 1; i < data.length; i++) {
                        value = value + data[i];
                    }
                    value = value.replaceAll("\"", "");
                    value = value.trim().replaceAll(" +", " ");

                    value = value.replaceAll("[\r\n]+", " ");
                    if (value.contains("END")) {
                        value = value.substring(0, value.length() - 3);
                    }
                    bw.write(label.toUpperCase());
                    bw.write("\t");
                    bw.write(value.toLowerCase());

                    bw.write("\n");
                }
            }
            bw.close(); return;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (br != null) {
                try
                {
                    br.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}

