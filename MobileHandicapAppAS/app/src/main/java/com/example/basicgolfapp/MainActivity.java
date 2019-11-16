package com.example.basicgolfapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String[][]> scores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configureEnterRoundButton();
        configureCalculateHandicapButton();
    }

    private void configureEnterRoundButton(){
        Button saveRoundButton = findViewById(R.id.enterRoundButton);
        saveRoundButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(MainActivity.this, SaveRound.class));
            }
        });
    }
    private void configureCalculateHandicapButton(){
        Button calculateHandicapButton = findViewById(R.id.calculateHandicapButton);
        calculateHandicapButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                scores = new ArrayList<>();
                TextView text = findViewById(R.id.handicapText);
                String stringToDisplay = getResources().getString(R.string.calculating);
                text.setText(stringToDisplay);
                getHandicapsFromFile();
            }
        });
    }

    private void getHandicapsFromFile(){
        int i = 0;
        while(checkFileExists(getApplicationContext(),"Scores"+i)){
            i++;
        }
        TextView text = findViewById(R.id.handicapText);
        String stringToDisplay = getResources().getString(R.string.handicap)+calculateHandicap();
        text.setText(stringToDisplay);
    }

    private boolean checkFileExists(Context context, String filename){
        File file = context.getFileStreamPath(filename);
        String[][] round = new String[18][3];
        if(file == null|| !file.exists()){
            return false;
        }
        try {
            String line;
            String[] hole = new String[3];
            int i = 0;
            InputStream inputStream = context.openFileInput(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            while((line = reader.readLine()) != null){
                if(hole[0] ==null){
                    hole[0] = line;
                }else if(hole[1] ==null){
                    hole[1] = line;
                }else if(hole[2] == null){
                    hole[2] = line;
                }else{
                    round[i] = hole;
                    hole = new String[3];
                    hole[0] = line;
                    i++;
                }
            }
            round[17] = hole;
            reader.close();
            inputStream.close();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
        scores.add(round);
        return true;
    }

    private String calculateHandicap(){
        if(scores.isEmpty()){
            return "No Scores Found, Please Save Some Rounds First.";
        }
        Iterator<String[][]> iterator = scores.iterator();
        float averageDifference =0;
        int loopcount = 0;
        while(iterator.hasNext()){
            String[][] currentround = iterator.next();
            for(int i=0;i<18;i++){
                averageDifference += Integer.parseInt(currentround[i][2]) -
                        Integer.parseInt(currentround[i][1]);
            }
            loopcount++;
        }
        return String.valueOf(averageDifference/(loopcount));
    }
}
