package com.example.poirecognition;
import java.util.ArrayList;
import java.util.List;
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
	private String _s = "";
	private double swaprss=0;
	private WifiManager wfm;

	// This method call when number of wifi connections changed
	public void onReceive(Context c, Intent intent) {
		
		wifiList = POI_training.wf.getScanResults(); 
		POI_training.scanList.clear();
		_s="";
		_s = _s + "<br>       <b>Number Of Wifi connections :" + wifiList.size() + "</b>" + "<br><br>";


		for(int i = 0; i < wifiList.size(); i++){
			
			_s = _s + "<b><font color=\"red\">WiFi " + (i+1) + "</font></b>" + "<br>" +
					"<b>SSID:</b> " + wifiList.get(i).SSID + "<br>" +
					"<b>MAC:</b> " + wifiList.get(i).BSSID + "<br>" +
					"<b>RSS[dBm]:</b> " + wifiList.get(i).level + "<br>" +
					"<br>";
			POI_training.accessPoint.mac= wifiList.get(i).BSSID;
			POI_training.accessPoint.rss= wifiList.get(i).level;
			POI_training.accessPoint.ssid=wifiList.get(i).SSID;
		POI_training.scanList.add(POI_training.accessPoint);
		
		}
		POI_training.scanSList.add(_s);
		for(int i=0; i<POI_training.scanList.size(); i++)
		{
			
			for(int j=i; j<POI_training.scanList.size(); j++)
			{				
				if(POI_training.scanList.get(i).rss<POI_training.scanList.get(j).rss)
				{
					swaprss=POI_training.scanList.get(i).rss;
					POI_training.scanList.get(i).rss=POI_training.scanList.get(j).rss;
					POI_training.scanList.get(j).rss=swaprss;
				}
			}
			
		}
		POI_training.history.add(POI_training.scanList);
		String a="";
		for(int i=POI_training.scanSList.size() -1; i>=0; i--)
		{
			a=a+POI_training.scanSList.get(i);  
		}
		POI_training.mainText.setText(Html.fromHtml(a));
		
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
