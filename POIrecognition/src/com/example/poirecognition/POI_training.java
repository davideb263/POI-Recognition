package com.example.poirecognition;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class POI_training extends Activity{
private EditText poiName=null;
private EditText numberScans=null;
private EditText intervalScans=null;
private Button bttTrain=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.poi_training);
		poiName=(EditText)findViewById(R.id.PoiName);
		numberScans=(EditText)findViewById(R.id.Scans);
		intervalScans=(EditText)findViewById(R.id.intervalScan);
		bttTrain=(Button)findViewById(R.id.Training);
		String name=poiName.getText().toString();
		int scansNumber=Integer.parseInt(numberScans.getText().toString());
		int interval=Integer.parseInt(intervalScans.getText().toString());
		Intent trainingIntent =getIntent();
		String s=trainingIntent.getStringExtra("stringTrain");
		bttTrain.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(Activity.RESULT_OK, new Intent().putExtra("training", "trainingString"));
				finish();
			}
		});
	}
}
