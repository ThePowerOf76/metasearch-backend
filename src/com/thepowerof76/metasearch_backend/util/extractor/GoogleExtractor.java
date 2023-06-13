package com.thepowerof76.metasearch_backend.util.extractor;

import com.thepowerof76.metasearch_backend.util.Extractor;
import com.thepowerof76.metasearch_backend.util.Result;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

public class GoogleExtractor extends Extractor {

    @Override
    public ArrayList<Result> searchQuery(String query, int page) {
        Document doc;
        ArrayList<Result> results = new ArrayList<>();
        try {
            if(page != 0) {
                doc = Jsoup.connect("https://www.google.com/search?q=" + query + "&start=" + page*10).get();
            } else {
                doc = Jsoup.connect("https://www.google.com/search?q=" + query).get();
            }
            Element rso = doc.getElementById("rso");
            if(rso == null) {
                return results;
            }
            for(Element res: rso.children()) {
                if(res.attr("class").equals("ULSxyf")) {
                    continue;
                }
                Element href_e = res.getElementsByClass("yuRUbf").first();
                if(href_e == null) {
                    continue;
                }
                String href = href_e.child(0).attr("href");
                String title = href_e.child(0).child(1).text();
                Element parent = href_e.parent();
                if(parent == null) {
                    throw new IOException("Lack of parents");
                }
                Element grandparent = parent.parent();
                if(grandparent == null) {
                    throw new IOException("Lack of (grand)parents");
                }
                Element span = grandparent.getElementsByTag("span").last();
                if(span == null) {
                    throw new IOException("Span missing");
                }
                String desc = span.text();
                results.add(new Result(title, href, desc, 1));

            }
        } catch (IOException e) {
            System.out.println("Website error: ");
            e.printStackTrace();
        }
        System.out.println("Retrieved Google");
        return results;
    }
}
