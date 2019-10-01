package com.basicNLP.classifier;

import com.basicNLP.preprocesser.PreprocessorBasic;
import com.basicNLP.preprocesser.Preprocessor;
import com.basicNLP.stemmers.PorterStem;
import com.basicNLP.stemmers.Stemmer;

import java.io.IOException;
import java.util.*;

public class ClassifierNaiveBayes {

    Map<String,Map<String,Double>> wordCount;
    Map<String,Double> categories;

    public ClassifierNaiveBayes(List<ClassifiedDocument> corpus, Stemmer stemmer, Preprocessor preprocess) throws IOException {
        categories=new HashMap<>();
        Map<String,Map<String,Double>> auxWordCount=new HashMap<>();
        for (ClassifiedDocument doc:corpus) {
            String text=doc.getText();
            text=preprocess.preprocess(text);
            for (String s:text.split(" ")) {
                if (s.length()>1) {
                s=stemmer.stem(s);
                if (categories.containsKey(doc.getCategory())) {
                    categories.put(doc.getCategory(),categories.get(doc.getCategory())+1.0);
                }
                else {
                    categories.put(doc.getCategory(),1.0);
                }
                if (auxWordCount.containsKey(s)) {
                    Map<String,Double> aux=auxWordCount.get(s);
                    if (aux.containsKey(doc.getCategory())) {
                        aux.put(doc.getCategory(),aux.get(doc.getCategory())+1.0);
                    }
                    else {
                        aux.put(doc.getCategory(),1.0);
                    }
                    auxWordCount.put(s,aux);
                }
                else {
                    Map<String,Double> aux=new HashMap<>();
                    aux.put(doc.getCategory(),1.0);
                    auxWordCount.put(s,aux);
                    }
                }
            }
        }
        wordCount=auxWordCount;
        System.out.println(wordCount.keySet());
    }

    public String classify(String s,Stemmer stemmer, Preprocessor preprocess) throws IOException {
        s=preprocess.preprocess(s);
        Map<String,Double> wordBag=new HashMap<>();
        for (String j:s.split(" ")) {
            j=stemmer.stem(j);
            if (wordBag.containsKey(j)) wordBag.put(j,wordBag.get(j)+1.0);
            else wordBag.put(j,1.0);
        }
        System.out.println(wordBag.keySet());
        Map<String,Double> scores=new HashMap<>();
        for (String category:categories.keySet()) {
            Double score=1.0;
            for (String k:wordBag.keySet()) {
                if (wordCount.containsKey(k)) {
                    Double wordTotal=0.0;
                    Map<String,Double> temporal=wordCount.get(k);
                    Double wordCategory=0.0;
                    Double wordCategoryFrequency=0.0;
                    if (temporal.containsKey(category)) wordCategory = categories.get(category);
                    for (Double d:categories.values()) {
                        wordTotal+=d;
                    }
                    if (temporal.containsKey(category)) wordCategoryFrequency=temporal.get(category);
                    Double prob=(1.0+wordCategoryFrequency)/(wordTotal+wordCategory);
                    score=score*prob;
                }
            }
            scores.put(category,score);
        }
        Double bigger=0.0;
        String result="";
        for (String j:scores.keySet()) {
            if (scores.get(j)>bigger) {
                bigger=scores.get(j);
                result=j;
            }
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        List<ClassifiedDocument> sing=new ArrayList<>();

        sing.add(new ClassifiedDocument("spam","You've won our contest, come claim your prize"));
        sing.add(new ClassifiedDocument("ham","I am independent and not like the other ones"));
        sing.add(new ClassifiedDocument("spam","contest won, come claim your prize"));
        Stemmer stem=new PorterStem();
        Preprocessor prep=new PreprocessorBasic();
        ClassifierNaiveBayes classifier=new ClassifierNaiveBayes(sing,stem,prep);
        System.out.println(classifier.classify("I am independent and not like the others",stem,prep));

    }
}

