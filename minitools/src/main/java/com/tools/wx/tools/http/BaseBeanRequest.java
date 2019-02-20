package com.tools.wx.tools.http;

import com.tools.wx.tools.utils.JsonUtils;
import com.tools.wx.tools.utils.Log;
import com.yanzhenjie.nohttp.Binary;
import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.RestRequest;
import com.yanzhenjie.nohttp.rest.StringRequest;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;

/**
 * Created by SensYang on 2017/03/14 10:10
 */
public class BaseBeanRequest<T> extends RestRequest<T> {
    private final Class<T> beanClass;
    private String data;

    public BaseBeanRequest(Class<T> beanClass, String url) {
        this(beanClass, url, RequestMethod.GET);
    }

    public BaseBeanRequest(Class<T> beanClass, String url, RequestMethod requestMethod) {
        super(url, requestMethod);
        this.beanClass = beanClass;
    }

    @Override
    public T parseResponse(Headers responseHeaders, byte[] responseBody) {
        String result = StringRequest.parseResponseString(responseHeaders, responseBody);
        Log.i("result:" + result);
        return JsonUtils.parserJson2Bean(result, beanClass);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public final void add(Object param) {
        try {
            Field[] fields = param.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals("serialVersionUID")) continue;
                IGnore ignore = field.getAnnotation(IGnore.class);
                if (ignore != null) continue;
                field.setAccessible(true);
                Object value = field.get(param);
                if (value != null) {
                    if (value instanceof Integer) {
                        add(field.getName(), (Integer) value);
                    } else if (value instanceof Long) {
                        add(field.getName(), (Long) value);
                    } else if (value instanceof Boolean) {
                        add(field.getName(), (Boolean) value);
                    } else if (value instanceof Character) {
                        add(field.getName(), (Character) value);
                    } else if (value instanceof Double) {
                        add(field.getName(), (Double) value);
                    } else if (value instanceof Float) {
                        add(field.getName(), (Float) value);
                    } else if (value instanceof Short) {
                        add(field.getName(), (Short) value);
                    } else if (value instanceof Byte) {
                        add(field.getName(), (Byte) value);
                    } else if (value instanceof String) {
                        add(field.getName(), (String) value);
                    } else if (value instanceof Binary) {
                        add(field.getName(), (Binary) value);
                    } else if (value instanceof File) {
                        String fileName = ((File) value).getName();
                        addHeader(Headers.HEAD_KEY_CONTENT_DISPOSITION, "form-data;name=\"" + fileName + "\";fileName=\"" + fileName + "\"");
                        add(field.getName(), (File) value);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWriteRequestBody(OutputStream writer) throws IOException {
        if (data != null) {
            byte[] stringData = data.getBytes();
            setHeader(Headers.HEAD_KEY_CONTENT_LENGTH, String.valueOf(stringData.length));
            writer.write(stringData);
            writer.flush();
        }
        super.onWriteRequestBody(writer);
    }

    @Override
    public void cancel() {
        super.cancel();
        data = null;
        onPreResponse(-1, null);
        setQueue(null);
        setSequence(0);
    }
}