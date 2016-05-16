package com.example.davide.poirecognition;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


public class POI_Training extends Activity {

    private static final String TAG = "Training";
    private EditText poiName = null;
    private EditText numberOfScans = null;
    private EditText intervalOfScans = null;
    private Button bttTraining = null;
    private Button bttGetData = null;

    public History history;
    public TimerTask timerTask;
    public Timer timer;
    public static WifiManager mWifiManager = null;
    public static TextView scanText;

    private Context context = null;
    private WifiReceiver receiverWifi = null;

    public String name = null;
    public int scansNum = 0;
    public int interval= 0;
    public int count = 0;

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
        scanText = (TextView)findViewById(R.id.textScan);

        Intent trainingIntent = getIntent();
        String s = trainingIntent.getStringExtra("stringTrain");
        history = new History();
        receiverWifi = new WifiReceiver(history);

        mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);



        bttGetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.i(TAG, "saving data from user via EditTexts");
                    name = poiName.getText().toString();
                    scansNum = Integer.parseInt(numberOfScans.getText().toString());
                    interval = Integer.parseInt(intervalOfScans.getText().toString());

                    //this hide the keyboard when the user click the GetData button
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    Toast.makeText(getApplicationContext(), "Input Got Successfully", Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "Invalid Integer. Void or to long", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bttTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name == null || scansNum == 0 || interval == 0)
                    Toast.makeText(getApplicationContext(), "Insert all data first", Toast.LENGTH_SHORT).show();
                else {

                    //Running the task (Training)
                    count = scansNum;
                    timer = new Timer();
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            Log.i(TAG, "running the task");
                            count--;
                            StartScan();
                            if(count == 0)
                                timer.cancel();
                        }
                    };
                    timer.schedule(timerTask, 0, interval*1000/scansNum);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Closing Training activity.(onDestroy)");
        unregisterReceiver(receiverWifi);
    }


    public int GetI(int index, String mac)
    {
        boolean isPresent=false;
        Scan listAccess= receiverWifi.getHistory().GetIndex(index);
        int pos=0;
        for(int a=0; a<listAccess.Size(); a++)
        {
            if(listAccess.GetIndex(a).getMac().equals(mac))
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

    public void StartScan() {
        Log.i(TAG, "StartTimerScan");

        if(!mWifiManager.isWifiEnabled())
        {
            //Toast.makeText(getApplicationContext(), "wifi is disable... miking it enable", Toast.LENGTH_SHORT).show();
            mWifiManager.setWifiEnabled(true);
        }

        receiverWifi = new WifiReceiver(history);
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        Log.i(TAG, "starting scan");
        mWifiManager.startScan();
        //Toast.makeText(getApplicationContext(), "starting scan", Toast.LENGTH_SHORT).show();
    }


}
