package com.github.hbq.common.actuator;

import com.github.hbq.common.actuator.model.AppNetInfo;
import com.github.hbq.common.actuator.model.ForeignInfo;
import com.github.hbq.common.actuator.model.LinkInfo;
import java.util.List;

/**
 * @author hbq
 */
public interface NetService
{

    /**
     * 查询tcp网络概况
     *
     * @return
     */
    String queryNetInfo();

    /**
     * 查询tcp网络概况,返回json格式
     *
     * @return
     */
    AppNetInfo queryNetInfoWithJson();

    /**
     * 查询tcp连接明细
     *
     * @return
     */
    String queryNetDetail();

    /**
     * 查询tcp连接明细,返回json格式
     *
     * @return
     */
    List<LinkInfo> queryNetDetailWithJson();

    /**
     * 查询tcp连接远端top明细
     *
     * @return
     */
    String queryTopForeignNetDetail();

    /**
     * 查询tcp连接远端top明细，返回json格式
     *
     * @return
     */
    List<ForeignInfo> queryTopForeignNetDetailWithJson();

}
