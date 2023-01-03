package com.github.hbq.common.actuator.model;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * @author hbq
 */
@Data
public class NetInfo
{

    private long tcp_established;
    private long tcp_syn_sent;
    private long tcp_syn_recv;
    private long tcp_fin_wait1;
    private long tcp_fin_wait2;
    private long tcp_time_wait;
    private long tcp_close;
    private long tcp_close_wait;
    private long tcp_last_ack;
    private long tcp_listen;
    private long tcp_closing;

    public void count(long status)
    {
        if (status == 1)
        {
            tcp_established++;
        }
        else if (status == 2)
        {
            tcp_syn_sent++;
        }
        else if (status == 3)
        {
            tcp_syn_recv++;
        }
        else if (status == 4)
        {
            tcp_fin_wait1++;
        }
        else if (status == 5)
        {
            tcp_fin_wait2++;
        }
        else if (status == 6)
        {
            tcp_time_wait++;
        }
        else if (status == 7)
        {
            tcp_close++;
        }
        else if (status == 8)
        {
            tcp_close_wait++;
        }
        else if (status == 9)
        {
            tcp_last_ack++;
        }
        else if (status == 10)
        {
            tcp_listen++;
        }
        else if (status == 11)
        {
            tcp_closing++;
        }
    }

    public long sum()
    {
        return tcp_established + tcp_syn_sent + tcp_syn_recv + tcp_fin_wait1 + tcp_fin_wait2 + tcp_time_wait + tcp_close + tcp_close_wait + tcp_last_ack
            + tcp_listen + tcp_closing;
    }

    @Override
    public String toString()
    {
        List<String> tcp = new ArrayList<>();
        tcp.add(String.format("%-25s\t%-25s", "State", "Count"));
        tcp.add(String.format("%-25s\t%-25s", "---------------", "---------------"));
        tcp.add(String.format("%-25s\t%-25s", "TCP_ESTABLISHED", tcp_established));
        tcp.add(String.format("%-25s\t%-25s", "TCP_SYN_SENT", tcp_syn_sent));
        tcp.add(String.format("%-25s\t%-25s", "TCP_SYN_RECV", tcp_syn_recv));
        tcp.add(String.format("%-25s\t%-25s", "TCP_FIN_WAIT1", tcp_fin_wait1));
        tcp.add(String.format("%-25s\t%-25s", "TCP_FIN_WAIT2", tcp_fin_wait2));
        tcp.add(String.format("%-25s\t%-25s", "TCP_TIME_WAIT", tcp_time_wait));
        tcp.add(String.format("%-25s\t%-25s", "TCP_CLOSE", tcp_close));
        tcp.add(String.format("%-25s\t%-25s", "TCP_CLOSE_WAIT", tcp_close_wait));
        tcp.add(String.format("%-25s\t%-25s", "TCP_LAST_ACK", tcp_last_ack));
        tcp.add(String.format("%-25s\t%-25s", "TCP_LISTEN", tcp_listen));
        tcp.add(String.format("%-25s\t%-25s", "TCP_CLOSING", tcp_closing));
        return Joiner.on('\n').join(tcp);
    }
}
