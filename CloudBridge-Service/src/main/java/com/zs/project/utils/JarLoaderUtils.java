package com.zs.project.utils;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.zs.project.common.ErrorCode;
import com.zs.project.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author ZhuangShuo
 * @date 2024/10/21
 * @description
 */
@Slf4j
public class JarLoaderUtils {

    private String GATEWAY_HOST;
    private String accessKey;
    private String secretKey;

    public JarLoaderUtils(String secretKey, String accessKey, String GATEWAY_HOST) {
        this.secretKey = secretKey;
        this.accessKey = accessKey;
        this.GATEWAY_HOST = GATEWAY_HOST;
    }

    public String invokeJarMethod(String jarPath, String requestMethod, String userRequestParams) {
        try {
            // 1. 加载JAR文件
            File jarFile = new File(jarPath);
            URLClassLoader classLoader = new URLClassLoader(new URL[]{jarFile.toURI().toURL()});

            // 2. 获取并实例化APIClient类
            Class<?> clientClass = classLoader.loadClass("com.cb.project.Client");
            Object tempClient = clientClass.getDeclaredConstructor(String.class, String.class, String.class)
                    .newInstance(GATEWAY_HOST, accessKey, secretKey);

            // 3. 获取并实例化Request类
            Class<?> requestClass = classLoader.loadClass(tempClient.getClass().getPackage().getName() + ".Request");
            Gson gson = new Gson();
            Object requestRequest = gson.fromJson(userRequestParams, requestClass);

            // 4. 通过反射调用方法
            Method method = clientClass.getMethod(requestMethod, requestClass);
            String result = (String) method.invoke(tempClient, requestRequest);

            return result;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "调用接口异常");
        }
    }
}