package com.example.davide.poirecognition;

import java.util.ArrayList;

public class WeightList {
	private ArrayList<ApWeight> weightsList;

	public WeightList() {
		weightsList = new ArrayList<ApWeight>();
	}

	public void add(String string, Double number) {
		weightsList.add(new ApWeight(string, number));
	}

	public int size() {
		return weightsList.size();
	}

	public ArrayList<ApWeight> getWeightsList() {
		return weightsList;
	}

	public void setWeightsList(ArrayList<ApWeight> weightsList) {
		this.weightsList = weightsList;
	}

	public void setApWeight(int index, ApWeight ap) {
		weightsList.set(index, ap);
	}

	public ApWeight getApWeight(int i) {
		return weightsList.get(i);
	}

	public ArrayList<String> stringList() {
		ArrayList<String> ap = new ArrayList<String>();
		for (int a = 0; a < weightsList.size(); a++) {
			ap.add(weightsList.get(a).getApMac());

		}
		return ap;
	}
}
