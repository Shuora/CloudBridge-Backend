package com.zs.project;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * @author ZhuangShuo
 * @date 2024/10/20
 * @description RouteConfigListener类，监听nacos路由配置信息，获取配置信息后刷新进程内路由信息
 */
@Slf4j
@Component
public class RouteConfigListener {

    @Value("${spring.cloud.nacos.config.extension-configs[0].dataId}")
    private String dataId;
    @Value("${spring.cloud.nacos.config.server-addr}")
    private String serverAddr;
    @Value("${spring.cloud.nacos.config.namespace}")
    private String namespace;
    @Value("${spring.cloud.nacos.config.extension-configs[0].group}")
    private String group;

    @Autowired
    private RouteOperator routeOperator;

    @PostConstruct
    public void dynamicRouteByNacosListener() throws NacosException {

        log.info("开始初始化动态路由");
        log.info("配置参数: dataId={}, serverAddr={}, namespace={}, group={}",
                dataId, serverAddr, namespace, group);

        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
        properties.put(PropertyKeyConst.NAMESPACE, namespace);

        try {
            ConfigService configService = NacosFactory.createConfigService(properties);
            log.info("Nacos配置服务创建成功");

            String initConfig = configService.getConfig(dataId, group, 5000);
            log.info("获取到的初始配置: {}", initConfig);

            // 立即更新
            routeOperator.refreshAll(initConfig);

            if (initConfig == null) {
                log.error("未能获取到配置，请检查Nacos配置是否正确");
            }

            // 添加监听，nacos上的配置变更后会执行
            configService.addListener(dataId, group, new Listener() {
                @Override
                public void receiveConfigInfo(String configInfo) {
                    log.info(" ******** receive config info is {}", configInfo);
                    // 解析和处理都交给RouteOperator完成
                    routeOperator.refreshAll(configInfo);
                }

                @Override
                public Executor getExecutor() {
                    return null;
                }
            });

        } catch (NacosException e) {
            log.error("初始化Nacos配置失败", e);
            throw e;
        }

        log.info("动态路由初始化完成");
    }
}
