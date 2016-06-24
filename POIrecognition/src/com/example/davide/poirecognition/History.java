package com.example.davide.poirecognition;

import java.util.ArrayList;

public class History {// lista di scansioni

	private ArrayList<Scan> history;

	public History() {
		history = new ArrayList<Scan>();
	}

	public ArrayList<Scan> getHistory() {
		return history;
	}

	public void setHistory(ArrayList<Scan> history) {
		this.history = history;
	}

	public void add(Scan scan) {
		history.add(scan);
	}

	public int size() {
		return history.size();
	}

	public Scan getScan(int index) {
		return history.get(index);
	}

	public void setScan(int index, Scan s) {
		history.set(index, s);
	}

	public ArrayList<String> stringList(int index) {// data la scansione ritorna una
													// lista di stringhe dei mac per poi 
		ArrayList<String> stringList = new ArrayList<String>();//trovare la posizione di un mac in una lista
		Scan listAccess = history.get(index);
		for (int a = 0; a < listAccess.size(); a++) {
			stringList.add(listAccess.getAp(a).getMac());
		}
		return stringList;
	}

	public WeightList firstMerge(int i, int j) {//fa  il merge e il sort tra 2 scansioni 
		Scan list1 = history.get(i);//e restituisce una lista di mac con i pesi
		Scan list2 = history.get(j);
		WeightList wl = new WeightList();
		StringWeight swap;//stringa e peso per lo swap
		//merge
		for (int a = 0; a < list1.size(); a++) {//mette ap della prima scansione
			wl.add(list1.getAp(a).getMac(), (double) 0);
		}
		for (int c = 0; c < list2.size(); c++) {
			if (Functions.GetI(wl.stringList(), list2.getAp(c).getMac()) == -1) {//se la lista di mac e pesi non contiene il mac della seconda scansione
				wl.add(list2.getAp(c).getMac(), (double) 0);//lo mette
			}
		}
		//sort
		for (int k = 0; k < wl.size(); k++) {
			double weight = 0;
			int pos1 = Functions.GetI(stringList(i), wl.getElement(k).getString());//trova la posizione del mac nella lista dei pesi nella prima scansione
			int pos2 = Functions.GetI(stringList(j), wl.getElement(k).getString());//nella seconda
			if (pos1 != -1 && pos2 != -1) {//se il mac è in entrambe le scansioni
				pos1++;//per evitare
				pos2++;//di dividere per zero
				weight = Functions.weight(pos1, pos2);//calcola il peso e lo mette nella lista dei pesi
				wl.getElement(k).setWeight(weight);
			}
			else {//se non è presente in entrambe il peso è nullo
				wl.getElement(k).setWeight(0);
			}

		}
		//ordine in base al peso
		for (int l = 0; l < wl.size(); l++) {
			for (int d = l; d < wl.size(); d++) {
				if (wl.getElement(l).getWeight() < wl.getElement(d).getWeight()) {
					swap = wl.getElement(l);
					wl.setElement(l, wl.getElement(d));
					wl.setElement(d, swap);
				}
			}
		}
		return wl;
	}

	public WeightList Merge(int index, WeightList wl) {//merge e ordinamento tra la fingerprint corrente nella weightList e l'ultima scansione
		Scan s = history.get(index);
		WeightList wl1 = new WeightList();
		//merge
		for (int a = 0; a < s.size(); a++) {//mette nella lista tutti i mac della scansione
			wl1.add(s.getAp(a).getMac(), (double) 0);
		}
		for (int c = 0; c < wl.size(); c++) {
			if (Functions.GetI(wl1.stringList(), wl.getElement(c).getString()) == -1) {//se nella lista non c'è un mac
				wl1.add(wl.getElement(c).getString(), (double) 0);//nella fingerprint corrente lo aggiunge
			}
		}
		//sort
		StringWeight swap;
		for (int k = 0; k < wl1.size(); k++) {//per tutti i mac aggiunti nella weight list
			double weight = 0;
			int pos1 = Functions.GetI(stringList(index), wl1.getElement(k).getString());//trova la posizione nella scansione
			int pos2 = Functions.GetI(wl.stringList(), wl1.getElement(k).getString());//trova la posizione nella fingerprint precedente
			if (pos1 != -1 && pos2 != -1) {//se il mac è in entrambe
				pos1++;
				pos2++;
				weight = Functions.weight(pos1, pos2);//calcola il peso
				wl1.getElement(k).setWeight(weight);
			}
			else {//altrimenti
				wl1.getElement(k).setWeight(0);// è zero
			}

		}
		for (int m = 0; m < wl1.size(); m++) {// per tutti i mac della fingerprint nella weightlist
			if (Functions.GetI(wl.stringList(), wl1.getElement(m).getString()) != -1) {// se è nella weightlist precedente
				int in = Functions.GetI(wl.stringList(), wl1.getElement(m).getString());//lo trova
				wl1.getElement(m).setWeight((wl1.getElement(m).getWeight() + (index -1) * wl.getElement(in).getWeight())
						/ (double) (index)); //mette il peso del mac della fingerprint corrente uguale alla media di quello calcolato e di
			}// quello precedente
			else {// se non è nella weight list di prima il peso è zero
				wl1.getElement(m).setWeight(0);
			}
		}
		for (int l = 0; l < wl1.size(); l++){
			for (int d = l; d < wl1.size(); d++) {
				{
					if (wl1.getElement(l).getWeight() < wl1.getElement(d).getWeight()) {//ordina la weight list
						swap = wl1.getElement(l);
						wl1.setElement(l, wl1.getWeightsList().get(d));
						wl1.setElement(d, swap);
					}
				}
			}
		}
		return wl1;
	}
}

