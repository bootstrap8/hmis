package com.github.hbq.common.actuator.model;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 * @author hbq
 */
@Data
public class AddrInfo
{

    private String addr;
    private int port;

    public AddrInfo dealAddress(String addr)
    {
        if (StringUtils.isEmpty(addr))
        {
            return this;
        }
        String[] array = addr.split(":");
        if (array.length != 2)
        {
            return this;
        }
        String strIp = array[0];
        String strPort = array[1];
        this.port = (int) Long.parseLong(strPort, 16);
        int len = strIp.length();
        if (len >= 8)
        {
            String ip = Joiner.on(".").join(Lists
                .newArrayList(Long.parseLong(strIp.substring(len - 2, len), 16), Long.parseLong(strIp.substring(len - 4, len - 2), 16),
                    Long.parseLong(strIp.substring(len - 6, len - 4), 16), Long.parseLong(strIp.substring(len - 8, len - 6), 16)));
            this.addr = ip;
        }
        return this;
    }
}
