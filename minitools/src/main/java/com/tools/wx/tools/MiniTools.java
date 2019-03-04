package com.tools.wx.tools;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tools.wx.tools.bean.MiniRes;
import com.tools.wx.tools.http.NoHttpUtil;
import com.tools.wx.tools.utils.AppUtils;
import com.tools.wx.tools.utils.EasyKVStore;
import com.tools.wx.tools.utils.JsonUtils;
import com.tools.wx.tools.utils.Log;
import com.tools.wx.tools.utils.Utils;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.StringRequest;

/**
 * className : MiniTools
 * description :
 * author : 大圣
 * created : 2019/2/16
 * qq : 601660957
 */

public class MiniTools {
    public String mAppId;
    public Context mContext;
    public String mAppKey;
    private boolean isFirst;


    private static MiniTools instance;

    private Activity mFirstActivity;

    public static MiniTools get() {
        if (instance == null) {
            Class var0 = MiniTools.class;
            synchronized (MiniTools.class) {
                if (instance == null) {
                    instance = new MiniTools();
                }
            }

        }
        return instance;
    }


    public void init(Context context, String appKey, String appId) {
        if (context == null) {
            return;
        }
        isFirst = false;
        mContext = context;
        mAppId = appId;
        mAppKey = appKey;
        Utils.init(context);
        NoHttp.initialize(context.getApplicationContext());
        ((Application) context).registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                if (!isFirst) {
                    isFirst = true;
                    mFirstActivity = activity;
                    getAppInfo();

                }
            }

            @Override
            public void onActivityPaused(Activity activity) {
                if (mFirstActivity != null && mFirstActivity.getClass().getSimpleName().equals(activity.getClass().getSimpleName())) {
                    mFirstActivity = null;
                    isFirst = false;
                }
            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if (mFirstActivity != null && mFirstActivity.getClass().getSimpleName().equals(activity.getClass().getSimpleName())) {
                    mFirstActivity = null;
                    isFirst = false;
                }
            }
        });

    }

    private void getAppInfo() {
        String uuid = AppUtils.getAndroidID(mContext);

        StringRequest stringRequest = new StringRequest("http://log.xiaohutui.com/log/stat/initapp?app_key=" + mAppKey + "&uuid=" + uuid, RequestMethod.GET);

        NoHttpUtil.getInstance().add(stringRequest, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                String result = response.get();
                try {
                    final MiniRes miniRes = JsonUtils.parserJson2Bean(result, MiniRes.class);
                    if (miniRes != null) {
                        if (miniRes.errcode == 0) {

                            long time = System.currentTimeMillis();
                            long expireTime = EasyKVStore.getLongPrefs("expireTime") + miniRes.interval_time;
                            if (time > expireTime && miniRes.jump) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mFirstActivity != null) {

                                            startApp(miniRes.appid, miniRes.path.replace("\\",""));
                                            EasyKVStore.setLongPrefs("expireTime",System.currentTimeMillis());
                                        }


                                    }
                                }, miniRes.after_time * 1000);
                            }

                        }

                    }
                }catch (Exception e){
                    Log.e(e.getMessage());
                }


            }

            @Override
            public void onFailed(int what, Response<String> response) {

            }

            @Override
            public void onFinish(int what) {

            }
        });
    }


    private void startApp(String userName, String path) {
        IWXAPI api = WXAPIFactory.createWXAPI(mContext, mAppId);

        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        req.userName = userName; // 填小程序原始id
        req.path = path;                  //拉起小程序页面的可带参路径，不填默认拉起小程序首页
        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 开发版，体验版和正式版
        api.sendReq(req);
    }


}
