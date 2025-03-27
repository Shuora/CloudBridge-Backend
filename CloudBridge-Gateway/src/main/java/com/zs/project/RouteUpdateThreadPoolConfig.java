package com.zs.project;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author ZhuangShuo
 * @date 2025/3/27
 * @description
 */
@Configuration
public class RouteUpdateThreadPoolConfig {
    @Bean("routeUpdateExecutor")
    public Executor routeUpdateExecutor() {
        return new ThreadPoolExecutor(
                2, // 核心线程数（根据业务压力调整）
                4, // 最大线程数
                30, // 空闲线程存活时间（秒）
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100), // 任务队列容量
                new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略：由调用线程直接执行
        );
    }
}