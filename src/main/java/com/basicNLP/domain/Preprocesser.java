package com.basicNLP.domain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Preprocesser {

    public static String removeStopWords(String text) throws IOException {
        String trueRes="";
        BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/StopWords.txt"));
        String word = null;
        Set<String> stopWords = new HashSet<>();
        while ((word = reader.readLine()) != null) {
            stopWords.add(word);
        }
        reader.close();
        //Standarize all text to lowercase
        for (String l : text.split(" ")) {
            // Remove all words of length 1 and all stopwords
                // Evade bug with toLowecase where "null" is given instead of null on some values
            if (!(l.toLowerCase().equals("null") && !l.equals("null") && !l.equals("Null")) && !l.toUpperCase().equals(l))  l = l.toLowerCase();
            if (l != null && !stopWords.contains(l) && l.length() > 1) {
                trueRes = trueRes.concat(l + " ");
            }
        }
        return trueRes;
    }

}
