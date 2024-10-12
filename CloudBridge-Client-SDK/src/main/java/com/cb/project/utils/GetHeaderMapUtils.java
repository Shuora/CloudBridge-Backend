package com.cb.project.utils;

import cn.hutool.core.util.RandomUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZhuangShuo
 * @date 2024/10/11
 * @description
 */
public class GetHeaderMapUtils {

    public static Map<String, String> getHeaderMap(String body, String accessKey, String secretKey) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("accessKey", accessKey);
        // 不能直接发送密码
//        headerMap.put("secretKey", secretKey);
        headerMap.put("nonce", RandomUtil.randomNumbers(5));
        headerMap.put("body", body);
        headerMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
        if (body == null) {
            body = "";
        }
        headerMap.put("sign", SignUtils.getSign(body, secretKey));
        return headerMap;
    }


}
