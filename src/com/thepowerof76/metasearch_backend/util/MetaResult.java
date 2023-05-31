package com.thepowerof76.metasearch_backend.util;

import java.util.Arrays;
public class MetaResult extends Result {
    private final boolean[] resultSrc = new boolean[6];

    public MetaResult(String t, String h, String d, int id) {
        super(t, h, d, id);
        Arrays.fill(resultSrc, false);
        resultSrc[id-1] = true;
    }
    public boolean[] getSources() {
        return resultSrc;
    }
    public void foundInSource(int sourceID) {
        if(sourceID > 0 && sourceID < 7) {
            resultSrc[sourceID-1] = true;
        } else {
            System.out.println("Wrong engine ID");
        }
    }
}

