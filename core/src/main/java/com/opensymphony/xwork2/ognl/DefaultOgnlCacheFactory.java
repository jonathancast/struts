/*
 * Copyright 2022 Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2.ognl;

import org.apache.commons.lang3.BooleanUtils;

/**
 * <p>Default OGNL Cache factory implementation.</p>
 *
 * <p>Currently used for Expression cache and BeanInfo cache creation.</p>
 *
 * @param <Key>   The type for the cache key entries
 * @param <Value> The type for the cache value entries
 */
public class DefaultOgnlCacheFactory<Key, Value> implements OgnlCacheFactory<Key, Value> {

    private CacheType defaultCacheType;
    private int cacheMaxSize;

    /**
     * @deprecated since 6.4.0, use {@link #DefaultOgnlCacheFactory(int, CacheType)}
     */
    @Deprecated
    public DefaultOgnlCacheFactory() {
        this.cacheMaxSize = 25000;
        this.defaultCacheType = CacheType.CAFFEINE_WTLFU;
    }

    public DefaultOgnlCacheFactory(int cacheMaxSize, CacheType defaultCacheType) {
        this.cacheMaxSize = cacheMaxSize;
        this.defaultCacheType = defaultCacheType;
    }

    @Override
    public OgnlCache<Key, Value> buildOgnlCache() {
        return buildOgnlCache(getCacheMaxSize(), 16, 0.75f, defaultCacheType);
    }

    @Override
    public OgnlCache<Key, Value> buildOgnlCache(int evictionLimit,
                                                int initialCapacity,
                                                float loadFactor,
                                                CacheType cacheType) {
        switch (cacheType) {
            case CONCURRENT_BASIC:
                return new OgnlDefaultCache<>(evictionLimit, initialCapacity, loadFactor);
            case SYNC_LINKED_LRU:
                return new OgnlLRUCache<>(evictionLimit, initialCapacity, loadFactor);
            case CAFFEINE_WTLFU:
                return new OgnlCaffeineCache<>(evictionLimit, initialCapacity, loadFactor);
            default:
                throw new IllegalArgumentException("Unknown cache type: " + cacheType);
        }
    }

    @Override
    public int getCacheMaxSize() {
        return cacheMaxSize;
    }

    /**
     * @deprecated since 6.4.0
     */
    @Deprecated
    protected void setCacheMaxSize(String maxSize) {
        cacheMaxSize = Integer.parseInt(maxSize);
    }

    @Override
    public CacheType getDefaultCacheType() {
        return defaultCacheType;
    }

    /**
     * No effect when {@code useLRUMode} is {@code false}
     *
     * @deprecated since 6.4.0
     */
    @Deprecated
    protected void setUseLRUCache(String useLRUMode) {
        if (BooleanUtils.toBoolean(useLRUMode)) {
            defaultCacheType = CacheType.SYNC_LINKED_LRU;
        }
    }
}
