package com.zs.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.project.common.ErrorCode;
import com.zs.project.constant.CommonConstant;
import com.zs.project.domain.dto.userinterfaceinfo.UserInterfaceInfoQueryRequest;
import com.zs.project.domain.entity.UserInterfaceInfo;
import com.zs.project.domain.vo.UserInterfaceInfoVO;
import com.zs.project.exception.BusinessException;
import com.zs.project.service.UserInterfaceInfoService;
import com.zs.project.mapper.UserInterfaceInfoMapper;
import com.zs.project.utils.SqlUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lenovo
 * @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Service实现
 * @createDate 2024-07-17 17:48:26
 */
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
        implements UserInterfaceInfoService {


    private UpdateWrapper<UserInterfaceInfo> updateWrapper;

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long id = userInterfaceInfo.getId();
        Long userId = userInterfaceInfo.getUserId();
        Long interfaceInfoId = userInterfaceInfo.getInterfaceInfoId();
        Integer totalNum = userInterfaceInfo.getTotalNum();
        Integer leftNum = userInterfaceInfo.getLeftNum();
        Integer status = userInterfaceInfo.getStatus();
        Date createTime = userInterfaceInfo.getCreateTime();
        Date updateTime = userInterfaceInfo.getUpdateTime();
        Integer isDelete = userInterfaceInfo.getIsDelete();

        // 创建时，参数不能为空
        if (add) {
            if (id == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "id不能为空");
            }
            if (userId == null || interfaceInfoId == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口或用户不能为空");
            }
        }
        if (userInterfaceInfo.getLeftNum() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "剩余次数不能小于0");
        }

    }

    /**
     * 获取查询包装类
     *
     * @param userInterfaceInfoQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<UserInterfaceInfo> getQueryWrapper(UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest) {
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        if (userInterfaceInfoQueryRequest == null) {
            return queryWrapper;
        }
        String sortField = userInterfaceInfoQueryRequest.getSortField();
        String sortOrder = userInterfaceInfoQueryRequest.getSortOrder();
        Long id = userInterfaceInfoQueryRequest.getId();
        Long userId = userInterfaceInfoQueryRequest.getUserId();
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }


    @Override
    public UserInterfaceInfoVO getUserInterfaceInfoVO(UserInterfaceInfo userInterfaceInfo, HttpServletRequest request) {
        if (userInterfaceInfo == null) {
            return null;
        }
        UserInterfaceInfoVO userInterfaceInfoVO = new UserInterfaceInfoVO();
        userInterfaceInfoVO.setId(userInterfaceInfo.getId());
        userInterfaceInfoVO.setUserId(userInterfaceInfo.getUserId());
        userInterfaceInfoVO.setInterfaceInfoId(userInterfaceInfo.getInterfaceInfoId());
        userInterfaceInfoVO.setTotalNum(userInterfaceInfo.getTotalNum());
        userInterfaceInfoVO.setLeftNum(userInterfaceInfo.getLeftNum());
        userInterfaceInfoVO.setStatus(userInterfaceInfo.getStatus());
        userInterfaceInfoVO.setCreateTime(userInterfaceInfo.getCreateTime());
        userInterfaceInfoVO.setUpdateTime(userInterfaceInfo.getUpdateTime());
        userInterfaceInfoVO.setIsDelete(userInterfaceInfo.getIsDelete());
        return userInterfaceInfoVO;
    }

    @Override
    public Page<UserInterfaceInfoVO> getUserInterfaceInfoVOPage(Page<UserInterfaceInfo> userInterfaceInfoPage, HttpServletRequest request) {
        List<UserInterfaceInfo> userInterfaceInfoList = userInterfaceInfoPage.getRecords();
        Page<UserInterfaceInfoVO> userInterfaceInfoVOPage = new Page<>(userInterfaceInfoPage.getCurrent(), userInterfaceInfoPage.getSize(), userInterfaceInfoPage.getTotal());
        // 将userInterfaceInfoList数据放入userInterfaceInfoVOPage
        userInterfaceInfoVOPage.setRecords(userInterfaceInfoList.stream().map((userInterfaceInfo) -> {
                    UserInterfaceInfoVO userInterfaceInfoVO = this.getUserInterfaceInfoVO(userInterfaceInfo, request);
                    return userInterfaceInfoVO;
                }
        ).collect(Collectors.toList()));

        if (CollectionUtils.isEmpty(userInterfaceInfoList)) {
            return userInterfaceInfoVOPage;
        }

        return userInterfaceInfoVOPage;
    }

    @Override
    public boolean invokeCount(Long interfaceInfoId, Long userId) {
        if (interfaceInfoId == null || userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("interfaceInfoId", interfaceInfoId);
        updateWrapper.eq("userId", userId);
        updateWrapper.gt("leftNum", 0);
        updateWrapper.setSql("leftNum = leftNum-1, totalNum = totalNum + 1");

        return this.update(updateWrapper);
    }

}




