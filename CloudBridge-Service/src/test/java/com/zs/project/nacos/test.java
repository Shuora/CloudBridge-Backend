package com.zs.project.nacos;

import com.zs.project.utils.NacosUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author ZhuangShuo
 * @date 2024/10/20
 * @description
 */
@SpringBootTest
public class test {

    @Test
    public void testGetNacosConfig() {
        NacosUtils nacosUtils = new NacosUtils();
        String config = nacosUtils.getConfig();
        System.out.println(config);
    }


}
