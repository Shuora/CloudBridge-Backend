package com.zs.project.controller;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zs.project.annotation.AuthCheck;
import com.zs.project.client.ZSAPIClient;
import com.zs.project.common.*;
import com.zs.project.constant.UserConstant;
import com.zs.project.domain.dto.interfaceinfo.InterfaceInfoAddRequest;
import com.zs.project.domain.dto.interfaceinfo.InterfaceInfoInvokeRequest;
import com.zs.project.domain.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.zs.project.domain.dto.interfaceinfo.InterfaceInfoUpdateRequest;
import com.zs.project.domain.entity.InterfaceInfo;
import com.zs.project.domain.entity.User;
import com.zs.project.domain.enums.InterfaceInfoStatusEnum;
import com.zs.project.domain.vo.InterfaceInfoVO;
import com.zs.project.exception.BusinessException;
import com.zs.project.exception.SentinelHandler;
import com.zs.project.exception.ThrowUtils;
import com.zs.project.service.InterfaceInfoService;
import com.zs.project.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author ZhuangShuo
 * @date 2024/7/5
 * @description
 */
@RestController
@RequestMapping("/interfaceInfo")
@Slf4j
public class InterfaceInfoController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    @Resource
    private ZSAPIClient zsapiClient;

    private final static Gson GSON = new Gson();


    /**
     * 创建
     *
     * @param interfaceInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
        if (interfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        interfaceInfoService.validInterfaceInfo(interfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId());
        boolean result = interfaceInfoService.save(interfaceInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newInterfaceInfoId = interfaceInfo.getId();
        return ResultUtils.success(newInterfaceInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = interfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param interfaceInfoUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest) {
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);
        // 参数校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
        long id = interfaceInfoUpdateRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 将接口信息设置为在线状态。
     * 该方法通过接收一个ID请求，验证请求的有效性后，将对应的接口信息状态更新为在线。
     * 在更新之前，会验证接口信息是否存在，并通过外部服务验证用户身份。
     *
     * @param idRequest 包含需要操作的接口信息ID的请求对象。
     * @return 返回一个包含操作结果的响应对象。
     */
    @PostMapping("/online")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody IdRequest idRequest) {
        // 验证idRequest是否为空或ID值是否有效
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long id = idRequest.getId();
        // 根据ID查询接口信息，确认该信息是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 初始化一个用户对象用于后续的身份验证
        com.zs.project.domain.User user = new com.zs.project.domain.User();
        user.setUserName("test");
        // 通过外部服务验证用户身份，并获取用户名
        String username = zsapiClient.getUsernameByPost(user);
        // 如果用户名为空或不存在，则抛出业务异常
        if (StringUtils.isBlank(username)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口验证失败！");
        }

        // 初始化一个新的接口信息对象，用于更新状态
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.ONLINE.getValue());
        // 更新接口信息的状态为在线
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        // 返回操作结果
        return ResultUtils.success(result);
    }


    /**
     * 下线接口信息
     *
     * @param idRequest 包含接口ID的请求对象
     * @return 返回操作结果的响应对象
     * @throws BusinessException 如果请求参数无效或接口信息不存在，则抛出业务异常
     */
    @PostMapping("/offline")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> offlineInterfaceInfo(@RequestBody IdRequest idRequest) {
        // 校验请求参数是否有效
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long id = idRequest.getId();
        // 根据ID查询接口信息
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        // 如果接口信息不存在，则抛出业务异常
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 初始化一个用户对象，用于后续调用验证接口
        com.zs.project.domain.User user = new com.zs.project.domain.User();
        user.setUserName("test");
        // 调用验证接口，获取用户名
        String username = zsapiClient.getUsernameByPost(user);
        // 如果用户名为空，则抛出业务异常，表示系统错误
        if (StringUtils.isBlank(username)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口验证失败！");
        }

        // 更新接口状态为下线
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.OFFLINE.getValue());
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        // 返回操作结果
        return ResultUtils.success(result);
    }


    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<InterfaceInfoVO> getInterfaceInfoVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(interfaceInfoService.getInterfaceInfoVO(interfaceInfo, request));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<InterfaceInfoVO>> listInterfaceInfoVOByPage(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest,
                                                                         HttpServletRequest request) {
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size),
                interfaceInfoService.getQueryWrapper(interfaceInfoQueryRequest));
        return ResultUtils.success(interfaceInfoService.getInterfaceInfoVOPage(interfaceInfoPage, request));
    }


    @PostMapping("invoke")
    @SentinelResource(value = "invokeInterfaceInfo", blockHandler = "doActionBlockHandler", blockHandlerClass = SentinelHandler.class, fallback = "doActionFallback", fallbackClass = SentinelHandler.class)
    public BaseResponse<Object> invokeInterfaceInfo(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest, HttpServletRequest request) {
        if (interfaceInfoInvokeRequest == null || interfaceInfoInvokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long id = interfaceInfoInvokeRequest.getId();
        String userRequestParams = interfaceInfoInvokeRequest.getUserRequestParams();
        // 根据ID查询接口信息
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        // 如果接口信息不存在，则抛出业务异常
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (oldInterfaceInfo.getStatus() != InterfaceInfoStatusEnum.ONLINE.getValue()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口已关闭！");
        }
        User user = userService.getLoginUser(request);
        String accessKey = user.getAccessKey();
        String secretKey = user.getSecretKey();
        ZSAPIClient tempClient = new ZSAPIClient(accessKey, secretKey);
        Gson gson = new Gson();
        com.zs.project.domain.User userRequest = gson.fromJson(userRequestParams, com.zs.project.domain.User.class);
        String result = tempClient.getUsernameByPost(userRequest);

        return ResultUtils.success(result);
    }


}
