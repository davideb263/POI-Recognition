package com.example.davide.poirecognition;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



public class POI_Recognition extends Activity {

    private TextView poiSL = null;
    private Button bttBack = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poi_recognition);

        poiSL = (TextView)findViewById(R.id.poiSL);
        bttBack = (Button)findViewById(R.id.BackToMain);

        Intent  recognitionIntent = getIntent();
        String s = recognitionIntent.getStringExtra("stringStart");

        bttBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_OK, new Intent().putExtra("recognition", "rec"));
                finish();
            }
        });

    }
}
