package com.github.hbq.common.actuator;

import com.github.hbq.common.actuator.model.ThreadTopInfo;
import com.github.hbq.common.utils.CmdUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;

/**
 * @author hbq
 */
@Endpoint(id = "thread-top")
@Slf4j
public class ThreadStackEndpoint
{

    private final static Pattern NUMBER = Pattern.compile("\\d+");

    public static final String QUERY_ALL = "all";

    @ReadOperation
    public String endpointByGet(@Selector String top)
    {
        int t = Integer.MAX_VALUE;
        if (StringUtils.isEmpty(top))
        {
            t = 10;
        }
        if (!StringUtils.equals(QUERY_ALL, top))
        {
            if (NUMBER.matcher(top).matches())
            {
                t = Integer.parseInt(top);
            }
        }
        String processNo = CmdUtils.getProcessNo();
        String cmd = String.format("ps -mp %s -o THREAD,tid,time", processNo);
        log.info("执行命令: {}", cmd);
        List<String> lines = CmdUtils.lines(cmd);
        if (log.isTraceEnabled())
        {
            log.trace("执行命令: {}, 原始结果: \n{}", cmd, String.join("\n", lines));
        }
        List<ThreadTopInfo> threadTopInfos = new ArrayList<>(lines.size());
        for (int i = 0; i < lines.size(); i++)
        {
            if (i > 1)
            {
                String[] array = lines.get(i).split(" +");
                ThreadTopInfo threadTopInfo = new ThreadTopInfo().build(array);
                if (threadTopInfo != null)
                {
                    threadTopInfos.add(threadTopInfo);
                }
            }
        }
        Collections.sort(threadTopInfos);
        StringBuilder sb = new StringBuilder();
        String[] formats = {"-15s", "-7s", "-5s", "-5s", "-5s", "-15s", "-10s", "-16s", "-10s"};
        String[] titles = {"USER", "%CPU", "PRI", "SCNT", "WCHAN", "USER", "SYSTEM", "TID", "TIME"};
        String fmt = String.join("\t%", formats).substring(1);
        sb.append(String.format(fmt, titles)).append('\n');
        for (int i = 0; i < threadTopInfos.size(); i++)
        {
            if (i <= t)
            {
                ThreadTopInfo topInfo = threadTopInfos.get(i);
                sb.append('\n').append(topInfo.formatString());
            }
        }
        sb.append("\n\n\n");
        sb.append(CmdUtils.result(String.format("jstack -l %s", processNo)));
        return sb.toString();
    }

}
