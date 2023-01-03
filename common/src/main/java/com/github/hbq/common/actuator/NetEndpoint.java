package com.github.hbq.common.actuator;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;

/**
 * @author hbq
 */
@Endpoint(id = "net")
public class NetEndpoint
{

    public static final String ACTUATOR_APP_NET_COUNT = "count";

    public static final String ACTUATOR_APP_NET_DETAIL = "detail";

    public static final String ACTUATOR_APP_NET_COUNT_JSON = "count,json";

    public static final String ACTUATOR_APP_NET_DETAIL_JSON = "detail,json";

    public static final String ACTUATOR_APP_NET_TOP = "top";

    public static final String ACTUATOR_APP_NET_TOP_JSON = "top,json";

    public static final String ACTUATOR_APP_NET_ALL = "all";

    @Autowired
    private NetService netService;

    @ReadOperation
    public String endpointByGet(@Selector String name)
    {
        if (ACTUATOR_APP_NET_ALL.equals(name))
        {
            String[] contents = {
                netService.queryNetInfo(), netService.queryTopForeignNetDetail(), netService.queryNetDetail()
            };
            return String.join("\n\n", contents);
        }
        else if (ACTUATOR_APP_NET_COUNT.equals(name))
        {
            return netService.queryNetInfo();
        }
        else if (ACTUATOR_APP_NET_DETAIL.equals(name))
        {
            return netService.queryNetDetail();
        }
        else if (ACTUATOR_APP_NET_COUNT_JSON.equals(name))
        {
            return JSON.toJSONString(netService.queryNetInfoWithJson());
        }
        else if (ACTUATOR_APP_NET_DETAIL_JSON.equals(name))
        {
            return JSON.toJSONString(netService.queryNetDetailWithJson());
        }
        else if (ACTUATOR_APP_NET_TOP.equals(name))
        {
            return netService.queryTopForeignNetDetail();
        }
        else if (ACTUATOR_APP_NET_TOP_JSON.equals(name))
        {
            return JSON.toJSONString(netService.queryTopForeignNetDetailWithJson());
        }
        else
        {
            return "UnsupportedOperation";
        }

    }
}
