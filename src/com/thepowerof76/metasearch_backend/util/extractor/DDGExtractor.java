package com.thepowerof76.metasearch_backend.util.extractor;

import com.thepowerof76.metasearch_backend.util.Result;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.thepowerof76.metasearch_backend.util.Extractor;
import java.io.IOException;
import java.util.ArrayList;

public class DDGExtractor extends Extractor {
    private String vqd_key;
    @Override
    public ArrayList<Result> searchQuery(String query, int page) {
        Document doc;
        ArrayList<Result> results = new ArrayList<>();
        try {
            if(page != 0) {
                doc = Jsoup.connect("https://html.duckduckgo.com/html/?q=" + query +"&s=" + (page * 20 - 1
                ) + "&nextParams=&v=l&o=json&dc=" + (page * 20) + "&api=d.js&vqd=" + vqd_key + "&kl=wt-wt").get();
            } else {
                doc = Jsoup.connect("https://duckduckgo.com/html/?q=" + query).get();
                Element vqd = doc.getElementsByAttributeValue("name", "vqd").first();
                if(vqd != null) {
                    vqd_key = vqd.text();
                }

            }
            Element links = doc.getElementById("links");
            if(links == null) {
                return results;
            }
            Elements result_list = links.getElementsByClass("result results_links results_links_deep web-result ");
            for(Element res: result_list) {
                Element sep = res.child(0);
                String href = sep.child(0).child(0).attr("href");
                String title = sep.child(0).child(0).text();
                String desc = sep.child(2).text();
                results.add(new Result(title, href, desc, 3));
            }
        } catch (IOException e) {
            System.out.println("Website error: ");
            e.printStackTrace();
        }
        System.out.println("Retrieved DDG");
        return results;
    }
}
