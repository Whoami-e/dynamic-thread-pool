package com.whoami.middleware.dynamic.thread.pool.sdk.registry.redis;

import com.whoami.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import com.whoami.middleware.dynamic.thread.pool.sdk.domain.model.valobj.RegistryEnumVO;
import com.whoami.middleware.dynamic.thread.pool.sdk.registry.IRegistry;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.List;

/**
 * @ Author：enrl
 * @ Date：2024-08-12-09:54
 * @ Version：1.0
 * @ Description：Redis注册中心
 */
public class RedisRegistry implements IRegistry {

    private final RedissonClient redissonClient;

    public RedisRegistry(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 注册所有线程池配置信息
     *
     * 通过清空并重新设置线程池配置信息列表，来确保Redisson客户端中仅存在最新的线程池配置
     * 这一操作是为了保持线程池配置的准确性和最新性，避免配置混乱或错误
     *
     * @param threadPoolEntities 线程池配置信息列表 包含所有需要注册的线程池配置
     */
    @Override
    public void reportThreadPool(List<ThreadPoolConfigEntity> threadPoolEntities) {
        // 获取线程池配置列表的RList实例
        RList<ThreadPoolConfigEntity> list = redissonClient.getList(RegistryEnumVO.THREAD_POOL_CONFIG_LIST_KEY.getKey());
        // 清空原有配置，以注册新的线程池配置
        list.delete();
        // 将新的线程池配置信息全部添加到Redisson的列表中
        list.addAll(threadPoolEntities);
    }


    /**
     * 注册某一个线程池配置参数
     *
     * @param threadPoolConfigEntity 线程池配置信息
     */
    @Override
    public void reportThreadPoolConfigParameter(ThreadPoolConfigEntity threadPoolConfigEntity) {
        // 构建缓存键名，用于唯一标识线程池配置参数的缓存位置
        String cacheKey = RegistryEnumVO.THREAD_POOL_CONFIG_PARAMETER_LIST_KEY.getKey() + "_" + threadPoolConfigEntity.getAppName() + "_" + threadPoolConfigEntity.getThreadPoolName();
        // 使用Redisson客户端获取缓存桶（RBucket）
        RBucket<ThreadPoolConfigEntity> bucket = redissonClient.getBucket(cacheKey);
        // 将线程池配置信息设置到缓存中，有效期设置为30天
        bucket.set(threadPoolConfigEntity, Duration.ofDays(30));
    }

}
