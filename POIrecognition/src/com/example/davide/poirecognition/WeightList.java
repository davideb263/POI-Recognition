package com.example.davide.poirecognition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

public class WeightList {
	private ArrayList<apWeight> weightsList;
	public WeightList()
	{
		weightsList	= new ArrayList<apWeight>();
	}
	public void Add(String string, Double number)
	{
		weightsList.add(new apWeight(string, number));
	}
	public int Size()
	{
		return weightsList.size();
	}
	public ArrayList<apWeight> getWeightsList() {
		return weightsList;
	}
	public void setWeightsList(ArrayList<apWeight> weightsList) {
		this.weightsList = weightsList;
	}
	
	public int GetI(String mac)
	{
		boolean isPresent=false;
		int pos=0;
		for(int a=0; a<weightsList.size(); a++)
		{
			if(weightsList.get(a).getApMac().compareTo(mac)==0)
			{
				pos=a;
				isPresent=true;
			}

		}
		if(isPresent)
			return pos;
		else
			return -1;
	}
}
