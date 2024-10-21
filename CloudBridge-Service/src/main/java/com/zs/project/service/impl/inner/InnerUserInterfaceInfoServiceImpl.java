package com.zs.project.service.impl.inner;

import com.zs.project.service.InnerUserInterfaceInfoService;
import com.zs.project.service.UserInterfaceInfoService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;


/**
 * @author ZhuangShuo
 * @date 2024/7/15
 * @description
 */
@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {
    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    /**
     * 调用接口的计数方法。
     *
     * @param interfaceInfoId 接口信息的唯一标识ID。此ID用于确定要计数的具体接口。
     * @param userId          用户的唯一标识ID。此ID用于确定是哪个用户进行了接口调用。
     * @return boolean 返回值表示计数操作是否成功。true表示成功，false表示失败。
     */
    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        return userInterfaceInfoService.invokeCount(interfaceInfoId, userId);
    }

}
