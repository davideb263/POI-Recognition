package com.example.davide.poirecognition;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

public class WiFiScanner extends BroadcastReceiver
{
	final private String TAG ="WifiReceiver";
	private List<ScanResult> wifiList = null;
	private static String _s = "";
	private AccessPoint swapAp;
	private int count;
	private int numberOfScans;
	private int percentage;
	private Context context;
	//private ProgressDialog progress;

	public WiFiScanner(int max, Context c)
	{
		count=0;
		numberOfScans=max;//total scans
		context=c;
		percentage=0;
		//progress=new ProgressDialog(c);
		//progress.show(c, "Training", "Scansioni in corso..", false);
		//progress.setMax(100);
		//progress.setProgress(percentage);
		
		POI_Training.progTv.setText("Scansioni in corso...");
	}	//This method call when timer task triggers
	public void onReceive(Context c, Intent intent) {
		Log.i(TAG, "scansione");
		count++;
		percentage=count*100/numberOfScans;
		swapAp=new AccessPoint();
		wifiList = POI_Training.wf.getScanResults(); 
		POI_Training.history.add(new Scan());//
		for (int i = 0; i < wifiList.size(); i++) {
			ScanResult sc= wifiList.get(i);			
			Log.i(TAG, "Rete numero "+ (i+1));
			Log.i(TAG, "SSID"+sc.SSID);
			Log.i(TAG, "MAC"+sc.BSSID);
			Log.i(TAG, "Ss [dBm]"+sc.level);
		}
		//

		for(int i = 0; i < wifiList.size(); i++){

			POI_Training.history.getScan(POI_Training.history.size()-1).add(new AccessPoint(wifiList.get(i).BSSID.toString(),wifiList.get(i).SSID.toString(),wifiList.get(i).level ));

		}
		for(int i=0; i<POI_Training.history.getScan(POI_Training.history.size() -1).size(); i++){	//sort		
			for(int j=i; j<POI_Training.history.getScan(POI_Training.history.size() -1).size(); j++){				
				if(POI_Training.history.getScan(POI_Training.history.size() -1).getAp(i).getRss()<POI_Training.history.getScan(POI_Training.history.size()-1).getAp(j).getRss()){
					swapAp=POI_Training.history.getScan(POI_Training.history.size() -1).getAp(i);
					POI_Training.history.getScan(POI_Training.history.size() -1).setAp(i, POI_Training.history.getScan(POI_Training.history.size()-1).getAp(j) ); 
					POI_Training.history.getScan(POI_Training.history.size()-1).setAp(j, swapAp);
				}
			}

		}
		if(count==2)
		{
			POI_Training.progTv.setText("merging...");
			POI_Training.wlWeight=POI_Training.history.firstMerge(0, 1);// la fp corrente è tra 2 scansioni
			POI_Training.pb.setProgress(percentage);
		}
		if(count>2)
		{
			POI_Training.progTv.setText("merging...");
			POI_Training.wlWeight=POI_Training.history.Merge(count-1, POI_Training.wlWeight);//la fp corrente è tra la fp e la scansione 
			POI_Training.pb.setProgress(percentage);
		}
//		if(count>=2)
//		{
			
//			_s = _s +"<br>       <b>Number Of Wifi connections :" + POI_Training.wlWeight.size() + "</b>" + "<br><br>";
//			for(int a=0; a<POI_Training.wlWeight.size(); a++)
//			{				
//				_s = _s+"<b><font color=\"red\">WiFi " + (a+1) + "</font></b>" + "<br>"+POI_Training.wlWeight.getApWeight(a).toString();
//				_s=_s+"<br>";
//			}
//
//			POI_Training.mainText.append(Html.fromHtml(_s));

//		}
		if(count== numberOfScans)
		{
			POI_Training.progTv.setText("Writing file...");
			Toast.makeText(context, "stop", Toast.LENGTH_SHORT).show();
			POI_Training.weightFilter();
			Log.i(TAG, "number");
			String storedir = Environment.getExternalStorageDirectory() + "/POI_Fingerprints";
			File f = new File(storedir);
			if (!f.exists()){
				if (!f.mkdir()) {
					Log.e("Error", "Can't create download directory");
				}
			}
			if (storedir != null) {
				String str = "";
				for (int i = 0; i < POI_Training.wlWeight.size(); i++) {
					str += POI_Training.wlWeight.getWeightsList().get(i).getApMac() + "\n";
				}
				FileOutputStream fostream = null;
				OutputStreamWriter outputwriter = null;

				String filename = storedir + "/" + POI_Training.name + ".txt";
				try {
					fostream = new FileOutputStream(filename);
					outputwriter = new OutputStreamWriter(fostream);
					outputwriter.append(str);
					outputwriter.close();
					fostream.close();
				} catch (FileNotFoundException e) {
					Log.e(TAG, e.getMessage());
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
				} finally
				{
					POI_Training.progTv.setText("Finish fingerprinting!\nFingerprint created successfully");
				}
			}
		}

	}

	/*private List <ScanResult> myScanResults=null;
	@Override
	public void onReceive(Context context, Intent arg1) {
		MainActivity.mainText.setText("Scan wifi signals\n");
		myScanResults=  MainActivity.wf.getScanResults();
		Log.i(TAG, "***************SCANSIONE RETI WiFi********************");
		for (int i = 0; i < myScanResults.size(); i++) {
			ScanResult sc= myScanResults.get(i);

			Log.i(TAG, "Rete numero "+ (i+1));
			Log.i(TAG, "SSID"+sc.SSID);
			Log.i(TAG, "MAC"+sc.BSSID);
			Log.i(TAG, "Ss [dBm]"+sc.level);
			MainActivity.mainText.append("Rete numero "+(i+1) +"SSID "+sc.SSID+"\n"+"MAC "+sc.BSSID +"Ss [dBm] "+sc.level+"\n");
		}
	}for(int i=history.Size() -1; i>=0; i--)
		{

			_s = _s +"<br>       <b>Number Of Wifi connections :" + history.getHistory().get(i).Size() + "</b>" + "<br><br>";


			for(int a = 0; a < history.getHistory().get(i).Size(); a++){

				_s = _s+"<b><font color=\"red\">WiFi " + (a+1) + "</font></b>" + "<br>"+history.getHistory().get(i).GetIndex(a).ToString();

			}

		}
		POI_Training.mainText.append(Html.fromHtml(_s));

	 */

}
