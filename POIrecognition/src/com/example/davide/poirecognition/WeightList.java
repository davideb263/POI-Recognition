package com.example.davide.poirecognition;

import java.util.ArrayList;

public class WeightList {
	private ArrayList<StringWeight> weightsList;

	public WeightList() {
		weightsList = new ArrayList<StringWeight>();
	}

	public void add(String string, Double number) {
		weightsList.add(new StringWeight(string, number));
	}

	public int size() {
		return weightsList.size();
	}

	public ArrayList<StringWeight> getWeightsList() {
		return weightsList;
	}

	public void setWeightsList(ArrayList<StringWeight> weightsList) {
		this.weightsList = weightsList;
	}

	public void setElement(int index, StringWeight sw) {
		weightsList.set(index, sw);
	}

	public StringWeight getElement(int i) {
		return weightsList.get(i);
	}

	public ArrayList<String> stringList() {
		ArrayList<String> listString = new ArrayList<String>();
		for (int a = 0; a < weightsList.size(); a++) {
			listString.add(weightsList.get(a).getString());

		}
		return listString;
	}
}
