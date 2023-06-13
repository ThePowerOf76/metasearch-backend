package com.thepowerof76.metasearch_backend.util;

import java.util.*;

import static java.lang.Math.min;
public class ResultMixin {
    private static final float percentageFail = 0.2f;
    private static int LevenshteinDistance(String title1, String title2) {
        int sizea = title1.length() + 1;
        int sizeb = title2.length() + 1;
        int[][] matrix = new int[sizea][sizeb];
        for(int[] row: matrix) {
            Arrays.fill(row, 0);
        }
        for(int cnt = 0; cnt < sizea; cnt++) {
            matrix[cnt][0] = cnt;
        }
        for(int cnt = 0; cnt < sizeb; cnt++) {
            matrix[0][cnt] = cnt;
        }
        for(int i = 1; i < sizea; i++) {
            for(int j = 1; j < sizeb; j++) {
                if(title1.charAt(i-1) == title2.charAt(j-1)) {
                    matrix[i][j] = min(matrix[i-1][j-1], min(matrix[i-1][j]+1,matrix[i][j-1]+1));
                } else {
                    matrix[i][j] = min(matrix[i-1][j]+1, min(matrix[i-1][j-1]+1, matrix[i][j-1]+1));
                }
            }
        }
        return matrix[sizea-1][sizeb-1];

    }
    private static boolean titlesComparable(String title1, String title2) {
        int distance = LevenshteinDistance(title1, title2);
        if(title1.length() < title2.length()) {
            return (((float)distance)/title1.length()) <= percentageFail;
        } else {
            return (((float)distance)/title2.length()) <= percentageFail;
        }
    }
    private static Pair<Boolean, Integer> containsSimilar(ArrayList<MetaResult> m, Result r) {

        int iter = 0;
        MetaResult tmp;
        Pair<Boolean, Integer> found = new Pair<>(true, null);
        while(iter < m.size() && found.first) {
            tmp = m.get(iter);
            if(tmp.getHref().equals(r.getHref()) || titlesComparable(tmp.getTitle(), r.getTitle())){
                found.first = false;
                found.second = iter;
            }
            iter++;
        }
        return found;
    }
    private static void updateSource(ArrayList<MetaResult> metaResults, ArrayList<Result> results, int engineID) {
        Pair<Boolean, Integer> pair;
        for(Result r: results) {
            pair = containsSimilar(metaResults, r);
            if(!pair.first) {
                metaResults.get(pair.second).foundInSource(engineID);
            } else {
                metaResults.add(new MetaResult(r.getTitle(), r.getHref(), r.getDesc(), engineID));
            }
        }
    }
    public static ArrayList<MetaResult> mixResults(ArrayList<Result> google, ArrayList<Result> bing, ArrayList<Result> ddg, ArrayList<Result> archive, ArrayList<Result> base, ArrayList<Result> wiby) {
        ArrayList<MetaResult> metaResults = new ArrayList<>();
        System.out.println("Parsing Google");
        for(Result r: google) {
            metaResults.add(new MetaResult(r.getTitle(), r.getHref(), r.getDesc(), 1));
        }
        System.out.println("Parsing Bing");
        updateSource(metaResults, bing, 2);
        System.out.println("Parsing DDG");
        updateSource(metaResults, ddg, 3);
        System.out.println("Parsing Archive");
        updateSource(metaResults, archive, 4);
        System.out.println("Parsing Base");
        updateSource(metaResults, base, 5);
        System.out.println("Parsing Wiby");
        updateSource(metaResults, wiby, 6);
        System.out.println("Shuffling");
        Collections.shuffle(metaResults, new Random(81));
        return metaResults;
    }
}
