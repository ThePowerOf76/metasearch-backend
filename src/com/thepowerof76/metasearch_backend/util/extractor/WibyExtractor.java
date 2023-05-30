package com.thepowerof76.metasearch_backend.util.extractor;

import com.thepowerof76.metasearch_backend.util.Extractor;
import com.thepowerof76.metasearch_backend.util.Result;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;
import java.io.IOException;
import java.util.ArrayList;

public class WibyExtractor extends Extractor {
    @Override
    public ArrayList<Result> searchQuery(String query, int page) {
        Document doc;
        ArrayList<Result> results = new ArrayList<>();
        try {
            doc = Jsoup.connect("http://wiby.me/?q=" + query + "&p=" + page).get();
            Elements links = doc.getElementsByTag("blockquote");
            for (Element res : links) {
                if(!res.getElementsByClass("more").isEmpty()) {
                    continue;
                }
                Element link = res.getElementsByClass("tlink").first();
                String href = link.attr("href");
                String title = link.text();
                String desc = res.child(3).text();
                results.add(new Result(title, href, desc, 6));
            }

        } catch (IOException e) {
            System.out.println("Website error: ");
            e.printStackTrace();
        }
        return results;
    }
}
