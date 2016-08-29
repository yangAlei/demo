package com.feicuiedu.readgroup.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 处理Bomb需要的统一的头字段
 */
class BombInterceptor implements Interceptor, BombConst{


    @Override public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        Request.Builder builder = request.newBuilder();

        // 用于让Bomb区分是哪一个应用
        builder.header(HEADER_APPLICATION_ID, APPLICATION_ID);
        // 用于授权
        builder.header(HEADER_REST_API_KEY, REST_API_KEY);
        // Bomb的请求体和响应体都是统一的Json格式
        builder.header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);

        return chain.proceed(builder.build());
    }
}
