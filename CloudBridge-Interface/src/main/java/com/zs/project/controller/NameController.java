package com.zs.project.controller;

import com.zs.project.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;


/**
 * @author ZhuangShuo
 * @date 2024/7/8
 * @description 名字API
 */
@RestController
@RequestMapping("/name")
public class NameController {


    @GetMapping("/get")
    public String getNameByGet(String name, HttpServletRequest request) {
        System.out.println(request.getHeader("zs"));
        return "GET your name is " + name;
    }

    @PostMapping("/post")
    public String getNameByPost(@RequestParam String name) {
        return "POST 你的名字是" + name;
    }

    @PostMapping("/user")
    public String getUsernameByPost(@RequestBody User user, HttpServletRequest request) {
//        String accessKey = request.getHeader("accessKey");
//        String nonce = request.getHeader("nonce");
//        String timestamp = request.getHeader("timestamp");
//        String sign = request.getHeader("sign");
//        String body = request.getHeader("body");

//        if (!accessKey.equals("zs")) {
//            throw new RuntimeException("无权限");
//        }

//        if (Long.parseLong(nonce) > 1000000) {
//            throw new RuntimeException("无权限");
//        }
//
//        if (System.currentTimeMillis() - Long.parseLong(timestamp) > 1000 * 60 * 5) {
//            throw new RuntimeException("无权限");
//        }

//        SignUtils.getSign(body, "123456");
//        if (!sign.equals(SignUtils.getSign(body, "123456"))) {
//            throw new RuntimeException("无权限");
//        }

        return "POST 你的名字是" + user.getUserName();
    }
}
