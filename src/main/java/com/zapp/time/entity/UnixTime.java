package com.zapp.time.entity;

import java.util.Date;

/**
 * @author zwd
 * @date 2018/1/9 09:44
 */
public class UnixTime {

    private final long value;

    public UnixTime(int value) {
        this.value = value;
    }
    public UnixTime(){
        this((int)(System.currentTimeMillis()/1000L+2208988800L));
    }
    public long value(){
        return value;
    }
    @Override
    public String toString(){
        return new Date((value()-2208988800L)*1000L).toString();
    }
}
