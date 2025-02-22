package com.ciphershare.v1.service;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class FileCacheService {

    private final int CACHE_SIZE = 30;
    private ConcurrentHashMap<String,byte[]> localCache = new ConcurrentHashMap<>(CACHE_SIZE);

    private RedisTemplate<String,byte[]> redisTemplate;
    private static final long expiry = 30;

    private final AtomicInteger hit = new AtomicInteger(0);
    private final AtomicInteger miss = new AtomicInteger(0);

    public String storeFileinCache(String fileName,byte[] content){

        localCache.put(fileName, content);
        redisTemplate.opsForValue().set(fileName,content,expiry,TimeUnit.MINUTES);

        return "Added file into the Local and redis cache";
    }

    public String removeFileFromCache(String fileName){

        localCache.remove(fileName);
        redisTemplate.delete(fileName);

        return "Removed file from local and redis cache";
    }

    public byte[] getCachedFile(String fileName){
        if(localCache.containsKey(fileName)){
            hit.incrementAndGet();
            return localCache.get(fileName);
        }

        byte[] redisContent = redisTemplate.opsForValue().get(fileName);
        if(redisContent == null){
            miss.incrementAndGet();
            return null;
        }
        hit.incrementAndGet();
        localCache.put(fileName, redisContent);
        return redisContent;
    }

    public boolean isFileCached(String fileName){
        return localCache.containsKey(fileName) || redisTemplate.hasKey(fileName);
    }

    public int gethits(){
        return hit.get();
    }

    public int getmiss(){
        return miss.get();
    }
}
