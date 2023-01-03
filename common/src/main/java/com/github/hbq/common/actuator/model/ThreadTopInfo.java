package com.github.hbq.common.actuator.model;

import com.github.hbq.common.utils.FormatNumber;
import lombok.Data;
import org.apache.commons.lang.math.NumberUtils;

/**
 * @author hbq
 */
@Data
public class ThreadTopInfo implements Comparable<ThreadTopInfo>
{

    private String userProcess;
    private double cpu;
    private int priority;
    private String scnt;
    private String wchan;
    private String userThread;
    private String system;
    private long tid;
    private String tidHex;
    private String time;

    @Override
    public int compareTo(ThreadTopInfo o)
    {
        return this.cpu > o.cpu ? -1 : this.cpu < o.cpu ? 1 : 0;
    }

    public ThreadTopInfo build(String[] array)
    {
        if (array.length != 9)
        {
            return null;
        }
        this.userProcess = array[0].trim();
        this.cpu = FormatNumber.format(NumberUtils.toDouble(array[1].trim()), 2);
        this.priority = NumberUtils.toInt(array[2].trim());
        this.scnt = array[3];
        this.wchan = array[4];
        this.userThread = array[5];
        this.system = array[6];
        this.tid = NumberUtils.toInt(array[7]);
        this.time = array[8];
        this.tidHex = Long.toHexString(tid);
        return this;
    }

    public String formatString()
    {
        StringBuilder sb = new StringBuilder(500);
        sb.append(String
            .format("%-15s\t%-7s\t%-5s\t%-5s\t%-5s\t%-15s\t%-10s\t%-16s\t%-10s", userProcess, cpu, priority, scnt, wchan, userThread, system, tidHex, time));
        return sb.toString();
    }
}
