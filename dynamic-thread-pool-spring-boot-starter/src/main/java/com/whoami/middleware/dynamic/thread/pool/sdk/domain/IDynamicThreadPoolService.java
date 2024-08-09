package com.whoami.middleware.dynamic.thread.pool.sdk.domain;

import com.whoami.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;

import java.util.List;

/**
 * @ Author：enrl
 * @ Date：2024-08-09-14:06
 * @ Version：1.0
 * @ Description：动态线程池服务
 */
public interface IDynamicThreadPoolService {

    /**
     * 查询线程池列表
     *
     * @return 线程池列表
     */
    List<ThreadPoolConfigEntity> queryThreadPoolList();

    /**
     * 根据线程池名称查询线程池配置
     *
     * @param threadPoolName 线程池名称
     * @return 线程池配置
     */
    ThreadPoolConfigEntity queryThreadPoolConfigByName(String threadPoolName);

    /**
     * 更新线程池配置
     *
     * @param threadPoolConfigEntity 线程池配置
     */
    void updateThreadPoolConfig(ThreadPoolConfigEntity threadPoolConfigEntity);
}
