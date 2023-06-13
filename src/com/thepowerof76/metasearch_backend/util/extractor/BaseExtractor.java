package com.thepowerof76.metasearch_backend.util.extractor;

import com.thepowerof76.metasearch_backend.util.Extractor;
import com.thepowerof76.metasearch_backend.util.Result;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;


public class BaseExtractor extends Extractor {
    @Override
    public ArrayList<Result> searchQuery(String query, int page) {
        Document doc;
        ArrayList<Result> results = new ArrayList<>();
        try {
            if (page != 0) {
                doc = Jsoup.connect("https://www.base-search.net/Search/Results?lookfor="+ query + "&page=" + page + "&oaboost=1").get();
            } else {
                doc = Jsoup.connect("https://www.base-search.net/Search/Results?lookfor=" + query + "&oaboost=1").get();
            }
            Element hit_list = doc.getElementById("hit-list");
            if(hit_list == null) {
                return results;
            }
            Elements records = hit_list.getElementsByAttributeValue("class", "record-panel panel panel-default");
            if(records.isEmpty()) {
                return results;
            }
            for(Element res: records) {
                Element link_e = res.child(0).getElementsByAttributeValueContaining("class", "link1").first();
                if(link_e == null) {
                     continue;
                }
                String href = link_e.attr("href");
                String title = link_e.text();
                String desc = res.child(1).child(0).child(1).text();
                results.add(new Result(title, href, desc, 5));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Retrieved Base");
        return results;
    }
}
