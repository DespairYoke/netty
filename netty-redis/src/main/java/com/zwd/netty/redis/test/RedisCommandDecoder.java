package com.zwd.netty.redis.test;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * @author zwd
 * @date 2018/12/26 17:26
 * @Email stephen.zwd@gmail.com
 */
public class RedisCommandDecoder extends ReplayingDecoder {
    public static final char CR = '/r';
    public static final char LF = '/n';
    public static final byte DOLLAR_BYTE = '$';
    public static final byte ASTERISK_BYTE = '*';
    private byte[][] bytes;
    private int arguments = 0;
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in,
                          List out) throws Exception {
        if (bytes != null) {
            int numArgs = bytes.length;
            for (int i = arguments; i < numArgs; i++) {
                if (in.readByte() == DOLLAR_BYTE) {
                    int l = RedisReplyDecoder.readInt(in);
                    if (l > Integer.MAX_VALUE) {
                        throw new IllegalArgumentException(
                                "Java only supports arrays up to "
                                        + Integer.MAX_VALUE + " in size");
                    }
                    int size = (int) l;
                    bytes[i] = new byte[size];
                    in.readBytes(bytes[i]);
                    if (in.bytesBefore((byte) CR) != 0) {
                        throw new RedisException("Argument doesn't end in CRLF");
                    }
// Skip CRLF(/r/n)
                    in.skipBytes(2);
                    arguments++;
                    checkpoint();
                } else {
                    throw new IOException("Unexpected character");
                }
            }
            try {
                out.add(new Command(bytes));
            } finally {
                bytes = null;
                arguments = 0;
            }
        } else if (in.readByte() == ASTERISK_BYTE) {
            int l = RedisReplyDecoder.readInt(in);
            if (l > Integer.MAX_VALUE) {
                throw new IllegalArgumentException(
                        "Java only supports arrays up to " + Integer.MAX_VALUE
                                + " in size");
            }
            int numArgs = (int) l;
            if (numArgs < 0) {
                throw new RedisException("Invalid size: " + numArgs);
            }
            bytes = new byte[numArgs][];
            checkpoint();
            decode(ctx, in, out);
        } else {
            in.readerIndex(in.readerIndex() - 1);
            byte[][] b = new byte[1][];
            b[0] = in.readBytes(in.bytesBefore((byte) CR)).array();
            in.skipBytes(2);
            out.add(new Command(b, true));
        }
    }
}
