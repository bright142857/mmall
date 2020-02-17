package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 购物车
 */
@Controller
@RequestMapping("/cart/")
public class CartController {
   @Autowired
   private ICartService iCartService;
    @Autowired
    private IUserService iUserService;


    /**
     * 购物车列表
     * @return
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse  list(HttpServletRequest request

    ){
        String token  = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByError("请先登录");
        }
        String strUser = RedisShardedPoolUtil.get(token);
        User user  = JsonUtil.string2Obj(strUser,User.class);
        if (user == null) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.list(user.getId());

    }

    /**
     * 添加购物车
     * @return
     */
    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse  add(HttpServletRequest request,
            Integer productId,
                               Integer count){
        String token  = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByError("请先登录");
        }
        String strUser = RedisShardedPoolUtil.get(token);
        User user  = JsonUtil.string2Obj(strUser,User.class);
        if(user == null){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }



        return iCartService.add(productId,count,user.getId());

    }




    /**
     * .更新购物车某个产品数量
     * @param productId
     * @param count
     * @return
     */
    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse  update(HttpServletRequest request,
                               Integer productId,
                               Integer count){
        String token  = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByError("请先登录");
        }
        String strUser = RedisShardedPoolUtil.get(token);
        User user  = JsonUtil.string2Obj(strUser,User.class);
        if(user == null){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }



        return iCartService.update(productId,count,user.getId());

    }

    /**
     * 移除购物车某个产品
     * @param
     * @param
     * @return
     */
    @RequestMapping("delete_product.do")
    @ResponseBody
    public ServerResponse  deleteProduct(HttpServletRequest request,
                                  String productIds
                                 ){
        String token  = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByError("请先登录");
        }
        String strUser = RedisShardedPoolUtil.get(token);
        User user  = JsonUtil.string2Obj(strUser,User.class);
        if (user == null) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.deleteProduct(productIds, user.getId());

    }

    //全选、

    @RequestMapping("select_all.do")
    @ResponseBody
    public ServerResponse  selectAll(HttpServletRequest request
    ){
        String token  = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByError("请先登录");
        }
        String strUser = RedisShardedPoolUtil.get(token);
        User user  = JsonUtil.string2Obj(strUser,User.class);
        if (user == null) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.checkedOrUncheckedProduct(user.getId(),null,Const.Cart.CHECKED);

    }
    //全不选
    @RequestMapping("un_select_all.do")
    @ResponseBody
    public ServerResponse  unSelectAll(HttpServletRequest request
    ){
        String token  = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByError("请先登录");
        }
        String strUser = RedisShardedPoolUtil.get(token);
        User user  = JsonUtil.string2Obj(strUser,User.class);
        if (user == null) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.checkedOrUncheckedProduct(user.getId(),null,Const.Cart.UN_CHECKED);

    }
    //选中单个
    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse  select(HttpServletRequest request,
                                  Integer productId
    ){
        String token  = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByError("请先登录");
        }
        String strUser = RedisShardedPoolUtil.get(token);
        User user  = JsonUtil.string2Obj(strUser,User.class);
        if (user == null) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.checkedOrUncheckedProduct(user.getId(),productId,Const.Cart.CHECKED);

    }
    //单个不选

    @RequestMapping("un_select.do")
    @ResponseBody
    public ServerResponse  unSelect(HttpServletRequest request,
                                  Integer productId
    ){
        String token  = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByError("请先登录");
        }
        String strUser = RedisShardedPoolUtil.get(token);
        User user  = JsonUtil.string2Obj(strUser,User.class);
        if (user == null) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.checkedOrUncheckedProduct(user.getId(),productId,Const.Cart.UN_CHECKED);

    }
    //产品数量
    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public ServerResponse getCartProductCount(HttpServletRequest request){
        String token  = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByError("请先登录");
        }
        String strUser = RedisShardedPoolUtil.get(token);
        User user  = JsonUtil.string2Obj(strUser,User.class);
        if (user == null) {
            return ServerResponse.createBySuccess(0);
        }

        return iCartService.getCartProductCount(user.getId());
    }














}
