package com.github.hbq.common.spring.boot.ctrl;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * @author hbq
 */
public class VersionRegistrations implements WebMvcRegistrations
{

    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping()
    {
        return new VersionRequestMappingHandler();
    }
}
