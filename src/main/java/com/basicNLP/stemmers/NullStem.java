package com.basicNLP.domain;

class NullStem implements Stemmer{

    public String stem(String s) {
        return s;
    }
}
