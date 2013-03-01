package com.clouidio.mobileasync;

import java.io.InputStream;
import com.clouidio.treexmlparser.*;

public abstract class StreamXMLParserEx <T> implements IStreamParser<T> {
    public T parse(InputStream is) throws Exception {
        return parseEx(StreamXMLParser.instance().parse(is));
    }
    
    public abstract T parseEx(XMLNode result) throws Exception;
}
