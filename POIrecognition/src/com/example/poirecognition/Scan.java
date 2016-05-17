package com.example.poirecognition;

import java.util.ArrayList;

public class Scan {

	private ArrayList<AccessPoint> scan;

	public Scan(){
		
		scan=new ArrayList<AccessPoint>();
	
	}
	public ArrayList<AccessPoint> getScan(){
return scan;
	}
	
	public void setScan(ArrayList<AccessPoint> scan) {
		this.scan = scan;
	}
	
		
		public void Add(AccessPoint accesspoint){
			scan.add(accesspoint);
		}
		public int Size()
		{
			return scan.size();
		}
		public AccessPoint GetIndex(int index){
		return scan.get(index);
		}
		public void SetIndex(int index, AccessPoint ap){
			scan.set(index, ap);
			
		}
	}

