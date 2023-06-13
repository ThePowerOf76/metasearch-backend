package com.thepowerof76.metasearch_backend.util;

import com.thepowerof76.metasearch_backend.util.extractor.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.commons.jcs3.JCS;
import org.apache.commons.jcs3.access.CacheAccess;
import org.apache.commons.jcs3.access.exception.CacheException;
public class ResultGetter {
    private CacheAccess<String, ArrayList<MetaResult>> cache;
    private final GoogleExtractor g = new GoogleExtractor();
    private final BingExtractor b = new BingExtractor();
    private final DDGExtractor d = new DDGExtractor();
    private final ArchiveExtractor a = new ArchiveExtractor();
    private final BaseExtractor bs = new BaseExtractor();
    private final WibyExtractor w = new WibyExtractor();
    public ResultGetter() {
        try
        {
            cache = JCS.getInstance( "default" );
        }
        catch ( CacheException e )
        {
            System.out.printf("Problem initializing cache: %s%n", e.getMessage() );
        }
    }


    public ArrayList<MetaResult> search(String query, int page) throws InterruptedException, ExecutionException {
        ArrayList<MetaResult> meta = cache.get(query+page);
        if(meta != null) {
            return meta;
        }
        List<ExtractorThread> threadExecutors = new ArrayList<>();
        threadExecutors.add(new ExtractorThread(g));
        threadExecutors.add(new ExtractorThread(b));
        threadExecutors.add(new ExtractorThread(d));
        threadExecutors.add(new ExtractorThread(a));
        threadExecutors.add(new ExtractorThread(bs));
        threadExecutors.add(new ExtractorThread(w));
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (ExtractorThread threadExecutor : threadExecutors) {
            threadExecutor.setQueryParams(query, page);
        }
        List<Future<ArrayList<Result>>> futures = executor.invokeAll(threadExecutors);
        meta = ResultMixin.mixResults(futures.get(0).get(), futures.get(1).get(), futures.get(2).get(), futures.get(3).get(), futures.get(4).get(), futures.get(5).get());
        cache.put(query+page, meta);
        return meta;
    }
}
