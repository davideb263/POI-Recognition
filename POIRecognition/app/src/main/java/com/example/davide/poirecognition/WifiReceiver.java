package com.example.davide.poirecognition;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.text.Html;

public class WifiReceiver extends BroadcastReceiver {

    private List<ScanResult> wifiList = null;
    private String _s = null;

    private WifiManager wfm;

    // This method call when number of wifi connections changed
    public void onReceive(Context c, Intent intent) {

        wifiList = POI_Training.mWifiManager.getScanResults();

        _s = _s + "<br>       <b>Number Of Wifi connections :" + wifiList.size() + "</b>" + "<br><br>";


        for(int i = 0; i < wifiList.size(); i++){

            _s = _s + "<b><font color=\"red\">WiFi " + (i+1) + "</font></b>" + "<br>" +
                    "<b>SSID:</b> " + wifiList.get(i).SSID + "<br>" +
                    "<b>MAC:</b> " + wifiList.get(i).BSSID + "<br>" +
                    "<b>RSS[dBm]:</b> " + wifiList.get(i).level + "<br>" +
                    "<br>";


            POI_Training.accessPoint.mac = wifiList.get(i).BSSID;
            POI_Training.accessPoint.ssid = wifiList.get(i).SSID;
            POI_Training.accessPoint.rss = wifiList.get(i).level;

            POI_Training.ScanList.add(POI_Training.accessPoint);
        }

        POI_Training.ScanHistory.add(POI_Training.ScanList);

        POI_Training.scanText.setText(Html.fromHtml(_s));
    }

}


