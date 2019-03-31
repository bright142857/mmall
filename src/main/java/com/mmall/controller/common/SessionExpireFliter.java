package com.mmall.controller.common;

import com.mmall.common.Const;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author wangmingliangwx
 * @version $Id: SessionExpireFliter, v 0.1 2019/3/31 10:17 wangmingliangwx Exp$
 * @Email bright142857@foxmail.com
 */
public class SessionExpireFliter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isNotEmpty(token)){
           String jsonStr =  RedisPoolUtil.get(token);
          User user  =  JsonUtil.string2Obj(jsonStr, User.class);
          if(user != null){
              RedisPoolUtil.expire(token, Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
          }
        }
    }

    @Override
    public void destroy() {

    }
}
