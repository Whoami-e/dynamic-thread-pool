package com.whoami.middleware.dynamic.thread.pool.sdk.domain;

import com.alibaba.fastjson.JSON;
import com.whoami.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ Author：enrl
 * @ Date：2024-08-09-14:15
 * @ Version：1.0
 * @ Description：动态线程池服务
 */
public class DynamicThreadPoolService implements IDynamicThreadPoolService{

    private final Logger log = LoggerFactory.getLogger(DynamicThreadPoolService.class);

    /** 应用名称 */
    private final String applicationName;
    /** 线程池信息 */
    private final Map<String, ThreadPoolExecutor> threadPoolExecutorMap;


    public DynamicThreadPoolService(String applicationName, Map<String,
            ThreadPoolExecutor> threadPoolExecutorMap) {
        this.applicationName = applicationName;
        this.threadPoolExecutorMap = threadPoolExecutorMap;
    }

    /**
     * 查询线程池列表
     *
     * @return 线程池列表
     */
    @Override
    public List<ThreadPoolConfigEntity> queryThreadPoolList() {
        // 获取线程池执行器的名称集合
        Set<String> threadPoolBeanNames = threadPoolExecutorMap.keySet();
        // 初始化线程池配置实体列表，用于存储所有线程池的信息
        List<ThreadPoolConfigEntity> threadPoolVOS = new ArrayList<>(threadPoolBeanNames.size());
        // 遍历线程池执行器的名称
        for (String beanName : threadPoolBeanNames) {
            // 根据名称获取线程池执行器
            ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(beanName);
            // 创建线程池配置实体，并设置基本属性
            ThreadPoolConfigEntity threadPoolConfigVO = new ThreadPoolConfigEntity(applicationName, beanName);
            // 设置核心线程数
            threadPoolConfigVO.setCorePoolSize(threadPoolExecutor.getCorePoolSize());
            // 设置最大线程数
            threadPoolConfigVO.setMaximumPoolSize(threadPoolExecutor.getMaximumPoolSize());
            // 设置当前活跃线程数
            threadPoolConfigVO.setActiveCount(threadPoolExecutor.getActiveCount());
            // 设置当前线程池中的线程数量
            threadPoolConfigVO.setPoolSize(threadPoolExecutor.getPoolSize());
            // 设置队列类型
            threadPoolConfigVO.setQueueType(threadPoolExecutor.getQueue().getClass().getSimpleName());
            // 设置队列当前的任务数量
            threadPoolConfigVO.setQueueSize(threadPoolExecutor.getQueue().size());
            // 设置队列剩余容量
            threadPoolConfigVO.setRemainingCapacity(threadPoolExecutor.getQueue().remainingCapacity());
            // 将线程池配置实体添加到列表中
            threadPoolVOS.add(threadPoolConfigVO);
        }
        // 返回线程池列表
        return threadPoolVOS;
    }


    /**
     * 根据线程池名称查询线程池配置
     *
     * @param threadPoolName 线程池名称
     * @return 线程池配置
     */
    @Override
    public ThreadPoolConfigEntity queryThreadPoolConfigByName(String threadPoolName) {
        // 根据线程池名称获取对应的线程池执行器
        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolName);
        // 如果线程池执行器不存在，返回一个新的线程池配置实体，状态为未找到
        if (null == threadPoolExecutor) return new ThreadPoolConfigEntity(applicationName, threadPoolName);

        // 初始化线程池配置数据对象
        ThreadPoolConfigEntity threadPoolConfigVO = new ThreadPoolConfigEntity(applicationName, threadPoolName);
        // 设置线程池的核心线程数
        threadPoolConfigVO.setCorePoolSize(threadPoolExecutor.getCorePoolSize());
        // 设置线程池的最大线程数
        threadPoolConfigVO.setMaximumPoolSize(threadPoolExecutor.getMaximumPoolSize());
        // 设置当前活跃的线程数
        threadPoolConfigVO.setActiveCount(threadPoolExecutor.getActiveCount());
        // 设置当前线程池中的线程数量
        threadPoolConfigVO.setPoolSize(threadPoolExecutor.getPoolSize());
        // 设置线程池的工作队列类型
        threadPoolConfigVO.setQueueType(threadPoolExecutor.getQueue().getClass().getSimpleName());
        // 设置线程池的工作队列当前大小
        threadPoolConfigVO.setQueueSize(threadPoolExecutor.getQueue().size());
        // 设置线程池的工作队列剩余容量
        threadPoolConfigVO.setRemainingCapacity(threadPoolExecutor.getQueue().remainingCapacity());

        // 如果日志级别为debug，记录线程池配置信息
        if (log.isDebugEnabled()) {
            log.info("动态线程池，配置查询 应用名:{} 线程名:{} 池化配置:{}", applicationName, threadPoolName, JSON.toJSONString(threadPoolConfigVO));
        }

        // 返回线程池配置数据对象
        return threadPoolConfigVO;

    }


    /**
     * 更新线程池配置
     *
     * @param threadPoolConfigEntity 线程池配置实体，包含线程池的配置信息
     */
    @Override
    public void updateThreadPoolConfig(ThreadPoolConfigEntity threadPoolConfigEntity) {
        // 检查配置实体是否为空或应用名称不匹配，如果是，则直接返回
        if (null == threadPoolConfigEntity || !applicationName.equals(threadPoolConfigEntity.getAppName())) return;

        // 根据线程池名称获取线程池执行器，如果不存在，则直接返回
        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolConfigEntity.getThreadPoolName());
        if (null == threadPoolExecutor) return;

        // 设置参数 「调整核心线程数和最大线程数」
        threadPoolExecutor.setCorePoolSize(threadPoolConfigEntity.getCorePoolSize());
        threadPoolExecutor.setMaximumPoolSize(threadPoolConfigEntity.getMaximumPoolSize());

    }

}
