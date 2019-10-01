package com.basicNLP.sentenceSimilarity;

import com.basicNLP.domain.CosineSimilarity;
import com.basicNLP.preprocesser.PreprocessorBasic;
import com.basicNLP.preprocesser.Preprocessor;
import com.basicNLP.domain.SingularDocument;
import com.basicNLP.stemmers.PorterStem;
import com.basicNLP.stemmers.Stemmer;
import com.basicNLP.tf_idf.TfIdf;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.util.Map;

public class SentenceSimilarityTfIdf implements SentenceSimilarity {

    Map<String, Map<String,Double>> model;

    // Must first generate a tfIdf model
    public SentenceSimilarityTfIdf(List<SingularDocument> corpus, Double cutoff, Stemmer stem, Preprocessor preprocessor) throws IOException {
        TfIdf tfIdf=new TfIdf(cutoff);
        model=tfIdf.computeTFIDF(corpus,stem, preprocessor);
        System.out.println(model.keySet());
    }
    @Override
    public Double sentenceSimilarity(String a,String b) {
        if (model.containsKey(a) && model.containsKey(b)) return CosineSimilarity.cosineSimilarity(model.get(a),model.get(b));
        else return 0.0;
    }


    // To test the algorithm and basic usage
    public static void  main(String[] args) throws IOException {
        List<SingularDocument> sing=new ArrayList<>();

        sing.add(new SingularDocument("1","This is example phrase 1"));
        sing.add(new SingularDocument("2","I am a new example"));
        sing.add(new SingularDocument("3","The following phrase is also an [word] example"));
        SentenceSimilarityTfIdf sentenceSimilarity=new SentenceSimilarityTfIdf(sing,1.0,new PorterStem(),new PreprocessorBasic());
        Double result=sentenceSimilarity.sentenceSimilarity("1","2");
        System.out.println(result);

    }
}
