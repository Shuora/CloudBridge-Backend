package com.zs.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zs.project.domain.entity.UserInterfaceInfo;

import java.util.List;

/**
 * @author lenovo
 * @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Mapper
 * @createDate 2024-07-17 17:48:26
 * @Entity com.zs.project.domain.entity.UserInterfaceInfo
 */
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {

    List<UserInterfaceInfo> listTopInvokeInterfaceInfo(int limit);
}




