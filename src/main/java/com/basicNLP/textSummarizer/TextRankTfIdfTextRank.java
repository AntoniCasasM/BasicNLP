package com.basicNLP.textSummarizer;

import com.basicNLP.domain.CosineSimilarity;
import com.basicNLP.domain.SingularDocument;
import com.basicNLP.preprocesser.Preprocessor;
import com.basicNLP.preprocesser.PreprocessorBasic;
import com.basicNLP.stemmers.PorterStem;
import com.basicNLP.stemmers.Stemmer;
import com.basicNLP.tf_idf.TfIdf;

import java.io.IOException;
import java.util.*;
import java.util.Map;
import java.util.stream.Collectors;

public class TextRankTfIdfTextRank implements TextSummarizerTextRank {

    Map<String, Map<String,Double>> model;
    Map<String,Map<String,Double>> probabilityMatrix;
    Map<String,Double> calculatedPageRank;


    //Corpus is assumed to be sentences in the same domain, not separate full documents
    public TextRankTfIdfTextRank(List<SingularDocument> corpus, Double cutoff, Stemmer stemmer, Preprocessor preprocessor) throws IOException {
        TfIdf tfIdf=new TfIdf(cutoff);
        model=tfIdf.computeTFIDF(corpus,stemmer,preprocessor);
        probabilityMatrix=computeProbabilityMatrix(model);
    }

    private List<String> getTopRankedN(int n){
        Map<String,Double> topN =
                calculatedPageRank.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .limit(n)
                        .collect(Collectors.toMap(
                                Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        return new ArrayList<>(topN.keySet());
    }

    // Damping factor is usually 0.85 or similar
    // Epoch is usually a low value, like 0.00005, higher epochs may lead to reaching maximum steps
    private  void iterate(Double eps,Double dampingFactor, int maxSteps) {
        int counter=0;
        Double epoch=10000000.0;
        Map<String,Double> pr=new HashMap<>();
        for (String s:probabilityMatrix.keySet()) {
            pr.put(s,1.0);
        }
        while (counter<maxSteps && epoch>eps) {
            Map<String,Double> auxPr=new HashMap<>();
            for (String s:probabilityMatrix.keySet()) {
                Double sum=0.0;
                for (String j:pr.keySet()) {
                    sum=sum+(probabilityMatrix.get(s).get(j)*pr.get(j));
                }
                auxPr.put(s,sum);
            }
            Set<String> keySet=auxPr.keySet();
            for (String s:keySet) {
                auxPr.put(s,(1-dampingFactor)+dampingFactor*auxPr.get(s));
            }
            Double convergence=0.0;
            for (String s:keySet) {
                convergence=convergence+Math.abs(pr.get(s)-auxPr.get(s));
            }
            if (convergence<epoch) epoch=convergence;
            pr=auxPr;
        }
        calculatedPageRank=pr;
    }

    @Override
    public List<String> summarizeText(int n,Double eps, Double dampingFactor, int maxSteps) {
        iterate(eps,dampingFactor,maxSteps);
        return getTopRankedN(n);
    }

    private Map<String, Map<String, Double>> computeProbabilityMatrix(Map<String, Map<String, Double>> model) {
        Map<String,Map<String,Double>> probability=new HashMap<>();
        int len=model.keySet().toArray().length;
        String[] arraySet= model.keySet().toArray(String[]::new);

        for (int i=0;i<len;i++) {
            Map<String,Double> auxMap=new HashMap<>();
            String outer=arraySet[i];
            for (int j=0;j<i;++j) {
                String inner=arraySet[j];
                if (outer.equals(inner)) auxMap.put(inner,1.0);
                else auxMap.put(inner, probability.get(inner).get(outer));
            }
            for (int j=i;j<len;j++) {
                String inner=arraySet[j];
                if (outer.equals(inner)) auxMap.put(inner,1.0);
                else auxMap.put(inner, CosineSimilarity.cosineSimilarity(model.get(outer),model.get(inner)));
            }
            probability.put(outer,auxMap);
        }
        for (String s:probability.keySet()) {
            Map<String,Double> aux=normalizeRow(probability.get(s));
            probability.put(s,aux);
        }
        return probability;
    }

    private Map<String, Double> normalizeRow(Map<String, Double> stringDoubleMap) {
        Double sum=0.0;
        for (Double values:stringDoubleMap.values()) {
            sum+=values;
        }
        Map<String,Double> normalizedRow=new HashMap<>();
        for (String s:stringDoubleMap.keySet()) {
            normalizedRow.put(s,stringDoubleMap.get(s)/sum);
        }
        return normalizedRow;
    }

    public static void  main(String[] args) throws IOException {
        List<SingularDocument> sing=new ArrayList<>();

        sing.add(new SingularDocument("1","This is example phrase 1"));
        sing.add(new SingularDocument("2","Nothing to do with each other"));
        sing.add(new SingularDocument("3","Completely new word"));
        sing.add(new SingularDocument("5","Phrases must be unique"));
        sing.add(new SingularDocument("4","Repeated not allowed"));
        sing.add(new SingularDocument("6","Add varied sentences"));
        sing.add(new SingularDocument("7","If a corpus is too similar, it won't see anything"));
        sing.add(new SingularDocument("8","A different and varied corpus is necessary"));


        TextRankTfIdfTextRank summarizer=new TextRankTfIdfTextRank(sing,1.0,new PorterStem(),new PreprocessorBasic());
        summarizer.iterate(0.05,0.85,20);
        System.out.println(summarizer.getTopRankedN(2));

    }


}
