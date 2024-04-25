package com.uni.quizlw;

import java.util.Comparator;

public class ScoreLine implements Comparator<ScoreLine> {
    private String name;
    private int score;
    private int maxScore;

    public ScoreLine(String name, int score, int maxScore) {
        this.name = "" + name;
        this.score = score;
        this.maxScore = maxScore;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public int getMaxScore() {
        return maxScore;
    }




    @Override
    public int compare(ScoreLine o1, ScoreLine o2) {
        if(o1.maxScore < o2.maxScore)
            return -1;
        if(o1.score < o2.score)
            return -1;
        return 0;
    }
}
