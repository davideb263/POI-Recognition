package com.example.davide.poirecognition;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.text.Html;
public class WiFiScanner extends BroadcastReceiver
{
	final String TAG ="WifiReceiver";
	private List<ScanResult> wifiList = null;
	private static String _s = "";
	private AccessPoint swapAp;
	private WifiManager wfm;
	private static History history;
	private static WeightList wlWeight;
	private int count;
	public WiFiScanner(History history, WeightList wlWeight)
	{
		this.history=POI_Training.history;
		this.wlWeight=POI_Training.wlWeight;
		count=0;
	}
	// This method call when number of wifi connections changed
	public void onReceive(Context c, Intent intent) {
		POI_Training.mainText.setText("new scan event ");
		count++;
		swapAp=new AccessPoint();
		wifiList = POI_Training.wf.getScanResults(); 
		POI_Training.history.Add(new Scan());
		//POI_training.scanList.clear();
		for(int i = 0; i < wifiList.size(); i++){

			POI_Training.history.getHistory().get(POI_Training.history.Size()-1).Add(new AccessPoint(wifiList.get(i).BSSID.toString(),wifiList.get(i).SSID.toString(),wifiList.get(i).level ));
	
		}
		for(int i=0; i<POI_Training.history.getHistory().get(POI_Training.history.Size() -1).Size(); i++)
		{
			
			for(int j=i; j<POI_Training.history.getHistory().get(POI_Training.history.Size() -1).Size(); j++)
			{				
				if(POI_Training.history.getHistory().get(POI_Training.history.Size() -1).GetIndex(i).getRss()<POI_Training.history.getHistory().get(POI_Training.history.Size()-1).GetIndex(j).getRss())
				{
					swapAp=POI_Training.history.getHistory().get(POI_Training.history.Size() -1).GetIndex(i);
					POI_Training.history.getHistory().get(POI_Training.history.Size() -1).SetIndex(i, POI_Training.history.getHistory().get(POI_Training.history.Size()-1).GetIndex(j) ); 
					POI_Training.history.getHistory().get(POI_Training.history.Size()-1).SetIndex(j, swapAp);
				}
			}
			
		}
		if(count==2)
		{
			POI_Training.wlWeight=POI_Training.history.firstMerge(0, 1);
			
		}
		else if(count>2)
		{
			POI_Training.wlWeight=POI_Training.history.Merge(count-1, POI_Training.wlWeight);
		
		}
		if(count>=2)
		{_s = _s +"<br>       <b>Number Of Wifi connections :" + POI_Training.wlWeight.Size() + "</b>" + "<br><br>";
			for(int a=0; a<POI_Training.wlWeight.Size(); a++)
			{				
			
					
					_s = _s+"<b><font color=\"red\">WiFi " + (a+1) + "</font></b>" + "<br>"+POI_Training.wlWeight.getWeightsList().get(a).toString();

				_s=_s+"<br>";
			}
			
			POI_Training.mainText.append(Html.fromHtml(_s));
			
		}
			
	}

	public History getHistory() {
		return history;
	}
	public void setHistory(History history) {
		this.history = history;
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
