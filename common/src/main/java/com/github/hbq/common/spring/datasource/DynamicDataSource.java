package com.github.hbq.common.spring.datasource;

import com.github.hbq.common.help.MDC;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author hbq
 */
@Slf4j
public class DynamicDataSource extends AbstractRoutingDataSource {

  @Override
  protected Object determineCurrentLookupKey() {
    return MDC.get("hds");
  }
}
