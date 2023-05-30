package com.thepowerof76.metasearch_backend.util;

import java.util.ArrayList;

public abstract class Extractor {
    public abstract ArrayList<Result> searchQuery(String query, int page);
}
