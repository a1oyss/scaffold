package com.chaoxing.common.idempotent.key.store.impl;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import com.chaoxing.common.idempotent.key.store.IdempotentKeyStore;

import java.util.concurrent.TimeUnit;

/**
 * 基于内存保存实现,默认缓存实现为HuTool的 {@code TimedCache},<br>
 * 默认缓存池为 {@code Integer.MAX_VALUE}
 *
 * @author hccake
 * @see cn.hutool.cache.impl.TimedCache
 */
public class InMemoryIdempotentKeyStore implements IdempotentKeyStore {
    private final TimedCache<String, Long> cache;

    public InMemoryIdempotentKeyStore() {
        this.cache = CacheUtil.newTimedCache(Integer.MAX_VALUE);
        cache.schedulePrune(1);
    }

    @Override
    public boolean saveIfAbsent(String key, long duration, TimeUnit timeUnit) {
        Long value = cache.get(key, false);
        if (value == null) {
            long timeOut = TimeUnit.MILLISECONDS.convert(duration, timeUnit);
            cache.put(key, System.currentTimeMillis(), timeOut);
            return true;
        }
        return false;
    }

    @Override
    public void remove(String key) {
        cache.remove(key);
    }
}
