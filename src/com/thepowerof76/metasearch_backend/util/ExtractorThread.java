package com.thepowerof76.metasearch_backend.util;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class ExtractorThread implements Callable<ArrayList<Result>> {

    private final Extractor ex;

    private String q;
    private int p;

    public void setQueryParams(String query, int page) {
        q = query;
        p = page;
    }
    public ExtractorThread(Extractor e) {
        ex = e;
    }

    @Override
    public ArrayList<Result> call() {
        return ex.searchQuery(q, p);
    }
}
