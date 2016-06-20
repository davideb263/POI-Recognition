package com.example.davide.poirecognition;

public class ApWeight {// lista di mac e pesi
	private String apMac;
	private double weight;
	public String getApMac() {
		return apMac;
	}
	public void setApMac(String apMac) {
		this.apMac = apMac;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public ApWeight(String apMac, double weight) {
		this.apMac = apMac;
		this.weight = weight;
	}
	@Override
	public String toString() {
		String stringa=apMac+" " +Double.toString(weight);
		return stringa;
	}
}
