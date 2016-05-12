package com.example.poirecognition;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class POI_training extends Activity{
	
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
public static AccessPoint accessPoint;
public static ArrayList<ArrayList> history;
public static ArrayList<AccessPoint> scanList;
public static ArrayList <String> scanSList;
public int count;
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.poi_training);
		
		poiName=(EditText)findViewById(R.id.PoiName);
		numberScans=(EditText)findViewById(R.id.Scans);
		intervalScans=(EditText)findViewById(R.id.intervalScan);
		bttTrain=(Button)findViewById(R.id.Training);
		bttFinish=(Button)findViewById(R.id.Data);
		scanSList=new ArrayList<String>();
		Intent trainingIntent =getIntent();
		String s=trainingIntent.getStringExtra("stringTrain");
		scanList=new ArrayList<AccessPoint>();
		accessPoint = new AccessPoint();
		mainText=(TextView)findViewById(R.id.textscan);
		context= getApplicationContext();
		wf=(WifiManager)getSystemService(Context.WIFI_SERVICE);
		// wifi scanned value broadcast receiver 
		wifiReceiver =new WiFiScanner();
		history=new ArrayList<ArrayList>();
		
		// Register broadcast receiver 
		// Broacast receiver will automatically call when number of wifi connections changed
				registerReceiver(wifiReceiver , new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
					
			
		
		bttTrain.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String name=poiName.getText().toString();
				int scansNumber=Integer.parseInt(numberScans.getText().toString());
				int interval=Integer.parseInt(intervalScans.getText().toString());
				count =scansNumber;
				final Handler handler=new Handler();				
				timer=new Timer();		
		timerTask=new TimerTask(){
			public void run() {
				handler.post(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						count--;
				startScan();
				if(count==0)
				{
					timer.cancel();
				}
					}					
				
				});
				
				
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
	public void startScan(){		
						// TODO Auto-generated method stub
				if(!wf.isWifiEnabled())
						{
							// If wifi disabled then enable it
							Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_SHORT).show();
							// Enable WiFi
							wf.setWifiEnabled(true);
						}
						wf.startScan();
						mainText.setText("Starting");				
				
						}
			protected void onDestroy() {
				super.onDestroy();
				unregisterReceiver(wifiReceiver);
			}
}
