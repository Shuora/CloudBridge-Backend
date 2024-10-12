package com.cb.project.client;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.cb.project.domain.User;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static com.cb.project.utils.GetHeaderMapUtils.getHeaderMap;

/**
 * @author ZhuangShuo
 * @date 2024/7/8
 * @description 调用第三方接口的客户端
 */
public class APIClient {

    private String GATEWAY_HOST;
    private String accessKey;
    private String secretKey;

    public APIClient(String GATEWAY_HOST, String accessKey, String secretKey) {
        this.GATEWAY_HOST = GATEWAY_HOST;
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


    public String getUsernameByPost(User user) {
        String json = JSONUtil.toJsonStr(user);
        HttpResponse response = HttpRequest.post(GATEWAY_HOST + "/api/name/user")
                .charset(StandardCharsets.UTF_8)
                .addHeaders(getHeaderMap(json, accessKey, secretKey))
                .body(json)
                .execute();
        System.out.println(response.getStatus());
        String result = response.body();
        System.out.println(result);
        return result;
    }
}
