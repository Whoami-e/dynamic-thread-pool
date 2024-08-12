package com.whoami.middleware.dynamic.thread.pool.sdk.trigger.job;

import com.alibaba.fastjson.JSON;
import com.whoami.middleware.dynamic.thread.pool.sdk.domain.IDynamicThreadPoolService;
import com.whoami.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import com.whoami.middleware.dynamic.thread.pool.sdk.registry.IRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * @ Author：enrl
 * @ Date：2024-08-12-10:13
 * @ Version：1.0
 * @ Description：线程池上报任务
 */
public class ThreadPoolDataReportJob {
    private final Logger logger = LoggerFactory.getLogger(ThreadPoolDataReportJob.class);

    private final IDynamicThreadPoolService dynamicThreadPoolService;

    private final IRegistry registry;

    public ThreadPoolDataReportJob(IDynamicThreadPoolService dynamicThreadPoolService, IRegistry registry) {
        this.dynamicThreadPoolService = dynamicThreadPoolService;
        this.registry = registry;
    }

    /**
     * 20秒上报一次线程池信息
     * 通过定时任务的方式，每隔20秒自动收集并上报一次动态线程池的运行信息和配置参数，
     * 以便监控和管理线程池的状态。
     */
    @Scheduled(cron = "0/20 * * * * ?")
    public void execReportThreadPoolList() {
        logger.info("动态线程池，开始上报线程池信息");
        // 查询并获取动态线程池的列表信息
        List<ThreadPoolConfigEntity> threadPoolConfigEntities = dynamicThreadPoolService.queryThreadPoolList();
        // 上报线程池的运行信息
        registry.reportThreadPool(threadPoolConfigEntities);
        // 记录线程池的运行信息，用于日志审计或问题追踪
        logger.info("动态线程池，上报线程池信息：{}", JSON.toJSONString(threadPoolConfigEntities));

        // 遍历线程池列表，分别上报每个线程池的配置参数
        for (ThreadPoolConfigEntity threadPoolConfigEntity : threadPoolConfigEntities) {
            // 上报线程池的配置参数
            registry.reportThreadPoolConfigParameter(threadPoolConfigEntity);
            // 记录线程池的配置参数，用于日志审计或问题追踪
            logger.info("动态线程池，上报线程池配置：{}", JSON.toJSONString(threadPoolConfigEntity));
        }
        logger.info("动态线程池，上报线程池信息结束");
    }

}
