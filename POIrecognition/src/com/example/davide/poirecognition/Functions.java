package com.example.davide.poirecognition;

import java.util.List;

public class Functions {

	public static int GetI(List<String> l, String mac) { // trova la posizione
															// della stringa
		boolean isPresent = false;
		int pos = 0;
		for (int a = 0; a < l.size(); a++) {
			if (l.get(a).compareTo(mac) == 0) {
				pos = a;
				isPresent = true;
			}

		}
		if (isPresent)
			return pos;
		else
			return -1;
	}

	public static double weight(int pos1, int pos2){// calcola il peso date le 2
													// posizioni
	
		double d = 0;
		if (pos1 != -1 && pos2 != -1) {
			d = 1.0 / ((double) (Math.abs(pos1 - pos2) + (double) (pos1 + pos2) / 2));
		}
		return d;
	}
}
