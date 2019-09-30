package com.basicNLP.preprocesser;

import java.io.IOException;

public interface Preprocessor {
    public String preprocess(String s) throws IOException;
}
