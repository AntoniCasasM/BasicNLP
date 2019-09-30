package com.basicNLP.textSummarizer;

import java.util.*;

public interface TextSummarizerTextRank {

    //n is the amount of top strings wanted, eps is the convergence treshold, if reached it will stop even if not at maxSteps, dampingFactor is the chance of an error, its usually 0.85
    // maxSteps is the maximum amount of steps
    public List<String> summarizeText(int n,Double eps, Double dampingFactor, int maxSteps);
}
