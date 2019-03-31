package com.mmall.controller.backend;


import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/manage/product/")
public class ProductManagerController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;
    /**
     * 新增或者修改
     * @param product
     * @return
     */
     @RequestMapping("save.do")
     @ResponseBody
       public ServerResponse save(HttpServletRequest request,
                                            Product product) {
         String token  = CookieUtil.readLoginToken(request);
         if(StringUtils.isEmpty(token)){
             return ServerResponse.createByError("获取不到session信息");
         }
         String strUser = RedisPoolUtil.get(token);
         User user  = JsonUtil.string2Obj(strUser,User.class);
        if (user == null) {
            return ServerResponse.createByError("未登录");
        }
        if (!iUserService.checkAdminRole(user).isSuccess()) {
            ServerResponse.createByError("当前操作需要管理员权限");

        }


        return iProductService.save(product);
    }

    /**
     * .产品上下架
     * @param productId
     * @param status
     * @return
     */
    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpServletRequest request, Integer productId, Integer status) {
        String token  = CookieUtil.readLoginToken(request);
        if(org.springframework.util.StringUtils.isEmpty(token)){
            return ServerResponse.createByError("获取不到session信息");
        }
        String strUser = RedisPoolUtil.get(token);
        User user  = JsonUtil.string2Obj(strUser,User.class);
        if (user == null) {
            return ServerResponse.createByError("未登录");
        }
        if (!iUserService.checkAdminRole(user).isSuccess()) {
            ServerResponse.createByError("当前操作需要管理员权限");
        }
        return iProductService.setSaleStatus(productId, status);

    }

    /**
     * 产品详情
     * @param productId
     * @return
     */
     @RequestMapping("detail.do")
     @ResponseBody
     public ServerResponse detail(HttpServletRequest request,Integer productId){
         String token  = CookieUtil.readLoginToken(request);
         if(org.springframework.util.StringUtils.isEmpty(token)){
             return ServerResponse.createByError("获取不到session信息");
         }
         String strUser = RedisPoolUtil.get(token);
         User user  = JsonUtil.string2Obj(strUser,User.class);
         if (user == null) {
             return ServerResponse.createByError("未登录");
         }
         if (!iUserService.checkAdminRole(user).isSuccess()) {
             ServerResponse.createByError("当前操作需要管理员权限");
         }
        return iProductService.detail(productId);
     }
     /*
     列表页
      */
     @RequestMapping("list.do")
     @ResponseBody
     public ServerResponse<PageInfo> list(@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
                                HttpServletRequest request){

         String token  = CookieUtil.readLoginToken(request);
         if(StringUtils.isEmpty(token)){
             return ServerResponse.createByError("获取不到session信息");
         }
         String strUser = RedisPoolUtil.get(token);
         User user  = JsonUtil.string2Obj(strUser,User.class);
         if (user == null) {
             return ServerResponse.createByError("未登录");
         }
         if (!iUserService.checkAdminRole(user).isSuccess()) {
             ServerResponse.createByError("当前操作需要管理员权限");
         }

         return iProductService.list(pageNum,pageSize);
     }

    /**
     * 产品搜索
     * @param pageNum
     * @param pageSize
     * @param productName
     * @param productId
     * @return
     */
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> search(@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
                                        HttpServletRequest request,
                                           String productName,
                                           String productId){

        String token  = CookieUtil.readLoginToken(request);
        if(org.springframework.util.StringUtils.isEmpty(token)){
            return ServerResponse.createByError("获取不到session信息");
        }
        String strUser = RedisPoolUtil.get(token);
        User user  = JsonUtil.string2Obj(strUser,User.class);        if (user == null) {
            return ServerResponse.createByError("未登录");
        }
        if (!iUserService.checkAdminRole(user).isSuccess()) {
            ServerResponse.createByError("当前操作需要管理员权限");
        }

        return iProductService.search(pageNum,pageSize,productName,productId);
    }

    /**
     * 上传文件
     * @param request
     * @param file
     * @return
     */
  @RequestMapping("upload.do")
  @ResponseBody
  public ServerResponse<Map> upload(HttpServletRequest request,

          @RequestParam(value = "upload_file",required = false) MultipartFile file){

      String token  = CookieUtil.readLoginToken(request);
      if(org.springframework.util.StringUtils.isEmpty(token)){
          return ServerResponse.createByError("获取不到session信息");
      }
      String strUser = RedisPoolUtil.get(token);
      User user  = JsonUtil.string2Obj(strUser,User.class);
      if (user == null) {
          return ServerResponse.createByError("未登录");
      }
      if (!iUserService.checkAdminRole(user).isSuccess()) {
          ServerResponse.createByError("当前操作需要管理员权限");
      }


      //创建临时保存文件的文件夹
      String path = request.getSession().getServletContext().getRealPath("upload");
      String resultFilePath = iFileService.upload(file, path);
      if(StringUtils.isBlank(resultFilePath)){
          return ServerResponse.createByError("上传失败");
      }
      //正式路径
      String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+resultFilePath;
      Map urlMap = new HashMap();
      urlMap.put("uri",resultFilePath);
      urlMap.put("url",url);
      return ServerResponse.createBySuccess("上传成功",urlMap);
  }


    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map richtextImgUpload(HttpSession session, @RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        Map resultMap = Maps.newHashMap();
        String token  = CookieUtil.readLoginToken(request);
        if(org.springframework.util.StringUtils.isEmpty(token)){
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员");
            return resultMap;        }
        String strUser = RedisPoolUtil.get(token);
        User user  = JsonUtil.string2Obj(strUser,User.class);
        if(user == null){
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员");
            return resultMap;
        }
        //富文本中对于返回值有自己的要求,我们使用是simditor所以按照simditor的要求进行返回
//        {
//            "success": true/false,
//                "msg": "error message", # optional
//            "file_path": "[real file path]"
//        }
        /*if(!iUserService.checkAdminRole(user).isSuccess()){
            resultMap.put("success",false);
            resultMap.put("msg","无权限操作");
            return resultMap;
        }*/
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            if(StringUtils.isBlank(targetFileName)){
                resultMap.put("success",false);
                resultMap.put("msg","上传失败");
                return resultMap;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
            resultMap.put("file_path",url);
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");
            return resultMap;

    }



}
