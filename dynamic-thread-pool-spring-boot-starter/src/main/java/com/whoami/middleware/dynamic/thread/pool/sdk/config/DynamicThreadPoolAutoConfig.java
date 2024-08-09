package com.whoami.middleware.dynamic.thread.pool.sdk.config;

import com.alibaba.fastjson.JSON;
import com.whoami.middleware.dynamic.thread.pool.sdk.domain.DynamicThreadPoolService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ Author：enrl
 * @ Date：2024-08-08-15:04
 * @ Version：1.0
 * @ Description：动态配置入口
 */
@Configuration
public class DynamicThreadPoolAutoConfig {

    private final Logger log =  LoggerFactory.getLogger(DynamicThreadPoolAutoConfig.class);

    /**
     * 动态线程池服务
     * @param applicationContext    应用上下文
     * @param threadPoolExecutorMap 线程池集合
     * @return
     */
    @Bean("dynamicThreadPoolService")
    public DynamicThreadPoolService dynamicThreadPoolService(ApplicationContext applicationContext,
                                                             Map<String, ThreadPoolExecutor> threadPoolExecutorMap) {
        // 获取应用名称
        String applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");

        if (StringUtils.isBlank(applicationName)) {
            applicationName = "缺省的";
            log.warn("动态线程池，启动提示。SpringBoot应用为配置 spring.application.name 无法获取到应用名称 ");
        }

        Set<String> threadPoolKeys = threadPoolExecutorMap.keySet();
        return new DynamicThreadPoolService(applicationName, threadPoolExecutorMap);
    }
}
