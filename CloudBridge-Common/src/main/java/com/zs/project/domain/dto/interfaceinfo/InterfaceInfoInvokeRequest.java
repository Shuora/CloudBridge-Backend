package com.zs.project.domain.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 接口调用请求
 *
 * @author ZhuangShuo
 */
@Data
public class InterfaceInfoInvokeRequest implements Serializable {
    /**
     * ID
     */
    private Long id;

    /**
     * 请求参数
     */
    private String userRequestParams;

    /**
     * 请求方式
     */
    private String method;

}