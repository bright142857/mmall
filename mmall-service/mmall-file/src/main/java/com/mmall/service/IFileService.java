package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @ClassName FileService
 * @Description TODO
 * @Author wangmingliang
 * @Date 2020/11/30 15:35
 */
public interface IFileService {
    /**
     *  上传附件
     * @param file
     * @param path 临时保存文件的地方
     * @return
     */
    String upload(MultipartFile file,String path);

}
