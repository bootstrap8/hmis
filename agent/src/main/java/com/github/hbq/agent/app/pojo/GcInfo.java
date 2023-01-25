package com.github.hbq.agent.app.pojo;

import com.github.hbq.agent.app.serv.Collect;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Data;

/**
 * @author hbq
 */
@Data
public class GcInfo implements Collect {

  private long youngCount;
  private long youngTime;
  private long fullCount;
  private long fullTime;

  @Override
  public Collection<QuotaData> collectData(InstInfo instInfo, long collectTime) {
    List<QuotaData> list = new ArrayList<>(4);

    QuotaInfo qi = new QuotaInfo(instInfo, "app,jvm,gc,ygc,count", "应用指标,jvm,新生代gc次数", "次");
    QuotaData qd = new QuotaData(qi);
    qd.collectData(new DataInfo(youngCount, ""), collectTime);
    list.add(qd);

    qi = new QuotaInfo(instInfo, "app,jvm,gc,ygc,time", "应用指标,jvm,新生代gc耗时", "ms");
    qd = new QuotaData(qi);
    qd.collectData(new DataInfo(youngTime, ""), collectTime);
    list.add(qd);

    qi = new QuotaInfo(instInfo, "app,jvm,gc,fgc,count", "应用指标,jvm,老年代gc次数", "次");
    qd = new QuotaData(qi);
    qd.collectData(new DataInfo(fullCount, ""), collectTime);
    list.add(qd);

    qi = new QuotaInfo(instInfo, "app,jvm,gc,fgc,time", "应用指标,jvm,老年代gc耗时", "ms");
    qd = new QuotaData(qi);
    qd.collectData(new DataInfo(fullTime, ""), collectTime);
    list.add(qd);

    return list;
  }
}
