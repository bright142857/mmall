package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import com.mmall.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.zip.CheckedInputStream;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0) {
            return ServerResponse.createByError("用户名不存在");
        }
        String md5password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5password);
        if (user == null) {
            return ServerResponse.createByError("密码错误");
        }
        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登陆成功", user);
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @Override
    public ServerResponse<String> register(User user) {
               if (user != null) {

            ServerResponse<String> serverResponse = this.checkValid(user.getUsername(), Const.USERNAME);
            if (!serverResponse.isSuccess()) {
                return serverResponse;
            }
            serverResponse = this.checkValid(user.getEmail(), Const.EMAIL);
            if (!serverResponse.isSuccess()) {
                return serverResponse;
            }
            user.setRole(Const.Role.ROLE_CUSTOMER);
            user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
            int resultcount = userMapper.insert(user);
            if (resultcount == 0) {
                return ServerResponse.createByError("注册失败");
            } else {
                return ServerResponse.createBySuccess("注册成功");
            }

        } else {

            return ServerResponse.createByError();

        }

    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if(StringUtils.isNotBlank(type)){
            int resultCount ;
            if(Const.USERNAME.equals(type)){
                resultCount = userMapper.checkUsername(str);
                if (resultCount > 0) {
                    return ServerResponse.createByError("用户名已存在");
                }
            }
            if(Const.EMAIL.equals(type)){
                resultCount = userMapper.checkEmail(str);
                if (resultCount > 0){
                    return ServerResponse.createByError("email已存在");
                }
            }
             return ServerResponse.createBySuccess();
        }else{
          return   ServerResponse.createByError("数据传输错误");

        }
    }

    @Override
    public ServerResponse<String> getUserQuestion(String username) {
        ServerResponse serverResponse = this.checkValid(username,Const.USERNAME);
        if(!serverResponse.isSuccess()){
           String question =  userMapper.getUserQuestion(username);
           if(StringUtils.isNotBlank(question)){
               return ServerResponse.createBySuccess(ResponseCode.SUCCESS.getDesc(),question);
           }

        }
        return ServerResponse.createByError("找回密码问题失败");
    }

    /**
     * 验证找回密码答案是否正确
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
            int resultcount = userMapper.checkAnswer(username,question,answer);
            if(resultcount == 1){
               String uuidToken =  UUID.randomUUID().toString();
                RedisPoolUtil.setEx(Const.TOKEN_PREFIX+username,uuidToken,60 * 60 * 12);
                ServerResponse.createBySuccess(uuidToken);
                return ServerResponse.createBySuccess(ResponseCode.SUCCESS.getDesc(),uuidToken);
            }
        return ServerResponse.createByError("答案错误");
    }

    /**
     * 修改密码
     * @param username
     * @param passwordNew
     * @param token
     * @return
     */
    @Override
    public ServerResponse<String> resetPassword(String username, String passwordNew, String token) {
        if(StringUtils.isNotBlank(token)){
            ServerResponse<String> serverResponse = this.checkValid(username, Const.USERNAME);
            if (serverResponse.isSuccess()){
                ServerResponse.createByError("用户名不存在");
            }
            String resultToken = RedisPoolUtil.get(Const.TOKEN_PREFIX+username);
            if(org.apache.commons.lang3.StringUtils.isBlank(token)){
                return ServerResponse.createByError("token无效或者过期");
            }
            if(org.apache.commons.lang3.StringUtils.equals(resultToken,token)){
                String md5password = MD5Util.MD5EncodeUtf8(passwordNew);
                int resultcount = userMapper.resetPassword(username,md5password);
                if(resultcount == 1){
                    return ServerResponse.createBySuccess("修改成功");
                }

            }else{
                return ServerResponse.createByError("token无效或过期");
            }



        }

            return ServerResponse.createByError();



    }

    @Override
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
        //防止横向越权,要校验一下这个用户的旧密码,一定要指定是这个用户.因为我们会查询一个count(1),如果不指定id,那么结果就是true啦count>0;
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if(resultCount == 0){
            return ServerResponse.createByError("旧密码错误");
        }

        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount > 0){
            return ServerResponse.createBySuccess("密码更新成功");
        }
        return ServerResponse.createByError("密码更新失败");
    }

    public ServerResponse<User> updateInformation(User user){
        //username是不能被更新的
        //email也要进行一个校验,校验新的email是不是已经存在,并且存在的email如果相同的话,不能是我们当前的这个用户的.
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount > 0){
            return ServerResponse.createByError("email已存在,请更换email再尝试更新");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount > 0){
            return ServerResponse.createBySuccess("更新个人信息成功",updateUser);
        }
        return ServerResponse.createByError("更新个人信息失败");
    }



    public ServerResponse<User> getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return ServerResponse.createByError("找不到当前用户");
        }
        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);

    }

    /**
     * 检查登陆人是否是管理员
     */
    public ServerResponse<String> checkAdminRole(User user) {
        if (user.getRole() == 1) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();

    }
}
