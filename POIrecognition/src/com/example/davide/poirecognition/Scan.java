package com.example.davide.poirecognition;

import java.util.ArrayList;

public class Scan {

	private ArrayList<AccessPoint> scan;

	public Scan() {
		scan = new ArrayList<AccessPoint>();
	}

	public ArrayList<AccessPoint> getScan() {
		return scan;
	}

	public void setScan(ArrayList<AccessPoint> scan) {
		this.scan = scan;
	}

	public void add(AccessPoint accesspoint) {
		scan.add(accesspoint);
	}

	public int size() {
		return scan.size();
	}

	public ArrayList<String> stringList() {// dato lo scan ritorna una lista di
											// mac
		ArrayList<String> stringList = new ArrayList<String>();
		for (int a = 0; a < this.size(); a++) {
			stringList.add(this.getAp(a).getMac());
		}
		return stringList;
	}

	public AccessPoint getAp(int index) {
		return scan.get(index);
	}

	public void setAp(int index, AccessPoint ap) {
		scan.set(index, ap);
	}
}
