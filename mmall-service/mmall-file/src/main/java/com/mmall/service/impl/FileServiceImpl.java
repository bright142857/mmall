package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @ClassName FileServiceImpl
 * @Description TODO
 * @Author wangmingliang
 * @Date 2020/11/30 15:38
 */
@Service
@Slf4j
public class FileServiceImpl implements IFileService {

    @Override
    public String upload(MultipartFile file, String path) {
        //文件名
        String fileName = file.getOriginalFilename();
        //防止文件名重复，创建的新文件名
        String FileNewName = UUID.randomUUID() + "." + fileName.substring(fileName.lastIndexOf(".") + 1);
        log.info("即将上传文件,文件名:{},新文件名:{},上传路径:{}", fileName, FileNewName, path);
        //创建文件夹
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            //复制权限===>创建
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File fileTarget = new File(path,FileNewName);
        try {
            file.transferTo(fileTarget);
            //上传ftp服务器
            boolean isSuccess =  FTPUtil.fileUpload(Lists.<File>newArrayList(fileTarget));
            if(!isSuccess){
                return null;
            }

            //删除tomcat上的文件
            fileTarget.delete();
        } catch (IOException e) {
            log.error("上传失败",e);
            return null;
        }
        return fileTarget.getName();
    }
}
