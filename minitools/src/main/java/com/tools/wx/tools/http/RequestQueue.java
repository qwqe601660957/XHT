/*
 * Copyright 2015 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tools.wx.tools.http;

import com.yanzhenjie.nohttp.BasicRequest;
import com.yanzhenjie.nohttp.Delivery;
import com.yanzhenjie.nohttp.HandlerDelivery;
import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestDispatcher;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestQueue {

    private AtomicInteger mInteger = new AtomicInteger();
    private final BlockingQueue<Request<?>> mUnFinishQueue = new LinkedBlockingDeque<>();
    private final BlockingQueue<Request<?>> mRequestQueue = new PriorityBlockingQueue<>();
    private static final OnResponseListener defaultResponseCallback = new ResponseCallback();

    private RequestDispatcher[] mDispatchers;

    private Delivery mDelivery;

    public RequestQueue(int threadPoolSize) {
        mDispatchers = new RequestDispatcher[threadPoolSize];
        mDelivery = HandlerDelivery.newInstance();
    }

    public void start() {
        stop();
        for (int i = 0; i < mDispatchers.length; i++) {
            RequestDispatcher networkDispatcher = new RequestDispatcher(mUnFinishQueue, mRequestQueue, mDelivery);
            mDispatchers[i] = networkDispatcher;
            networkDispatcher.start();
        }
    }

    public <T> void add(Object cancelSign, Request<T> request, OnResponseListener<T> responseListener) {
        if (request.inQueue())
            Logger.w("This request has been in the queue");
        else {
            boolean hasSetSign = false;
            try {
                Field field = BasicRequest.class.getDeclaredField("mCancelSign");
                if (field != null) {
                    field.setAccessible(true);
                    if (field.get(request) != null) {
                        hasSetSign = true;
                    }
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (!hasSetSign) {
                request.setCancelSign(cancelSign);
            }
            if (responseListener == null) {
                responseListener = defaultResponseCallback;
            }
            request.onPreResponse(responseListener.hashCode(), responseListener);
            request.setQueue(mUnFinishQueue);
            request.setSequence(mInteger.incrementAndGet());
            mUnFinishQueue.add(request);
            mRequestQueue.add(request);
        }
    }

    public int unStartSize() {
        return mRequestQueue.size();
    }

    public int unFinishSize() {
        return mUnFinishQueue.size();
    }

    public void stop() {
        for (RequestDispatcher dispatcher : mDispatchers)
            if (dispatcher != null)
                dispatcher.quit();
    }

    public void cancelBySign(Object sign) {
        synchronized (mUnFinishQueue) {
            List<Request> requestList = new ArrayList<>(mUnFinishQueue.size());
            for (Request<?> request : mUnFinishQueue) {
                if (request instanceof BasicRequest) {
                    try {
                        Field field = BasicRequest.class.getDeclaredField("mCancelSign");
                        if (field != null) {
                            field.setAccessible(true);
                            if (field.get(request).equals(sign)) {
                                sign = field.get(request);
                                requestList.add(request);
                                request.cancel();
                                field.setAccessible(false);
                                continue;
                            }
                            field.setAccessible(false);
                        }
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                request.cancelBySign(sign);
            }
            mUnFinishQueue.removeAll(requestList);
            mRequestQueue.removeAll(requestList);
            start();
        }
    }

    public void cancelAll() {
        synchronized (mUnFinishQueue) {
            for (Request<?> request : mUnFinishQueue)
                request.cancel();
            mUnFinishQueue.clear();
            mRequestQueue.clear();
        }
    }
}