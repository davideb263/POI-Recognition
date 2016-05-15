package com.example.davide.poirecognition;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.R.bool;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.input.InputManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class POI_Training extends Activity{

	private EditText poiName=null;
	private EditText numberScans=null;
	private EditText intervalScans=null;
	private Button bttTrain=null;
	private Button bttFinish=null;
	private WiFiScanner wifiReceiver =null;
	public static TextView mainText=null;
	private Context context =null;
	public static WifiManager wf= null;
	public TimerTask timerTask;
	public Timer timer;
	public static History history;
	public int count=0;
	private final String TAG="Training";
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.poi_training);

		poiName=(EditText)findViewById(R.id.PoiName);
		numberScans=(EditText)findViewById(R.id.WiFiScanNumber);
		intervalScans=(EditText)findViewById(R.id.TimeInterval);
		bttTrain=(Button)findViewById(R.id.Training);
		bttFinish=(Button)findViewById(R.id.getData);
		Intent trainingIntent =getIntent();
		String s=trainingIntent.getStringExtra("stringTrain");;
		mainText=(TextView)findViewById(R.id.textScan);
		context= getApplicationContext();
		history=new History();
		wf=(WifiManager)getSystemService(Context.WIFI_SERVICE);
		// wifi scanned value broadcast receiver 
		wifiReceiver =new WiFiScanner(history);
		// Register broadcast receiver 
		// Broacast receiver will automatically call when number of wifi connections changed
		registerReceiver(wifiReceiver , new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		


		bttTrain.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(TAG, "Btt train listener");
				
				String name=poiName.getText().toString();
				int scansNumber=Integer.parseInt(numberScans.getText().toString());
				int interval=Integer.parseInt(intervalScans.getText().toString());
				count =scansNumber;
				InputMethodManager inputManager= (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow((null==getCurrentFocus())?null:getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

				timer=new Timer();		
				timerTask=new TimerTask(){
					@Override
					public void run() {
						Log.i(TAG, "Scansione");
						
						count--;
								startScan();
								if(count==0)
								{
									timer.cancel();
								}
					}
				};
				timer.schedule(timerTask, 0, interval*1000/scansNumber);
			}
		});
		bttFinish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(Activity.RESULT_OK, new Intent().putExtra("training", "trainingString"));
				finish();
			}
		});
	}
	public int GetI(int index, String mac)
	{
		boolean isPresent=false;
		Scan listAccess= wifiReceiver.getHistory().GetIndex(index);
		int pos=0;
		for(int a=0; a<listAccess.Size(); a++)
		{
			if(listAccess.GetIndex(a).getMac()==mac)
			{
				pos=a;
				isPresent=true;
			}

		}
		if(isPresent)
			return pos;
		else
			return -1;
	}
	public void startScan(){		
		// TODO Auto-generated method stub
		if(!wf.isWifiEnabled())
		{
			// If wifi disabled then enable it
			//Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_SHORT).show();
			// Enable WiFi
			wf.setWifiEnabled(true);
		}
		wf.startScan();
		//mainText.setText("Starting");				

	}
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(wifiReceiver);
	}
}
