package com.example.poirecognition;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	private Button bttstart=null;
	private Button btttrain=null;
	final int START_REQUEST_CODE=1;
	final int TRAIN_REQUEST_CODE=2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		bttstart=(Button)findViewById(R.id.Start);
		btttrain=(Button)findViewById(R.id.Train);
		
		bttstart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v){
				String s="start";
				Intent startIntent=new Intent("com.example.poirecognitionStart");
				startIntent.putExtra("startString", s);
				startActivityForResult(startIntent, START_REQUEST_CODE);
				
			}
		});
		 btttrain.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v){
				String t="train";
				Intent trainIntent=new Intent("com.example.poirecognitionTrain");
				trainIntent.putExtra("trainString", t);
				startActivityForResult(trainIntent, TRAIN_REQUEST_CODE);
				
							
			}
		});
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==START_REQUEST_CODE){
			if(resultCode==Activity.RESULT_OK){
				String s=data.getStringExtra("recognition");
			}
			
		}
		else if(requestCode==TRAIN_REQUEST_CODE){
			if(resultCode==Activity.RESULT_OK){
				String s=data.getStringExtra("training");
			}
		}
	
	
	
	}
	
}
