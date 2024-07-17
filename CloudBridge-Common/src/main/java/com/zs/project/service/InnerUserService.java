package com.zs.project.service;

import com.zs.project.domain.entity.User;

/**
 * 用户服务
 *
 * @author ZhuangShuo
 */
public interface InnerUserService {

    /**
     * 数据库中查是否已分配给用户密钥（acessKey）
     *
     * @param accessKey
     * @return
     */
    User getInvokeUser(String accessKey);

}
