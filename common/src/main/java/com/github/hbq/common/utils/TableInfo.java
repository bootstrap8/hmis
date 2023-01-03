package com.github.hbq.common.utils;

import java.util.concurrent.TimeUnit;
import lombok.Data;

/**
 * @author hbq
 */
@Data
public class TableInfo
{

    private RoundInfo roundInfo;
    private String tablePrefix;
    private String tableSuffix;
    private String tableName;

    public TableInfo format()
    {
        if (StrUtils.strEmpty(tableName))
        {
            if (roundInfo == null)
            {
                throw new IllegalArgumentException("roundInfo不能为空");
            }
            String suffix = FormatTime.format(tableSuffix, roundInfo.getStartTime().getSecs(), TimeUnit.SECONDS);
            this.tableName = String.join("", tablePrefix, suffix);
        }
        return this;
    }

    public long getStartTimeSec()
    {
        return roundInfo == null ? -1 : roundInfo.getStartTime() == null ? -1 : roundInfo.getStartTime().getSecs();
    }

    public long getStartTimeMills()
    {
        return roundInfo == null ? -1 : roundInfo.getStartTime() == null ? -1 : roundInfo.getStartTime().getMills();
    }

    public long getEndTimeSec()
    {
        return roundInfo == null ? -1 : roundInfo.getEndTime() == null ? -1 : roundInfo.getEndTime().getSecs();
    }

    public long getEndTimeMills()
    {
        return roundInfo == null ? -1 : roundInfo.getEndTime() == null ? -1 : roundInfo.getEndTime().getMills();
    }
}
