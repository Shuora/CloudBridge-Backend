package com.zs.project.utils;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.github.benmanes.caffeine.cache.*;
import com.zs.project.common.ErrorCode;
import com.zs.project.exception.BusinessException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class JarLoaderUtil{
    private final Cache<String, JarClasses> jarCache;
    private final ExecutorService executor;
    private final String GATEWAY_HOST;
    private final String accessKey;
    private final String secretKey;

    public JarLoaderUtil(String secretKey, String accessKey, String GATEWAY_HOST) {
        this.secretKey = secretKey;
        this.accessKey = accessKey;
        this.GATEWAY_HOST = GATEWAY_HOST;

        this.jarCache = Caffeine.newBuilder()
                .softValues()
                .maximumSize(100)
                .expireAfterWrite(1, TimeUnit.DAYS)
                .removalListener(this::handleRemoval)
                .build();

        this.executor = new ThreadPoolExecutor(
                4, 8, 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000)
        );
    }

    @PostConstruct
    public void init() {
        loadHighFrequencyClasses();
    }

    private void loadHighFrequencyClasses() {
        List<String> topJars = getTopInvokedJarsFromDB();
        topJars.forEach(this::preloadJar);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    private void refreshCache() {
        List<String> frequentJars = getTopInvokedJarsFromDB();
        jarCache.invalidateAll();
        frequentJars.forEach(this::preloadJar);
    }

    public CompletableFuture<String> invokeJarMethodAsync(String jarPath, String method, String params) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JarClasses cls = jarCache.get(jarPath, k -> loadJarClasses(jarPath));
                return executeReflectionCall(cls, method, params);
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "调用失败: " + e.getMessage());
            }
        }, executor);
    }

    private String executeReflectionCall(JarClasses cls, String methodName, String params) throws Exception {
        Object client = cls.clientClass
                .getDeclaredConstructor(String.class, String.class, String.class)
                .newInstance(GATEWAY_HOST, accessKey, secretKey);

        Object request = new Gson().fromJson(params, cls.requestClass);
        Method method = cls.clientClass.getMethod(methodName, cls.requestClass);
        return (String) method.invoke(client, request);
    }

    private JarClasses loadJarClasses(String jarPath) throws Exception {
        URLClassLoader loader = new URLClassLoader(new URL[]{new File(jarPath).toURI().toURL()});
        Class<?> clientClass = loader.loadClass("com.cb.project.Client");
        Class<?> requestClass = loader.loadClass(clientClass.getPackage().getName() + ".Request");
        return new JarClasses(clientClass, requestClass, loader);
    }

    private void handleRemoval(String key, JarClasses value, RemovalCause cause) {
        if (value != null) {
            try {
                value.classLoader.close();
            } catch (IOException e) {
                log.error("ClassLoader关闭失败: {}", key, e);
            }
        }
    }

    private static class JarClasses {
        final Class<?> clientClass;
        final Class<?> requestClass;
        final URLClassLoader classLoader;

        JarClasses(Class<?> clientClass, Class<?> requestClass, URLClassLoader classLoader) {
            this.clientClass = clientClass;
            this.requestClass = requestClass;
            this.classLoader = classLoader;
        }
    }

    // 以下为模拟方法（需根据实际实现）
    private List<String> getTopInvokedJarsFromDB() {
        return List.of("path/to/frequent.jar");
    }

    private void preloadJar(String jarPath) {
        try {
            jarCache.get(jarPath, this::loadJarClasses);
        } catch (Exception e) {
            log.error("预加载JAR失败: {}", jarPath, e);
        }
    }
}