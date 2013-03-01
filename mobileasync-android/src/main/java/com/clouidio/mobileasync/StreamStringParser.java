package com.clouidio.mobileasync;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class StreamStringParser implements IStreamParser<String> {
    private static final String defaultEncoding = "UTF-8";
    private static StreamStringParser instance;
    
    private final String encoding;
    
    private StreamStringParser(String encoding) {
        this.encoding = encoding;
    }
    
    public synchronized static StreamStringParser instance() {
        if (instance == null)
            instance = new StreamStringParser(defaultEncoding);
        return instance;
    }
    
    @Override
    public String parse(InputStream is) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int c;

        while ((c = is.read(buf, 0, 1024)) != -1)
            baos.write(buf, 0, c);

        byte[] bytes = baos.toByteArray();
        String result = new String(bytes, encoding);
        return result;
    }
    
}
