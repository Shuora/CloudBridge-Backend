package com.zs.project.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * SDK相关信息
 *
 * @TableName sdk_info
 */
@Data
public class SdkInfoVO implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
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