package com.github.hbq.common.actuator.model;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hbq
 */
@Slf4j
@Data
public class ForeignInfo implements Comparable<ForeignInfo>
{

    private String foreign_address;
    private List<AddrInfo> addrInfos;
    private int count;
    private List<PortInfo> portInfos;

    public void topPort()
    {
        if (Objects.nonNull(addrInfos))
        {
            Map<Integer, List<AddrInfo>> group = addrInfos.stream().collect(Collectors.groupingBy(a -> a.getPort()));
            portInfos = new ArrayList<>();
            for (Entry<Integer, List<AddrInfo>> entry : group.entrySet())
            {
                PortInfo portInfo = new PortInfo();
                portInfo.setPort(entry.getKey());
                portInfo.setCount(entry.getValue().size());
                portInfos.add(portInfo);
            }
            Collections.sort(portInfos);
            log.info("端口排序 => {}, {}", foreign_address, portInfos.toString());
        }
    }

    @Override
    public int compareTo(ForeignInfo o)
    {
        return count > o.count ? -11 : count < o.count ? 1 : 0;
    }

    @Override
    public String toString()
    {
        String portStr = Joiner.on(", ").join(portInfos.size() > 10 ? portInfos.subList(0, 10) : portInfos);
        return String.format("%-25s\t%-25s\t%-120s\n", foreign_address, count, portStr);
    }
}
