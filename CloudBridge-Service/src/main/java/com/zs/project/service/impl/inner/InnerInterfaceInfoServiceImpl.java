package com.zs.project.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zs.project.common.ErrorCode;
import com.zs.project.domain.entity.InterfaceInfo;
import com.zs.project.exception.BusinessException;
import com.zs.project.mapper.InterfaceInfoMapper;
import com.zs.project.service.InnerInterfaceInfoService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import java.net.MalformedURLException;


/**
 * @author ZhuangShuo
 * @date 2024/7/15
 * @description
 */
@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;


    /**
     * 根据路径和方法名称获取接口信息。
     *
     * @param path   接口的路径，用于唯一标识一个接口。
     * @param method 接口的方法，与路径结合使用以唯一标识一个接口。
     * @return InterfaceInfo 查询到的接口信息对象，如果不存在则返回null。
     * @throws BusinessException 如果路径或方法为空，则抛出此异常，表示参数错误。
     */
    @Override
    public InterfaceInfo getInterfaceInfo(String path, String method) {
        // 验证路径和方法是否为空，如果为空则抛出业务异常
        if (StringUtils.isAnyBlank(path, method)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        java.net.URL urlObj = null;
        try {
            urlObj = new java.net.URL(path);
        } catch (MalformedURLException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "路径格式错误");
        }
        String newPath = urlObj.getPath();  // 这会返回 "/api/getUserNameByPost"

        // 构造查询条件，查询与给定路径和方法匹配的接口信息
        QueryWrapper<InterfaceInfo> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("url", newPath);
        objectQueryWrapper.eq("method", method);

        // 执行查询并返回结果
        return interfaceInfoMapper.selectOne(objectQueryWrapper);
    }
}
