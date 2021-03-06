package com.feicuiedu.readgroup.network;


/**
 * 云逻辑：http://cloud.bmob.cn/5a3f440fff51573e/test?name=jeff
 * 文件上传： https://api.bmob.cn/2/files/fileName
 */
interface BombConst {

    // 统一的Bomb请求头
    String HEADER_SESSION_TOKEN = "X-Bmob-Session-Token";
    String HEADER_APPLICATION_ID = "X-Bmob-Application-Id";
    String HEADER_REST_API_KEY = "X-Bmob-REST-API-Key";
    String HEADER_CONTENT_TYPE = "Content-Type";

    // Bomb请求头的值
    String CONTENT_TYPE_JSON = "application/json";
    String APPLICATION_ID = "e54c192bdb057b18eda0a5032da883e8";
    String REST_API_KEY = "d04d49e3ed57dac2f3557592540868fb";

    String REGISTER_URL = "http://cloud.bmob.cn/5a3f440fff51573e/register?username=%s&password=%s";

    String SEARCH_USER_URL = "http://cloud.bmob.cn/5a3f440fff51573e/searchUser?query=%s";

    String GET_USERS_URL = "http://cloud.bmob.cn/5a3f440fff51573e/getUser/?ids=%s";

    // Note: 服务器有1分钟的数据缓存，如果连续两次请求参数一样，下次请求会返回缓存数据
    String BOOKS_URL = "http://cloud.bmob.cn/5a3f440fff51573e/books?time=%s";

    String BOOK_INFO_URL = "http://cloud.bmob.cn/5a3f440fff51573e/getBookInfo?bookId=%s&time=%s";

    String USER_LIKES_URL = "http://cloud.bmob.cn/5a3f440fff51573e/getUserLikes?userId=%s&time=%s";

    String UPDATE_USER_URL = "https://api.bmob.cn/1/users/%s";

    String LOGIN_URL = "https://api.bmob.cn/1/login?username=%s&password=%s";

    String UPLOAD_FILE_URL = "https://api.bmob.cn/2/files/avatar.jpeg";

    String BOOK_LIKE_URL = "http://cloud.bmob.cn/5a3f440fff51573e/changeLike?bookId=%s&userId=%s&action=%s";
}
