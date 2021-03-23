package com.mbld.jigsly.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BucketService {

    private final Map<String, Bucket> buckets;

    public BucketService(){
        buckets = new ConcurrentHashMap<>();
    }

    public void addBucket(String username){
        buckets.put(username, standardBucket());
    }

    public boolean containsBucketByUsername(String username){
        return buckets.containsKey(username);
    }

    public boolean tryConsumeOne(String username){
        return buckets.get(username).tryConsume(1);
    }

    private static Bucket standardBucket() {
        Bandwidth limit = Bandwidth.classic(1, Refill.greedy(1, Duration.ofMillis(50)));

        return Bucket4j.builder()
                .addLimit(limit)
                .build();
    }

    public void removeBucket(String username) {
        if(username != null && containsBucketByUsername(username))
            buckets.remove(username);
    }
}
