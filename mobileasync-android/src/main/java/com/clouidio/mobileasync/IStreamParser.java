package com.clouidio.mobileasync;

import java.io.InputStream;

public interface IStreamParser <T> {
    public T parse(InputStream is) throws Exception;
}
