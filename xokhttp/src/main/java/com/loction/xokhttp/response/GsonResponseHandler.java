package com.loction.xokhttp.response;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loction.xokhttp.BaseResponse;
import com.loction.xokhttp.XOkhttpClient;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by localadmin on 2017/11/13.
 *
 * update  2018/8/28
 * 适配WanAndroid体系结构
 * 这里填的泛型
 * 为wanAndroid返回数据data内容所生成的实体类
 * errCode为0是回调onSuccful方法
 * 不为0时回调失败方法 onFail方法  并且返回错误码和失败原因
 *
 */

public abstract class GsonResponseHandler<T> implements IResponse,ParameterizedType  {
    private String bodyStr;


    public abstract void onSuccful(T response);

    @Override
    public void onSuccful(final Response response) {
        final ResponseBody body = response.body();

        try {
            bodyStr = body.string();
        } catch (IOException e) {
            e.printStackTrace();
            XOkhttpClient.handler.post(new Runnable() {
                @Override
                public void run() {
                    onFail(response.code(), "error read msg");
                }
            });
            return;
        } finally {
            response.close();
        }
        final Type gsonType = this;
        XOkhttpClient.handler.post(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
//                onSuccful(new Responseer<T>((T) gson.fromJson(bodyStr, mType), response));
                TypeToken<BaseResponse<T>> typeToken = new TypeToken<BaseResponse<T>>() {
                };
                BaseResponse<T> baseResponse = gson.fromJson(bodyStr, gsonType);
                if(baseResponse.getErrorCode()==0){
                    onSuccful(baseResponse.getData());
                }else{
                    onFail(baseResponse.getErrorCode(),baseResponse.getErrorMsg());
                }

            }
        });
    }

    @Override
    public void onFail(int errorCode, String errorMessage) {

    }



    @Override
    public Type[] getActualTypeArguments() {
        Class clz = this.getClass();

        Type superclass = clz.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return parameterized.getActualTypeArguments();
    }

    @Override
    public Type getOwnerType() {
        return null;
    }
    @Override
    public Type getRawType() {
        return BaseResponse.class;
    }
}
