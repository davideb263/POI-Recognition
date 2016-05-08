package com.example.davide.poirecognition;


public class AccessPoint{

    public String mac;
    public String ssid;
    public double rss;

    public AccessPoint()
    {
        mac = null;
        ssid = null;
        rss = 0;
    }

    public AccessPoint(String _mac, String _ssid, double _rss)
    {
        mac = _mac;
        ssid = _ssid;
        rss = _rss;
    }
}

