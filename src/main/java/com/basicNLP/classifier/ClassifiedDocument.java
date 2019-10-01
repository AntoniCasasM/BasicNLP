package com.basicNLP.classifier;

public class ClassifiedDocument {
    String text;
    String category;

    public ClassifiedDocument(String category,String text) {
        this.text=text;
        this.category=category;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


}
