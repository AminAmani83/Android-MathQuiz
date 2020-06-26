package com.jac.projectaminamani;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jac.projectaminamani.model.MathQuestion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener {

    RadioGroup radioGroupFilter, radioGroupSort;
    TextView textViewScore;
    ListView listView;
    EditText editTextRegisterName;
    Button buttonBack, buttonShow;

    int score;
    private ArrayList<MathQuestion> mathQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        initPageElements();
        initPageContents();
    }

    private void initPageElements() {
        // Set the Elements and their Event Handlers
        editTextRegisterName = findViewById(R.id.editTextRegisterName);
        textViewScore = findViewById(R.id.textViewScore);
        listView = findViewById(R.id.listView);

        buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(this);

        //buttonShow = findViewById(R.id.buttonShow);
        //buttonShow.setOnClickListener(this);

        radioGroupFilter = findViewById(R.id.radioGroupFilter);
        radioGroupFilter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int selectedFilterId) {
                // Deselect Sort buttons if needed
                radioGroupSort.clearCheck();

                getMyIntent(); // reset the previous list
                ArrayList<MathQuestion> filteredArray = new ArrayList<>();

                switch (selectedFilterId) {
                    case R.id.radioButtonFilterCorrect:
                        for (MathQuestion mathQuestion : mathQuestions) {
                            if (mathQuestion.userAnswerIsCorrect())
                                filteredArray.add(mathQuestion);
                        }
                        break;
                    case R.id.radioButtonFilterWrong:
                        for (MathQuestion mathQuestion : mathQuestions) {
                            if (!mathQuestion.userAnswerIsCorrect())
                                filteredArray.add(mathQuestion);
                        }
                        break;
                    default:
                        filteredArray = mathQuestions;
                        break;
                }

                mathQuestions = filteredArray;
                updateDisplayedList();
            }
        });

        radioGroupSort = findViewById(R.id.radioGroupSort);
        radioGroupSort.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int selectedSortId) {

                if (selectedSortId == R.id.radioButtonSortAsc) {
                    Collections.sort(mathQuestions);
                } else if (selectedSortId == R.id.radioButtonSortDesc) {
                    Collections.sort(mathQuestions, Collections.reverseOrder());
                }

                updateDisplayedList();
            }
        });
    }

    private void initPageContents() {
        // Set MathQuestions list contents
        getMyIntent();
        calculateScore();
        // Display the default results
        updateDisplayedList();
    }

    private void getMyIntent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        Serializable bundleMathQuestions = bundle.getSerializable("mathQuestions");
        mathQuestions = (ArrayList<MathQuestion>) bundleMathQuestions;
    }

    private void calculateScore() {
        int counter = 0;
        for (MathQuestion mathQuestion : mathQuestions) {
            if (mathQuestion.userAnswerIsCorrect()) counter++;
        }
        score = (mathQuestions.size() == 0) ? 0 : counter * 100 / mathQuestions.size();
        textViewScore.setText(String.valueOf(score) + "%");
    }

    private void updateDisplayedList() {
        // Option 1: Using ListView
        ArrayAdapter<MathQuestion> arrayAdapter = new ArrayAdapter<MathQuestion>(this,
                android.R.layout.simple_list_item_1,
                mathQuestions);
        arrayAdapter.notifyDataSetChanged();
        listView.setAdapter(arrayAdapter);
    }


    // Event Interface

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        intent.putExtra("name", editTextRegisterName.getText().toString());
        intent.putExtra("score", score + "%");
        setResult(RESULT_OK, intent);
        finish();
    }
}