package com.zs.project.service;


/**
 * @author lenovo
 * @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Service
 * @createDate 2024-07-11 17:33:20
 */
public interface InnerUserInterfaceInfoService{

    /**
     * 调用接口
     *
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    public boolean invokeCount(long interfaceInfoId, long userId);

}
