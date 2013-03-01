package com.clouidio.mobileasync;

import java.io.InputStream;

public abstract class StreamStringParserEx <T> implements IStreamParser<T> {
    public T parse(InputStream is) throws Exception {
        return parseEx(StreamStringParser.instance().parse(is));
    }
    
    public abstract T parseEx(String result) throws Exception;
}
