package com.uni.quizlw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Question {
    private String question_text;
    private ArrayList<String> answers = new ArrayList<>();
    private String correctAnswer;


    public Question(String question_text, ArrayList<String> answers, String correctAnswer) {
        this.question_text = question_text;
        this.answers = answers;
        this.correctAnswer = correctAnswer;
    }

    public Question(){}
    public String getQuestion_text() {
        return question_text;
    }

    public ArrayList<String> getAnswers() {
        return answers;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void shuffleAnswers(){
        Collections.shuffle(this.answers);
    }
}
