package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUsername(String username);
    int checkEmail(String email);

    String getUserQuestion(String username);

    int checkAnswer(@Param("username") String username, @Param("question") String question, @Param("answer") String answer);

    User selectLogin(@Param("username") String username, @Param("password") String password);

    int resetPassword(@Param("username") String username, @Param("password") String passwordNew);

    int checkPassword(@Param("password") String password, @Param("userId") int userId);

    int checkEmailByUserId(@Param(value = "email") String email, @Param(value = "userId") Integer userId);

}