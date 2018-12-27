package com.zwd.netty.redis.test;

/**
 * @author zwd
 * @date 2018/12/26 17:29
 * @Email stephen.zwd@gmail.com
 */
public interface RedisServer {
    public BulkReply get(byte[] key0) throws RedisException;
    public StatusReply set(byte[] key0, byte[] value1) throws RedisException;
}