package com.basicNLP.tf_idf;

import com.basicNLP.preprocesser.PreprocessorBasic;
import com.basicNLP.preprocesser.Preprocessor;
import com.basicNLP.domain.SingularDocument;
import com.basicNLP.stemmers.PorterStem;
import com.basicNLP.stemmers.Stemmer;

import java.io.IOException;
import java.util.*;


public class TfIdf {

    private Double cutoffParameter = 0.0; //This can be set to different values for different selectivity (more or less keywords)
    private HashMap<String, Integer> corpusFrequency = new HashMap<>();

    public TfIdf(Double cutoff) {
        cutoffParameter=cutoff;
    }

    private Map<String, Integer> tf(List<String> doc) {
        Map<String, Integer> frequency = new HashMap<>();
        for (String s : doc) {
            if (frequency.containsKey(s)) frequency.put(s, frequency.get(s) + 1);
            else {
                frequency.put(s, 1);
                if (corpusFrequency.containsKey(s)) corpusFrequency.put(s, corpusFrequency.get(s) + 1);
                else corpusFrequency.put(s, 1);
            }

        }
        return frequency;
    }


    private double idf(Integer size, Integer frequency) {
        return Math.log(size.doubleValue() / frequency.doubleValue() + 1.0);
    }


    private List<String> analyze(String text, Stemmer stemmer, Preprocessor preprocessor) throws IOException {
        text = preprocessText(text, preprocessor);
        List<String> auxText= new ArrayList<>();
        for (String s:text.split(" ")) {
            auxText.add(stemmer.stem(s)+" ");
        }
        return auxText;
    }

    public Map<String, Map<String, Double>> computeTFIDF(List<SingularDocument> corpus, Stemmer stemmer, Preprocessor preprocessor) throws IOException {
        List<List<String>> docs = new ArrayList<>();
        for (SingularDocument r : corpus) {
            docs.add(analyze(r.getText(),stemmer, preprocessor));
        }
        List<Map<String, Double>> res = tfIdf(docs);
        int counter = 0;
        return tfIdfMatrix(corpus, res, counter);

    }

    private Map<String, Map<String, Double>> tfIdfMatrix(List<SingularDocument> corpus, List<Map<String, Double>> res, int counter) {
        Map<String, Map<String, Double>> ret = new HashMap<>();
        for (SingularDocument r : corpus) {
            ret.put(r.getId(), res.get(counter));
            counter++;
        }
        return ret;
    }

    private List<Map<String, Double>> tfIdf(List<List<String>> docs) {
        List<Map<String, Double>> tfidfComputed = new ArrayList<>();
        List<Map<String, Integer>> wordBag = new ArrayList<>();
        for (List<String> doc : docs) {
            wordBag.add(tf(doc));
        }
        int i = 0;
        for (List<String> doc : docs) {
            HashMap<String, Double> aux = new HashMap<>();
            for (String s : doc) {
                Double idf = idf(docs.size(), corpusFrequency.get(s));
                Integer tf = wordBag.get(i).get(s);
                Double tfidf = idf * tf;
                if (tfidf >= cutoffParameter && s.length() > 1) aux.put(s, tfidf);
            }
            tfidfComputed.add(aux);
            ++i;
        }
        return tfidfComputed;

    }

    private String preprocessText(String text, Preprocessor preprocessor) throws IOException {
        text = preprocessor.preprocess(text);
        return text;
    }


    public HashMap<String, Integer> getCorpusFrequency() {
        return corpusFrequency;
    }

    public void setCorpusFrequency(HashMap<String, Integer> corpusFrequency) {
        this.corpusFrequency = corpusFrequency;
    }

    public Double getCutoffParameter() {
        return cutoffParameter;
    }

    public void setCutoffParameter(Double cutoffParameter) {
        this.cutoffParameter = cutoffParameter;
    }


    // To test the algorithm and basic usage
    public static void  main(String[] args) throws IOException {
        List<SingularDocument> sing=new ArrayList<>();

        sing.add(new SingularDocument("1","This is example phrase 1"));
        sing.add(new SingularDocument("2","I am a new example"));
        sing.add(new SingularDocument("3","The following phrase is also an [word] example"));
        TfIdf tfidf=new TfIdf(1.0);
        Map<String, Map<String, Double>> result=tfidf.computeTFIDF(sing,new PorterStem(),new PreprocessorBasic());
        for (String aux:result.get("3").keySet()) {
            System.out.println(aux+" "+result.get("3").get(aux));
        }

    }

}
