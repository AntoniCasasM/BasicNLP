package com.basicNLP.sentenceSimilarity;

import com.basicNLP.domain.CosineSimilarity;
import com.basicNLP.preprocesser.Preprocessor;
import com.basicNLP.domain.SingularDocument;
import com.basicNLP.stemmers.Stemmer;
import com.basicNLP.tf_idf.TfIdf;

import java.util.List;

import java.io.IOException;
import java.util.Map;

public class SentenceSimilarityTfIdf implements SentenceSimilarity {

    Map<String, Map<String,Double>> model;

    // Must first generate a tfIdf model
    public void TfIdfSentenceSimilarity(List<SingularDocument> corpus, Double cutoff, Stemmer stem, Preprocessor preprocessor) throws IOException {
        TfIdf tfIdf=new TfIdf(cutoff);
        model=tfIdf.computeTFIDF(corpus,stem, preprocessor);
    }
    @Override
    public Double sentenceSimilarity(String a,String b) {
        return CosineSimilarity.cosineSimilarity(model.get(a),model.get(b));
    }

}
