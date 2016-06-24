package com.example.davide.poirecognition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class WiFiScanner extends BroadcastReceiver {
	final private String TAG = "WifiReceiver";

	private List<ScanResult> wifiList = null;

	private int count;
	private int numberOfScans;
	private int percentage;
	private Context context;

	public WiFiScanner(int max, Context c) {
		count = 0;
		numberOfScans = max;// totale scansioni
		context = c;
		percentage = 0;
	} 
	//La funzione è eseguita quando il WiFiManager restituisce la scansione
	//ed è svolta in modo asincrono
	public void onReceive(Context c, Intent intent) {
		Log.i(TAG, "scansione");
		POI_Training.progTv.setText("Scan in progress...");
		count++;
		percentage = count * 100 / numberOfScans;//percentuale svolta
		AccessPoint swapAp = new AccessPoint();
		wifiList = POI_Training.wf.getScanResults();//WiFiManager restituisce la scansione
		POI_Training.history.add(new Scan());//aggiungiamo una scansione

		//aggiunge all'ultima scansione accesspoint con mac, nome della rete e potenza
		for (int i = 0; i < wifiList.size(); i++) {

			POI_Training.history.getScan(POI_Training.history.size() - 1).add(new AccessPoint(
					wifiList.get(i).BSSID.toString(), wifiList.get(i).SSID.toString(), wifiList.get(i).level));

		}
		//sort scansione ricevuta
		for (int i = 0; i < POI_Training.history.getScan(POI_Training.history.size() - 1).size(); i++) {
			for (int j = i; j < POI_Training.history.getScan(POI_Training.history.size() - 1).size(); j++) {
				if (POI_Training.history.getScan(POI_Training.history.size() - 1).getAp(i)
						.getRss() < POI_Training.history.getScan(POI_Training.history.size() - 1).getAp(j).getRss()) {
					swapAp = POI_Training.history.getScan(POI_Training.history.size() - 1).getAp(i);
					POI_Training.history.getScan(POI_Training.history.size() - 1).setAp(i,
							POI_Training.history.getScan(POI_Training.history.size() - 1).getAp(j));
					POI_Training.history.getScan(POI_Training.history.size() - 1).setAp(j, swapAp);
				}
			}

		}
		for (int i = 0; i < POI_Training.history.getScan(POI_Training.history.size() -1).size(); i++) {
			AccessPoint a= POI_Training.history.getScan(POI_Training.history.size() -1).getAp(i);
			Log.i(TAG, "Rete numero " + (i + 1));
			Log.i(TAG, "SSID" + a.getSsid());
			Log.i(TAG, "MAC" + a.getMac());
			Log.i(TAG, "Ss [dBm]" + a.getRss());
		}
		if (count == 2) {
			POI_Training.progTv.setText("merging...");
			POI_Training.wlWeight = POI_Training.history.firstMerge(0, 1);// la fp corrente è il merge e sort tra
																			//le prime 2 scansioni
			POI_Training.pb.setProgress(percentage);
		}
		else if (count > 2) {
			POI_Training.progTv.setText("merging...");
			POI_Training.wlWeight = POI_Training.history.Merge(count - 1, POI_Training.wlWeight);// la
			// fp
			// corrente
			// è il merge e sort tra la fp corrente e la scansione/
			POI_Training.pb.setProgress(percentage);
		}
		// if(count>=2)
		// {

		// _s = _s +"<br> <b>Number Of Wifi connections :" +
		// POI_Training.wlWeight.size() + "</b>" + "<br><br>";
		// for(int a=0; a<POI_Training.wlWeight.size(); a++)
		// {
		// _s = _s+"<b><font color=\"red\">WiFi " + (a+1) + "</font></b>" +
		// "<br>"+POI_Training.wlWeight.getApWeight(a).toString();
		// _s=_s+"<br>";
		// }
		//
		// POI_Training.mainText.append(Html.fromHtml(_s));

		// }
		if (count == numberOfScans) {
			POI_Training.progTv.setText("Writing file...");
			POI_Training.weightFilter();//rimuove dall'ultima fp i mac con i pesi minori della soglia
			Log.i(TAG, "numberOfScans");
			String storedir = Environment.getExternalStorageDirectory() + "/POI_Fingerprints";
			File f = new File(storedir);
			if (!f.exists()) {//prova a creare una directory
				if (!f.mkdir()) {
					Log.e("Error", "Can't create directory");
				}
			}
			if (storedir != null) {
				String str = "";//se ha creato la cartella
				for (int i = 0; i < POI_Training.wlWeight.size(); i++) {//scrive sul file una stringa con gli ap separati dagli a capo
					str += POI_Training.wlWeight.getWeightsList().get(i).getString() + "\n";//ordinati per peso
				}
				FileOutputStream fostream = null;
				OutputStreamWriter outputwriter = null;

				String filename = storedir + "/" + POI_Training.name + ".txt";
				try {
					fostream = new FileOutputStream(filename);
					outputwriter = new OutputStreamWriter(fostream);
					outputwriter.append(str);//scrive un file che ha nome il nome del poi e contiene i mac
					outputwriter.close();
					fostream.close();
					POI_Training.pb.setVisibility(View.GONE);
					POI_Training.progTv.setText("Finish fingerprinting!\nFingerprint created successfully");
					Toast.makeText(context, "Saved file now", Toast.LENGTH_SHORT).show();
				}
				catch (FileNotFoundException e) {
					Log.e(TAG, e.getMessage());
				}
				catch (IOException e) {
					Log.e(TAG, e.getMessage());
				}
			}
		}
	}


}
