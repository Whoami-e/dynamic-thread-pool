package com.whoami.middleware.dynamic.thread.pool.sdk.registry;

import com.whoami.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;

import java.util.List;

/**
 * @ Author：enrl
 * @ Date：2024-08-12-09:53
 * @ Version：1.0
 * @ Description：注册中心接口
 */
public interface IRegistry {

    /**
     * 注册所有线程池配置信息
     * @param threadPoolEntities 线程池配置信息列表
     */
    void reportThreadPool(List<ThreadPoolConfigEntity> threadPoolEntities);

    /**
     * 注册某一个线程池配置参数
     * @param threadPoolConfigEntity 线程池配置信息
     */
    void reportThreadPoolConfigParameter(ThreadPoolConfigEntity threadPoolConfigEntity);

}
