package com.zs.project.nacos;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;

/**
 * @author ZhuangShuo
 * @date 2024/10/16
 * @description
 */
public class NacosConfigManager {

    private ConfigService configService;


    public void updateConfig(String dataId, String group, String content) {
        try {
            boolean isSuccess = configService.publishConfig(dataId, group, content);
            if (isSuccess) {
                System.out.println("Configuration updated successfully");
            } else {
                System.out.println("Failed to update configuration");
            }
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }


}
