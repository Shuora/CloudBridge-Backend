package com.zs.project.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zs.project.common.ErrorCode;
import com.zs.project.domain.entity.User;
import com.zs.project.exception.BusinessException;
import com.zs.project.mapper.UserMapper;
import com.zs.project.service.InnerUserService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;


/**
 * @author ZhuangShuo
 * @date 2024/7/15
 * @description
 */
@DubboService
public class InnerUserServiceImpl implements InnerUserService {
    @Resource
    private UserMapper userMapper;

    /**
     * 根据访问密钥、符获取用户信息。
     *
     * @param accessKey 用户的访问密钥，用于身份验证。
     * @return Request 返回匹配的用户对象，如果找不到，则返回null。
     * @throws BusinessException 如果访问密钥或密钥识别符为空，则抛出此异常。
     */
    @Override
    public User getInvokeUser(String accessKey) {
        // 验证访问密钥和密钥识别符是否都不为空
        if (StringUtils.isAnyBlank(accessKey)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 构造查询条件，查询拥有指定访问密钥和密钥识别符的用户
        QueryWrapper<User> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("accessKey", accessKey);

        // 根据查询条件尝试获取用户信息
        return userMapper.selectOne(objectQueryWrapper);
    }
}
