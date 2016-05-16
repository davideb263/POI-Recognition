package com.example.davide.poirecognition;

import java.util.ArrayList;


public class Scan {

    private ArrayList<AccessPoint> scan;

    public ArrayList<AccessPoint> getScan() {
        return scan;
    }

    public void setScan(ArrayList<AccessPoint> scan) {
        this.scan = scan;
    }

    public Scan(){
        this.scan = new ArrayList<AccessPoint>();
    }

    public void Add(AccessPoint ap){
        scan.add(ap);
    }

    public int Size()
    {
        return scan.size();
    }
    public AccessPoint GetIndex(int index)
    {
        return scan.get(index);
    }
    public void SetIndex(int index, AccessPoint ap)
    {
        scan.set(index, ap);
    }
}

