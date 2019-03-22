package com.mmall.controller.backend;


import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
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
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping("get_category.do")
    @ResponseBody
    public ServerResponse<List<Category>> getCategory(
            HttpSession session,
            @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
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
            HttpSession session,
             @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId,
            String categoryName
        ){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByError("未登录");
        }
        if (!iUserService.checkAdminRole(user).isSuccess()) {
            ServerResponse.createByError("当前操作需要管理员权限");
        }
        return iCategoryService.addCategory(categoryId,categoryName);
    }

    /**
     * .修改品类名字
     * @param session
     * @param categoryId
     * @param categoryName
     * @return
     */
    @RequestMapping("set_category_name.do")
    @ResponseBody
    public  ServerResponse<String> setCategoryName(HttpSession session,Integer categoryId,String categoryName){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
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
            HttpSession session,
            @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByError("未登录");
        }
        if (!iUserService.checkAdminRole(user).isSuccess()) {
            ServerResponse.createByError("当前操作需要管理员权限");

        }

        return iCategoryService.getDeepCategory(categoryId);
    }

}
