package com.example.davide.poirecognition;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
public class WiFiScanner extends BroadcastReceiver
{
	final String TAG ="WifiReceiver";
	private List<ScanResult> wifiList = null;
	private static String _s = "";
	private AccessPoint swapAp;
	private int count;
	private int numberOfScans;
	public WiFiScanner(int max)
	{
		count=0;
		numberOfScans=max;
	}
	// This method call when number of wifi connections changed
	public void onReceive(Context c, Intent intent) {
		Log.i(TAG, "scansione");
		POI_Training.mainText.setText("new scan event ");
		count++;
		swapAp=new AccessPoint();
		wifiList = POI_Training.wf.getScanResults(); 
		POI_Training.history.add(new Scan());
		//POI_training.scanList.clear();
		for(int i = 0; i < wifiList.size(); i++){

			POI_Training.history.getScan(POI_Training.history.size()-1).add(new AccessPoint(wifiList.get(i).BSSID.toString(),wifiList.get(i).SSID.toString(),wifiList.get(i).level ));
	
		}
		for(int i=0; i<POI_Training.history.getScan(POI_Training.history.size() -1).size(); i++)
		{
			
			for(int j=i; j<POI_Training.history.getScan(POI_Training.history.size() -1).size(); j++)
			{				
				if(POI_Training.history.getScan(POI_Training.history.size() -1).getAp(i).getRss()<POI_Training.history.getScan(POI_Training.history.size()-1).getAp(j).getRss())
				{
					swapAp=POI_Training.history.getScan(POI_Training.history.size() -1).getAp(i);
					POI_Training.history.getScan(POI_Training.history.size() -1).setAp(i, POI_Training.history.getScan(POI_Training.history.size()-1).getAp(j) ); 
					POI_Training.history.getScan(POI_Training.history.size()-1).setAp(j, swapAp);
				}
			}
			
		}
		if(count==2)
		{
			POI_Training.wlWeight=POI_Training.history.firstMerge(0, 1);
			
		}
		if(count>2)
		{
			POI_Training.wlWeight=POI_Training.history.Merge(count-1, POI_Training.wlWeight);
		
		}
		if(count>=2)
		{_s = _s +"<br>       <b>Number Of Wifi connections :" + POI_Training.wlWeight.size() + "</b>" + "<br><br>";
			for(int a=0; a<POI_Training.wlWeight.size(); a++)
			{				
			
					
					_s = _s+"<b><font color=\"red\">WiFi " + (a+1) + "</font></b>" + "<br>"+POI_Training.wlWeight.getWeightsList().get(a).toString();

				_s=_s+"<br>";
			}
			
			POI_Training.mainText.append(Html.fromHtml(_s));
			
		}
		if(count== numberOfScans)
		{
			POI_Training.mainText.append("End scansioni");
			POI_Training.weightFilter();
			Log.i(TAG, "number");
			String storedir = Environment.getExternalStorageDirectory() + "/POI_Fingerprints";
			File f = new File(storedir);
			if (!f.exists())
				if (!f.mkdir()) {
					Log.e("Error", "Can't create download directory");
				}

			if (storedir != null) {
				String str = "";
				for (int i = 0; i < POI_Training.wlWeight.size(); i++) {
					str += POI_Training.wlWeight.getWeightsList().get(i).getApMac() + "\n";
				}
				FileOutputStream fostream = null;
				OutputStreamWriter outputwriter = null;
				try {
					String filename = storedir + "/" + POI_Training.name + ".txt";
					fostream = new FileOutputStream(filename);
					outputwriter = new OutputStreamWriter(fostream);
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
				}
				try {
					outputwriter.append(str);
					outputwriter.close();
					fostream.close();
				} catch (IOException exc) {
					Log.e(TAG, "errore");
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
