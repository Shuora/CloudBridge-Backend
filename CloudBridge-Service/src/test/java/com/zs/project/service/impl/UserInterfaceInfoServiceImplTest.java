package com.zs.project.service.impl;

import com.zs.project.service.UserInterfaceInfoService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * @author ZhuangShuo
 * @date 2024/7/11
 * @description
 */
@SpringBootTest
class UserInterfaceInfoServiceImplTest {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Test
    void invokeCount() {
        boolean b = userInterfaceInfoService.invokeCount(1L, 1L);
        Assertions.assertTrue(b);
    }
}