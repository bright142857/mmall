package com.mmall.util;

import com.mmall.common.RedisShardedPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;

/**
 * @author wangmingliangwx
 * @version $Id: RedisShardedPoolUtil, v 0.1 2019/3/28 14:59 wangmingliangwx Exp$
 * @Email mingliang.online@foxmail.com
 */
@Slf4j
public class RedisShardedPoolUtil {

    /**
     *  设置 key的有效期 exTime 秒
     * @param key
     * @param exTime
     * @return
     */
    public static Long expire(String key,int exTime){
        ShardedJedis   jedis   = null;
        Long  result  = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result=jedis.expire(key,exTime);
        } catch (Exception e) {
            RedisShardedPool.returnBrokenResource(jedis);
            log.error("expire key:{} exTime:{}",key,exTime,e);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }


    // exTime 秒
    public static String setEx(String key,String value,int exTime){
        ShardedJedis   jedis   = null;
        String  result  = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result=jedis.setex(key,exTime,value);
        } catch (Exception e) {
            RedisShardedPool.returnBrokenResource(jedis);
            log.error("setEx key:{} value:{} exTime:{}",key,value,exTime,e);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static String set(String key,String value){
        ShardedJedis   jedis   = null;
        String  result  = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result=jedis.set(key,value);
        } catch (Exception e) {
            RedisShardedPool.returnBrokenResource(jedis);
            log.error("set key:{} value:{}",key,value,e);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static String get(String key){
        ShardedJedis   jedis   = null;
        String  result  = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result=jedis.get(key);
        } catch (Exception e) {
            RedisShardedPool.returnBrokenResource(jedis);
            log.error("get key:{}",key,e);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static Long del(String key){
        ShardedJedis   jedis   = null;
        Long  result  = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result=jedis.del(key);
        } catch (Exception e) {
            RedisShardedPool.returnBrokenResource(jedis);
            log.error("del key:{}",key,e);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }






}
