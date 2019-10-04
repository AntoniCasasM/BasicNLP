package com.basicNLP.markov;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarkovChain {

    private Map<String, Map<String,Double>> chain;
    private Map<String,Integer> totalCasesObserved;

    public MarkovChain(List<String> toFeed) {
        trainChain(toFeed);
    }

    private void trainChain(List<String> toFeed) {
        Map<String,Integer> casesObserved=new HashMap<>();
        Map<String,Map<String,Integer>> nextWordCount=new HashMap<>();
        casesObserved.put(null,0);
        for (String s:toFeed) {
            s = s.replaceAll("[.,;\\\"/:|!?=()><_{}'+\\[\\]]", " ");
            String[] toIterate=s.split(" ");
            String pastWord=null;
            for (int i=0;i<toIterate.length;++i) {
                if (casesObserved.containsKey(toIterate[i])) casesObserved.put(toIterate[i],1+casesObserved.get(toIterate[i]));
                else casesObserved.put(toIterate[i],1);
                if (nextWordCount.containsKey(pastWord)) {
                    Map<String,Integer> aux=nextWordCount.get(pastWord);
                    if (aux.containsKey(toIterate[i])) {
                        aux.put(toIterate[i],aux.get(toIterate[i])+1);
                    }
                    else {
                        aux.put(toIterate[i],1);
                    }
                    nextWordCount.put(pastWord,aux);
                }
                else {
                    Map<String,Integer> aux=new HashMap<>();
                    aux.put(toIterate[i],1);
                    nextWordCount.put(pastWord,aux);
                }
                pastWord=toIterate[i];
            }
            casesObserved.put(null,1+casesObserved.get(null));
            if (nextWordCount.containsKey(pastWord)) {
                Map<String,Integer> aux=nextWordCount.get(pastWord);
                if (aux.containsKey(null)) {
                    aux.put(null,aux.get(null)+1);
                }
                else {
                    aux.put(null,1);
                }
                nextWordCount.put(pastWord,aux);
            }
            else {
                Map<String,Integer> aux=new HashMap<>();
                aux.put(null,1);
                nextWordCount.put(pastWord,aux);
            }

        }
        Map<String,Map<String,Double>> aux=new HashMap<>();
        for (String s:nextWordCount.keySet()) {
            int cases=casesObserved.get(s);
            Map<String,Double> helper=new HashMap<>();
            for (String j:nextWordCount.get(s).keySet()) {
                helper.put(j, (nextWordCount.get(s).get(j)/(double) cases));
            }
            aux.put(s,helper);
        }
        chain=aux;
        totalCasesObserved=casesObserved;
    }

    public List<String> textChain(int length) {
        List<String> result=new ArrayList<>();
        String currentState=null;
        for (int i=0;i<length;++i) {
            Double prob=Math.random();
            Double probSum=0.0;
            for (String m:chain.get(currentState).keySet()) {
                Double transitionProb=chain.get(currentState).get(m);
                System.out.println(m+" "+transitionProb);
                if ((transitionProb+probSum)>=prob) {
                    result.add(m);
                    currentState=m;
                    break;
                }
                probSum+=transitionProb;
                if (probSum>1.0) {
                    currentState=m;
                    result.add(m);
                    break;
                }
            }
            if (currentState==null) break;
        }
        return result;
    }
    public String nextWord(String s) {
        Double prob=Math.random();
        Double probSum=0.0;
        for (String m:chain.get(s).keySet()) {
            Double transitionProb=chain.get(s).get(m);
            if ((transitionProb+probSum)>=prob) {
                return m;
            }
            probSum+=transitionProb;
            if (probSum>1.0) {
                return m;
            }
        }
        return null;
    }


        public static void main(String[] args) {
        List<String> aux=new ArrayList<>();
        aux.add("This is the first sentence");
        aux.add("This is the second sentence");
        aux.add("This nonsensical");
        aux.add("This second nonsensical sentence");
        MarkovChain mark=new MarkovChain(aux);
        System.out.println(mark.textChain(6));
        System.out.println(mark.nextWord("second"));
    }

}
