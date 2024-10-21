package com.zs.project;


import com.cb.project.utils.SignUtils;
import com.zs.project.domain.entity.InterfaceInfo;
import com.zs.project.domain.entity.User;
import com.zs.project.service.InnerInterfaceInfoService;
import com.zs.project.service.InnerUserInterfaceInfoService;
import com.zs.project.service.InnerUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author ZhuangShuo
 * @date 2024/7/13
 * @description
 */
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1");

    public static final String INTERFACE_HOST = "http://localhost:8123";

    @DubboReference
    private InnerUserService innerUserService;
    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;
    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    /**
     * 网关过滤器，用于请求的鉴权和路由转发。
     *
     * @param exchange 服务器web交换机，提供请求和响应的交互信息。
     * @param chain    网关过滤器链，用于继续处理过滤链中的下一个过滤器。
     * @return Mono<Void>，表示异步处理结果。
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 记录请求相关信息
        // 1. 日志
        ServerHttpRequest request = exchange.getRequest();
        log.info("请求唯一标识" + request.getId());
        String path = INTERFACE_HOST + request.getPath().value();
        log.info("请求路径" + request.getPath());
        String method = request.getMethod().toString();
        log.info("请求方法" + request.getMethod());
        log.info("请求参数" + request.getQueryParams());
        String hostName = request.getLocalAddress().getHostName();
        log.info("请求来源地址" + request.getRemoteAddress());
        log.info("请求头" + request.getHeaders());
        log.info("请求体" + request.getBody());
        ServerHttpResponse response = exchange.getResponse();

        // 对请求来源进行访问控制，如果不是白名单内的IP，则禁止访问
        // 2. 访问控制 -（黑白名单）
        if (!IP_WHITE_LIST.contains(hostName)) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }

        // 进行用户鉴权，检查请求头中的accessKey是否有效
        // 3. 用户鉴权（判断 AK、SK 是否合法）
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");
        User invokeUser = null;
        try {
            invokeUser = innerUserService.getInvokeUser(accessKey);
        } catch (Exception e) {
            log.error("accessKey error", e);
        }
        if (invokeUser == null) {
            return handlerNoAuth(response);
        }
        String userRole = invokeUser.getUserRole();
        if (!"admin".equals(userRole)) { // 普通用户需要鉴权
            // 防止高并发攻击
            if (Long.parseLong(nonce) > 1000000L) {
                return handlerNoAuth(response);
            }
            // 检查请求时间是否过期
            if (System.currentTimeMillis() - Long.parseLong(timestamp) > 1000 * 60 * 5) {
                return handlerNoAuth(response);
            }
            // 验证签名是否匹配
            String secretKey = invokeUser.getSecretKey();
            String serverSign = SignUtils.getSign(body, secretKey);
            if (sign == null || !sign.equals(serverSign)) {
                return handlerNoAuth(response);
            }

            // 检查请求的接口是否存在以及请求方法是否匹配
            // 4. 请求的模拟接口是否存在？以及请求方法是否匹配？
            InterfaceInfo interfaceInfo = null;
            try {
                interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path, method);
            } catch (Exception e) {
                log.error("interface is not exists", e);
            }
            if (interfaceInfo == null) {
                return handlerNoAuth(response);
            }

            // 路由转发，调用相应的模拟接口
            // 5. 请求转发，调用模拟接口
//        Mono<Void> filter = chain.filter(exchange);
//        log.info("响应", response.getStatusCode());
            // 调用接口
            return handleResponse(exchange, chain, interfaceInfo.getId(), invokeUser.getId());
        } else { // 管理员不用鉴权
            return chain.filter(exchange);
        }
    }


    /**
     * 处理响应
     *
     * @param exchange        服务器web交换信息，包含请求和响应信息
     * @param chain           网关过滤器链，用于继续处理过滤器链中的下一个过滤器
     * @param interfaceInfoId 接口信息ID，用于标识调用的接口
     * @param userId          用户ID，用于标识调用接口的用户
     * @return Mono<Void> 表示异步处理完成后不返回任何结果
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId) {
        try {
            // 获取原始响应对象
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 获取数据缓冲区工厂，用于创建和管理数据缓冲区
            // 缓存数据的工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 获取响应状态码
            // 拿到响应码
            HttpStatus statusCode = (HttpStatus) originalResponse.getStatusCode();

            // 当响应状态码为HTTP 200 OK时，对响应进行进一步处理
            if (statusCode == HttpStatus.OK) {
                // 创建装饰过的响应对象，用于在写入响应体时进行额外的操作
                // 装饰，增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    // 覆写写入响应体的方法
                    // 等调用完转发的接口后才会执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        // 判断响应体是否为Flux类型
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            // 将Publisher转换为Flux，以便进行操作
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 对每个数据缓冲区进行处理
                            // 往返回值里写数据
                            // 拼接字符串
                            return super.writeWith(
                                    fluxBody.map(dataBuffer -> {
                                        // 更新接口调用次数
                                        // 7. 调用成功，接口调用次数 + 1 invokeCount
                                        try {
                                            innerUserInterfaceInfoService.invokeCount(interfaceInfoId, userId);
                                        } catch (Exception e) {
                                            log.error("invokeCount error", e);
                                        }
                                        // 读取并释放数据缓冲区的内容
                                        byte[] content = new byte[dataBuffer.readableByteCount()];
                                        dataBuffer.read(content);
                                        DataBufferUtils.release(dataBuffer);//释放掉内存
                                        // 构造新的数据缓冲区
                                        // 构建日志
                                        StringBuilder sb2 = new StringBuilder(200);
                                        List<Object> rspArgs = new ArrayList<>();
                                        rspArgs.add(originalResponse.getStatusCode());
                                        String data = new String(content, StandardCharsets.UTF_8); //data
                                        sb2.append(data);
                                        // 打印日志
                                        log.info("响应结果：" + data);
                                        return bufferFactory.wrap(content);
                                    }));
                        } else {
                            // 对于非Flux类型的响应体，记录错误日志
                            // 8. 调用失败，返回一个规范的错误码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 使用装饰过的响应对象继续处理过滤器链
                // 设置 response 对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            // 如果响应状态码不是HTTP 200 OK，则直接继续处理过滤器链
            return chain.filter(exchange); // 降级处理返回数据
        } catch (Exception e) {
            // 捕获并记录异常
            log.error("网关处理响应异常" + e);
            // 继续处理过滤器链
            return chain.filter(exchange);
        }
    }


    @Override
    public int getOrder() {
        return -1;
    }

    public Mono<Void> handlerNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }


    private Mono<Void> handlerInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }
}
