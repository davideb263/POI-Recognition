package com.example.davide.poirecognition;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
final String tag="Main activity";
Button bttstart=null;
Button btttrain=null;
final int START_REQUEST_CODE = 1;
final int TRAIN_REQUEST_CODE=2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		bttstart=(Button)findViewById(R.id.Start);
		btttrain=(Button)findViewById(R.id.Train);
		bttstart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i(tag, "btt listener");
				String s = "start";
				Intent startIntent=new Intent("com.example.davide.poirecognitionStart");
				startIntent.putExtra("stringStart", s);				
				startActivityForResult(startIntent, START_REQUEST_CODE);				
			}
		});
	
	btttrain.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Log.i(tag, "Btt Train Listener");
			String s2 = "train";
			Intent trainIntent=new Intent("com.example.davide.poirecognitionTrain");
			trainIntent.putExtra("stringTrain", s2);				
			startActivityForResult(trainIntent, TRAIN_REQUEST_CODE);			
		}
	});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==START_REQUEST_CODE)
		{
			if(resultCode==Activity.RESULT_OK)
			{				
				Log.i(tag, "recognition stop");
				//data.getStringExtra("recognition");				
			}
		}
		else if(requestCode==TRAIN_REQUEST_CODE)
		{
			if(resultCode== Activity.RESULT_OK)
			{
				Log.i(tag, "train stop");
				//data.getStringExtra("training");
				
			}			
		}
	}
}
