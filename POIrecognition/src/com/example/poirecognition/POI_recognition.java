package com.example.poirecognition;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class POI_recognition extends Activity
{
	private TextView poiSL=null;
	private Button bttBack=null;
@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.poi_recognition);
poiSL=(TextView)findViewById(R.id.PoiSL);
bttBack=(Button)findViewById(R.id.Return);


Intent recognitionIntent=getIntent();
String s2=recognitionIntent.getStringExtra("startString");

bttBack.setOnClickListener(new OnClickListener() {
	
	@Override
	public void onClick(View v) {
		setResult(Activity.RESULT_OK, new Intent().putExtra("recognition", "rec"));
		finish();
	}
});



}
}
