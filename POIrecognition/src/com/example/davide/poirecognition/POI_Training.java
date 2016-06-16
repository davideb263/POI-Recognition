package com.example.davide.poirecognition;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
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
	private int scansNumber;
	private int interval;
	private String _s;
	public String name;
	static public WeightList wlWeight;
	private final String TAG="Training";
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.poi_training);
		name="";
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
		wlWeight=new WeightList();
		wf=(WifiManager)getSystemService(Context.WIFI_SERVICE);
		// wifi scanned value broadcast receiver 
		wifiReceiver =new WiFiScanner(history, wlWeight);
		// Register broadcast receiver 
		// Broacast receiver will automatically call when number of wifi connections changed
		registerReceiver(wifiReceiver , new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		Log.i(TAG, "creating the training activity");
		

		bttTrain.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try{
				Log.i(TAG, "saving data from user via EditTexts");
				
				name=poiName.getText().toString();
				scansNumber=Integer.parseInt(numberScans.getText().toString());
				interval=Integer.parseInt(intervalScans.getText().toString());
				count =scansNumber;
				InputMethodManager inputManager= (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow((null==getCurrentFocus())?null:getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				Toast.makeText(getApplicationContext(), "Input Got Successfully", Toast.LENGTH_SHORT).show();
				}
				catch(NumberFormatException e)
				{
					Toast.makeText(getApplicationContext(), "Not correct input", Toast.LENGTH_LONG).show();
					
				}
				if(poiName==null||scansNumber<=0||interval<=0)
				{
					Toast.makeText(getApplicationContext(), "Enter data first", Toast.LENGTH_SHORT).show();
				}
				else{
				timer=new Timer();		
				timerTask=new TimerTask(){
					@Override
					public void run() {
						
						count--;
						Log.i(TAG, "Scansione");
						startScan();
								/*if(count==2)
								{
									wlWeight=history.firstMerge(0, 1);
									
								}
								else if(count>2)
								{
									wlWeight=history.Merge(count-1, wlWeight);
								
								}*/
								if(count==0)
								{Log.i(TAG, "termine scansione");
									timer.cancel();
									WeightFilter();								
									String storedir = Environment.getExternalStorageDirectory()+"/POI_Fingerprints";
									File f = new File(storedir);
									if(!f.exists())
										if(!f.mkdir()){
											Log.e("Error","Can't create download directory");
										}

										if(storedir!=null)
									{
											String str="";
											for (int i = 0; i < wlWeight.Size(); i++) { 
				                            //str += "MAC : " + wlWeight.getWeightsList().get(i).getApMac() + "\nWEIGHT : " + wlWeight.getWeightsList().get(i).getWeight() + "\n\n"; 
											//str +=  wlWeight.getWeightsList().get(i).getApMac() + "\n" + wlWeight.getWeightsList().get(i).getWeight() + "\n"; 
											str += wlWeight.getWeightsList().get(i).getApMac() + "\n";
											}
											FileOutputStream fostream=null;
											OutputStreamWriter outputwriter=null;
											try {
												String filename=storedir+"/"+name+".txt";
												fostream = new FileOutputStream(filename);
												outputwriter=new OutputStreamWriter(fostream);
											} catch (IOException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
											;
											try {
												outputwriter.append(str);
												outputwriter.close();
												fostream.close();
											} catch (IOException exc) {
												// TODO Auto-generated catch block
												Log.e(TAG,"errore");
											}
											
									}
								}
					}
				};
				timer.schedule(timerTask, 0, interval*1000/scansNumber);
				}				
				
				
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
		Scan listAccess= history.getHistory().get(index);
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
	public void WeightFilter()
	{
		double threshold=0.01;
		String macAddress="";
		Log.i(TAG, "Enter weight filter");
		for(int a=0; a<wlWeight.getWeightsList().size(); a++)
		{
			//Log.i(TAG, "iterator");
			if(wlWeight.getWeightsList().get(a).getWeight()<threshold)
				{macAddress=wlWeight.getWeightsList().get(a).getApMac();
				Log.i(TAG, macAddress);
					wlWeight.getWeightsList().remove(a);
					a=0;
				}
		}
		
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
		//Toast.makeText(getApplicationContext(), "Start scanning", Toast.LENGTH_SHORT).show();
		wf.startScan();
		//mainText.setText("Starting");				

	}
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(wifiReceiver);
		Log.i(TAG, "Closing Training activity.(onDestroy)");
	}
}
