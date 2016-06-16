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
	public void setApWeight(int index, apWeight ap)	{
		weightsList.set(index, ap);		
	}
	public apWeight getApWeight(int i)	{
		return weightsList.get(i);
	}
	public ArrayList<String> stringList()
	{
		ArrayList<String> ap= new ArrayList<String>();
		for(int a=0; a<weightsList.size(); a++)
		{
			ap.add(weightsList.get(a).getApMac());

		}
			return ap;
	}
}
