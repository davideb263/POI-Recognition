package com.example.davide.poirecognition;

public class AccessPoint {// classe cha contiene mac, ssid e received signal strenght 

	private String mac;
	private String ssid;
	private int rss;// scan result restituisce un int

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public int getRss() {
		return rss;
	}

	public void setRss(int rss) {
		this.rss = rss;
	}

	public AccessPoint() {
		mac = null;
		ssid = null;
		rss = 0;
	}

	public AccessPoint(String _mac, String _ssid, int _rss) {
		mac = _mac;
		ssid = _ssid;
		rss = _rss;
	}

	@Override
	public String toString() {//stampa in grassetto
		String result = "<b>SSID:</b> " + ssid + "<br>" + "<b>MAC:</b> " + mac + "<br>" + "<b>RSS[dBm]:</b> " + rss
				+ "<br>" + "<br>";
		return result;
	}
}
