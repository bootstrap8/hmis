package com.github.hbq.common.actuator.model;

import lombok.Data;

/**
 * @author hbq
 */
@Data
public class PortInfo implements Comparable<PortInfo>
{

    private int port;
    private int count;

    @Override
    public int compareTo(PortInfo o)
    {
        return count > o.count ? -1 : count < o.count ? 1 : 0;
    }

    @Override
    public String toString()
    {
        return String.join(":", String.valueOf(port), String.valueOf(count));
    }
}
