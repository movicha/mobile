package com.clouidio.mobileasync;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ConnectionBuilder {
    private String url = "";      
    private boolean useCaches = false;
    public ConnectionBuilder(String url) {            
        this.url = url;
    }

    public ConnectionBuilder(String url, boolean useCaches) {            
        this.url = url;
        this.useCaches = useCaches;
    }        

    public boolean isUseCaches() {
        return useCaches;
    }

    public String getUrl() {
        return url;
    }

    public URLConnection buildRequest() throws MalformedURLException, IOException {
        return new URL(url).openConnection();
    }
}
