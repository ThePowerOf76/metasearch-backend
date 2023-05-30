package com.thepowerof76.metasearch_backend.util.extractor;

import com.thepowerof76.metasearch_backend.util.Result;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import com.thepowerof76.metasearch_backend.util.Extractor;
public class BingExtractor extends Extractor{
    @Override
    public ArrayList<Result> searchQuery(String query, int page) {
        Document doc;
        ArrayList<Result> results = new ArrayList<>();
        try {
            if(page == 0) {
                doc = Jsoup.connect("https://www.bing.com/search?q=" + query + "&setlang=en&cc=gb").get();
            } else {
                doc = Jsoup.connect("https://www.bing.com/search?q=" + query + "&first=" + (page*10 + 1) + "&setlang=en&cc=gb").get();
            }

            Elements algo_elements = Objects.requireNonNull(doc.getElementById("b_results")).children();
            for(Element res: algo_elements) {
                if(res.attr("class").contains("b_algo")) {
                    Elements href_search = res.getElementsByTag("a");
                    Element href_e = href_search.first();
                    for(Element hreff: href_search) {
                        if(!hreff.text().isBlank()) {
                            href_e = hreff;
                            break;
                        }
                    }
                    if(href_e == null) {
                        throw new IOException("href_e empty");
                    }
                    String href = href_e.attr("href");
                    String title = href_e.text();
                    String desc = "";
                    Elements tmp = Objects.requireNonNull(res.getElementsByClass("b_caption").first()).getElementsByTag("p");
                    if (!tmp.isEmpty()) {
                        desc = Objects.requireNonNull(tmp.first()).text();
                    } else {
                        tmp = Objects.requireNonNull(res.getElementsByClass("b_caption").first()).getElementsByTag("ul");
                        if (!tmp.isEmpty()) {
                            desc = Objects.requireNonNull(tmp.first()).text();
                        } else {
                            tmp = res.getElementsByAttributeValueContaining("class", "description");
                            if (!tmp.isEmpty()) {
                                desc = Objects.requireNonNull(tmp.first()).child(1).text();
                            }
                        }
                    }
                    results.add(new Result(title, href, desc, 2));
                }
            }
        } catch (IOException e) {
            System.out.println("Website error: ");
            e.printStackTrace();
        }
        return results;
    }
}
