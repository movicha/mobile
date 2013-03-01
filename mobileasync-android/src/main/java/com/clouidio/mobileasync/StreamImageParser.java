package com.clouidio.mobileasync;

import java.io.InputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class StreamImageParser implements IStreamParser<Bitmap> 

    private static StreamImageParser instance;
    
    private StreamImageParser() {
    }
    
    public synchronized static StreamImageParser instance() {
        if (instance == null)
            instance = new StreamImageParser();
        return instance;
    }
    
    @Override
    public Bitmap parse(InputStream is) throws Exception {
        return BitmapFactory.decodeStream(is);
    }
}
