package com.github.hbq.common.utils;

import com.google.common.base.Joiner;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

/**
 * @author hbq
 */
@Slf4j
public class CmdUtils
{

    /**
     * 执行linux命令，如果异常返回空
     *
     * @param cmd
     * @return
     */
    public static Process process(String cmd)
    {
        try
        {
            return Runtime.getRuntime().exec(new String[]{"sh", "-c", cmd});
        }
        catch (IOException e)
        {
            log.info(String.format("执行命令[%s]异常", cmd), e);
            return null;
        }
    }

    /**
     * 执行linux命令，如果异常返回空
     *
     * @param cmd
     * @return
     */
    public static List<String> lines(String cmd)
    {
        try
        {
            Process p = process(cmd);
            try (InputStream in = p.getInputStream())
            {
                return IOUtils.readLines(in, Charset.defaultCharset());
            }
        }
        catch (IOException e)
        {
            log.info(String.format("执行命令[%s]异常", cmd), e);
            return null;
        }
    }

    /**
     * 执行linux命令，如果异常返回空
     *
     * @param cmd
     * @return
     */
    public static String result(String cmd)
    {
        List<String> lines = lines(cmd);
        if (Objects.isNull(lines))
        {
            return null;
        }
        else
        {
            return Joiner.on('\n').join(lines);
        }
    }

    public static String getProcessNo()
    {
        String processNo;
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        processNo = runtimeMXBean.getName().split("@")[0];
        return processNo;
    }

    public static String getLocalAddress()
    {
        String ip = "127.0.0.1";
        try
        {
            ip = InetAddress.getLocalHost().getHostAddress();
        }
        catch (Exception e)
        {
            log.error("获取本机IP异常:", e);
        }
        return ip;
    }
}
