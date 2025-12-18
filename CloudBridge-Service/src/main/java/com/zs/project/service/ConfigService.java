package com.zs.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.stereotype.Service;

@Service
public class ConfigService {
    @Value("${spring.cloud.nacos.config.extension-configs[0].dataId}")
    private String dataId;
    @Value("${spring.cloud.nacos.config.server-addr}")
    private String serverAddr;
    @Value("${spring.cloud.nacos.config.namespace}")
    private String namespace;
    @Value("${spring.cloud.nacos.config.extension-configs[0].group}")
    private String group;

    private final RedisTemplate<String, String> redisTemplate;
    private final StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer;

    @Autowired
    public ConfigService(RedisTemplate<String, String> redisTemplate, StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer) {
        this.redisTemplate = redisTemplate;
        this.streamMessageListenerContainer = streamMessageListenerContainer;
    }

    // 监听 Redis Stream 配置变更消息
    public void startConfigStreamListener() {
        streamMessageListenerContainer.receive(
                Consumer.from("service-consumer-group", "service-consumer"),
                StreamOffset.create("config:stream", ReadOffset.lastConsumed()),
                (StreamListener<String, MapRecord<String, String, String>>) message -> {
                    String dataId = message.getValue("dataId");
                    String group = message.getValue("group");
                    String config = message.getValue("config");
                    // 消费到的配置消息，直接在这里处理配置更新
                    updateServiceConfig(dataId, group, config);
                });
    }

    // 更新服务端配置，可能是 Nacos 配置的更新等
    private void updateServiceConfig(String dataId, String group, String config) {
        System.out.println("Updating service config: " + config);
        // 具体的配置更新逻辑
    }
}
