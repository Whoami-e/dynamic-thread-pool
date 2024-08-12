package com.whoami.middleware.dynamic.thread.pool.sdk.config;

import com.whoami.middleware.dynamic.thread.pool.sdk.domain.DynamicThreadPoolService;
import com.whoami.middleware.dynamic.thread.pool.sdk.domain.IDynamicThreadPoolService;
import com.whoami.middleware.dynamic.thread.pool.sdk.registry.IRegistry;
import com.whoami.middleware.dynamic.thread.pool.sdk.registry.redis.RedisRegistry;
import com.whoami.middleware.dynamic.thread.pool.sdk.trigger.job.ThreadPoolDataReportJob;
import org.apache.commons.lang.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

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
@EnableConfigurationProperties(DynamicThreadPoolAutoProperties.class)
@EnableScheduling
public class DynamicThreadPoolAutoConfig {

    private final Logger log =  LoggerFactory.getLogger(DynamicThreadPoolAutoConfig.class);

    /**
     * redisson客户端
     * @param properties  配置
     * @return 客户端
     */
    @Bean("dynamicThreadRedissonClient")
    public RedissonClient redissonClient(DynamicThreadPoolAutoProperties properties) {
        log.info("动态线程池，注册器（redis）链接初始化开始。");
        Config config = new Config();
        // 根据需要可以设定编解码器；https://github.com/redisson/redisson/wiki/4.-%E6%95%B0%E6%8D%AE%E5%BA%8F%E5%88%97%E5%8C%96
        config.setCodec(JsonJacksonCodec.INSTANCE);

        config.useSingleServer()
                .setAddress("redis://" + properties.getHost() + ":" + properties.getPort())
                .setPassword(properties.getPassword())
                .setConnectionPoolSize(properties.getPoolSize())
                .setConnectionMinimumIdleSize(properties.getMinIdleSize())
                .setIdleConnectionTimeout(properties.getIdleTimeout())
                .setConnectTimeout(properties.getConnectTimeout())
                .setRetryAttempts(properties.getRetryAttempts())
                .setRetryInterval(properties.getRetryInterval())
                .setPingConnectionInterval(properties.getPingInterval())
                .setKeepAlive(properties.isKeepAlive())
        ;

        RedissonClient redissonClient = Redisson.create(config);

        log.info("动态线程池，注册器（redis）链接初始化完成。{} {} {}", properties.getHost(), properties.getPoolSize(), !redissonClient.isShutdown());

        return redissonClient;
    }

    /**
     * 注册器
     * @param dynamicThreadRedissonClient redisson客户端
     * @return 注册中心
     */
    @Bean
    public IRegistry redisRegistry(RedissonClient dynamicThreadRedissonClient) {
        return new RedisRegistry(dynamicThreadRedissonClient);
    }


    /**
     * 动态线程池服务
     * @param applicationContext    应用上下文
     * @param threadPoolExecutorMap 线程池集合
     * @return 动态线程池服务
     */
    @Bean("dynamicThreadPoolService")
    public DynamicThreadPoolService dynamicThreadPoolService(ApplicationContext applicationContext,
                                                             Map<String, ThreadPoolExecutor> threadPoolExecutorMap) {
        log.info("动态线程池，服务初始化开始。");
        // 获取应用名称
        String applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");

        if (StringUtils.isBlank(applicationName)) {
            applicationName = "缺省的";
            log.warn("动态线程池，启动提示。SpringBoot应用为配置 spring.application.name 无法获取到应用名称 ");
        }

        Set<String> threadPoolKeys = threadPoolExecutorMap.keySet();
        log.info("动态线程池，服务初始化完成。");
        return new DynamicThreadPoolService(applicationName, threadPoolExecutorMap);
    }

    /**
     * 线程池数据上报任务
     * @param dynamicThreadPoolService 动态线程池服务
     * @param registry                 注册中心
     * @return 任务
     */
    @Bean
    public ThreadPoolDataReportJob threadPoolDataReportJob(IDynamicThreadPoolService dynamicThreadPoolService, IRegistry registry) {
        return new ThreadPoolDataReportJob(dynamicThreadPoolService, registry);
    }

}
