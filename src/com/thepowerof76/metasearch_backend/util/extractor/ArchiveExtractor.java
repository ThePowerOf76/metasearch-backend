package com.thepowerof76.metasearch_backend.util.extractor;

import com.fasterxml.jackson.core.*;
import com.thepowerof76.metasearch_backend.util.Result;
import java.io.IOException;
import com.thepowerof76.metasearch_backend.util.Pair;
import java.net.URL;
import java.util.ArrayList;

import com.thepowerof76.metasearch_backend.util.Extractor;
public class ArchiveExtractor extends Extractor {

    private ArrayList<Result> results;
    private String lastQuery;
    private final Pair<String, String> currCursor;
    private final Pair<Integer, Integer> currentHeldRange;

    public void getSearchResults(String query, String cursor, boolean forward) {
        int flag;
        results = new ArrayList<>();
        try {
            JsonFactory jasonFactory = new JsonFactory();
            URL archiveURL;
            if (cursor.equals("")) {
                archiveURL = new URL("https://archive.org/services/search/v1/scrape?fields=title,identifier,description&q=" + query + "&count=100");
            } else if (forward) {
                archiveURL = new URL("https://archive.org/services/search/v1/scrape?fields=title,identifier,description&q=" + query + "&count=100&cursor=" + cursor);
            } else {
                archiveURL = new URL("https://archive.org/services/search/v1/scrape?fields=title,identifier,description&q=" + query + "&count=100&previous=" + cursor);
            }
            try (JsonParser jsonParser = jasonFactory.createParser(archiveURL)) {
                StringBuilder desc;
                StringBuilder href;
                StringBuilder title;
                String txt;
                while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                    String fieldname = jsonParser.getCurrentName();
                    if ("items".equals(fieldname)) {
                        jsonParser.nextToken();
                        jsonParser.nextToken();
                        if(jsonParser.currentToken() == JsonToken.END_ARRAY) {
                            return;
                        }
                        while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                            href = new StringBuilder("https://archive.org/details/");
                            desc = new StringBuilder(title = new StringBuilder());
                            flag = 0;
                            jsonParser.nextToken();
                            while (jsonParser.getCurrentToken() != JsonToken.END_OBJECT) {
                                if (jsonParser.getCurrentToken() == JsonToken.FIELD_NAME) {
                                    if (jsonParser.getText().equals("description")) {
                                        flag = 1;
                                    } else if (jsonParser.getText().equals("title")) {
                                        flag = 2;
                                    }
                                } else {
                                    txt = jsonParser.getText();
                                    if (flag == 0) {
                                        href.append(txt);
                                    } else if (flag == 1) {
                                        desc.append(txt);
                                    } else {
                                        title.append(txt);
                                    }
                                }
                                jsonParser.nextToken();
                            }
                            results.add(new Result(title.toString(), href.toString(), desc.toString(), 4));
                        }
                    }
                    if ("cursor".equals(fieldname)) {
                        jsonParser.nextToken();
                        currCursor.first = jsonParser.getText();
                    }
                    if ("previous".equals(fieldname)) {
                        jsonParser.nextToken();
                        currCursor.second = jsonParser.getText();
                        System.out.println();
                    }
                    if ("count".equals(fieldname)) {
                        jsonParser.nextToken();
                        currentHeldRange.second = Integer.parseInt(jsonParser.getText());
                    }
                }
            }


        } catch (JsonParseException f) {
            System.out.println("Json Parsing Error");
            f.printStackTrace();
        } catch (IOException e) {
            System.out.println("Website error: ");
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Result> searchQuery(String query, int page) {
        if (lastQuery.equals(query)) {
            if (page * 10 <= currentHeldRange.first) {
                getSearchResults(query, currCursor.second, false);
                currentHeldRange.first = ((int) (page * 0.1)) * 100;
                currentHeldRange.second += ((int) (page * 0.1)) * 100;
            } else if (page * 10 >= currentHeldRange.second) {
                getSearchResults(query, currCursor.first, true);
                currentHeldRange.first = ((int) (page * 0.1)) * 100;
                currentHeldRange.second += ((int) (page * 0.1)) * 100;
            }
        } else {
            getSearchResults(query, "", true);
            currentHeldRange.first = 0;
            currentHeldRange.second = 100;
            lastQuery = query;
        }
        ArrayList<Result> tmp = new ArrayList<>();
        Result tmp_r;
        int index, l_bound, u_bound;
        for (int i = 0; i < 10 && ((page % 10) * 10 + i < results.size()); i++) {
            tmp_r = results.get((page % 10) * 10 + i);
            if(tmp_r.getDesc().length() > 50) {
                index = tmp_r.getDesc().indexOf(query);
                if(index == -1) {
                    index = tmp_r.getDesc().indexOf(query.toUpperCase());
                    if(index == -1) {
                        index = tmp_r.getDesc().indexOf(query.substring(0, 1).toUpperCase() + query.substring(1).toLowerCase());
                        if(index == -1) {
                            tmp_r.setDesc(tmp_r.getDesc().substring(0, 50));
                        } else {
                            l_bound = Math.max(index - 22, 0);
                            u_bound = Math.min(index + 22, tmp_r.getDesc().length());
                            tmp_r.setDesc((l_bound == 0 ? "..." : "") + tmp_r.getDesc().substring(l_bound, u_bound) + (u_bound == tmp_r.getDesc().length() ? "..." : ""));
                        }
                    } else {
                        l_bound = Math.max(index - 22, 0);
                        u_bound = Math.min(index + 22, tmp_r.getDesc().length());
                        tmp_r.setDesc((l_bound == 0 ? "..." : "") + tmp_r.getDesc().substring(l_bound, u_bound) + (u_bound == tmp_r.getDesc().length() ? "..." : ""));
                    }
                } else {
                    l_bound = Math.max(index - 22, 0);
                    u_bound = Math.min(index + 22, tmp_r.getDesc().length());
                    tmp_r.setDesc((l_bound == 0 ? "..." : "") + tmp_r.getDesc().substring(l_bound, u_bound) + (u_bound == tmp_r.getDesc().length() ? "..." : ""));
                }
            }
            tmp.add(tmp_r);
        }

        System.out.println("Retrieved Archive");
        return tmp;
    }
    public ArchiveExtractor() {
            lastQuery = "";
            currCursor = new Pair<>("", "");
            currentHeldRange = new Pair<>(-1, -1);
        }
    }

