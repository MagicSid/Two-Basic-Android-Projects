package com.example.basicgolfapp;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class SaveRound extends AppCompatActivity {
    private int currentHole;
    private String[][] scores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_round);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        currentHole = 1;
        scores = new String[18][3];
        configureAllWidgets();
    }

    private void configureAllWidgets(){
        configureSaveButton();
        configureBackButton();
        setupSISpinner();
        setupParSpinner();
        setupYourScoreSpinner();
        updateText();
    }


    private void configureSaveButton(){
        Button saveRoundButton = findViewById(R.id.saveButton);

        saveRoundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean SICheckFailBool = false;
                String[] SICheck = new String[18];
                Spinner SISpinner = findViewById(R.id.HoleStrokeIndexSpinner);
                Spinner ParSpinner = findViewById(R.id.HoleParSpinner);
                Spinner YourScoreSpinner = findViewById(R.id.HoleYourScoreSpinner);

                if(currentHole ==18){
                    scores[currentHole-1][0] = (String) SISpinner.getSelectedItem();
                    scores[currentHole-1][1] = (String) ParSpinner.getSelectedItem();
                    scores[currentHole-1][2] = (String) YourScoreSpinner.getSelectedItem();

                    for(int i=0;i<=17;i++){
                        if(SICheck[Integer.parseInt(scores[i][0])-1] == null){
                            SICheck[Integer.parseInt(scores[i][0])-1] = "Used";
                        }else{
                            SICheckFailBool = true;
                        }
                    }
                    if(SICheckFailBool == true){
                        Toast.makeText(getApplicationContext(),
                                "Each hole should have a different Stroke Index",
                                Toast.LENGTH_LONG).show();
                    }else{
                        if(saveScores()) {
                            finish();
                        }
                    }

                }else{

                    scores[currentHole-1][0] = (String) SISpinner.getSelectedItem();
                    scores[currentHole-1][1] = (String) ParSpinner.getSelectedItem();
                    scores[currentHole-1][2] = (String) YourScoreSpinner.getSelectedItem();
                    currentHole++;
                    updateText();
                }
            }

        });
    }

    private void configureBackButton(){
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(currentHole ==1){
                    finish();
                }else{
                    currentHole--;
                    updateText();
                }
            }
        });
    }

    private void updateText(){
        TextView text = findViewById(R.id.holeName);
        text.setText("Hole "+currentHole);
    }

    private void setupSISpinner(){
        Spinner SISpinner = findViewById(R.id.HoleStrokeIndexSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.StrokeIndexArray,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SISpinner.setAdapter(adapter);
    }

    private void setupParSpinner(){
        Spinner ParSpinner = findViewById(R.id.HoleParSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.ParArray,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ParSpinner.setAdapter(adapter);
    }

    private void setupYourScoreSpinner(){
        Spinner YourScoreSpinner = findViewById(R.id.HoleYourScoreSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.ScoreArray,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        YourScoreSpinner.setAdapter(adapter);
    }

    private boolean saveScores(){
        try{
            saveToFile();
            return true;
        }catch (Exception error){
            Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void saveToFile(){
        int i = 0;
        while(checkFileExists(getApplicationContext(),"Scores"+i)){
            i++;
        }
        String filename = "Scores"+i;
        try{
            writeScoreArray(filename);
        }catch (Exception error){
            Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG);
        }
    }

    private void writeScoreArray(String filename){
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(filename,Context.MODE_PRIVATE);
            BufferedWriter streamWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            for(int length=0;length<18;length++) {
                for (int values = 0; values < 3; values++) {
                    streamWriter.write(scores[length][values]);
                    streamWriter.newLine();
                }
            }
            streamWriter.close();
            outputStream.close();
        } catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private boolean checkFileExists(Context context, String filename){
        File file = context.getFileStreamPath(filename);
        if(file == null|| !file.exists()){
            return false;
        }
        return true;
    }

}
