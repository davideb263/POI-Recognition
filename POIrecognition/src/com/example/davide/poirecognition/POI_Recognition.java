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
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class POI_Recognition extends Activity {

	private final static String TAG = "Recognition";
	private TextView poiSL = null;
	private Button bttback = null;
	private Button bttRec = null;
	private Button bttTrain=null;
	private TextView fingerPrintTV=null;
	private ProgressBar progressB=null;

	private List<String> fpDataBase = null;//file
	private List<String> fpList = null;//contenuti file
	private ArrayList<Double> matchList = null;//pesi dei luoghi
	private	WeightList recPlaces=null;//posti e pesi
	private HashMap<String,Integer> stayLength=null; //posti
	private List<ScanResult> results=null;//risultato da getScan
	private ArrayList<String> recScan = null;

	private BroadcastReceiver broadcastReceiver;
	private TimerTask timerTask;
	private Timer timer;
	private int count = 0;
	private int countNoPlace=0;
	private final int SCANNUMBER = 60;
	private final int SCANLENGHT = 5;
	private final int SCAN_TO_REC = 6;
	private String placeOrNotPlace="";
	private String place = "";
	private WifiManager wifi;
	private double accuracy=0;
	private double[][] weightMatrix;
	private ArrayList<ArrayList<String>> apListAll;//liste ap dei contenuti delle finger print
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.poi_recognition);
		poiSL = (TextView) findViewById(R.id.poiSL);
		bttback = (Button) findViewById(R.id.BackToMain);
		bttRec = (Button) findViewById(R.id.Recognition);		
		bttTrain=(Button)findViewById(R.id.bttTrain);
		progressB=(ProgressBar)findViewById(R.id.recProgress);
		fingerPrintTV=(TextView)findViewById(R.id.placeTv);
		fpDataBase = new ArrayList<String>(); //lista dei percorsi nella cartella
		fpList = new ArrayList<String>(); //lista contenuto fingerprint
		recScan = new ArrayList<String>(); //lista di access point della recognition da scan
		apListAll= new ArrayList<ArrayList<String>>();// liste di contenuti di access points
		recPlaces = new WeightList();//lista di posti riconosciuti e pesi
		matchList= new ArrayList<Double>();// lista dei pesi
		stayLength= new HashMap<String, Integer>();// posti e sl
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		getIntent();

		poiSL.setMovementMethod(new ScrollingMovementMethod());
		broadcastReceiver= new BroadcastReceiver(){
			@Override
			public void onReceive(Context c, Intent intent) {
				placeOrNotPlace="";
				fingerPrintTV.setText("Recognition... Scan n°:" + count);
				Log.i(TAG, "onReceive");
				if (!wifi.isWifiEnabled()) {
					Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
					wifi.setWifiEnabled(true);
				}				
				results = wifi.getScanResults();
				Log.i(TAG, "scan");
				recScan.clear();//clear scan list
				ScanResult swap;
				for(int a=0; a< results.size(); a++){//sort list
					for(int b=a; b<results.size(); b++){
						if(results.get(a).level<results.get(b).level){
							swap= results.get(a);
							results.set(a, results.get(b));
							results.set(b, swap);							
						}						
					}
				}
				for (int j = 0; j < results.size(); j++) {
					Log.i(TAG, results.get(j).BSSID +" "+results.get(j).level);
					//_s+=results.get(j).BSSID +" "+results.get(j).level;
					recScan.add(results.get(j).BSSID);
				}
				if(count <=SCAN_TO_REC){//matrix
					allPlaceScore(recScan, count-1);//confronta con fp e calcola i pesi
				}
				if(count > SCAN_TO_REC)
				{
					for(int a=0; a< weightMatrix.length - 1 ; a++)//shift
					{
						for(int b=0; b< weightMatrix[0].length; b++)
						{
							weightMatrix[a][b]= weightMatrix[a+1][b];
						}
					}
					allPlaceScore(recScan, weightMatrix.length -1);
				}
				if(count >= SCAN_TO_REC){					
					weightAverage();//mean
					Double sum=0d;
					Integer sl=0;					
					placeScoreSort();
					filter();
					for(int a=0; a< matchList.size(); a++){
						sum+=matchList.get(a);
					}
					if(recPlaces.getElement(0).getWeight()> 40*sum/100){
						accuracy=recPlaces.getElement(0).getWeight()*100/sum;
						bttTrain.setVisibility(View.INVISIBLE);
						if(count == SCAN_TO_REC){
							sl = (Integer)stayLength.get(recPlaces.getElement(0).getString())+SCANLENGHT*SCAN_TO_REC;
							place=recPlaces.getElement(0).getString();
							}else if(count > SCAN_TO_REC)
							{
								Log.i(TAG, place+"\n"+recPlaces.getElement(0).getString());
								sl = incrementStayLength();
							}
						place=recPlaces.getElement(0).getString();
						stayLength.put(recPlaces.getElement(0).getString(), sl );
						String recPlaceAccuracy="You are here: "+recPlaces.getElement(0).getString();
						recPlaceAccuracy+= " accuracy: "+accuracy+"%";
						Toast.makeText(getApplicationContext(), recPlaceAccuracy, Toast.LENGTH_SHORT).show();
						
					}
					else{
						if(count==SCAN_TO_REC){
							countNoPlace=SCAN_TO_REC;
						}
						else{
						countNoPlace++;
						}
						Log.i(TAG, "noplace");
						placeOrNotPlace="<b>No recognized" + "</b>" + "<br> ";
						if(countNoPlace>5){
							Toast.makeText(getApplicationContext(),"Press train to do training", Toast.LENGTH_SHORT).show();
							bttTrain.setVisibility(View.VISIBLE);
						}
						
					}
					placeOrNotPlace+=weightPlaceList();
					poiSL.setText(Html.fromHtml(placeOrNotPlace));					
					matchList.clear();
					recPlaces.getWeightsList().clear();
				}				
				if (count == SCANNUMBER)
					progressB.setVisibility(View.INVISIBLE);
			}
	
		};
		registerReceiver(broadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		bttTrain.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i= new Intent("com.example.davide.training");
				startActivity(i);		
				finish();
			}
		});

		bttback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Log.i(TAG, "Recognition to main Activity");
				recPlaces.getWeightsList().clear();
				recScan.clear();
				matchList.clear();
				stayLength.clear();
				if(weightMatrix!=null){
					for(int l=0; l<weightMatrix.length; l++){
						for(int m=0; m<weightMatrix[0].length; m++){
							weightMatrix[l][m]=0;
						}
					}
				}
				if(timer!=null)
				timer.cancel();
				count=0;
				fpList.clear();
				fpDataBase.clear();
				apListAll.clear();
				setResult(Activity.RESULT_OK, new Intent());
				finish();
			}
		});

		bttRec.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Button Pressed. Please wait..", Toast.LENGTH_SHORT).show();
				recPlaces.getWeightsList().clear();
				recScan.clear();
				matchList.clear();
				stayLength.clear();
				stayLenghtInit();
				if(weightMatrix!=null){
					for(int l=0; l<weightMatrix.length; l++){
						for(int m=0; m<weightMatrix[0].length; m++){
							weightMatrix[l][m]=0;
						}
					}
				}
				count=0;
				fpList.clear();
				fpDataBase.clear();
				apListAll.clear();
				if(timer!=null){
				timer.cancel();
				}
				progressB.setVisibility(View.VISIBLE);
				fingerPrintTV.setText("Recognition...");

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

	public void allPlaceScore(ArrayList<String> scanList, int index) {
		Log.i(TAG, "allPlaceScore ap list size" + String.valueOf(apListAll.size()));//quante fingerprint cartella
		for(int j=0; j<apListAll.size(); j++){    		//per ogni fingerprint
			weightMatrix[index][j] = singlePlaceScore(scanList, apListAll.get(j));//calcola la funzione
		} 
		Log.i(TAG,"count " + String.valueOf(count));       
		//_s+=String.valueOf(count);
	}    

	public void apFromString()
	{
		for (int i = 0; i < fpDataBase.size(); i++) {
			fpList.add(getStringFromFile(fpDataBase.get(i)));//legge il contenuto
		}
		for (int j = 0; j < fpList.size(); j++) {  //per ogni place
			Log.i(TAG, "Lista");
			String[] parts = fpList.get(j).split("\n");
			apListAll.add(new ArrayList<String>());// aggiunge una lista
			for (int a = 0; a < parts.length; a++) {   //crea una lista di access point di un place
				apListAll.get(apListAll.size()-1).add(parts[a]);                
			}
		}    	
	}
	public double singlePlaceScore(ArrayList<String> scan, ArrayList<String> place) {
		Log.i(TAG, "singlePlaceScore");
		double sumWeight = 0;
		ArrayList<String> commonAP = new ArrayList<String>();
		for (int i = 0; i < scan.size(); i++) {
			if (place.contains(scan.get(i))) {
				commonAP.add(scan.get(i));//trova gli ap in comune
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

	public void weightAverage() {//mean

		for (int i = 0; i < weightMatrix[0].length; i++) {//ciclando sui place
			Double d = 0d;
			for (int j = 0; j < weightMatrix.length; j++) {//per ogni place fa la somma della colonna
				d += weightMatrix[j][i];
			}
			matchList.add( d /SCAN_TO_REC);//lista dei pesi delle finger print
			Log.i(TAG, "average"+String.valueOf(d/SCAN_TO_REC));
		}
	}

	private void placeScoreSort()
	{
		Log.i(TAG, "placeScore");
		String place;
		StringWeight tmp;
		for(int i=0; i< matchList.size(); i++)// i pesi sono nello stesso ordine della folder
		{
			place= fpDataBase.get(i);
			place=place.substring(place.lastIndexOf("/")+1);
			place= place.substring(0, place.indexOf("."));
			recPlaces.add(place, matchList.get(i));//posto
		}
		//sort
		for(int c=0; c<recPlaces.size(); c++){
			for(int d=c; d<recPlaces.size(); d++){
				if(recPlaces.getElement(c).getWeight()<recPlaces.getElement(d).getWeight()){
					tmp=recPlaces.getElement(c);
					recPlaces.setElement(c, recPlaces.getElement(d));
					recPlaces.setElement(d, tmp);    				
				}
			}
		}
	}

	private void stayLenghtInit(){
		Log.i(TAG, "stayLenght");
		String place;
		for(int i=0; i< fpDataBase.size(); i++)	{
			place= fpDataBase.get(i);
			place=place.substring(place.lastIndexOf("/")+1);
			place= place.substring(0, place.indexOf("."));
			stayLength.put(place, 0);
		}
	}

	public Integer incrementStayLength(){
		Integer sl=0;
		Log.i(TAG, recPlaces.getElement(0).getString()+""+place+"increment stay length");
		Log.i(TAG, place.compareTo(recPlaces.getElement(0).getString()) + " " );
		if(place.compareTo(recPlaces.getElement(0).getString())==0&&countNoPlace==0){
		sl = (Integer)stayLength.get(recPlaces.getElement(0).getString())+SCANLENGHT; 
		}
		else{
			stayLength.put(recPlaces.getElement(0).getString(), 0);
			sl=SCANLENGHT;
			
			countNoPlace=0;
		}
		return sl;
	}

	public void filter() {
		double threshold = 0.1d;
		for (int i = 0; i < recPlaces.size(); i++) {
			Log.i(TAG, "filtro");
			if (recPlaces.getElement(i).getWeight() < threshold) {
				Log.i(TAG, "to remove" + recPlaces.getElement(i).getWeight());
				recPlaces.getWeightsList().remove(recPlaces.getElement(i));                
				i = 0;
			}
		}
	}
	private String weightPlaceList(){
		String _s="";
		Log.i(TAG, "print place + stay length");
		Set<Entry<String, Integer>> entries = stayLength.entrySet();
		
		for (Entry<String, Integer> entry : entries) {
			String key = entry.getKey().toString();
			Integer value = entry.getValue();
			if(value > 0){
				_s += "<b>Place :" + key + " " + "Stay Lenght: " + value + "</b>" + "<br>";
				
			}
				
		}
		_s+="<br>";
		for(Entry<String, Integer> entry: entries){
			Integer v=entry.getValue();
			String key=entry.getKey();
			if(v==0){
				_s+="Possible place: "+key + "<br>";
			}	
		}
		return _s;
	}
	public static String convertStreamToString(InputStream is){
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
		} catch (IOException e) {
			Log.e(TAG, "can't read file");
		}
		try {
			reader.close();
		} catch (IOException e) {
			Log.e(TAG, "not close");
		}
		return sb.toString();
	}

	public static String getStringFromFile(String filePath){
		File fl = new File(filePath);
		FileInputStream fin= null;
		String ret="";
		try {
			fin = new FileInputStream(fl);
			ret = convertStreamToString(fin);
			fin.close();
			//Make sure you close all streams.
		} 
		catch (FileNotFoundException e) {
			Log.e(TAG, e.getMessage());
		} 
		catch (IOException e) {
			Log.e(TAG, "can't close");
		}
		return ret;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "closing train");
		if(broadcastReceiver!=null)	{
			unregisterReceiver(broadcastReceiver);
		}
		if(timer!=null)
		timer.cancel();
	}
}
