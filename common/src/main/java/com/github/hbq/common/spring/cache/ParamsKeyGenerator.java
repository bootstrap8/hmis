package com.github.hbq.common.spring.cache;

import java.lang.reflect.Method;
import org.springframework.cache.interceptor.SimpleKeyGenerator;

/**
 * @author hbq
 */
public class ParamsKeyGenerator extends SimpleKeyGenerator
{

    @Override
    public Object generate(Object target, Method method, Object... params)
    {
        return ApiKeyGenerator.createKey(false, target, method, params);
    }
}
