package com.example.poirecognition;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.text.Html;

public class WiFiScanner1 extends BroadcastReceiver {
	final String TAG ="WifiReceiver";
	private List<ScanResult> wifiList = null;
	private String _s = "";
	private AccessPoint swapAp;
	private WifiManager wfm;
	private static History history;
	public void WiFiScanner(History history)
	{
		this.history=history;
	}
	// This method call when number of wifi connections changed
	public void onReceive(Context c, Intent intent) {
		POI_training.mainText.setText("new scan event ");
		_s="";
		swapAp=new AccessPoint();
		wifiList = POI_training.wf.getScanResults(); 
		history.Add(new Scan());
		//POI_training.scanList.clear();
		for(int i = 0; i < wifiList.size(); i++){

		history.GetIndex(history.Size()-1).Add(new AccessPoint(wifiList.get(i).BSSID.toString(),wifiList.get(i).SSID.toString(),wifiList.get(i).level ));
	
		}
		for(int i=0; i<history.GetIndex(history.Size() -1).Size(); i++)
		{
			
			for(int j=i; j<history.GetIndex(history.Size() -1).Size(); j++)
			{				
				if(history.GetIndex(history.Size() -1).GetIndex(i).getRss()<history.GetIndex(history.Size()-1).GetIndex(j).getRss())
				{
					swapAp=history.GetIndex(history.Size() -1).GetIndex(i);
					history.GetIndex(history.Size() -1).SetIndex(i, history.GetIndex(history.Size()-1).GetIndex(j) ); 
					history.GetIndex(history.Size()-1).SetIndex(j, swapAp);
				}
			}
			
		}
		
		for(int i=history.Size() -1; i>=0; i--)
		{
			
			_s = _s +"<br>       <b>Number Of Wifi connections :" + history.GetIndex(i).Size() + "</b>" + "<br><br>";
			

			for(int a = 0; a < history.GetIndex(i).Size(); a++){
				
				_s = _s+"<b><font color=\"red\">WiFi " + (a+1) + "</font></b>" + "<br>"+history.GetIndex(i).GetIndex(a).ToString();
				
			}
			
		}
		POI_training.mainText.append(Html.fromHtml(_s));
		
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
	}*/
	

}
