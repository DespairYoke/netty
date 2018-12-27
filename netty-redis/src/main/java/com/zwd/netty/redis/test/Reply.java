package com.zwd.netty.redis.test;

/**
 * @author zwd
 * @date 2018/12/26 17:29
 * @Email stephen.zwd@gmail.com
 */
public interface Reply {
    byte[] CRLF = new byte[] { RedisReplyDecoder.CR, RedisReplyDecoder.LF };
    T data();
    void write(ByteBuf os) throws IOException;
}