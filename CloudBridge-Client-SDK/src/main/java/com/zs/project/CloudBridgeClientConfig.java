package com.zs.project;

import com.zs.project.client.ZSAPIClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author ZhuangShuo
 * @date 2024/7/9
 * @description
 */
@Configuration
@ConfigurationProperties("cloudbridge.client")
@Data
@ComponentScan(basePackages = "com.zs.project")
public class CloudBridgeClientConfig {

    private String accessKey;
    private String secretKey;

    @Bean
    public ZSAPIClient zsAPIClient() {
        return new ZSAPIClient(accessKey, secretKey);
    }

}
