package com.tools.wx.tools.http;

import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.tools.NetUtil;

/**
 * Created by SensYang on 2017/03/14 9:13
 */
public class NoHttpUtil {
    private RequestQueue requestQueue;
    private static NoHttpUtil instance;

    public static NoHttpUtil getInstance() {
        if (instance == null) {
            instance = new NoHttpUtil();
        }
        return instance;
    }

    public <T> void add(Request<T> request) {
        add(-1, request);
    }

    public <T> void add(int what, Request<T> request) {
        add(what, request, null);
    }

    public <T> void add(Request<T> request, OnResponseListener<T> responseListener) {
        add(null, request, responseListener);
    }

    public <T> void add(Object cancelSign, Request<T> request, OnResponseListener<T> responseListener) {
        if (NetUtil.isNetworkAvailable()) {
            requestQueue.add(cancelSign, request, responseListener);
        } else {
            if (responseListener != null)
                responseListener.onFailed(400, null);
        }
    }

    private NoHttpUtil() {
        initRequestQueue();
    }

    public void cancelBytag(Object o) {
        requestQueue.cancelBySign(o);
    }

    private void initRequestQueue() {
        requestQueue = new RequestQueue(3);
        requestQueue.start();
    }

    public void restart() {
        requestQueue.start();
    }
}
