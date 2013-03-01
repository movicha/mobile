package com.clouidio.mobileasync;

public abstract class MobileAsyncTaskListener<T> {
    protected final Object tag;
    
    public MobileAsyncTaskListener() {
        this.tag = null;
    }
    
    public MobileAsyncTaskListener(Object tag) {
        this.tag = tag;
    }
    
    protected abstract void onSuccess(T result);
    
    protected void onFailure(Exception exception) {
        exception.printStackTrace();
    }
    
}
