package com.basicNLP.preprocesser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class BasicPreprocessor implements Preprocessor {
    @Override
    public String preprocess(String s) throws IOException {
        s=clean(s);
        return removeStopWords(s);
    }

    public String clean(String s) {
        // Removes all text between { } and replaces it with the word code
        s = s.replaceAll("(\\{.*?})", " code ");
        // Removes special characters and numbers
        s = s.replaceAll("[.$,;\\\"/:|!?=()><_{}'+%[0-9]\\[\\]]", " ");
        return s;
    }

    public String removeStopWords(String text) throws IOException {
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
