package com.zs.project.domain.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新请求
 *
 * @author ZhuangShuo
 */
@Data
public class InterfaceInfoUpdateRequest implements Serializable {
    /**
     * ID
     */
    private Long id;

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
     * 接口状态，0表示关闭，1表示开启
     */
    private Integer status;

    /**
     * 请求方式
     */
    private String method;


    /**
     * 是否删除，0表示未删除，1表示删除
     */
    private Integer isDelete;

    private Long sdkId;

}