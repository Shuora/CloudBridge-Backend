package com.zs.project.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ZhuangShuo
 * @date 2024/7/16
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InterfaceInfoVO2 extends InterfaceInfoVO {

    /**
     * 调用次数
     */
    private Integer totalNum;

    private static final long serialVersionUID = 1L;
}
