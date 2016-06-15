package com.example.davide.poirecognition;


import java.util.List;

public class Functions {


    public static int GetI(List<String> l, String mac){
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


}
