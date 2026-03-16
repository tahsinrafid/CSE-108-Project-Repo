package com.example.examsystemproject;

public class Question {
    private String quesID;
    private String quesText;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String correctAns;

    public Question(String quesID, String quesText, String option1, String option2, String option3, String option4, String correctAns){
        this.quesID = quesID;
        this.quesText = quesText;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.correctAns = correctAns;
    }

    public String getQuesID() {
        return quesID;
    }

    public String getQuesText() {
        return quesText;
    }

    public String getOption1() {
        return option1;
    }

    public String getOption2() {
        return option2;
    }

    public String getOption3() {
        return option3;
    }

    public String getOption4() {
        return option4;
    }

    public String getCorrectAns() {
        return correctAns;
    }
}
