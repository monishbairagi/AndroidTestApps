package com.example.diceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.concurrent.ThreadLocalRandom;

public class DiceActivity extends AppCompatActivity {

    private ImageView diceFace;
    private Button roll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dice);

        diceFace = findViewById(R.id.diceImageView);
        roll = findViewById(R.id.rollButton);

        roll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rollDice();
            }
        });

    }

    private void rollDice() {
        int rand = ThreadLocalRandom.current().nextInt(1,7);
        switch (rand){
            case 1:
                diceFace.setImageResource(R.drawable.dice1);
                break;
            case 2:
                diceFace.setImageResource(R.drawable.dice2);
                break;
            case 3:
                diceFace.setImageResource(R.drawable.dice3);
                break;
            case 4:
                diceFace.setImageResource(R.drawable.dice4);
                break;
            case 5:
                diceFace.setImageResource(R.drawable.dice5);
                break;
            case 6:
                diceFace.setImageResource(R.drawable.dice6);
                break;
        }
    }
}