package com.example.davide.poirecognition;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class POI_Training extends Activity {

    private EditText poiName = null;
    private EditText numberOfScans = null;
    private EditText intervalOfScans = null;
    private Button bttTraining = null;
    private Button bttGetData = null;

    public static ArrayList<AccessPoint> ScanList = new ArrayList<AccessPoint>();
    public static ArrayList<ArrayList> ScanHistory = new ArrayList<ArrayList>();
    public static AccessPoint accessPoint = new AccessPoint();
    public TimerTask timerTask;
    public Timer timer;
    public static WifiManager mWifiManager = null;
    public static TextView scanText;

    //private Context context = null;
    private WifiReceiver receiverWifi = null;

    public String name = null;
    public int scansNum = 0;
    public int interval= 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poi_training);

        poiName = (EditText)findViewById(R.id.PoiName);
        numberOfScans = (EditText)findViewById(R.id.WiFiScanNumber);
        intervalOfScans = (EditText)findViewById(R.id.TimeInterval);
        bttTraining = (Button)findViewById(R.id.Training);
        bttGetData = (Button)findViewById(R.id.getData);

        Intent trainingIntent = getIntent();
        String s = trainingIntent.getStringExtra("stringTrain");

        bttGetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    name = poiName.getText().toString();
                    scansNum = Integer.parseInt(numberOfScans.getText().toString());
                    interval = Integer.parseInt(intervalOfScans.getText().toString());

                }
                catch(NumberFormatException e) {
                    Toast.makeText(getApplicationContext(),"Invalid Integer. Void or to long", Toast.LENGTH_SHORT).show();
                }
                finally {
                    Toast.makeText(getApplicationContext(),"Input Got Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bttTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Running the task (Training)
                StartTimerScan(interval, scansNum);

                setResult(Activity.RESULT_OK, new Intent().putExtra("training", "trn"));
                finish();
            }
        });
    }

    public void StartTimerScan(int _interval, int _scansNum)
    {
        timer = new Timer();

        timerTask = new TimerTask() {
            @Override
            public void run() {
                // Check for wifi is disabled
                if (!mWifiManager.isWifiEnabled()) {
                    // If wifi disabled then enable it
                    Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
                    // Enable WiFi
                    mWifiManager.setWifiEnabled(true);
                }

                // wifi scanned value broadcast receiver
                receiverWifi = new WifiReceiver();

                // Register broadcast receiver
                // Broadcast receiver will automatically call when number of wifi connections changed
                registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                mWifiManager.startScan();

                Toast.makeText(getApplicationContext(), "Starting scan", Toast.LENGTH_LONG).show();
            }
        };

        timer.scheduleAtFixedRate(timerTask, 0, (_interval/_scansNum)*1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiverWifi);
    }
}
