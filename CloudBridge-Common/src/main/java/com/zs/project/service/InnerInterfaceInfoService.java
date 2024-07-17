package com.zs.project.service;

import com.zs.project.domain.entity.InterfaceInfo;

/**
 * @author lenovo
 * @description 针对表【interface_info(接口信息)】的数据库操作Service
 * @createDate 2024-07-05 19:03:32
 */
public interface InnerInterfaceInfoService{

    /**
     * 从数据库中查询模拟接口是否存在（请求路径、请求方法）
     *
     * @param path
     * @param method
     * @return
     */
    InterfaceInfo getInterfaceInfo(String path, String method);


}
