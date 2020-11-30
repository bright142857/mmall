package com.mmall.controller;

import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.service.IFileService;
import com.mmall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * @ClassName FileController
 * @Description TODO
 * @Author wangmingliang
 * @Date 2020/11/30 15:21
 */
@RestController
public class FileController {

    @Autowired
    private IFileService fileService;

    /**
     *  上传文件
     * @param request
     * @param file
     * @return
     */
    @RequestMapping("/update")
    public ServerResponse upload(HttpServletRequest request,
                                 @RequestParam(value = "upload_file",required = false) MultipartFile file){
        HashMap<String, String> resMap = Maps.newHashMap();
        String path = PropertiesUtil.getProperty("ftp.tempUrl");
        String targetFileName = fileService.upload(file,path);
        if (StringUtils.isBlank(targetFileName)) {
            return ServerResponse.createByError("上传失败");
        }
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
        resMap.put("uri",targetFileName);
        resMap.put("url",url);
        return ServerResponse.createBySuccess("上传成功", resMap);
    }


}
