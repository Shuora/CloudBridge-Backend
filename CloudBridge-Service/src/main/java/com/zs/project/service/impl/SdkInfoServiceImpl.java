package com.zs.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.project.common.ErrorCode;
import com.zs.project.constant.CommonConstant;
import com.zs.project.domain.dto.sdkinfo.SdkInfoQueryRequest;
import com.zs.project.domain.entity.SdkInfo;
import com.zs.project.domain.vo.SdkInfoVO;
import com.zs.project.exception.BusinessException;
import com.zs.project.exception.ThrowUtils;
import com.zs.project.mapper.SdkInfoMapper;
import com.zs.project.service.SdkInfoService;
import com.zs.project.utils.SqlUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lenovo
 * @description 针对表【sdk_info(SDK相关信息)】的数据库操作Service实现
 * @createDate 2024-10-17 11:47:28
 */
@Service
public class SdkInfoServiceImpl extends ServiceImpl<SdkInfoMapper, SdkInfo>
        implements SdkInfoService {


    @Override
    public void validSdkInfo(SdkInfo sdkInfo, boolean add) {
        if (sdkInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = sdkInfo.getName();
        String url = sdkInfo.getPath();

        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(name, url), ErrorCode.PARAMS_ERROR);
        }
    }


    /**
     * 获取查询包装类
     *
     * @param sdkInfoQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<SdkInfo> getQueryWrapper(SdkInfoQueryRequest sdkInfoQueryRequest) {
        QueryWrapper<SdkInfo> queryWrapper = new QueryWrapper<>();
        if (sdkInfoQueryRequest == null) {
            return queryWrapper;
        }
        String sortField = sdkInfoQueryRequest.getSortField();
        String sortOrder = sdkInfoQueryRequest.getSortOrder();
        Long id = sdkInfoQueryRequest.getId();
        Long userId = sdkInfoQueryRequest.getUserId();
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }


    @Override
    public SdkInfoVO getSdkInfoVO(SdkInfo sdkInfo, HttpServletRequest request) {
        SdkInfoVO sdkInfoVO = new SdkInfoVO();
        sdkInfoVO.setId(sdkInfo.getId());
        sdkInfoVO.setName(sdkInfo.getName());
        sdkInfoVO.setPath(sdkInfo.getPath());
        sdkInfoVO.setStatus(sdkInfo.getStatus());
        sdkInfoVO.setUserId(sdkInfo.getUserId());
        sdkInfoVO.setCreateTime(sdkInfo.getCreateTime());
        sdkInfoVO.setUpdateTime(sdkInfo.getUpdateTime());
        sdkInfoVO.setIsDelete(sdkInfo.getIsDelete());
        return sdkInfoVO;
    }

    @Override
    public Page<SdkInfoVO> getSdkInfoVOPage(Page<SdkInfo> sdkInfoPage, HttpServletRequest request) {
        List<SdkInfo> sdkInfoList = sdkInfoPage.getRecords();
        Page<SdkInfoVO> sdkInfoVOPage = new Page<>(sdkInfoPage.getCurrent(), sdkInfoPage.getSize(), sdkInfoPage.getTotal());
        // 将sdkInfoList数据放入sdkInfoVOPage
        sdkInfoVOPage.setRecords(sdkInfoList.stream().map((sdkInfo) -> {
                    SdkInfoVO sdkInfoVO = this.getSdkInfoVO(sdkInfo, request);
                    return sdkInfoVO;
                }
        ).collect(Collectors.toList()));

        if (CollectionUtils.isEmpty(sdkInfoList)) {
            return sdkInfoVOPage;
        }

        return sdkInfoVOPage;
    }


}




