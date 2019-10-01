package com.basicNLP.domain;

import java.util.Map;

public class CosineSimilarity {
    public static Double cosineSimilarity(Map<String,Double> map1, Map<String,Double> map2) {
        Double sum = 0.0;
        for (String s:map1.keySet()) {
            if (map2.containsKey(s)) {
                sum += map1.get(s) * map2.get(s);
            }
        }
        Double toDivideBy=CosineSimilarity.norm(map1)*CosineSimilarity.norm(map2);
        if (toDivideBy==0) return 0.0;
        else return sum /toDivideBy;
    }

    public static Double norm(Map<String,Double> map) {
        Double sum=0.0;
        for (Double s:map.values()) {
            sum=sum+s*s;
        }
        return sum;
    }
}
