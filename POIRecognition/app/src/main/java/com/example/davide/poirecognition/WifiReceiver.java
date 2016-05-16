package com.example.davide.poirecognition;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.text.Html;

public class WifiReceiver extends BroadcastReceiver {

    private AccessPoint swapAp;
    private History history;
    List<ScanResult> wifiList = null;
    String _s = "";


    public WifiReceiver(History h){
        this.history = h;
    }

    // This method call when number of wifi connections changed
    public void onReceive(Context c, Intent intent) {
        POI_Training.scanText.setText("new scan event ");
        _s="";
        swapAp=new AccessPoint();
        wifiList = POI_Training.mWifiManager.getScanResults();
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

                _s = _s+"<b><font color=\"red\">WiFi " + (a+1) + "</font></b>" + "<br>"+history.GetIndex(i).GetIndex(a).toString();

            }

        }
        POI_Training.scanText.append(Html.fromHtml(_s));

    }

    public History getHistory() {
        return history;
    }
    public void setHistory(History history) {
        this.history = history;
    }

}


