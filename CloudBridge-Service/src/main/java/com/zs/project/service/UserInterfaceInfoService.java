package com.zs.project.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zs.project.domain.dto.userinterfaceinfo.UserInterfaceInfoQueryRequest;
import com.zs.project.domain.entity.UserInterfaceInfo;
import com.zs.project.domain.vo.UserInterfaceInfoVO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author lenovo
 * @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Service
 * @createDate 2024-07-17 17:48:26
 */
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {

    /**
     * 校验
     *
     * @param userInterfaceInfo
     * @param add
     */
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    /**
     * 获取查询条件
     *
     * @param userInterfaceInfoQueryRequest
     * @return
     */
    QueryWrapper<UserInterfaceInfo> getQueryWrapper(UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest);

    /**
     * 获取封装
     *
     * @param userInterfaceInfo
     * @param request
     * @return
     */
    UserInterfaceInfoVO getUserInterfaceInfoVO(UserInterfaceInfo userInterfaceInfo, HttpServletRequest request);

    /**
     * 分页获取封装
     *
     * @param userInterfaceInfoPage
     * @param request
     * @return
     */
    Page<UserInterfaceInfoVO> getUserInterfaceInfoVOPage(Page<UserInterfaceInfo> userInterfaceInfoPage, HttpServletRequest request);

    /**
     * 调用接口
     *
     * @param interfaceInfoId 接口id
     * @param userId
     * @return
     */
    boolean invokeCount(Long interfaceInfoId, Long userId);

    /**
     * 获取剩余调用次数
     *
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean getLeftNumberOfCalls(Long interfaceInfoId, Long userId);
}
