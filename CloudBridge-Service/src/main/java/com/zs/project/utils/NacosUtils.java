package com.zs.project.utils;

import cn.hutool.core.lang.UUID;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.client.config.filter.impl.ConfigResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zs.project.common.ErrorCode;
import com.zs.project.exception.BusinessException;
import com.zs.project.exception.ThrowUtils;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author ZhuangShuo
 * @date 2024/10/20
 * @description
 */
@Component
public class NacosUtils {

    private final static long DEFAULT_TIMEOUT_MS = 5000;
    @Value("${spring.cloud.nacos.config.extension-configs[0].dataId}")
    private String dataId;
    @Value("${spring.cloud.nacos.config.server-addr}")
    private String serverAddr;
    @Value("${spring.cloud.nacos.config.namespace}")
    private String namespace;
    @Value("${spring.cloud.nacos.config.extension-configs[0].group}")
    private String group;
    private ConfigService configService;
    private RedisTemplate redisTemplate;
    private static final int MAX_RETRIES = 3;

    // 更新配置并推送到 Redis Stream
    public boolean updateConfigAndNotify(String uri, String name) {
        int retryCount = 0;

        while (retryCount < MAX_RETRIES) {
            try {
                // 获取当前版本号
                String versionKey = "config_ver:" + dataId + ":" + group;
                String configKey = "config:" + dataId + ":" + group;
                int currentVersion = Integer.parseInt((String) redisTemplate.opsForValue().get(versionKey));

                // 执行 Lua 脚本（CAS 更新配置并推送到 Stream）
                DefaultRedisScript<List> redisScript = new DefaultRedisScript<>();
                redisScript.setScriptText(loadLuaScript());
                redisScript.setResultType(List.class);

                // 执行 Lua 脚本，返回结果
                List<Object> result = (List<Object>) redisTemplate.execute(redisScript, null,
                        uri, name, currentVersion);

                String status = (String) result.get(0);

                if ("CONFLICT".equals(status)) {
                    // 获取最新版本和配置，重新合并后再提交
                    int latestVersion = (int) result.get(1);
                    String latestConfig = (String) result.get(2);
                    String mergedConfig = appendConfig(uri, name); // 合并配置
                    // 递增重试计数
                    retryCount++;
                    if (retryCount >= MAX_RETRIES) {
                        // 达到最大重试次数，抛出异常
                        throw new RuntimeException("Configuration update failed due to version conflict after " + MAX_RETRIES + " retries.");
                    }
                    // 如果冲突，跳过本轮重试并继续尝试
                    continue; // 继续重试
                }
                return true; // 成功更新，退出方法

            } catch (Exception e) {
                // 捕获异常并处理
                throw new RuntimeException("Failed to update configuration for " + dataId + ":" + group, e);
            }
        }
        return false; // 达到最大重试次数仍然失败
    }

    private String loadLuaScript() {
        // 读取并返回 Lua 脚本内容
        // 从文件读取
        File file = new File("src/main/resources/updateConfig.lua");
        if (file.exists()) {
            try {
                return new String(java.nio.file.Files.readAllBytes(file.toPath()));
            } catch (java.io.IOException e) {
                throw new RuntimeException("Failed to load Lua script", e);
            }
        }
        // 如果文件不存在，可以选择抛出异常或返回默认脚本
        throw new RuntimeException("Lua script file not found");
    }


    @PostConstruct
    public void InitNacosUtils() {
        try {
            Properties properties = new Properties();
            properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
            properties.put(PropertyKeyConst.NAMESPACE, namespace);

            // 初始化 Nacos 配置服务
            configService = NacosFactory.createConfigService(properties);
        } catch (NacosException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "初始化 Nacos 配置服务失败");
        }
    }

    /**
     * 发布配置
     *
     * @param content 配置内容
     * @return 是否发布成功
     */
    public boolean publishConfig(String content) {
        try {
            return configService.publishConfig(dataId, group, content);
        } catch (NacosException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "发布配置失败");
        }
    }

    /**
     * 获取配置
     *
     * @return 配置内容
     */
    public String getConfig() {
        try {
            return configService.getConfig(dataId, group, DEFAULT_TIMEOUT_MS);
        } catch (NacosException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取配置失败");
        }
    }

    /**
     * 删除配置
     *
     * @param dataId 配置 ID
     * @param group  配置分组
     * @return 是否删除成功
     */
    public boolean removeConfig(String dataId, String group) {
        try {
            return configService.removeConfig(dataId, group);
        } catch (NacosException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除配置失败");
        }
    }

    /**
     * 在现有配置文件后追加新配置
     *
     * @return 是否追加成功
     */
    public String appendConfig(String uri, String name) {
        // 获取现有的配置
        String existingConfig = getConfig();

        ObjectMapper objectMapper = new ObjectMapper();

        // 读取 JSON 数组为 List<Map<String, Object>>
        List<Map<String, Object>> jsonArray = null;
        try {
            jsonArray = objectMapper.readValue(existingConfig, new TypeReference<List<Map<String, Object>>>() {
            });
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "解析配置失败");
        }

        for (Map<String, Object> element : jsonArray) {
            ThrowUtils.throwIf((element.get("id").equals(name)), ErrorCode.SYSTEM_ERROR, "该名称已存在");
        }

        // 创建一个新的元素
        Map<String, Object> newElement = new HashMap<>();
        newElement.put("id", name);
        newElement.put("uri", uri);

        // 创建 predicates 数组
        List<Map<String, Object>> predicates = new ArrayList<>();
        Map<String, Object> predicate = new HashMap<>();
        predicate.put("name", "Path");
        Map<String, String> args = new HashMap<>();
        args.put("pattern", "/api/**");
        predicate.put("args", args);
        predicates.add(predicate);

        newElement.put("predicates", predicates);

        // 添加新元素到数组中
        jsonArray.add(newElement);

        // 将更新后的数组转换回 JSON 字符串
        String updatedJsonString = null;
        try {
            updatedJsonString = objectMapper.writeValueAsString(jsonArray);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新配置失败");
        }

        // 发布更新后的配置
        return updatedJsonString;
    }


    /**
     * 根据传入的 uri 和 id 创建配置 JSON
     *
     * @param uri  配置的目标 URI
     * @param name 配置的 name
     * @return 生成的配置 JSON 字符串
     */
    public String createConfig(String uri, String name) {
        // 创建 Jackson ObjectMapper 实例
        ObjectMapper objectMapper = new ObjectMapper();

        // 创建根节点对象
        ObjectNode config = objectMapper.createObjectNode();

        // 设置 "id" 字段
        UUID uuid = UUID.randomUUID();
        config.put("id", uuid.toString());

        // 设置 "uri" 字段
        config.put("uri", uri);

        // 创建 "predicates" 数组节点
        ArrayNode predicatesArray = objectMapper.createArrayNode();
        ObjectNode predicateObject = objectMapper.createObjectNode();
        ObjectNode argsObject = objectMapper.createObjectNode();

        // 设置 "pattern" 到 args
        argsObject.put("pattern", "/api/**");
        predicateObject.set("args", argsObject);

        // 设置 "name" 为 "Path"
        predicateObject.put("name", name);

        // 将 predicate 对象加入 predicates 数组
        predicatesArray.add(predicateObject);

        // 将 predicates 数组加入 config
        config.set("predicates", predicatesArray);

        // 序列化为 JSON 字符串并返回
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(config);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建配置失败");
        }
    }


}