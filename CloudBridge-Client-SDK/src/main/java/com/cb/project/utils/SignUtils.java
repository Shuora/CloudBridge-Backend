package com.cb.project.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @author ZhuangShuo
 * @date 2024/7/9
 * @description 签名工具类
 */
public class SignUtils {

    public static String getSign(String body, String secretKey) {
//        Digester md5 = new Digester(DigestAlgorithm.MD5);
//        String content = body + "." + secretKey;
//        return md5.digestHex(content);
        Mac sha256_HMAC = null;
        try {
            sha256_HMAC = Mac.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
        try {
            sha256_HMAC.init(secretKeySpec);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        String data = body + "." + secretKey;
        byte[] hash = sha256_HMAC.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }
}
