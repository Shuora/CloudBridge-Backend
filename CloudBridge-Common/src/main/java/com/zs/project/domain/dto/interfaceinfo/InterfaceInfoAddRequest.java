package com.zs.project.domain.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建请求
 *
 * @author ZhuangShuo
 */
@Data
public class InterfaceInfoAddRequest implements Serializable {

    /**
     * 接口名称
     */
    private String name;

    /**
     * 接口描述
     */
    private String description;

    /**
     * 接口URL
     */
    private String url;

    /**
     * 请求参数
     */
    private String requestParams;
    /**
     * 请求头信息
     */
    private String requestHeader;

    /**
     * 响应头信息
     */
    private String responseHeader;


    /**
     * 请求方式
     */
    private String method;

    /**
     * SDKId
     */
    private Long sdkId;
}