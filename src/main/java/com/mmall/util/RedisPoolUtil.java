package com.mmall.util;

import com.mmall.common.RedisPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

/**
 * @author wangmingliangwx
 * @version $Id: RedisPoolUtil, v 0.1 2019/3/28 14:59 wangmingliangwx Exp$
 * @Email bright142857@foxmail.com
 */
@Slf4j
public class RedisPoolUtil {

    /**
     *  设置 key的有效期 exTime 秒
     * @param key
     * @param exTime
     * @return
     */
    public static Long expire(String key,int exTime){
        Jedis   jedis   = null;
        Long  result  = null;

        try {
            jedis = RedisPool.getJedis();
            result=jedis.expire(key,exTime);
        } catch (Exception e) {
            RedisPool.returnBrokenResource(jedis);
            log.error("expire key:{} exTime:{}",key,exTime,e);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }


    // exTime 秒
    public static String setEx(String key,String value,int exTime){
        Jedis   jedis   = null;
        String  result  = null;

        try {
            jedis = RedisPool.getJedis();
            result=jedis.setex(key,exTime,value);
        } catch (Exception e) {
            RedisPool.returnBrokenResource(jedis);
            log.error("setEx key:{} value:{} exTime:{}",key,value,exTime,e);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static String set(String key,String value){
        Jedis   jedis   = null;
        String  result  = null;

        try {
            jedis = RedisPool.getJedis();
            result=jedis.set(key,value);
        } catch (Exception e) {
            RedisPool.returnBrokenResource(jedis);
            log.error("set key:{} value:{}",key,value,e);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static String get(String key){
        Jedis   jedis   = null;
        String  result  = null;

        try {
            jedis = RedisPool.getJedis();
            result=jedis.get(key);
        } catch (Exception e) {
            RedisPool.returnBrokenResource(jedis);
            log.error("get key:{}",key,e);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static Long del(String key){
        Jedis   jedis   = null;
        Long  result  = null;

        try {
            jedis = RedisPool.getJedis();
            result=jedis.del(key);
        } catch (Exception e) {
            RedisPool.returnBrokenResource(jedis);
            log.error("del key:{}",key,e);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }


    public static void main(String[] args) {
            RedisPoolUtil.set("set","setvalue");
            String res = RedisPoolUtil.get("set");
            RedisPoolUtil.setEx("setEx","sexExvalue",60*10);
            RedisPoolUtil.expire("set",60*20);
            RedisPoolUtil.del("set");
    }
}
