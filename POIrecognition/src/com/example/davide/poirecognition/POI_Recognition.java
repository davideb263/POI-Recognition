package com.example.davide.poirecognition;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class POI_Recognition extends Activity {

	private final static String TAG = "Recognition";
	private TextView poiSL = null;
	private Button bttback = null;
	private Button bttRec = null;
	private Button bttTrain = null;
	private TextView fingerPrintTV = null;
	private ProgressBar progressB = null;

	private List<String> fpDataBase = null;// file
	private List<String> fpList = null;// contenuti file
	private ArrayList<Double> matchList = null;// pesi dei luoghi nello stesso
												// ordine dei file
	private WeightList recPlaces = null;// posti e pesi
	private HashMap<String, Integer> stayLength = null; // posti e sl
	private List<ScanResult> results = null;// risultato da getScan
	private ArrayList<String> recScan = null;// mac della scan

	private BroadcastReceiver broadcastReceiver;
	private TimerTask timerTask;
	private Timer timer;
	private int count = 0;
	private int countNoPlace = 0;// quante volte il posto non è stato
									// riconosciuto
	private final int SCANNUMBER = 60;// numero di scansioni in totale
	private final int SCANLENGHT = 5;// ogni quanto fa la scansione in secondi
	private final int SCANWINDOW = 6;// quante in una finestra
	private String placeOrNotPlace = "";
	private String place = "";// posto precedente
	private WifiManager wifi;
	private double accuracy = 0;// percentuale del peso del posto sul totale dei
								// pesi di tutti i posti possibili
	private double[][] weightMatrix;// matrice con colonne posti salvati e righe
									// numero di scansioni per realizzare la
									// finestra
	private ArrayList<ArrayList<String>> apListAll;// liste ap dei contenuti
													// delle finger print
													// salvate nella memoria

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.poi_recognition);
		poiSL = (TextView) findViewById(R.id.poiSL);
		bttback = (Button) findViewById(R.id.BackToMain);
		bttRec = (Button) findViewById(R.id.Recognition);
		bttTrain = (Button) findViewById(R.id.bttTrain);
		progressB = (ProgressBar) findViewById(R.id.recProgress);
		fingerPrintTV = (TextView) findViewById(R.id.placeTv);
		fpDataBase = new ArrayList<String>(); // lista dei percorsi nella
												// cartella
		fpList = new ArrayList<String>(); // lista contenuto fingerprint
		recScan = new ArrayList<String>(); // lista di access point della
											// recognition da scan
		apListAll = new ArrayList<ArrayList<String>>();// liste di contenuti di
														// access points
		recPlaces = new WeightList();// lista di posti riconosciuti e pesi
		matchList = new ArrayList<Double>();// lista dei pesi
		stayLength = new HashMap<String, Integer>();// posti e sl
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		getIntent();

		poiSL.setMovementMethod(new ScrollingMovementMethod());

		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context c, Intent intent) {

				fingerPrintTV.setText("Recognition... Scan n°:" + count);
				placeOrNotPlace = "";
				Log.i(TAG, "onReceive");
				if (!wifi.isWifiEnabled()) {
					Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG)
							.show();
					wifi.setWifiEnabled(true);
				}
				results = wifi.getScanResults();
				Log.i(TAG, "scan");
				recScan.clear();// list con i mac la svuoto
				ScanResult swap;
				for (int a = 0; a < results.size(); a++) {// sort la lista dei
															// mac letta in base
															// alla potenza
					for (int b = a; b < results.size(); b++) {
						if (results.get(a).level < results.get(b).level) {
							swap = results.get(a);
							results.set(a, results.get(b));
							results.set(b, swap);
						}
					}
				}
				for (int j = 0; j < results.size(); j++) {
					Log.i(TAG, results.get(j).BSSID + " " + results.get(j).level);
					recScan.add(results.get(j).BSSID);// aggiunge ogni elemento
														// della lista a recScan
				}
				if (count <= SCANWINDOW) {// riempie weight matrix
					allPlaceScore(recScan, count - 1);// confronta la lista di
														// mac con fp e calcola
														// i pesi match per riga
				}
				else if (count > SCANWINDOW) {// shift
					for (int a = 0; a < weightMatrix.length - 1; a++) {// numero di righe meno uno
						for (int b = 0; b < weightMatrix[0].length; b++) {
							weightMatrix[a][b] = weightMatrix[a + 1][b];// il peso più in alto diventa quello successivo
						}
					}
					allPlaceScore(recScan, weightMatrix.length - 1);// calcola i pesi match dell'ultima riga
				}
				
				
				
				if (count >= SCANWINDOW) {
					weightAverage();// fa la media
					Double sum = 0d;
					Integer sl = 0;
					placeScoreSort();
					filter();// toglie i posti con i match più bassi di 0.1
					for (int a = 0; a < matchList.size(); a++) {// somma dei pesi dei posti possibili
						sum += matchList.get(a);
					}
					if (recPlaces.getElement(0).getWeight() > 40 * sum / 100) {// se il match del posto riconosciuto
																				//è >40 percento del totale
						accuracy = recPlaces.getElement(0).getWeight() * 100 / sum;// percentuale del
																					// match
						bttTrain.setVisibility(View.INVISIBLE);// bottone diventa invisibile
						if (count == SCANWINDOW) {// se il numero è uguale alle scansioni per una finestra
							sl = SCANLENGHT * SCANWINDOW;// la sl è il numero di scansioni per la finestra * la durata	
						}
						else if (count > SCANWINDOW) {// altrimenti aumenta la
														// sl del posto giusto
							Log.i(TAG, place + "\n" + recPlaces.getElement(0).getString());
							sl = incrementStayLength();
						}
						place = recPlaces.getElement(0).getString();// il posto è quello con il
																		// match
																		// maggiore
						stayLength.put(recPlaces.getElement(0).getString(), sl);//da la corretta sl al posto
						String recPlaceAccuracy = "You are here: " + recPlaces.getElement(0).getString();
						recPlaceAccuracy += " accuracy: " + accuracy + "%";
						Toast.makeText(getApplicationContext(), recPlaceAccuracy, Toast.LENGTH_SHORT).show();

					}
					else {//se non ha la percentuale dei pesi
						if (count == SCANWINDOW) {// se il contatore ha raggiunto la durata della window
							countNoPlace = SCANWINDOW;//il contatore di no place è uguale a scanwindow
						}
						else {
							countNoPlace++;//lo incremento
						}
						Log.i(TAG, "no place");
						placeOrNotPlace = "<b>No recognized" + "</b>" + "<br> ";
						if (countNoPlace > SCANWINDOW) {// se count no place è maggiore del numero di scnasioni per finestra
							Toast.makeText(getApplicationContext(), "Press train to do training", Toast.LENGTH_SHORT).show();
							bttTrain.setVisibility(View.VISIBLE);//training
						}
					}
					placeOrNotPlace += weightPlaceList();//aggiunge alla stringa i posti con la stay length
					poiSL.setText(Html.fromHtml(placeOrNotPlace));
					matchList.clear();
					recPlaces.getWeightsList().clear();
				}
				if (count == SCANNUMBER)// se ha finito toglie la progressbar
					progressB.setVisibility(View.INVISIBLE);
			}

		};
		registerReceiver(broadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		bttTrain.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent("com.example.davide.training");//parte il training
				startActivity(i);
				finish();
			}
		});

		bttback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Log.i(TAG, "Recognition callback to main Activity");
				recPlaces.getWeightsList().clear();
				recScan.clear();
				matchList.clear();
				stayLength.clear();
				if (weightMatrix != null) {
					for (int l = 0; l < weightMatrix.length; l++) {
						for (int m = 0; m < weightMatrix[0].length; m++) {
							weightMatrix[l][m] = 0;
						}
					}
				}
				if (timer != null)
					timer.cancel();
				count = 0;
				fpList.clear();
				fpDataBase.clear();
				apListAll.clear();
				setResult(Activity.RESULT_OK, new Intent());
				finish();
			}
		});

		bttRec.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Button Pressed. Please wait..", Toast.LENGTH_SHORT).show();
				recPlaces.getWeightsList().clear();
				recScan.clear();
				matchList.clear();
				stayLength.clear();
				stayLenghtInit();
				if (weightMatrix != null) {
					for (int l = 0; l < weightMatrix.length; l++) {
						for (int m = 0; m < weightMatrix[0].length; m++) {
							weightMatrix[l][m] = 0;
						}
					}
				}
				count = 0;
				fpList.clear();
				fpDataBase.clear();
				apListAll.clear();
				if (timer != null) {
					timer.cancel();
				}
				progressB.setVisibility(View.VISIBLE);
				fingerPrintTV.setText("Recognition...");

				// creo una lista con i percorsi dei file contenenti le varie
				// fingerprint.
				File fpDirectory = new File(Environment.getExternalStorageDirectory() + "/POI_Fingerprints");
				File[] fpFiles = fpDirectory.listFiles();
				weightMatrix = new double[SCANWINDOW][fpFiles.length];//la matrice ha colonne uguali al numero di POI salvati sulla memoria interna
																	// e righe uguali al numero di scansioni per una finestra
				for (int i = 0; i < fpFiles.length; i++) {
					fpDataBase.add(fpFiles[i].getAbsolutePath());//lista dei file
					Log.i(TAG, fpFiles[i].getAbsolutePath());
				}
				apFromString();//legge i file in stringhe e mette gli ap delle fingerprint in liste
				stayLenghtInit();//fa una hash map dei posti salvati con stay length zero
				timer = new Timer();
				timerTask = new TimerTask() {
					@Override
					public void run() {
						count++;//incrementa il contatore
						wifi.startScan();
						if (count == SCANNUMBER) {
							timer.cancel();
						}
					}
				};
				timer.schedule(timerTask, 0, SCANLENGHT * 1000);
			}
		});
	}

	public void allPlaceScore(ArrayList<String> scanList, int index) {
		Log.i(TAG, "allPlaceScore ap list size" + String.valueOf(apListAll.size()));// quante
																					// fingerprint
																					// cartella
		for (int j = 0; j < apListAll.size(); j++) { // per ogni fingerprint
			weightMatrix[index][j] = singlePlaceScore(scanList, apListAll.get(j));// calcola match tra fingerprint e scansione
		}
		Log.i(TAG, "count " + String.valueOf(count));
	}

	public void apFromString() {
		for (int i = 0; i < fpDataBase.size(); i++) {
			fpList.add(getStringFromFile(fpDataBase.get(i)));// legge il contenuto del file in una striga e la mette nella lista
		}
		for (int j = 0; j < fpList.size(); j++) { // per ogni stringa
			Log.i(TAG, "Lista");
			String[] parts = fpList.get(j).split("\n");//divide in mac
			apListAll.add(new ArrayList<String>());// aggiunge una lista
			for (int a = 0; a < parts.length; a++) { // crea una lista di access
														// point di un place
				apListAll.get(apListAll.size() - 1).add(parts[a]);//aggiunge i mac
			}
		}
	}

	public double singlePlaceScore(ArrayList<String> scan, ArrayList<String> place) {//match del posto
		Log.i(TAG, "singlePlaceScore");
		double sumWeight = 0;
		ArrayList<String> commonAP = new ArrayList<String>();
		for (int i = 0; i < scan.size(); i++) {//per ogni mac della scansione
			if (place.contains(scan.get(i))) {//se la lista di mac del posto contiene il mac aggiunge il posto alla lista e
				commonAP.add(scan.get(i));// trova gli ap in comune
			}
		}
		for (int j = 0; j < commonAP.size(); j++) {//per ogni mac in comune
			int pos1 = Functions.GetI(scan, commonAP.get(j));//trova la posizione nella scansione
			int pos2 = Functions.GetI(place, commonAP.get(j));//trova la posizione nella fingerprint
			if (pos1 != -1 && pos2 != -1) {
				pos1++;
				pos2++;
				sumWeight += Functions.weight(pos1, pos2);//somma il peso
				Log.i(TAG, "weight add" + String.valueOf(sumWeight));
			}
		}
		return sumWeight;
	}

	public void weightAverage() {// mean dei pesi della finestra

		for (int i = 0; i < weightMatrix[0].length; i++) {// ciclando sui place
			Double d = 0d;
			for (int j = 0; j < weightMatrix.length; j++) {// per ogni place fa
															// la somma dei pesi della
															// colonna, delle diverse scans
				d += weightMatrix[j][i];
			}
			matchList.add(d / SCANWINDOW);//aggiunge alla lista dei pesi delle finger print l'average
			Log.i(TAG, "average" + String.valueOf(d / SCANWINDOW));
		}
	}

	private void placeScoreSort() {//ordina i posti con i pesi maggiori
		Log.i(TAG, "placeScore");
		String place;
		StringWeight tmp;
		for (int i = 0; i < matchList.size(); i++){// i pesi sono nello stesso
													// ordine della folder
		
			place = fpDataBase.get(i);
			place = place.substring(place.lastIndexOf("/") + 1);
			place = place.substring(0, place.indexOf("."));
			recPlaces.add(place, matchList.get(i));// posto con peso
		}
		// sort
		for (int c = 0; c < recPlaces.size(); c++) {
			for (int d = c; d < recPlaces.size(); d++) {
				if (recPlaces.getElement(c).getWeight() < recPlaces.getElement(d).getWeight()) {
					tmp = recPlaces.getElement(c);
					recPlaces.setElement(c, recPlaces.getElement(d));
					recPlaces.setElement(d, tmp);
				}
			}
		}
	}

	private void stayLenghtInit() {
		Log.i(TAG, "stayLenght");
		String place;
		for (int i = 0; i < fpDataBase.size(); i++) {//per ogni POI
			place = fpDataBase.get(i);
			place = place.substring(place.lastIndexOf("/") + 1);
			place = place.substring(0, place.indexOf("."));//trova il nome del posto
			stayLength.put(place, 0);//aggiunge alla hash table il nome e stay length a zero
		}
	}

	public Integer incrementStayLength() {
		Integer sl = 0;
		Log.i(TAG, recPlaces.getElement(0).getString() + "" + place + "increment stay length");
		Log.i(TAG, place.compareTo(recPlaces.getElement(0).getString()) + " ");
		//se il posto è il precedente e non mi sono spostato
		if (place.compareTo(recPlaces.getElement(0).getString()) == 0 && countNoPlace == 0) {
			sl = (Integer) stayLength.get(recPlaces.getElement(0).getString()) + SCANLENGHT;
		}
		else {
			stayLength.put(recPlaces.getElement(0).getString(), 0);//altrimenti azzero la sl del posto dove sono e la metto uguale alla scanlength
			sl = SCANLENGHT;
			countNoPlace = 0;
		}
		return sl;
	}
	//rimuove i pesi minori della soglia dalla lista dei posti con i pesi
	public void filter() {
		double threshold = 0.1d;
		for (int i = 0; i < recPlaces.size(); i++) {
			Log.i(TAG, "filtro");
			if (recPlaces.getElement(i).getWeight() < threshold) {
				Log.i(TAG, "to remove" + recPlaces.getElement(i).getWeight());
				recPlaces.getWeightsList().remove(recPlaces.getElement(i));
				i = 0;
			}
		}
	}

	private String weightPlaceList() {
		String _s = "";
		Log.i(TAG, "print place + stay length");
		Set<Entry<String, Integer>> entries = stayLength.entrySet();

		for (Entry<String, Integer> entry : entries) {
			String key = entry.getKey().toString();
			Integer value = entry.getValue();
			if (value > 0) {
				_s += "<b>Place :" + key + " " + "Stay Lenght: " + value + "</b>" + "<br>";
				//stampa in grassetto i posti con la stay length maggiore di 0
			}

		}
		_s += "<br>";//va a capo
		//elenco degli altri posti salvati
		for (Entry<String, Integer> entry : entries) {
			Integer v = entry.getValue();
			String key = entry.getKey();
			if (v == 0) {
				_s += "Possible place: " + key + "<br>";
			}
		}
		return _s;
	}

	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
		}
		catch (IOException e) {
			Log.e(TAG, "can't read file");
		}
		try {
			reader.close();
		}
		catch (IOException e) {
			Log.e(TAG, "not close");
		}
		return sb.toString();
	}

	public static String getStringFromFile(String filePath) {
		File fl = new File(filePath);
		FileInputStream fin = null;
		String ret = "";
		try {
			fin = new FileInputStream(fl);
			ret = convertStreamToString(fin);
			fin.close();
			// Make sure you close all streams.
		}
		catch (FileNotFoundException e) {
			Log.e(TAG, e.getMessage());
		}
		catch (IOException e) {
			Log.e(TAG, "can't close");
		}
		return ret;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "closing train");
		if (broadcastReceiver != null) {
			unregisterReceiver(broadcastReceiver);
		}
		if (timer != null)
			timer.cancel();
	}
}
