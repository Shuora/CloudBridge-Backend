package com.cb.project;

import com.cb.project.client.APIClient;
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
@ComponentScan
public class CloudBridgeClientConfig {

    private String GATEWAY_HOST;
    private String accessKey;
    private String secretKey;

    @Bean
    public APIClient zsAPIClient() {
        return new APIClient(GATEWAY_HOST, accessKey, secretKey);
    }

}
