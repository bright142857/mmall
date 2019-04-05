package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/springsession/")
public class SpringSessionUserController {

    @Autowired
    private IUserService iUserService;


    /**
     * 用户登录方法
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> login(HttpSession session, String username,
                                      String password, HttpServletResponse httpServletResponse){

        ServerResponse<User>  serverResponse = iUserService.login(username,password);
        if(serverResponse.isSuccess()){
          /*  CookieUtil.writeLoginToken(httpServletResponse,session.getId());
            RedisShardedPoolUtil.setEx(session.getId(), JsonUtil.obj2String(serverResponse.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
*/
            session.setAttribute(Const.CURRENT_USER,serverResponse.getData());

        }

        return serverResponse;
    }




    /**
     * 用户登出
     * @return
     */
    @RequestMapping(value = "logout.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
      /*  String token  = CookieUtil.readLoginToken(request);
        CookieUtil.delLoginToken(request,response);
        RedisShardedPoolUtil.del(token);*/
        return ServerResponse.createBySuccess();
    }


    /**
     * 获取用户信息
     * @return
     */
    @RequestMapping(value = "get_user_info.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){
       /* String token  = CookieUtil.readLoginToken(request);
       if(StringUtils.isEmpty(token)){
           return ServerResponse.createByError("获取不到session信息");
       }
       String strUser = RedisShardedPoolUtil.get(token);
       User user  = JsonUtil.string2Obj(strUser,User.class);*/
      User user =  (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByError("获取不到session信息");
       }
       return ServerResponse.createBySuccess(user);
    }


}
