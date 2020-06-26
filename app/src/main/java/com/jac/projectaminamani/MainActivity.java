package com.jac.projectaminamani;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jac.projectaminamani.model.MathQuestion;

import java.util.ArrayList;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private TextView textViewCalcDisplay, textViewQuestion, textViewTitle;
    private Button buttonValidate;
    private boolean periodIsClicked;
    private ArrayList<MathQuestion> mathQuestions;
    private final int RESULT_CODE_1 = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        textViewCalcDisplay = findViewById(R.id.textViewCalcDisplay);
        textViewQuestion = findViewById(R.id.textViewQuestion);
        textViewTitle = findViewById(R.id.textViewTitle);
        buttonValidate = findViewById(R.id.buttonValidate);

        startQuiz();
    }

    private void startQuiz() {
        buttonValidate.setEnabled(false);
        mathQuestions = new ArrayList<>();
        generateQuestion();
    }

    private void generateQuestion() {
        // Clear the Display
        clearDisplay();

        // Create a Question & Add to List
        mathQuestions.add(new MathQuestion());

        // Display the Question
        textViewQuestion.setText(Objects.requireNonNull(getCurrentQuestion()).getQuestionStr());
    }

    private MathQuestion getCurrentQuestion() {
        return mathQuestions != null && !mathQuestions.isEmpty() ? mathQuestions.get(mathQuestions.size() - 1) : null;
    }

    private void clearDisplay() {
        textViewCalcDisplay.setText("0");
        periodIsClicked = false;
        buttonValidate.setEnabled(false);
    }

    private void validateAnswer() {
        try {
            // get user answer
            double userAnswer = Double.parseDouble(textViewCalcDisplay.getText().toString());
            // save & check user answer
            Objects.requireNonNull(getCurrentQuestion()).saveAndValidateUserAnswer(userAnswer);
        } catch (Exception ignored) {
            // userAnswer remains null and userAnswerIsCorrect remains false.
        }

        // display message to user
        Toast toastMessage = Toast.makeText(this,
                Objects.requireNonNull(getCurrentQuestion()).userAnswerIsCorrect()
                        ? getText(R.string.correct_message)
                        : getText(R.string.wrong_message),
                Toast.LENGTH_SHORT);
        toastMessage.setGravity(Gravity.TOP, 0, 412);
        toastMessage.show();

        // generate next question
        generateQuestion();
    }

    private void displayScore() {
        // remove the last question, since the user cancelled the rest of the quiz
        mathQuestions.remove(getCurrentQuestion());

        Bundle bundle = new Bundle();
        bundle.putSerializable("mathQuestions", mathQuestions);

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("bundle", bundle);
        startActivityForResult(intent, RESULT_CODE_1);
    }

    public void onKeyPadClick(View view) {
        String displayStr = textViewCalcDisplay.getText().toString();

        switch (view.getId()) {
            case R.id.buttonNegative:
                if (!displayStr.equals("0")) return;
                // else:
                textViewCalcDisplay.setText(null);
                break;
            case R.id.buttonPeriod:
                if (periodIsClicked) return;
                // else:
                periodIsClicked = true;
                if (displayStr.equals("-")) textViewCalcDisplay.setText("-0");
                buttonValidate.setEnabled(false);
                break;
            case R.id.button0:
                if (displayStr.equals("-0")) return;
                // continue to default
            default: // number button pressed
                if (displayStr.equals("0")) textViewCalcDisplay.setText(null);
                buttonValidate.setEnabled(true);
        }

        displayStr = textViewCalcDisplay.getText().toString(); // refresh after above changes
        String buttonStr = ((Button) view).getText().toString();
        textViewCalcDisplay.setText(String.format("%s%s", displayStr, buttonStr));
    }

    public void onActionClick(View view) {
        switch (view.getId()) {
            case R.id.buttonGenerate:
                generateQuestion();
                break;
            case R.id.buttonValidate:
                validateAnswer();
                break;
            case R.id.buttonClear:
                clearDisplay();
                break;
            case R.id.buttonScore:
                displayScore();
                break;
            case R.id.buttonFinish:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_CODE_1) {
            assert data != null;
            String name = data.getStringExtra("name");
            String score = data.getStringExtra("score");
            assert name != null;
            if (name.isEmpty()) {
                textViewTitle.setText(getString(R.string.math_quiz_title));
            } else {
                textViewTitle.setText(String.format("%s (%s)", name, score));
            }
        }

        // New Quiz
        startQuiz();
    }
}