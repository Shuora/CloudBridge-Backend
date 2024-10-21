package com.zs.project.exception;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.zs.project.common.BaseResponse;
import com.zs.project.common.ErrorCode;
import com.zs.project.common.ResultUtils;
import com.zs.project.domain.dto.interfaceinfo.InterfaceInfoInvokeRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author ZhuangShuo
 * @date 2024/7/18
 * @description 注意方法签名要完全一致，包括返回值和参数
 */
@Slf4j
@Component
public class SentinelHandler {

    public static BaseResponse<Object> doActionBlockHandler(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest, HttpServletRequest request, BlockException e) {
        log.error("sentinel配置自定义限流了:{}", e);
        return ResultUtils.error(ErrorCode.FORBIDDEN_ERROR, "访问接口过快，请稍后再试！");
    }

    public static BaseResponse<Object> doActionFallback(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest, HttpServletRequest request, Throwable e) throws Throwable {
        if (e instanceof BusinessException) {
            throw e;
        }
        log.error("程序逻辑异常了:{}", e);
        return ResultUtils.error(ErrorCode.FORBIDDEN_ERROR, "服务器暂时不可用");
    }
}
