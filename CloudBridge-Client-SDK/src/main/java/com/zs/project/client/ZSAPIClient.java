package com.zs.project.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.zs.project.domain.User;
import com.zs.project.utils.SignUtils;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ZhuangShuo
 * @date 2024/7/8
 * @description 调用第三方接口的客户端
 */
public class ZSAPIClient {

    public static final String GATEWAY_HOST = "http://localhost:8090";
    private String accessKey;
    private String secretKey;

    public ZSAPIClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String getNameByGet(String name) {

        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);

        String result3 = HttpUtil.get(GATEWAY_HOST + "/api/name/get", paramMap);
        System.out.println(result3);
        return result3;
    }

    public String getNameByPost(@RequestParam String name) {
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);

        String result3 = HttpUtil.post(GATEWAY_HOST + "/api/name/post", paramMap);
        System.out.println(result3);
        return result3;
    }


    private Map<String, String> getHeaderMap(String body) {
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


    public String getUsernameByPost(User user) {
        String json = JSONUtil.toJsonStr(user);
        HttpResponse response = HttpRequest.post(GATEWAY_HOST + "/api/name/user")
                .charset(StandardCharsets.UTF_8)
                .addHeaders(getHeaderMap(json))
                .body(json)
                .execute();
        System.out.println(response.getStatus());
        String result = response.body();
        System.out.println(result);
        return result;
    }
}
