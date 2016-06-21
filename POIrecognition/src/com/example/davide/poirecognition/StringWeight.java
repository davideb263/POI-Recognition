package com.example.davide.poirecognition;

public class StringWeight {// lista di stringhe e pesi
	private String string;
	private double weight;
	public String getString() {
		return string;
	}
	public void setString(String string) {
		this.string = string;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public StringWeight(String string, double weight) {
		this.string = string;
		this.weight = weight;
	}
	@Override
	public String toString() {
		String stringa=string+" " +Double.toString(weight);
		return stringa;
	}
}
