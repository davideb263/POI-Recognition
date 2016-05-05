package com.example.davide.poirecognition;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;



public class POI_Training extends Activity {
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    private EditText poiName = null;
    private EditText numberOfScans = null;
    private EditText intervalOfScans = null;
    private Button bttTraining = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poi_training);

        poiName = (EditText)findViewById(R.id.PoiName);
        numberOfScans = (EditText)findViewById(R.id.WiFiScanNumber);
        intervalOfScans = (EditText)findViewById(R.id.TimeInterval);
        bttTraining = (Button)findViewById(R.id.Training);

        String name = poiName.getText().toString();
        int scansNum = Integer.parseInt(numberOfScans.getText().toString());
        int interval = Integer.parseInt(intervalOfScans.getText().toString());


        Intent trainingIntent = getIntent();
        String s = trainingIntent.getStringExtra("stringTrain");

        bttTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_OK, new Intent().putExtra("training", "trn"));
                finish();
            }
        });
    }


}
