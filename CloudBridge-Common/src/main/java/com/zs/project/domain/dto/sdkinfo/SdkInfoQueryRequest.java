package com.zs.project.domain.dto.sdkinfo;

import com.zs.project.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ZhuangShuo
 * @date 2024/10/17
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SdkInfoQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * SDK名称
     */
    private String name;

    /**
     * SDK存储路径
     */
    private String path;

    /**
     * 状态，0启用1禁用
     */
    private Integer status;

    /**
     * 创建人
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除(0-未删, 1-已删)
     */
    private Integer isDelete;

}
