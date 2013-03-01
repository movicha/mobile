package com.clouidio.mobileasync;

import android.os.AsyncTask;

import java.io.InputStream;
import java.net.URLConnection;
import java.util.*;

public class MobileAsync {
    private static final int MAX_CONCURRENT_TASKS = 15;
    
    private static MobileAsync instance = null;      
    
    private Map<ConnectionBuilder, MobileAsyncTask<? extends Object>> reqs
            = new HashMap<ConnectionBuilder, MobileAsyncTask<? extends Object>>();
    private Map<String, Set<ConnectionBuilder>> taggedReqs
            = new HashMap<String, Set<ConnectionBuilder>>(); 
    private LinkedList<ConnectionBuilder> reqsQueue
            = new LinkedList<ConnectionBuilder>();
    private Map<String, Object> cache = new HashMap<String, Object>();
    
    private MobileAsync() {}

    private synchronized void startRequest(ConnectionBuilder req) {
        MobileAsyncTask<? extends Object> task = reqs.get(req);
        if (checkChache(req, task.listener)) {
            requestFinished(req, task);
            return;
        }
        task.active = true;
        task.execute();
    }
    
    private synchronized void enqueueRequest(ConnectionBuilder req) {
        reqsQueue.add(req);
    }
    
    private synchronized ConnectionBuilder dequeueRequest() {
        return reqsQueue.removeFirst();
    }
    
    private synchronized void requestFinished(ConnectionBuilder req, MobileAsyncTask<?> task) {
        reqs.remove(req);
        taggedReqs.get(task.getTag()).remove(req);
        if (reqs.size() - reqsQueue.size() < MAX_CONCURRENT_TASKS
                && reqsQueue.size() > 0)
            startRequest(dequeueRequest());
    }
    
    @SuppressWarnings("unchecked")
    private synchronized <T> boolean checkChache(ConnectionBuilder req, MobileAsyncTaskListener<T> listener) {
        if (req.isUseCaches() == false)
            return false;
        Object result = cache.get(req.getUrl());
        if (result == null)
            return false;
        try {
            listener.onSuccess((T)result);
            return true;
        }
        catch (ClassCastException ex) {
            return false;
        }
    }
    
    private synchronized void addToCache(String url, Object object) {
        cache.put(url, object);
    }
    
    // Public interface
    
    public static synchronized MobileAsync instance()  {
        if (instance == null)
            instance = new MobileAsync();
        return instance;
    }
    
    public <T> ConnectionBuilder addRequest(String tag, String url,
            MobileAsyncTaskListener<T> listener, IStreamParser<T> streamParser) {
        return addRequest(tag, url, listener, streamParser, false);
    }
    
    public <T> ConnectionBuilder addRequest(String tag,
            ConnectionBuilder req, MobileAsyncTaskListener<T> listener,
            IStreamParser<T> streamParser) {
        return addRequest(tag, req, listener, streamParser, false);
    }
    
    public <T> ConnectionBuilder addRequest(String tag, String url,
            MobileAsyncTaskListener<T> listener, IStreamParser<T> streamParser,
            boolean startImmediately) {
            final ConnectionBuilder builder = new ConnectionBuilder(url);           
            return addRequest(tag, builder, listener, streamParser,
                    startImmediately);
    }
    
    public synchronized <T> ConnectionBuilder addRequest(String tag,
            ConnectionBuilder req, MobileAsyncTaskListener<T> listener,
            IStreamParser<T> streamParser, boolean startImmediately) {
        if (req == null) {
            listener.onFailure(new NullPointerException());
            return null;
        }

        if (checkChache(req, listener))
            return null;

        MobileAsyncTask<T> task = new MobileAsyncTask<T>(tag, req, listener,
                streamParser);
        reqs.put(req, task);
        Set<ConnectionBuilder> reqsWithSameTag = taggedReqs.get(tag);
       
 		if (reqsWithSameTag == null) {
            reqsWithSameTag = new HashSet<ConnectionBuilder>();
            taggedReqs.put(tag, reqsWithSameTag);
        }

        reqsWithSameTag.add(req);

        if (startImmediately || reqs.size() - reqsQueue.size() < MAX_CONCURRENT_TASKS)
            startRequest(req);
        else
            enqueueRequest(req);

        return req;
    }
    
    public synchronized void cancelRequest(ConnectionBuilder req) {
        MobileAsyncTask<? extends Object> task = reqs.get(req);
       
 		if (task == null)
            return;
        
		if (task.active)
            task.cancel(true);
        else
            reqsQueue.remove(req);
        
		requestFinished(req, task);
    }
    
    public synchronized void cancelRequestsWithTag(String tag) {
        Set<ConnectionBuilder> reqsWithTag = taggedReqs.get(tag);

        if (reqsWithTag == null)
            return;

        while (reqsWithTag.isEmpty() == false)
            cancelRequest(reqsWithTag.iterator().next());
    }
    
    public synchronized void clearCache() {
        cache.clear();
    }
    
    // Classes
    
    private class MobileAsyncTask<T> extends AsyncTask<Void, Void, T> {      
        private final String tag;
        private final ConnectionBuilder request;
        private final MobileAsyncTaskListener<T> listener;
        private final IStreamParser<T> streamParser;
        private Exception exception = null;
        public boolean active = false;
        
        public MobileAsyncTask(String tag, ConnectionBuilder request,
                MobileAsyncTaskListener<T> listener, IStreamParser<T> streamParser) {
            this.tag = tag;
            this.request = request;
            this.listener = listener;
            this.streamParser = streamParser;
        }
        
        public String getTag() {
            return tag;
        }
        
        // AsyncTask
        
        protected T doInBackground(Void... dummy) {
            try {
                final URLConnection connection = request.buildRequest();
                // temporary work-around;
                // more at http://stackoverflow.com/questions/1440957/                
                System.setProperty("http.keepAlive", "false");
                connection.connect();
                InputStream is = connection.getInputStream();
                
				if (isCancelled())
                    return null;
                
				T result = streamParser.parse(is);
                is.close();
                
				if (request.isUseCaches())
                    addToCache(request.getUrl(), result);
                
				return result;
            }
            catch (Exception e) {
                exception = e;
                return null;
            }
        }
        
        protected void onPostExecute(T result) {
            if (isCancelled())
                return;

            requestFinished(request, this);

            if (exception != null)
                listener.onFailure(exception);
            else
                listener.onSuccess(result);
        }    
    }
}
