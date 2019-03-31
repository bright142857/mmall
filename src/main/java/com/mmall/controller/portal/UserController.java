package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;


    /**
     * 用户登录方法
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(HttpSession session, String username,
                                      String password, HttpServletResponse httpServletResponse){

        ServerResponse<User>  serverResponse = iUserService.login(username,password);
        if(serverResponse.isSuccess()){
            CookieUtil.writeLoginToken(httpServletResponse,session.getId());
            RedisPoolUtil.setEx(session.getId(), JsonUtil.obj2String(serverResponse.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);

        }

        return serverResponse;
    }




    /**
     * 用户登出
     * @return
     */
    @RequestMapping(value = "logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpServletRequest request,HttpServletResponse response){
//        session.removeAttribute(Const.CURRENT_USER);
        String token  = CookieUtil.readLoginToken(request);
        CookieUtil.delLoginToken(request,response);
        RedisPoolUtil.del(token);
        return ServerResponse.createBySuccess();
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @RequestMapping(value = "register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){

        return iUserService.register(user);
    }

    /**
     * 检查用户名是否有效
     * @param str
     * @param type
     * @return
     */
    @RequestMapping(value = "check_valid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str,String type){

        return  iUserService.checkValid(str,type);
    }

    /**
     * 获取用户信息
     * @return
     */
    @RequestMapping(value = "get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpServletRequest request){
        String token  = CookieUtil.readLoginToken(request);
       if(StringUtils.isEmpty(token)){
           return ServerResponse.createByError("获取不到session信息");
       }
       String strUser = RedisPoolUtil.get(token);
       User user  = JsonUtil.string2Obj(strUser,User.class);
        if(user == null){
            return ServerResponse.createByError("获取不到session信息");
       }
       return ServerResponse.createBySuccess(user);
    }

    /**
     * 找回密码问题
     * @param username
     * @return
     */
    @RequestMapping(value = "forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public  ServerResponse<String> getUserQuestion(String username){
        return iUserService.getUserQuestion(username);
    }

    /**
     * 验证密码问题是否正确
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @RequestMapping(value = "forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkAnswer(String username,String question,String answer){
      return iUserService.checkAnswer(username,question,answer);
    }

    /**
     * 修改密码
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    @RequestMapping(value = "forget_reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public  ServerResponse<String> resetPassword(String username,String passwordNew,String forgetToken){
        return iUserService.resetPassword(username,passwordNew,forgetToken);
    }

    /**
     * 登陆状态修改密码
     * @param passwordOld
     * @param passwordNew
     * @return
     */
    @RequestMapping(value = "reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpServletRequest request,String passwordOld,String passwordNew){
        String token  = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByError("获取不到session信息");
        }
        String strUser = RedisPoolUtil.get(token);
        User user  = JsonUtil.string2Obj(strUser,User.class);
        if(user == null){
            return ServerResponse.createByError("用户未登录");
        }
        return iUserService.resetPassword(passwordOld,passwordNew,user);
    }

    /**
     * 修改用户信息
     * @param user
     * @return
     */
    @RequestMapping(value = "update_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> update_information(HttpServletRequest request,User user){
        String token  = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByError("获取不到session信息");
        }
        String strUser = RedisPoolUtil.get(token);
        User currentUser  = JsonUtil.string2Obj(strUser,User.class);
        if(currentUser == null){
            return ServerResponse.createByError("用户未登录");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response = iUserService.updateInformation(user);
        if(response.isSuccess()){
            response.getData().setUsername(currentUser.getUsername());
//            session.setAttribute(Const.CURRENT_USER,response.getData());
            RedisPoolUtil.setEx(token, JsonUtil.obj2String(user),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);

        }
        return response;
    }

    /**
     * 查看用户详情
     * @return
     */
    @RequestMapping(value = "get_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> get_information(HttpServletRequest request){
        String token  = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByError("获取不到session信息");
        }
        String strUser = RedisPoolUtil.get(token);
        User currentUser  = JsonUtil.string2Obj(strUser,User.class);
        if(currentUser == null){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(),"未登录,需要强制登录status=10");
        }
        return iUserService.getInformation(currentUser.getId());
    }

}
