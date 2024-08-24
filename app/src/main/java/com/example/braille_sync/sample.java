package com.example.braille_sync;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class sample extends AppCompatActivity {

    private static final int MAX_COLUMNS = 10;
    private static final int MAX_ROWS = 27;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        final EditText inputText = findViewById(R.id.inputText);
        Button formatButton = findViewById(R.id.formatButton);
        final TextView outputText = findViewById(R.id.outputText);

        formatButton.setOnClickListener(v -> {
            String input = inputText.getText().toString();
            List<String> words = GetEveryword(input);

            for (String word : words) {
                Log.d("Word", word);
            }

            outputText.setText("Check Logcat for each word logged individually.");
        });
    }



    public List<String> GetEveryword(String input) {
        String[] wordsArray = input.split("\\s+");
        List<String> wordsList = new ArrayList<>();
        for (String word : wordsArray) {
            if (!word.trim().isEmpty()) {
                String transformedWord = transformWord(word);
                wordsList.add(transformedWord);
            }
        }
        return wordsList;
    }

    // Function to transform a word based on the specified rules
    public String transformWord(String word) {

    }





}