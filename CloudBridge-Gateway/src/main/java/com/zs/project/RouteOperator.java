package com.zs.project;

import com.alibaba.nacos.client.config.NacosConfigService;
import com.alibaba.nacos.client.config.filter.impl.ConfigResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author ZhuangShuo
 * @date 2024/10/20
 * @description RouteOperator类，提供网关进程的删除、添加等操作，对外提供更新。
 */
@Slf4j
public class RouteOperator {
    private final NacosConfigService nacosConfigService;
    private final String dataId = "route_config";
    private final String group = "DEFAULT_GROUP";
    private static final List<String> routeList = new ArrayList<>();
    private ObjectMapper objectMapper;
    private RouteDefinitionWriter routeDefinitionWriter;
    private ApplicationEventPublisher applicationEventPublisher;

    public RouteOperator(ObjectMapper objectMapper, RouteDefinitionWriter routeDefinitionWriter, ApplicationEventPublisher applicationEventPublisher) {
        this.objectMapper = objectMapper;
        this.routeDefinitionWriter = routeDefinitionWriter;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * 清理集合中的所有路由，并清空集合
     */
    private void clear() {
        // 全部调用API清理掉
        try {
            routeList.forEach(id -> routeDefinitionWriter.delete(Mono.just(id)).subscribe());
        } catch (Exception e) {
            log.error("clear Route is error !");
        }
        // 清空集合
        routeList.clear();
    }

    /**
     * 新增路由
     *
     * @param routeDefinitions
     */
    private void add(List<RouteDefinition> routeDefinitions) {

        try {
            routeDefinitions.forEach(routeDefinition -> {
                routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe();
                routeList.add(routeDefinition.getId());
            });
        } catch (Exception exception) {
            log.error("add route is error", exception);
        }
    }

    /**
     * 发布进程内通知，更新路由
     */
    private void publish() {
        applicationEventPublisher.publishEvent(new RefreshRoutesEvent(routeDefinitionWriter));
    }

    /**
     * 更新所有路由信息
     *
     * @param configStr
     */
    public void refreshAll(String configStr) {
        log.info("开始刷新路由配置: {} ....", configStr);
        // 无效字符串不处理
        if (!StringUtils.hasText(configStr)) {
            log.error("invalid string for route config");
            return;
        }
        // 用Jackson反序列化
        List<RouteDefinition> routeDefinitions = null;
        try {
            routeDefinitions = objectMapper.readValue(configStr, new TypeReference<List<RouteDefinition>>() {
            });
        } catch (JsonProcessingException e) {
            log.error("从nacos字符串获取路由定义错误！", e);
        }
        // 如果等于null，表示反序列化失败，立即返回
        if (null == routeDefinitions) {
            log.error("序列化失败！");
            return;
        }
        // 清理掉当前所有路由
        clear();
        // 添加最新路由
        add(routeDefinitions);

        // 通过应用内消息的方式发布
        publish();

        log.info("路由刷新完成");
    }

    public void updateWithRetry(
            List<RouteDefinition> newRoutes,
            int maxRetries
    ) {
        int retryCount = 0;
        while (retryCount < maxRetries) {
            // 1. 获取当前配置及MD5
            ConfigResponse config = nacosConfigService.getConfigAndMd5(dataId, group, 5000);
            String currentContent = config.getContent();
            String currentMd5 = config.getMd5();

            // 2. 解析配置内容
            RouteConfig currentConfig = parseConfig(currentContent);
            long currentVersion = currentConfig.getVersion();
            List<RouteDefinition> currentRoutes = currentConfig.getRoutes();

            // 3. 合并路由（去重）
            List<RouteDefinition> mergedRoutes = mergeRoutes(currentRoutes, newRoutes);

            // 4. 生成新配置（版本号+1）
            RouteConfig newConfig = new RouteConfig(currentVersion + 1, mergedRoutes);
            String newContent = serializeConfig(newConfig);

            // 5. 条件更新（原子操作）
            boolean success = nacosConfigService.publishConfig(
                    dataId, group, newContent, currentMd5 // 关键：携带MD5作为CAS条件
            );

            if (success) {
                return; // 更新成功
            } else {
                retryCount++;
                log.warn("版本冲突，第{}次重试...", retryCount);
            }
        }
        throw new RuntimeException("更新失败，超过最大重试次数");
    }

    /**
     * 合并路由（去重逻辑）
     */
    private List<RouteDefinition> mergeRoutes(
            List<RouteDefinition> baseRoutes,
            List<RouteDefinition> newRoutes
    ) {
        Set<String> existingIds = baseRoutes.stream()
                .map(RouteDefinition::getId)
                .collect(Collectors.toSet());

        List<RouteDefinition> merged = new ArrayList<>(baseRoutes);
        for (RouteDefinition route : newRoutes) {
            if (!existingIds.contains(route.getId())) {
                merged.add(route);
                existingIds.add(route.getId());
            }
        }
        return merged;
    }

    /**
     * 配置解析（JSON示例）
     */
    private RouteConfig parseConfig(String content) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(content, RouteConfig.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("配置解析失败", e);
        }
    }

    /**
     * 配置序列化（JSON示例）
     */
    private String serializeConfig(RouteConfig config) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(config);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("配置序列化失败", e);
        }
    }

    /**
     * 配置包装类（内部静态类）
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class RouteConfig {
        private long version;
        private List<RouteDefinition> routes;
    }
}
