package com.example.davide.poirecognition;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class POI_Recognition extends Activity {

	private final String TAG = "Recognition";
	private TextView poiSL = null;
	private Button bttback = null;
	private Button bttRec = null;

	private List<String> fpDataBase = null;
	private List<String> fpList = null;
	private ArrayList<Double> matchList = null;
	private ArrayList<apWeight> recPlaces=null;
	private HashMap<String,Integer> stayLength=null; 
	private BroadcastReceiver broadcastReceiver;
	public TimerTask timerTask;
	public Timer timer;
	public int count = 0;
	public final int SCANNUMBER = 60;
	public final int SCANLENGHT = 5;
	public final int SCAN_TO_REC = 5;

	WifiManager wifi;
	List<ScanResult> results=null;
	ArrayList<String> recScan = null;
	double[][] weightMatrix;
	ArrayList<ArrayList<String>> apListAll; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.poi_recognition);
		poiSL = (TextView) findViewById(R.id.poiSL);
		bttback = (Button) findViewById(R.id.BackToMain);
		bttRec = (Button) findViewById(R.id.Recognition);
		getIntent();
		//recognitionIntent.getStringExtra("stringStart");

		fpDataBase = new ArrayList<String>(); //lista dei percorsi
		fpList = new ArrayList<String>(); //lista contenuto fingerprint
		recScan = new ArrayList<String>(); //lista di access point della recognition
		apListAll= new ArrayList<ArrayList<String>>();
		recPlaces = new ArrayList<apWeight>();
		matchList= new ArrayList<Double>();
		stayLength= new HashMap<String, Integer>();
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		broadcastReceiver= new BroadcastReceiver(){
			@Override
			public void onReceive(Context c, Intent intent) {
				Log.i(TAG, "onReceive");

				if (!wifi.isWifiEnabled()) {
					Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
					wifi.setWifiEnabled(true);
				}
				results = wifi.getScanResults();
				Log.i(TAG, "scan");
				recScan.clear();
				for (int j = 0; j < results.size(); j++) {
					Log.i(TAG, results.get(j).BSSID);
					recScan.add(results.get(j).BSSID);
				}
				allPlaceScore(recScan);
				if(count%SCAN_TO_REC==0)
				{
					weightAverage();
					String pesi="";
					Double sum=0d;
					for(int a=0; a<matchList.size(); a++)
					{
						pesi+=matchList.get(a)+"\n";
					}
					poiSL.setText(pesi);
					placeScore();
					filter();
					for(int a=0; a< matchList.size(); a++)
					{
						sum+=matchList.get(a);
					}
					if(recPlaces.get(0).getWeight()> 40*sum/100 && recPlaces.get(0).getWeight()>1.1*recPlaces.get(1).getWeight()){
						Integer sl = (Integer)stayLength.get(recPlaces.get(0).getApMac())+SCANLENGHT*SCAN_TO_REC;
						stayLength.put(recPlaces.get(0).getApMac(), sl );
					}

					poiSL.append(weightPlaceList());

					matchList.clear();
					recPlaces.clear();

					for(int a=0; a< weightMatrix.length; a++)
					{
						for(int b=0; b< weightMatrix[0].length; b++)
						{
							weightMatrix[a][b]=0;
						}
					}
				}
			}
		};
		registerReceiver(broadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));


		bttback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Recognition to main Activity");
				setResult(Activity.RESULT_OK, new Intent().putExtra("recognition", "rec"));
				finish();
			}
		});

		bttRec.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				count = 0;
				// creo una lista con i percorsi dei file contenenti le varie fingerprint.
				File fpDirectory = new File(Environment.getExternalStorageDirectory() + "/POI_Fingerprints");
				File[] fpFiles = fpDirectory.listFiles();
				weightMatrix = new double[SCAN_TO_REC][fpFiles.length];
				for (int i = 0; i < fpFiles.length; i++) {
					fpDataBase.add(fpFiles[i].getAbsolutePath());
					Log.i(TAG, fpFiles[i].getAbsolutePath());
				}
				apFromString();
				stayLenghtInit();
				timer = new Timer();
				timerTask = new TimerTask() {
					@Override
					public void run() {
						count++;
						wifi.startScan();
						if (count == SCANNUMBER) { 
							timer.cancel();
						}
					}
				};
				timer.schedule(timerTask, 0, SCANLENGHT*1000);
			}
		});
	}

	public void allPlaceScore(ArrayList<String> scanList) {
		Log.i(TAG, "allPlaceScore ap list size"+String.valueOf(apListAll.size()));
		for(int j=0; j<apListAll.size(); j++)
		{    		
			weightMatrix[(count-1)%SCAN_TO_REC][j] = singlePlaceScore(scanList, apListAll.get(j));
		} 
		Log.i(TAG,"count " + String.valueOf(count));       
	}    
	public void apFromString()
	{
		for (int i = 0; i < fpDataBase.size(); i++) {
			try {
				fpList.add(getStringFromFile(fpDataBase.get(i)));
			} 
			catch (IOException fnfe) {
				Log.e(TAG, fnfe.getMessage());
			}
		}
		for (int j = 0; j < fpList.size(); j++) {  //per ogni place
			Log.i(TAG, "Lista");
			String[] parts = fpList.get(j).split("\n");
			apListAll.add(new ArrayList<String>());
			for (int a = 0; a < parts.length; a++) {   //crea una lista di access point di un place
				apListAll.get(apListAll.size()-1).add(parts[a]);                
			}
		}    	
	}
	public double singlePlaceScore(ArrayList<String> scan, ArrayList<String> place) {
		Log.i(TAG, "singlePlaceScore");
		double sumWeight = 0;
		ArrayList<String> commonAP = new ArrayList<String>();
		for (int i = 0; i < scan.size(); i++) 
		{
			if (place.contains(scan.get(i))) {
				commonAP.add(scan.get(i));
			}
		}

		for (int j = 0; j < commonAP.size(); j++) {
			int pos1 = Functions.GetI(scan, commonAP.get(j));
			int pos2 = Functions.GetI(place, commonAP.get(j));
			if (pos1 != -1 && pos2 != -1) {
				pos1++;
				pos2++;
				sumWeight += Functions.weight(pos1, pos2);
				Log.i(TAG, "weight add"+String.valueOf(sumWeight));
			}
		}
		return sumWeight;
	}

	public void weightAverage() {

		for (int i = 0; i < weightMatrix[0].length; i++) {//ciclando sui place
			Double d = 0d;
			for (int j = 0; j < weightMatrix.length; j++) {//per ogni place fa la somma della colonna
				d += weightMatrix[j][i];
			}
			matchList.add( d /SCAN_TO_REC);
			Log.i(TAG, "average"+String.valueOf(d/SCAN_TO_REC));
		}
	}

	private void placeScore()
	{
		Log.i(TAG, "placeScore");
		String place;
		apWeight tmp;
		for(int i=0; i< matchList.size(); i++)
		{
			place= fpDataBase.get(i);
			place=place.substring(place.lastIndexOf("/")+1);
			place= place.substring(0, place.indexOf("."));
			recPlaces.add(new apWeight(place, matchList.get(i)));    		
		}
		for(int c=0; c<recPlaces.size(); c++)
		{
			for(int d=c; d<recPlaces.size(); d++)
			{
				if(recPlaces.get(c).getWeight()<recPlaces.get(d).getWeight())
				{
					tmp=recPlaces.get(c);
					recPlaces.set(c, recPlaces.get(d));
					recPlaces.set(d, tmp);    				
				}

			}
		}

	}

	private void stayLenghtInit()
	{
		Log.i(TAG, "stayLenght");
		String place;
		for(int i=0; i< fpDataBase.size(); i++)
		{
			place= fpDataBase.get(i);
			place=place.substring(place.lastIndexOf("/")+1);
			place= place.substring(0, place.indexOf("."));
			stayLength.put(place, 0);
		}
	}


	public void filter() {
		double threshold = 0.1d;
		for (int i = 0; i < recPlaces.size(); i++) {
			Log.i(TAG, "filtro");
			if (recPlaces.get(i).getWeight() < threshold) {
				Log.i(TAG, "to remove"+recPlaces.get(i).getWeight());
				recPlaces.remove(recPlaces.get(i));                
				i = 0;
			}
		}
	}
	private String weightPlaceList()
	{
		String list="";
		Log.i(TAG, "print place + stay length");
		Set<Entry<String, Integer>> entries = stayLength.entrySet();
		for (Map.Entry<String, Integer> entry : entries) {
			String key = entry.getKey().toString();
			Integer value = entry.getValue();
			if(value > 0){
				list += " Place: " + key + "\t" + "Stay Lenght: " + value;
			}
		}
		return list;
	}
	public static String convertStreamToString(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line).append("\n");
		}
		reader.close();
		return sb.toString();
	}

	public static String getStringFromFile(String filePath) throws IOException {
		File fl = new File(filePath);
		FileInputStream fin = new FileInputStream(fl);
		String ret = convertStreamToString(fin);
		//Make sure you close all streams.
		fin.close();
		return ret;
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "closing train");
		if(broadcastReceiver!=null)	{
			unregisterReceiver(broadcastReceiver);
		}
	}

}
