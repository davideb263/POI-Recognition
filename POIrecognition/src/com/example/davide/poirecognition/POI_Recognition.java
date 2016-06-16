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
import java.io.FileNotFoundException;
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
    private ArrayList<String> apList = null;
    private ArrayList<Double> matchList = null;

    public TimerTask timerTask;
    public Timer timer;
    public int count = 0;

    WifiManager wifi;
    List<ScanResult> results;
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
        Intent recognitionIntent = getIntent();
        String s = recognitionIntent.getStringExtra("stringStart");

        fpDataBase = new ArrayList<String>(); //lista dei percorsi
        fpList = new ArrayList<String>(); //lista contenuto fingerprint
        apList = new ArrayList<String>(); //lista di access point della fp corrente
        recScan = new ArrayList<String>(); //lista di access point della recognition
        apListAll= new ArrayList<ArrayList<String>>();

        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (!wifi.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {

                recScan.clear();
                results = wifi.getScanResults();
                for (int j = 0; j < results.size(); j++) {
                    recScan.add(results.get(j).BSSID);
                }

            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));


        bttback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
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
                }
                apFromString();
                timer = new Timer();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        count++;
                        wifi.startScan();
                        allPlaceScore(recScan);
                        if (count == 9) {
                            timer.cancel();
                            matchList = weightAverage();
                            filter();
                        }
                    }
                };
                timer.schedule(timerTask, 0, 30000);
            }
        });
    }
    
    public void allPlaceScore(ArrayList<String> scanList) {
    	for(int j=0; j<apListAll.size(); j++)
    	{
    		weightMatrix[count][j] = singlePlaceScore(scanList, apListAll.get(j));
    	}
        
    }
    
    public void apFromString()
    {
    	for (int i = 0; i < fpDataBase.size(); i++) {
            try {
                fpList.add(getStringFromFile(fpDataBase.get(i)));

            } catch (IOException fnfe) {
                Log.e(TAG, fnfe.getMessage());
            }
        }

        for (int j = 0; j < fpList.size(); j++) {  //per ogni place
        	
            String[] parts = fpList.get(j).split("\n");
            for (int a = 0; a < parts.length; a++) {   //crea una lista di access point di un place
                apList.add(parts[a]);                
            }
            apListAll.add(apList);
            apList.clear();
        }    	
    }
    public double singlePlaceScore(ArrayList<String> scan, ArrayList<String> place) {

        double sumWeight = 0;
        ArrayList<String> commonAP = new ArrayList<String>();
        for (int i = 0; i < scan.size(); i++) {
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
                sumWeight += 1.0 / ((double) (Math.abs(pos1 - pos2) + (double) (pos1 + pos2) / 2));
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

            result.add( d / 10);
        }
        return result;
    }

    public void filter() {
        double threshold = 0.1d;
        for (int i = 0; i < matchList.size(); i++) {
            if (matchList.get(i) < threshold) {
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

}
