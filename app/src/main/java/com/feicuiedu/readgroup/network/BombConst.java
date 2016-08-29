package com.feicuiedu.readgroup.network;


/**
 * 云逻辑：http://cloud.bmob.cn/5a3f440fff51573e/test?name=jeff
 * 文件上传： https://api.bmob.cn/2/files/fileName
 */
interface BombConst {

    // 统一的Bomb请求头
    String HEADER_APPLICATION_ID = "X-Bmob-Application-Id";
    String HEADER_REST_API_KEY = "X-Bmob-REST-API-Key";
    String HEADER_CONTENT_TYPE = "Content-Type";

    // Bomb请求头的值
    String CONTENT_TYPE_JSON = "application/json";
    String APPLICATION_ID = "e54c192bdb057b18eda0a5032da883e8";
    String REST_API_KEY = "d04d49e3ed57dac2f3557592540868fb";

    String REGISTER_URL = "http://cloud.bmob.cn/5a3f440fff51573e/register?username=%s&password=%s";

    String SEARCH_USER_URL = "http://cloud.bmob.cn/5a3f440fff51573e/searchUser/?query=%s";

    String LOGIN_URL = "https://api.bmob.cn/1/login?username=%s&password=%s";




}
