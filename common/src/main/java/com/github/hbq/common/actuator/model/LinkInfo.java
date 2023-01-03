package com.github.hbq.common.actuator.model;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 * @author hbq
 */
@Data
public class LinkInfo implements Comparable<LinkInfo>
{

    private String local_address;
    private String foreign_address;
    private String state;

    public LinkInfo dealLocalAddress(String local_address)
    {
        if (StringUtils.isEmpty(local_address))
        {
            return this;
        }
        String[] array = local_address.split(":");
        if (array.length != 2)
        {
            return this;
        }
        String strIp = array[0];
        String strPort = array[1];
        strPort = String.valueOf(Long.parseLong(strPort, 16));
        int len = strIp.length();
        if (len >= 8)
        {
            String ip = Joiner.on(".").join(Lists
                .newArrayList(Long.parseLong(strIp.substring(len - 2, len), 16), Long.parseLong(strIp.substring(len - 4, len - 2), 16),
                    Long.parseLong(strIp.substring(len - 6, len - 4), 16), Long.parseLong(strIp.substring(len - 8, len - 6), 16)));
            this.local_address = String.join(":", ip, strPort);
        }
        return this;
    }

    public LinkInfo dealForeignAddress(String foreign_address)
    {
        if (StringUtils.isEmpty(foreign_address))
        {
            return this;
        }
        String[] array = foreign_address.split(":");
        if (array.length != 2)
        {
            return this;
        }
        String strIp = array[0];
        String strPort = array[1];
        strPort = String.valueOf(Long.parseLong(strPort, 16));
        int len = strIp.length();
        if (len >= 8)
        {
            String ip = Joiner.on(".").join(Lists
                .newArrayList(Long.parseLong(strIp.substring(len - 2, len), 16), Long.parseLong(strIp.substring(len - 4, len - 2), 16),
                    Long.parseLong(strIp.substring(len - 6, len - 4), 16), Long.parseLong(strIp.substring(len - 8, len - 6), 16)));
            this.foreign_address = String.join(":", ip, strPort);
        }
        return this;
    }

    public LinkInfo dealState(String state)
    {
        long status = Long.parseLong(state, 16);
        if (status == 1)
        {
            this.state = "tcp_established";
        }
        else if (status == 2)
        {
            this.state = "tcp_syn_sent";
        }
        else if (status == 3)
        {
            this.state = "tcp_syn_recv";
        }
        else if (status == 4)
        {
            this.state = "tcp_fin_wait1";
        }
        else if (status == 5)
        {
            this.state = "tcp_fin_wait2";
        }
        else if (status == 6)
        {
            this.state = "tcp_time_wait";
        }
        else if (status == 7)
        {
            this.state = "tcp_close";
        }
        else if (status == 8)
        {
            this.state = "tcp_close_wait";
        }
        else if (status == 9)
        {
            this.state = "tcp_last_ack";
        }
        else if (status == 10)
        {
            this.state = "tcp_listen";
        }
        else if (status == 11)
        {
            this.state = "tcp_closing";
        }
        if (StringUtils.isNotBlank(this.state))
        {
            this.state = this.state.toUpperCase();
        }
        return this;
    }

    @Override
    public int compareTo(LinkInfo o)
    {
        return StringUtils.isEmpty(foreign_address) ? 1 : foreign_address.compareTo(o.foreign_address);
    }

    @Override
    public String toString()
    {
        return String.format("%-25s\t%-25s\t%-20s\n", local_address, foreign_address, state);
    }
}
