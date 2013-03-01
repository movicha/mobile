package com.clouidio.mobileasync;

import java.io.InputStream;
import com.clouidio.treexmlparser.*;

public class StreamXMLParser implements IStreamParser<XMLNode> {
    private static StreamXMLParser instance;
    
    private StreamXMLParser() {
    }
    
    public synchronized static StreamXMLParser instance() {
        if (instance == null)
            instance = new StreamXMLParser();

        return instance;
    }
    
    @Override
    public XMLNode parse(InputStream is) throws Exception {
        XMLParser parser = new XMLParser();
        return parser.parse(is);
    }
}
