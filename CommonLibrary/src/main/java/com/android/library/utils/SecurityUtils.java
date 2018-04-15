package com.android.library.utils;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Administrator on 2015/9/24.
 */
public class SecurityUtils {
    public static String KEY = "zh_req!@#W";

    public static String sign(String datas, String key) {
        String sign = "";
        try {
            //根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
            SecretKey secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA1");
            //生成一个指定 Mac 算法 的 Mac 对象
            Mac mac = Mac.getInstance("HmacSHA1");
            //用给定密钥初始化 Mac 对象
            mac.init(secretKey);

            sign = Base64.encode(mac.doFinal(datas.getBytes("UTF-8")));

        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        return sign;
    }

    /**
     * Des 加密
     *
     * @param data
     * @param key
     * @return
     */
    public static String encodeByDES(String data, String key) {
        return DesUtil.encrypt(data, key);
    }

    public static String decodeByDES(String data, String key) {
        return DesUtil.decrypt(data, key);
    }
}
