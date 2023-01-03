package com.github.hbq.common.actuator;

import com.github.hbq.common.actuator.model.AddrInfo;
import com.github.hbq.common.actuator.model.AppNetInfo;
import com.github.hbq.common.utils.CmdUtils;
import com.github.hbq.common.actuator.model.ForeignInfo;
import com.github.hbq.common.actuator.model.LinkInfo;
import com.github.hbq.common.actuator.model.NetInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

/**
 * @author hbq
 */
@Slf4j
public class NetServiceImpl implements NetService
{

    @Override
    public AppNetInfo queryNetInfoWithJson()
    {
        AppNetInfo appNetInfo = new AppNetInfo();
        NetInfo netInfo = new NetInfo();
        appNetInfo.setNetInfo(netInfo);
        String cmd = null;
        String processNo = null;
        try
        {
            processNo = CmdUtils.getProcessNo();
            appNetInfo.setProcessNo(processNo);
            log.info("当前应用进程号: {}", processNo);
            cmd = String.format("cat /proc/%s/net/tcp | grep -v local_address | awk '{print $4}'", processNo);
            List<String> lines = CmdUtils.lines(cmd);
            if (CollectionUtils.isEmpty(lines))
            {
                cmd = String.format("cat /proc/%s/net/tcp6 | grep -v local_address | awk '{print $4}'", processNo);
                lines = CmdUtils.lines(cmd);
                if (CollectionUtils.isEmpty(lines))
                {
                    return appNetInfo;
                }
            }
            if (log.isDebugEnabled())
            {
                log.debug("执行tcp状态查询命令: {}, 原始结果: {}", cmd, lines);
            }
            long status;
            for (String line : lines)
            {
                try
                {
                    status = Long.parseLong(line, 16);
                    netInfo.count(status);
                }
                catch (NumberFormatException e)
                {
                    continue;
                }
            }
            log.info("执行tcp状态查询命令:{}, 结果: {}", cmd, netInfo);
        }
        catch (Exception e)
        {
            log.error("执行tcp状态查询异常", e);
        }
        return appNetInfo;
    }

    @Override
    public String queryNetInfo()
    {
        AppNetInfo appNetInfo = queryNetInfoWithJson();
        return appNetInfo.getNetInfo().toString();
    }

    @Override
    public List<LinkInfo> queryNetDetailWithJson()
    {
        List<LinkInfo> links = new ArrayList<>();
        String cmd = null;
        try
        {
            String processNo = CmdUtils.getProcessNo();
            log.info("当前应用进程号: {}", processNo);
            cmd = String.format("cat /proc/%s/net/tcp | grep -v local_address | awk '{print $2\",\"$3\",\"$4}'", processNo);
            List<String> lines = CmdUtils.lines(cmd);
            if (CollectionUtils.isEmpty(lines))
            {
                cmd = String.format("cat /proc/%s/net/tcp6 | grep -v local_address | awk '{print $2\",\"$3\",\"$4}'", processNo);
                lines = CmdUtils.lines(cmd);
                if (CollectionUtils.isEmpty(lines))
                {
                    return links;
                }
            }
            if (log.isDebugEnabled())
            {
                log.debug("查询tcp连接明细, 命令: {}, 原始结果: {}", cmd, lines);
            }
            for (String line : lines)
            {
                String[] array = line.split(",");
                if (array.length != 3)
                {
                    continue;
                }
                LinkInfo link = new LinkInfo();
                link.dealLocalAddress(array[0]);
                link.dealForeignAddress(array[1]);
                link.dealState(array[2]);
                links.add(link);
            }
            Collections.sort(links);
        }
        catch (Exception e)
        {
            log.error("查询tcp连接明细异常", e);
        }
        return links;
    }

    @Override
    public String queryNetDetail()
    {
        List<LinkInfo> links = queryNetDetailWithJson();
        StringBuilder result = new StringBuilder();
        String[] formats = {"-25s", "-25s", "-20s"};
        String[] titles = {"Local Address", "Foreign Address", "State"};
        result.append(String.format(String.join("\t\n", formats), titles)).append('\n');
        titles = new String[]{"---------------", "---------------", "---------------"};
        result.append(String.format(String.join("\t\n", formats), titles)).append('\n');
        for (LinkInfo link : links)
        {
            result.append(link.toString());
        }
        String str = result.toString();
        log.info("查询tcp连接明细, 结果: \n{}", str);
        return result.toString();
    }

    @Override
    public List<ForeignInfo> queryTopForeignNetDetailWithJson()
    {
        List<ForeignInfo> fs = new ArrayList<>();
        List<AddrInfo> addrInfos = new ArrayList<>();
        String cmd = null;
        try
        {
            String processNo = CmdUtils.getProcessNo();
            log.info("当前应用进程号: {}", processNo);
            cmd = String.format("cat /proc/%s/net/tcp | grep -v local_address | awk '{print $3}'", processNo);
            List<String> lines = CmdUtils.lines(cmd);
            if (CollectionUtils.isEmpty(lines))
            {
                cmd = String.format("cat /proc/%s/net/tcp6 | grep -v local_address | awk '{print $3}'", processNo);
                lines = CmdUtils.lines(cmd);
                if (CollectionUtils.isEmpty(lines))
                {
                    return fs;
                }
            }
            if (log.isDebugEnabled())
            {
                log.debug("查询tcp远端连接top明细, 命令: {}, 原始结果: {}", cmd, lines);
            }
            for (String line : lines)
            {
                AddrInfo addrInfo = new AddrInfo();
                addrInfo.dealAddress(line);
                addrInfos.add(addrInfo);
            }
            Map<String, List<AddrInfo>> group = addrInfos.stream().collect(Collectors.groupingBy(a -> a.getAddr()));
            for (Entry<String, List<AddrInfo>> entry : group.entrySet())
            {
                ForeignInfo f = new ForeignInfo();
                f.setForeign_address(entry.getKey());
                f.setAddrInfos(entry.getValue());
                f.setCount(entry.getValue().size());
                fs.add(f);
            }
            Collections.sort(fs);
            for (ForeignInfo f : fs)
            {
                f.topPort();
            }
        }
        catch (Exception e)
        {
            log.error("查询tcp连接明细异常", e);
        }
        return fs;
    }

    @Override
    public String queryTopForeignNetDetail()
    {
        List<ForeignInfo> fs = queryTopForeignNetDetailWithJson();
        StringBuilder result = new StringBuilder();
        String[] formats = {"-25s", "-25s", "-120s"};
        String[] titles = {"Foreign Address", "Count", "Ports"};
        result.append(String.format(String.join("\t\n", formats), titles));
        titles = new String[]{"---------------", "---------------", "------------------------------"};
        result.append(String.format(String.join("\t\n", formats), titles));
        for (ForeignInfo f : fs)
        {
            result.append(f.toString());
        }
        String str = result.toString();
        log.info("查询tcp远端连接top明细, 结果: \n{}", str);
        return result.toString();
    }


}
