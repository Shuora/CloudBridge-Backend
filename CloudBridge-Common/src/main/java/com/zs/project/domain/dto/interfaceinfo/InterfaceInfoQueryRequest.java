package com.zs.project.domain.dto.interfaceinfo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.zs.project.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 *
 * @author ZhuangShuo
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InterfaceInfoQueryRequest extends PageRequest implements Serializable {
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
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
     * 创建人ID
     */
    private Long userId;
}