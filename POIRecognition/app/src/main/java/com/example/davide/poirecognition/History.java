package com.example.davide.poirecognition;

import java.util.ArrayList;


public class History {

    private ArrayList<Scan> history;

    public ArrayList<Scan> getHistory() {
        return history;
    }

    public void setHistory(ArrayList<Scan> history) {
        this.history = history;
    }

    public History()
    {
        this.history = new ArrayList<Scan>();
    }

    public void Add(Scan scan)
    {
        history.add(scan);

    }
    public int Size()
    {
        return history.size();
    }
    public void SetIndex(int index, Scan scan)
    {
        history.set(index, scan);
    }
    public Scan GetIndex(int index)
    {
        return history.get(index);
    }
}
