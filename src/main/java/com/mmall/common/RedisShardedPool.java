package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangmingliangwx
 * @version $Id: RedisPool, v 0.1 2019/3/28 14:27 wangmingliangwx Exp$
 * @Email mingliang.online@foxmail.com
 */
public class RedisShardedPool {
    private static ShardedJedisPool pool;

    private static  String     redis1Ip    = PropertiesUtil.getProperty("redis1.ip");
    private static  int        redis1Port  = Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));

    private static  String     redis2Ip    = PropertiesUtil.getProperty("redis2.ip");
    private static  int        redis2Port  = Integer.parseInt(PropertiesUtil.getProperty("redis2.port"));



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
        JedisShardInfo info1 = new JedisShardInfo(redis1Ip,redis1Port,1000*2);

        JedisShardInfo info2 = new JedisShardInfo(redis2Ip,redis2Port,1000*2);

        List<JedisShardInfo> jedisShardInfoList = new ArrayList<JedisShardInfo>(2);

        jedisShardInfoList.add(info1);
        jedisShardInfoList.add(info2);

        pool = new ShardedJedisPool(poolConfig,jedisShardInfoList, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);    }

    public static ShardedJedis getJedis(){
        return pool.getResource();
    }

    public static void returnBrokenResource(ShardedJedis jedis){
        pool.returnBrokenResource(jedis);
    }



    public static void returnResource(ShardedJedis jedis){
        pool.returnResource(jedis);
    }

    public static void main(String[] args) {
        ShardedJedis jedis = pool.getResource();

        for(int i =0;i<10;i++){
            jedis.set("key"+i,"value"+i);
        }
        returnResource(jedis);

//        pool.destroy();//临时调用，销毁连接池中的所有连接
        System.out.println("program is end");
    }

}
