package com.github.hbq.monitor;

import com.github.hbq.common.utils.ConfigUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @author hbq
 */
@Configuration
public class MvcConfig extends WebMvcConfigurationSupport {

  private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
      "classpath:/META-INF/resources/", "classpath:/resources/",
      "classpath:/static/", "classpath:/public/"};

  @Override
  protected void addResourceHandlers(ResourceHandlerRegistry registry) {
    if (!registry.hasMappingForPattern("/swagger-ui/**")) {
      registry.addResourceHandler("/swagger-ui/**").addResourceLocations(
          "classpath:/META-INF/resources/webjars/springfox-swagger-ui/");
    }
    if (!registry.hasMappingForPattern("/**")) {
      registry.addResourceHandler("/**").addResourceLocations(
          CLASSPATH_RESOURCE_LOCATIONS);
    }
  }

  public static void main(String[] args) throws Exception {
    ConfigUtils.of("monitor").build();
    ConfigUtils.of("monitor").buildDev();
  }
}
