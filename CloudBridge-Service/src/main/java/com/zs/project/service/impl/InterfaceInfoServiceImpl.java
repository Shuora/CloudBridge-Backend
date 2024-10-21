package com.zs.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.project.common.ErrorCode;
import com.zs.project.constant.CommonConstant;
import com.zs.project.domain.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.zs.project.domain.entity.InterfaceInfo;
import com.zs.project.domain.entity.UserInterfaceInfo;
import com.zs.project.domain.vo.InterfaceInfoVO;
import com.zs.project.exception.BusinessException;
import com.zs.project.exception.ThrowUtils;
import com.zs.project.mapper.InterfaceInfoMapper;
import com.zs.project.mapper.UserInterfaceInfoMapper;
import com.zs.project.service.InterfaceInfoService;
import com.zs.project.utils.SqlUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lenovo
 * @description 针对表【interface_info(接口信息)】的数据库操作Service实现
 * @createDate 2024-07-17 17:48:55
 */

@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
        implements InterfaceInfoService {

    @Autowired
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceinfo, boolean add) {
        if (interfaceinfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = interfaceinfo.getName();
        String url = interfaceinfo.getUrl();

        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(name, url), ErrorCode.PARAMS_ERROR);
        }

    }


    /**
     * 获取查询包装类
     *
     * @param interfaceinfoQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryRequest interfaceinfoQueryRequest) {
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        if (interfaceinfoQueryRequest == null) {
            return queryWrapper;
        }
        String sortField = interfaceinfoQueryRequest.getSortField();
        String sortOrder = interfaceinfoQueryRequest.getSortOrder();
        Long id = interfaceinfoQueryRequest.getId();
        Long userId = interfaceinfoQueryRequest.getUserId();
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }


    @Override
    public InterfaceInfoVO getInterfaceInfoVO(InterfaceInfo interfaceinfo, HttpServletRequest request) {
        InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
        interfaceInfoVO.setId(interfaceinfo.getId());
        interfaceInfoVO.setName(interfaceinfo.getName());
        interfaceInfoVO.setDescription(interfaceinfo.getDescription());
        interfaceInfoVO.setUrl(interfaceinfo.getUrl());
        interfaceInfoVO.setRequestParams(interfaceinfo.getRequestParams());
        interfaceInfoVO.setRequestHeader(interfaceinfo.getRequestHeader());
        interfaceInfoVO.setResponseHeader(interfaceinfo.getResponseHeader());
        interfaceInfoVO.setStatus(interfaceinfo.getStatus());
        interfaceInfoVO.setMethod(interfaceinfo.getMethod());
        interfaceInfoVO.setUserId(interfaceinfo.getUserId());
        interfaceInfoVO.setIsDelete(interfaceinfo.getIsDelete());
        interfaceInfoVO.setCreateTime(interfaceinfo.getCreateTime());
        interfaceInfoVO.setUpdateTime(interfaceinfo.getUpdateTime());
        interfaceInfoVO.setSdkId(interfaceinfo.getSdkId());
        return interfaceInfoVO;
    }

    @Override
    public Page<InterfaceInfoVO> getInterfaceInfoVOPage(Page<InterfaceInfo> interfaceinfoPage, HttpServletRequest request) {
        List<InterfaceInfo> interfaceinfoList = interfaceinfoPage.getRecords();
        Page<InterfaceInfoVO> interfaceinfoVOPage = new Page<>(interfaceinfoPage.getCurrent(), interfaceinfoPage.getSize(), interfaceinfoPage.getTotal());
        // 将interfaceinfoList数据放入interfaceinfoVOPage
        interfaceinfoVOPage.setRecords(interfaceinfoList.stream().map((interfaceinfo) -> {
                    InterfaceInfoVO interfaceinfoVO = this.getInterfaceInfoVO(interfaceinfo, request);
                    return interfaceinfoVO;
                }
        ).collect(Collectors.toList()));

        if (CollectionUtils.isEmpty(interfaceinfoList)) {
            return interfaceinfoVOPage;
        }

        return interfaceinfoVOPage;
    }

    @Override
    public Page<InterfaceInfoVO> getInterfaceInfoVOPageAndUser(Page<InterfaceInfo> interfaceinfoPage, HttpServletRequest request) {
        List<InterfaceInfo> interfaceinfoList = interfaceinfoPage.getRecords();
        Page<InterfaceInfoVO> interfaceinfoVOPage = new Page<>(interfaceinfoPage.getCurrent(), interfaceinfoPage.getSize(), interfaceinfoPage.getTotal());
        // 将interfaceinfoList数据放入interfaceinfoVOPage
        interfaceinfoVOPage.setRecords(interfaceinfoList.stream().map((interfaceinfo) -> {
            InterfaceInfoVO interfaceinfoVO = this.getInterfaceInfoVO(interfaceinfo, request);
                    return interfaceinfoVO;
                }
        ).collect(Collectors.toList()));

        if (CollectionUtils.isEmpty(interfaceinfoList)) {
            return interfaceinfoVOPage;
        }

        return interfaceinfoVOPage;
    }

    @Override
    public boolean buyInterfaceCount(Long interfaceInfoId, Long userId) {

        // 根据接口id和用户id查询是否已经存在
        LambdaQueryWrapper<UserInterfaceInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInterfaceInfo::getInterfaceInfoId, interfaceInfoId);
        queryWrapper.eq(UserInterfaceInfo::getUserId, userId);
        UserInterfaceInfo userInterfaceInfo;
        userInterfaceInfo = userInterfaceInfoMapper.selectOne(queryWrapper);
        if (userInterfaceInfo != null) {
            // 已经存在，更新
            userInterfaceInfo.setLeftNum(userInterfaceInfo.getLeftNum() + 1000);
            return userInterfaceInfoMapper.updateById(userInterfaceInfo) > 0;
        }
        // 不存在，新增
        userInterfaceInfo = new UserInterfaceInfo();
        userInterfaceInfo.setInterfaceInfoId(interfaceInfoId);
        userInterfaceInfo.setUserId(userId);
        userInterfaceInfo.setTotalNum(0);
        userInterfaceInfo.setLeftNum(1000);

        return userInterfaceInfoMapper.insert(userInterfaceInfo) > 0;

    }

}








