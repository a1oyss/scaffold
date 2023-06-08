package com.chaoxing.common.idempotent.key.store;

import java.util.concurrent.TimeUnit;

/**
 * 幂等key保存接口
 *
 * @author SK
 * @since 2023/6/7
 */
public interface IdempotentKeyStore {
    /**
     * 当不存在有效 key 时将其存储下来
     * @param key idempotentKey
     * @param duration key的有效时长
     * @param timeUnit 时长单位
     * @return boolean true: 存储成功 false: 存储失败
     */
    boolean saveIfAbsent(String key, long duration, TimeUnit timeUnit);

    /**
     * 删除 key
     * @param key idempotentKey
     */
    void remove(String key);
}
