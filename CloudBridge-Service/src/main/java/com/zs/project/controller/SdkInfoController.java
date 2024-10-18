package com.zs.project.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zs.project.annotation.AuthCheck;
import com.zs.project.common.BaseResponse;
import com.zs.project.common.DeleteRequest;
import com.zs.project.common.ErrorCode;
import com.zs.project.common.ResultUtils;
import com.zs.project.constant.UserConstant;
import com.zs.project.domain.dto.sdkinfo.SdkInfoAddRequest;
import com.zs.project.domain.dto.sdkinfo.SdkInfoQueryRequest;
import com.zs.project.domain.dto.sdkinfo.SdkInfoUpdateRequest;
import com.zs.project.domain.entity.SdkInfo;
import com.zs.project.domain.entity.User;
import com.zs.project.domain.vo.SdkInfoVO;
import com.zs.project.exception.BusinessException;
import com.zs.project.exception.ThrowUtils;
import com.zs.project.service.SdkInfoService;
import com.zs.project.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author ZhuangShuo
 * @date 2024/10/17
 * @description SDK
 */
@RestController
@RequestMapping("/sdkInfo")
@Slf4j
public class SdkInfoController {

    @Autowired
    private SdkInfoService sdkInfoService;
    @Autowired
    private UserService userService;

    /**
     * 创建
     *
     * @param sdkInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addSdkInfo(@RequestBody SdkInfoAddRequest sdkInfoAddRequest, HttpServletRequest request) {
        if (sdkInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SdkInfo sdkInfo = new SdkInfo();
        BeanUtils.copyProperties(sdkInfoAddRequest, sdkInfo);
        sdkInfoService.validSdkInfo(sdkInfo, true);
        User loginUser = userService.getLoginUser(request);
        sdkInfo.setUserId(loginUser.getId());
        boolean result = sdkInfoService.save(sdkInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newSdkInfoId = sdkInfo.getId();
        return ResultUtils.success(newSdkInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteSdkInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        SdkInfo oldSdkInfo = sdkInfoService.getById(id);
        ThrowUtils.throwIf(oldSdkInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldSdkInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = sdkInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param sdkInfoUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateSdkInfo(@RequestBody SdkInfoUpdateRequest sdkInfoUpdateRequest) {
        if (sdkInfoUpdateRequest == null || sdkInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SdkInfo sdkInfo = new SdkInfo();
        BeanUtils.copyProperties(sdkInfoUpdateRequest, sdkInfo);
        // 参数校验
        sdkInfoService.validSdkInfo(sdkInfo, false);
        long id = sdkInfoUpdateRequest.getId();
        // 判断是否存在
        SdkInfo oldSdkInfo = sdkInfoService.getById(id);
        ThrowUtils.throwIf(oldSdkInfo == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = sdkInfoService.updateById(sdkInfo);
        return ResultUtils.success(result);
    }


    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<SdkInfoVO> getSdkInfoVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SdkInfo sdkInfo = sdkInfoService.getById(id);
        if (sdkInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(sdkInfoService.getSdkInfoVO(sdkInfo, request));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param sdkInfoQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<SdkInfoVO>> listSdkInfoVOByPage(@RequestBody SdkInfoQueryRequest sdkInfoQueryRequest,
                                                             HttpServletRequest request) {
        long current = sdkInfoQueryRequest.getCurrent();
        long size = sdkInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<SdkInfo> sdkInfoPage = sdkInfoService.page(new Page<>(current, size),
                sdkInfoService.getQueryWrapper(sdkInfoQueryRequest));
        return ResultUtils.success(sdkInfoService.getSdkInfoVOPage(sdkInfoPage, request));
    }


}
