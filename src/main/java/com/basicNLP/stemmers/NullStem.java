package com.basicNLP.stemmers;

import com.basicNLP.stemmers.Stemmer;

class NullStem implements Stemmer {
    @Override
    public String stem(String s) {
        return s;
    }
}
