package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author wangmingliangwx
 * @version $Id: RedisPool, v 0.1 2019/3/28 14:27 wangmingliangwx Exp$
 * @Email bright142857@foxmail.com
 */
public class RedisPool {
    private static JedisPool pool;
    private static  String     redisIp    = PropertiesUtil.getProperty("redis1.ip");
    private static  int        redisPort  = Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));
    // 最大连接数
    private static  int        maxTotal   = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total","20"));
    // 最大空闲数
    private static  int        maxIdle    = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle","20"));
    // 最小空闲数
    private static  int        minIdle    = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle","2"));
    // 从jedis连接池获取连接时，校验并返回可用的连接
    private static  Boolean    testBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow","true"));
    // 把连接放回jedis连接池时，校验并返回可用的连接
    private static  Boolean    testReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return","false"));

    static {
        initPool();
    }

    private static  void initPool(){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setTestOnBorrow(testBorrow);
        poolConfig.setTestOnReturn(testReturn);
        // 连接耗尽的时候，是否阻塞，false会抛出异常，true阻塞直到超时。默认为true
        poolConfig.setBlockWhenExhausted(true);
        pool = new JedisPool(poolConfig,redisIp,redisPort,1000*2);
    }

    public static Jedis getJedis(){
        return pool.getResource();
    }

    public static void returnBrokenResource(Jedis jedis){
        pool.returnBrokenResource(jedis);
    }



    public static void returnResource(Jedis jedis){
        pool.returnResource(jedis);
    }

    public static void main(String[] args) {
            Jedis jedis = getJedis();
            jedis.set("bright","brightValue");
            String val = jedis.get("bright");
             System.out.println( val);
            pool.destroy();  // 临时调用销毁连接池中的所有连接
    }

}
