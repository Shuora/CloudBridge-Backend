package com.zs.project.domain.dto.sdkinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ZhuangShuo
 * @date 2024/10/17
 * @description
 */
@Data
public class SdkInfoAddRequest implements Serializable {

    /**
     * SDK名称
     */
    private String name;

    /**
     * SDK存储路径
     */
    private String path;


}
