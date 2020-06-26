package com.jac.projectaminamani.model;

import androidx.annotation.NonNull;

import com.jac.projectaminamani.Operation;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Random;

public class MathQuestion implements Comparable, Serializable {
    private String questionStr;
    private Operation operation;
    private double correctAnswer;
    private Double userAnswer; // Default: null for un-answered questions
    private boolean userAnswerIsCorrect;

    public MathQuestion() {
        createMathQuestion();
    }

    private void createMathQuestion() {
        // Create Random Numbers and Operation
        Random random = new Random();
        operation = Operation.values()[random.nextInt(4)];
        int firstNumber = random.nextInt(10);
        int secondNumber;
        if (operation == Operation.DIVISION) {
            secondNumber = random.nextInt(9) + 1;
        } else {
            secondNumber = random.nextInt(10);
        }

        // Calculate the correct answer
        switch (operation) {
            case ADDITION:
                correctAnswer = firstNumber + secondNumber;
                break;
            case SUBTRACTION:
                correctAnswer = firstNumber - secondNumber;
                break;
            case MULTIPLICATION:
                correctAnswer = firstNumber * secondNumber;
                break;
            case DIVISION:
                correctAnswer = (double) firstNumber / secondNumber;
                break;
        }

        // Save the Question Text
        String[] operationsStr = {"+", "-", "*", "/"};
        int index = Arrays.asList(Operation.values()).indexOf(operation);
        questionStr = String.format("%d %s %d", firstNumber, operationsStr[index], secondNumber);
    }

    public void saveAndValidateUserAnswer(double userAnswer) {
        this.userAnswer = userAnswer;
        this.userAnswerIsCorrect = (userAnswer == correctAnswer ||
                (operation == Operation.DIVISION && Math.abs(userAnswer - correctAnswer) < 0.01));
    }

    public String getQuestionStr() {
        return questionStr;
    }

    public boolean userAnswerIsCorrect() {
        return userAnswerIsCorrect;
    }


    @Override
    public int compareTo(Object o) {
        MathQuestion mathQuestion = (MathQuestion) o;
        return Arrays.asList(Operation.values()).indexOf(this.operation) -
                Arrays.asList(Operation.values()).indexOf(mathQuestion.operation);
    }

    @NonNull
    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#.##");

        return String.format("%-17s %-20s %-7s",
                questionStr + " = " + df.format(correctAnswer),
                " Your Answer: " + (userAnswer == null ? " " : df.format(userAnswer.doubleValue())),
                (userAnswerIsCorrect ? " Correct" : " Wrong")
        );

    }
}
