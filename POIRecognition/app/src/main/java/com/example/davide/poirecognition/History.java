package com.example.davide.poirecognition;

import java.util.ArrayList;

public class History {

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

    public void Add(Scan scan) {
        history.add(scan);

    }

    public int Size() {
        return history.size();
    }

    public int GetI(int index, String mac) {
        boolean isPresent = false;
        Scan listAccess = history.get(index);
        int pos = 0;
        for (int a = 0; a < listAccess.Size(); a++) {
            if (listAccess.GetIndex(a).getMac().compareTo(mac) == 0) {
                pos = a;
                isPresent = true;
            }

        }
        if (isPresent)
            return pos;
        else
            return -1;
    }

    public WeightList firstMerge(int i, int j) {
        Scan list1 = history.get(i);
        Scan list2 = history.get(j);
        WeightList wl = new WeightList();
        for (int a = 0; a < list1.Size(); a++) {
            wl.Add(list1.GetIndex(a).getMac(), (double) 0);
        }
        for (int c = 0; c < list2.Size(); c++) {
            if (wl.GetI(list2.GetIndex(c).getMac()) == -1) {
                wl.Add(list2.GetIndex(c).getMac(), (double) 0);
            }
        }
        apWeight swap;
        for (int k = 0; k < wl.Size(); k++) {
            double weight = 0;
            int pos1 = GetI(i, wl.getWeightsList().get(k).getApMac());
            int pos2 = GetI(j, wl.getWeightsList().get(k).getApMac());
            if (pos1 != -1 && pos2 != -1) {
                pos1++;
                pos2++;
                weight = 1. / ((double) Math.abs(pos1 - pos2) + (double) (pos1 + pos2) / 2);
                wl.getWeightsList().get(k).setWeight(weight);
            } else {
                wl.getWeightsList().get(k).setWeight(0);
            }


        }

        for (int l = 0; l < wl.Size(); l++) {

            for (int d = l; d < wl.Size(); d++) {
                if (wl.getWeightsList().get(l).getWeight() < wl.getWeightsList().get(d).getWeight()) {
                    swap = wl.getWeightsList().get(l);
                    wl.getWeightsList().set(l, wl.getWeightsList().get(d));
                    wl.getWeightsList().set(d, swap);

                }
            }

        }
        return wl;
    }

    public WeightList Merge(int index, WeightList wl) {
        Scan s = history.get(index);
        WeightList wl1 = new WeightList();
        for (int a = 0; a < s.Size(); a++) {
            wl1.Add(s.GetIndex(a).getMac(), (double) 0);
        }
        for (int c = 0; c < wl.Size(); c++) {
            if (wl1.GetI(wl.getWeightsList().get(c).getApMac()) == -1) {
                wl1.Add(wl.getWeightsList().get(c).getApMac(), (double) 0);
            }
        }
        apWeight swap;
        for (int k = 0; k < wl1.Size(); k++) {
            double weight = 0;
            int pos1 = GetI(index, wl1.getWeightsList().get(k).getApMac());
            int pos2 = wl.GetI(wl1.getWeightsList().get(k).getApMac());
            if (pos1 != -1 && pos2 != -1) {
                pos1++;
                pos2++;
                weight = 1.0 / ((double) (Math.abs(pos1 - pos2) + (double) (pos1 + pos2) / 2));
                wl1.getWeightsList().get(k).setWeight(weight);
            } else {
                wl1.getWeightsList().get(k).setWeight(0);
            }

        }
        for (int m = 0; m < wl1.Size(); m++) {
            if (wl.GetI((wl1.getWeightsList().get(m).getApMac())) != -1) {
                int in = wl.GetI(wl1.getWeightsList().get(m).getApMac());
                wl1.getWeightsList().get(m).setWeight((wl1.getWeightsList().get(m).getWeight() + (index) * wl.getWeightsList().get(in).getWeight()) / (double) (index + 1));
            } else {
                wl1.getWeightsList().get(m).setWeight(0);
            }
        }
        for (int l = 0; l < wl1.Size(); l++)
            for (int d = l; d < wl1.Size(); d++) {

                {

                    if (wl1.getWeightsList().get(l).getWeight() < wl1.getWeightsList().get(d).getWeight()) {
                        swap = wl1.getWeightsList().get(l);
                        wl1.getWeightsList().set(l, wl1.getWeightsList().get(d));
                        wl1.getWeightsList().set(d, swap);

                    }

                }
            }
        return wl1;
    }
}
