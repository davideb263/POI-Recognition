package com.example.davide.poirecognition;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
//import java.util.Timer;
//import java.util.TimerTask;
import android.os.Handler;


public class POI_Training extends Activity {

    private static final String TAG = "Training";
    private EditText poiName = null;
    private EditText numberOfScans = null;
    private EditText intervalOfScans = null;
    private Button bttTraining = null;
    private Button bttGetData = null;
    private ScrollView scrollScan = null;

    public static ArrayList<AccessPoint> ScanList = new ArrayList<>();
    public static ArrayList<ArrayList> ScanHistory = new ArrayList<>();
    public static AccessPoint accessPoint = new AccessPoint();
    //public TimerTask timerTask;
    //public Timer timer;
    public Handler m_handler;
    public Runnable m_handlerTask;
    public static WifiManager mWifiManager = null;
    public static TextView scanText;

    private Context context = null;
    private WifiReceiver receiverWifi = null;

    public String name = null;
    public int scansNum = 0;
    public int interval= 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "creating the training activity");
        setContentView(R.layout.poi_training);

        context = this;

        poiName = (EditText)findViewById(R.id.PoiName);
        numberOfScans = (EditText)findViewById(R.id.WiFiScanNumber);
        intervalOfScans = (EditText)findViewById(R.id.TimeInterval);
        bttTraining = (Button)findViewById(R.id.Training);
        bttGetData = (Button)findViewById(R.id.getData);
        scrollScan = (ScrollView)findViewById(R.id.scrollScan);
        scanText = (TextView)findViewById(R.id.textScan);

        Intent trainingIntent = getIntent();
        String s = trainingIntent.getStringExtra("stringTrain");

        mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);

        bttGetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.i(TAG, "saving data from user via EditTexts");
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
                Log.i(TAG, "starting the actual Scan on train click");
                //Running the task (Training)
                StartTimerScan(interval, scansNum);

                //setResult(Activity.RESULT_OK, new Intent().putExtra("training", "trn"));
                //finish();
            }
        });
    }

    public void StartTimerScan(final int _interval, final int _scansNum)
    {
        Log.i(TAG, "StartTimerScan");
        m_handler = new Handler();
        m_handlerTask = new Runnable() {
            @Override
            public void run() {

                Log.i(TAG, "run of the handlerTask");
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

                m_handler.postDelayed(m_handlerTask, (_interval/_scansNum)*1000);
            }
        };
        m_handlerTask.run();


        /*timer = new Timer();

        timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.i(TAG, "run of the timerTask");
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

        timer.scheduleAtFixedRate(timerTask, 0, (_interval/_scansNum)*1000);*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "unregister wifiReceiver (onDestroy)");
        unregisterReceiver(receiverWifi);
        m_handler.removeCallbacks(m_handlerTask);
    }
}
