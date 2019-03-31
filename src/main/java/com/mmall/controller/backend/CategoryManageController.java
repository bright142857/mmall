package com.mmall.controller.backend;


import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/manage/category/")
public class CategoryManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;

    /**
     * ###获取品类子节点(平级)
     * @param categoryId
     * @return
     */
    @RequestMapping("get_category.do")
    @ResponseBody
    public ServerResponse<List<Category>> getCategory(
            HttpServletRequest request,
            @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        String token  = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByError("获取不到session信息");
        }
        String strUser = RedisShardedPoolUtil.get(token);
        User user  = JsonUtil.string2Obj(strUser,User.class);
        if (user == null) {
            return ServerResponse.createByError("未登录");
        }
        if (!iUserService.checkAdminRole(user).isSuccess()) {
            ServerResponse.createByError("当前操作需要管理员权限");

        }
        return iCategoryService.getCategory(categoryId);
    }

    /**
     * ####增加节点


     * @param session
     * @param categoryId
     * @param categoryName
     * @return
     */
    @RequestMapping("add_category.do")
    @ResponseBody
    public  ServerResponse<String> addCategory(
           HttpServletRequest request,
             @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId,
            String categoryName
        ){
        String token  = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByError("获取不到session信息");
        }
        String strUser = RedisShardedPoolUtil.get(token);
        User user  = JsonUtil.string2Obj(strUser,User.class);        if (user == null) {
            return ServerResponse.createByError("未登录");
        }
        if (!iUserService.checkAdminRole(user).isSuccess()) {
            ServerResponse.createByError("当前操作需要管理员权限");
        }
        return iCategoryService.addCategory(categoryId,categoryName);
    }

    /**
     * .修改品类名字
     * @param categoryId
     * @param categoryName
     * @return
     */
    @RequestMapping("set_category_name.do")
    @ResponseBody
    public  ServerResponse<String> setCategoryName(HttpServletRequest request,Integer categoryId,String categoryName){
        String token  = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByError("获取不到session信息");
        }
        String strUser = RedisShardedPoolUtil.get(token);
        User user  = JsonUtil.string2Obj(strUser,User.class);
        if (user == null) {
            return ServerResponse.createByError("未登录");
        }
        if (!iUserService.checkAdminRole(user).isSuccess()) {
            ServerResponse.createByError("当前操作需要管理员权限");
        }
        return iCategoryService.setCategoryName(categoryId, categoryName);
    }


    /**
     * 获取当前分类id及递归子节点categoryId
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServerResponse<List<Integer>> getDeepCategory(
            HttpServletRequest request,
            @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        String token  = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByError("获取不到session信息");
        }
        String strUser = RedisShardedPoolUtil.get(token);
        User user  = JsonUtil.string2Obj(strUser,User.class);
        if (user == null) {
            return ServerResponse.createByError("未登录");
        }
        if (!iUserService.checkAdminRole(user).isSuccess()) {
            ServerResponse.createByError("当前操作需要管理员权限");

        }

        return iCategoryService.getDeepCategory(categoryId);
    }

}
