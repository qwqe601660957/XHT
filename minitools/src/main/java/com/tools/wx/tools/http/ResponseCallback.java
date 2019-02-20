package com.tools.wx.tools.http;

import com.tools.wx.tools.utils.JsonUtils;
import com.tools.wx.tools.utils.Log;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Response;

/**
 * Created by SensYang on 2017/03/14 14:02
 */

public class ResponseCallback<T> implements OnResponseListener<T> {
    @Override
    public void onStart(int what) {

    }

    @Override
    public void onSucceed(int what, Response<T> response) {
        Log.d("response:" + JsonUtils.parserBean2Json(response.get()));
    }

    @Override
    public void onFailed(int what, Response<T> response) {
        Log.d("failed:" + what);
    }

    @Override
    public void onFinish(int what) {

    }
}
