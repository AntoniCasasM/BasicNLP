package com.basicNLP.preprocesser;

import java.io.IOException;

public class PreprocessorNull implements Preprocessor {
    @Override
    public String preprocess(String s) throws IOException {
        return s;
    }

}
