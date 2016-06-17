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
import java.util.List;
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
    
    private BroadcastReceiver broadcastReceiver;
    public TimerTask timerTask;
    public Timer timer;
    public int count = 0;

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
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        
        broadcastReceiver= new BroadcastReceiver(){
            @Override
            public void onReceive(Context c, Intent intent) {
            	Log.i(TAG, "onReceive");
                poiSL.setText("");
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
                if(count==3)
                {
                matchList = weightAverage();
                filter();
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
                weightMatrix = new double[10][fpFiles.length];
                for (int i = 0; i < fpFiles.length; i++) {
                    fpDataBase.add(fpFiles[i].getAbsolutePath());
                    Log.i(TAG, fpFiles[i].getAbsolutePath());
                }
                apFromString();
                timer = new Timer();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        count++;
                        wifi.startScan();
                        if (count == 3) { 
                        	timer.cancel();
                        }
                    }
                };
                timer.schedule(timerTask, 0, 10000);
            }
        });
    }
    
    public void allPlaceScore(ArrayList<String> scanList) {
    	Log.i(TAG, "allPlaceScore ap list size"+String.valueOf(apListAll.size()));
    	for(int j=0; j<apListAll.size(); j++)
    	{    		
    		weightMatrix[count][j] = singlePlaceScore(scanList, apListAll.get(j));
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

    public ArrayList<Double> weightAverage() {
        ArrayList<Double> result = new ArrayList<Double>();

        for (int i = 0; i < weightMatrix[0].length; i++) {//ciclando sui place
            Double d = 0d;
            for (int j = 0; j < weightMatrix.length; j++) {//per ogni place fa la somma della colonna
                d += weightMatrix[j][i];
            }
            result.add( d /3);
            Log.i(TAG, "average"+String.valueOf(d));
        }
        return result;
    }

    public void filter() {
        double threshold = 0.1d;
        for (int i = 0; i < matchList.size(); i++) {
        	Log.i(TAG, "filtro");
            if (matchList.get(i) < threshold) {
            	Log.i(TAG, "to remove"+matchList.get(i));
                matchList.remove(matchList.get(i));                
                i = 0;
            }
        }
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
    	if(broadcastReceiver!=null)
    	{
    	unregisterReceiver(broadcastReceiver);
    	}
    }

}
