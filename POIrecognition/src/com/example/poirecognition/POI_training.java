package com.example.poirecognition;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class POI_training extends Activity {
	private EditText poiName=null;
	private EditText nofScans=null;
	private EditText scanInterval=null;
	private Button bttTraining=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.poi_training);
		poiName=(EditText)findViewById(R.id.PoiName);
		nofScans=(EditText)findViewById(R.id.NofScans);
		scanInterval=(EditText)findViewById(R.id.ScanInterval);
		bttTraining=(Button)findViewById(R.id.Training);
		
		String name=poiName.getText().toString();
		int scansNumber=Integer.parseInt(nofScans.getText().toString());
		int interval=Integer.parseInt(scanInterval.getText().toString());
		
		Intent trainingIntent=getIntent();
		String s2=trainingIntent.getStringExtra("trainString");

		bttTraining.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setResult(Activity.RESULT_OK, new Intent().putExtra("training", "tr"));
				finish();
			}
		
		
	});
	}

}
