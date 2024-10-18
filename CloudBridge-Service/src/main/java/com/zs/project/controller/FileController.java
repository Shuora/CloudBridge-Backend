package com.zs.project.controller;

import com.zs.project.common.BaseResponse;
import com.zs.project.common.ErrorCode;
import com.zs.project.common.ResultUtils;
import com.zs.project.domain.entity.SdkInfo;
import com.zs.project.exception.BusinessException;
import com.zs.project.exception.ThrowUtils;
import com.zs.project.service.SdkInfoService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;

/**
 * @author ZhuangShuo
 * @date 2024/10/17
 * @description
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Value("${sdk.path}")
    private String baseSDKPath;

    @Autowired
    private SdkInfoService sdkInfoService;

    /**
     * SDK上传
     *
     * @param file
     * @return
     */
    @PostMapping("/uploadSDK")
    public BaseResponse<String> upload(@RequestParam("file") MultipartFile file) {//参数名字不能随便写，必须和前端请求的name保持一致
        //file是一个临时文件，需要转存到指定位置，否则本次请求完成之后临时文件会删除
        log.info(file.toString());

        //原始文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf('.'));

        //使用uuid重新生成文件名，防止文件名称重复造成文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix;

        //创建一个目录对象
        File dir = new File(baseSDKPath);
        //判断当前目录是否存在
        if (!dir.exists()) {
            //目录不存在，需要创建
            dir.mkdirs();
        }
        try {
            //将临时文件转存到指定位置
            file.transferTo(new File(baseSDKPath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String newPath = baseSDKPath + fileName;
        return ResultUtils.success(newPath);
    }

    /**
     * SDK下载
     */
    @PostMapping("/downloadSDK")
    public void download(@RequestParam("sdkId") Long sdkId, HttpServletResponse response) {
        try {
            SdkInfo sdkInfo = sdkInfoService.getById(sdkId);
            ThrowUtils.throwIf((sdkInfo == null || sdkInfo.getPath() == null || sdkInfo.getName() == null), ErrorCode.PARAMS_ERROR);

            String fileName = sdkInfo.getName();
            File file = new File(sdkInfo.getPath());
            ThrowUtils.throwIf(!file.exists(), ErrorCode.NOT_FOUND_ERROR);


            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            response.setHeader("Content-Length", String.valueOf(file.length()));

            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                 BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream())) {

                byte[] buffer = new byte[8192];
                int read;
                while ((read = bis.read(buffer)) != -1) {
                    bos.write(buffer, 0, read);
                }
                bos.flush();
            }
        } catch (Exception e) {
            log.error("文件下载失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件下载失败");
        }


    }
}