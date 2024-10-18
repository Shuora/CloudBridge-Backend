package com.zs.project.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zs.project.domain.dto.sdkinfo.SdkInfoQueryRequest;
import com.zs.project.domain.entity.SdkInfo;
import com.zs.project.domain.vo.SdkInfoVO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author lenovo
 * @description 针对表【sdk_info(SDK相关信息)】的数据库操作Service
 * @createDate 2024-10-17 11:47:28
 */
public interface SdkInfoService extends IService<SdkInfo> {


    /**
     * 校验
     *
     * @param sdkInfo
     * @param add
     */
    void validSdkInfo(SdkInfo sdkInfo, boolean add);


    /**
     * 获取查询条件
     *
     * @param sdkInfoQueryRequest
     * @return
     */
    QueryWrapper<SdkInfo> getQueryWrapper(SdkInfoQueryRequest sdkInfoQueryRequest);


    /**
     * 获取帖子封装
     *
     * @param sdkInfo
     * @param request
     * @return
     */
    SdkInfoVO getSdkInfoVO(SdkInfo sdkInfo, HttpServletRequest request);

    /**
     * 分页获取帖子封装
     *
     * @param sdkInfoPage
     * @param request
     * @return
     */
    Page<SdkInfoVO> getSdkInfoVOPage(Page<SdkInfo> sdkInfoPage, HttpServletRequest request);

}
