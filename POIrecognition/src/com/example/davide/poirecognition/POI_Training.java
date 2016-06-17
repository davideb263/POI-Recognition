package com.example.davide.poirecognition;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class POI_Training extends Activity {

	private EditText poiName = null;
	private EditText numberScans = null;
	private EditText intervalScans = null;
	private Button bttTrain = null;
	private Button bttFinish = null;
	private WiFiScanner wifiReceiver = null;
	public static TextView mainText = null;
	public static WifiManager wf = null;
	public TimerTask timerTask;
	public Timer timer;
	public static History history;
	public int count = 0;
	private int scansNumber;
	private int interval;
	public static String name;
	static public WeightList wlWeight;
	private final static String TAG = "Training";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.poi_training);
		name = "";
		poiName = (EditText) findViewById(R.id.PoiName);
		numberScans = (EditText) findViewById(R.id.WiFiScanNumber);
		intervalScans = (EditText) findViewById(R.id.TimeInterval);
		bttTrain = (Button) findViewById(R.id.Training);
		bttFinish = (Button) findViewById(R.id.getData);
		getIntent();
		//trainingIntent.getStringExtra("stringTrain");
		mainText = (TextView) findViewById(R.id.textScan);

		history = new History();
		wlWeight = new WeightList();
		wf = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		// wifi scanned value broadcast receiver
		// Register broadcast receiver
		// Broacast receiver will automatically call when number of wifi
		// connections changed
		
		Log.i(TAG, "creating the training activity");

		bttTrain.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					Log.i(TAG, "saving data from user via EditTexts");

					name = poiName.getText().toString();
					scansNumber = Integer.parseInt(numberScans.getText().toString());
					interval = Integer.parseInt(intervalScans.getText().toString());
					count = scansNumber;
					wifiReceiver = new WiFiScanner(scansNumber);
					registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
					InputMethodManager inputManager = (InputMethodManager) getSystemService(
							Context.INPUT_METHOD_SERVICE);
					inputManager.hideSoftInputFromWindow(
							(null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
					Toast.makeText(getApplicationContext(), "Input Got Successfully", Toast.LENGTH_SHORT).show();
				} catch (NumberFormatException e) {
					Toast.makeText(getApplicationContext(), "Not correct input", Toast.LENGTH_LONG).show();

				}
				if (poiName == null || scansNumber <= 0 || interval <= 0) {
					Toast.makeText(getApplicationContext(), "Enter data first", Toast.LENGTH_SHORT).show();
				} else {
					timer = new Timer();
					timerTask = new TimerTask() {
						@Override
						public void run() {
							count--;
							Log.i(TAG, "run");
							startScan();
							if (count == 0) {
								Log.i(TAG, "termine scansione");
								timer.cancel();
							}
						}
					};
					timer.schedule(timerTask, 0, interval * 1000 / scansNumber);
				}

			}
		});
		bttFinish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				setResult(Activity.RESULT_OK, new Intent().putExtra("training", "trainingString"));
				finish();
			}
		});
	}

	public static void weightFilter() {
		double threshold = 0.01;
		String macAddress = "";
		Log.i(TAG, "Enter weight filter");
		for (int a = 0; a < wlWeight.size(); a++) {
			// Log.i(TAG, "iterator");
			if (wlWeight.getApWeight(a).getWeight() < threshold) {
				macAddress = wlWeight.getApWeight(a).getApMac();
				Log.i(TAG, macAddress);
				wlWeight.getWeightsList().remove(a);
				a = 0;
			}
		}

	}

	public void startScan() {
		if (!wf.isWifiEnabled()) {
			// If wifi disabled then enable it
			// Toast.makeText(getApplicationContext(), "wifi is disabled..making
			// it enabled", Toast.LENGTH_SHORT).show();
			// Enable WiFi
			wf.setWifiEnabled(true);
		}
		// Toast.makeText(getApplicationContext(), "Start scanning",
		// Toast.LENGTH_SHORT).show();
		wf.startScan();
		// mainText.setText("Starting");

	}

	protected void onDestroy() {
		super.onDestroy();
		if(wifiReceiver!=null)
		{
		unregisterReceiver(wifiReceiver);
		}
		Log.i(TAG, "Closing Training activity.(onDestroy)");
	}
}
