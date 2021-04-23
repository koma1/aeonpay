package pw.komarov.aeonpay.utils;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;

public final class CacheUtils {
    public static CacheManager createCacheManager() {
        CacheManager cm = CacheManagerBuilder.newCacheManagerBuilder().build();
        cm.init();

        return cm;
    }

    @SuppressWarnings({"rawtypes","unchecked"})
    private static CacheConfigurationBuilder cacheConfigurationBuilder(
            Class<?> keyClass,
            Class<?> valueClass,
            int heapSize,
            Duration ttl,
            Duration tti) {
        CacheConfigurationBuilder result = CacheConfigurationBuilder.newCacheConfigurationBuilder(
                keyClass,
                valueClass,
                ResourcePoolsBuilder.heap(heapSize));
        if (ttl == null && tti == null)
            result = result.withExpiry(Expirations.noExpiration());
        else {
            if (ttl != null)
                result = result.withExpiry(Expirations.timeToLiveExpiration(ttl));
            if (tti != null)
                result = result.withExpiry(Expirations.timeToIdleExpiration(tti));
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public static Cache<Object,Object> createObjectsHeapCache(
            CacheManager cacheManager,
            String name,
            int heapSize,
            Duration ttl,
            Duration tti) {

        return cacheManager.createCache(name,
                cacheConfigurationBuilder(Object.class, Object.class, heapSize, ttl, tti).build()
        );
    }

    @SuppressWarnings("unchecked")
    public static Cache<String,Integer> createStringIntegerHeapCache(
            CacheManager cacheManager,
            String name,
            int heapSize,
            Duration ttl,
            Duration tti) {

        return cacheManager.createCache(name,
                cacheConfigurationBuilder(String.class, Integer.class, heapSize, ttl, tti).build()
        );
    }
}
