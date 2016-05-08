package com.example.davide.poirecognition;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button bttStart = null;
    Button bttTrain = null;

    final int START_REQUEST_CODE = 1;
    final int TRAIN_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bttStart = (Button)findViewById(R.id.Start);
        bttTrain = (Button)findViewById(R.id.Train);

        bttStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String s = "start";
                Intent startIntent = new Intent("com.example.davide.poirecognitionStart");
                startIntent.putExtra("stringStart", s);
                startActivityForResult(startIntent, START_REQUEST_CODE);

            }
        });

        bttTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String s = "train";
                Intent trainIntent = new Intent("com.example.davide.poirecognitionTrain");
                trainIntent.putExtra("stringTrain", s);
                startActivityForResult(trainIntent, TRAIN_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == START_REQUEST_CODE)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                String s = data.getStringExtra("recognition");
            }
        }
        else if(requestCode == TRAIN_REQUEST_CODE)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                String s = data.getStringExtra("training");
            }
        }
    }
}
