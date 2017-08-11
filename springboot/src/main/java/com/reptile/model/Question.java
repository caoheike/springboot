package com.reptile.model;

import java.util.List;

/**
 * Created by HotWong on 2017/6/7 0007.
 */
public class Question {
    private String question;
    private List<Option> options;

    @Override
    public String toString() {
        return "Question{" + "question='" + question + '\'' + ", options='" + options.toString() + '\'' +'}';
    }
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }


}
