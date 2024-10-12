package com.cb.project.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

/**
 * @author ZhuangShuo
 * @date 2024/7/9
 * @description 签名工具类
 */
public class SignUtils {

    public static String getSign(String body, String secretKey) {
        Digester md5 = new Digester(DigestAlgorithm.MD5);
        String content = body + "." + secretKey;
        return md5.digestHex(content);
    }
}
