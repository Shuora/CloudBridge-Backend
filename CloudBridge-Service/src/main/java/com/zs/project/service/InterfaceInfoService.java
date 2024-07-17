package com.zs.project.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zs.project.domain.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.zs.project.domain.entity.InterfaceInfo;
import com.zs.project.domain.vo.InterfaceInfoVO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author lenovo
 * @description 针对表【interface_info(接口信息)】的数据库操作Service
 * @createDate 2024-07-17 17:48:55
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {


    /**
     * 校验
     *
     * @param interfaceinfo
     * @param add
     */
    void validInterfaceInfo(InterfaceInfo interfaceinfo, boolean add);


    /**
     * 获取查询条件
     *
     * @param interfaceinfoQueryRequest
     * @return
     */
    QueryWrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryRequest interfaceinfoQueryRequest);


    /**
     * 获取帖子封装
     *
     * @param interfaceinfo
     * @param request
     * @return
     */
    InterfaceInfoVO getInterfaceInfoVO(InterfaceInfo interfaceinfo, HttpServletRequest request);

    /**
     * 分页获取帖子封装
     *
     * @param interfaceinfoPage
     * @param request
     * @return
     */
    Page<InterfaceInfoVO> getInterfaceInfoVOPage(Page<InterfaceInfo> interfaceinfoPage, HttpServletRequest request);

}
