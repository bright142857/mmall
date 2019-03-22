package com.mmall.util;



import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;


public class FTPUtil {

    private  static  final Logger logger = LoggerFactory.getLogger(FTPUtil.class);
    //获取ftp的属性
    private  static  String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private  static String ftpPort = PropertiesUtil.getProperty("ftp.port");
    private  static String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private  static String ftpPass = PropertiesUtil.getProperty("ftp.pass");
    private  static String ftpFile = PropertiesUtil.getProperty("ftp.file");


    private  String ip;
    private  int port;
    private  String user;
    private  String pass;
    private FTPClient ftpClient;

    private FTPUtil(String ip, int port, String user, String pass) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pass = pass;
    }

    public static  boolean fileUpload(List<File> files) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIp,Integer.parseInt(ftpPort),ftpUser,ftpPass);
        logger.info("开始上传ftp服务器");
        boolean isSuccess = ftpUtil.fileUpload(ftpFile,files);
        logger.info("上传结束，上传结果:{}",isSuccess);

        return isSuccess;

    }

    private boolean fileUpload(String remotePath, List<File> files) throws IOException {
        boolean isSuccess = false;
        FileInputStream fileInputStream = null;
        if (this.connService(this.ip, this.port, this.user, this.pass)) {
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                for(File fileItem : files){
                    fileInputStream = new FileInputStream(fileItem);
                    isSuccess  = ftpClient.storeFile(fileItem.getName(),fileInputStream);
                }
            } catch (IOException e) {
                logger.error("上传ftp服务器异常", e);
                isSuccess = false;
            }finally {
                fileInputStream.close();
                ftpClient.disconnect();
            }
        }


        return isSuccess;

    }

    private boolean connService(String ip, int port, String user, String pass) {
        boolean isSuccess = false;
        ftpClient = new FTPClient();

        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user, pass);
        } catch (IOException e) {
            logger.error("连接服务器失败", e);
        }


        return isSuccess;

    }




    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }


}
