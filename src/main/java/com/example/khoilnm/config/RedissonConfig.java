package com.example.khoilnm.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RedissonConfig {

    /**
     * lockWatchdogTimeout: TTL of a lock taken without an explicit lease time. While the owning thread still
     * holds it, Redisson's watchdog renews it (every timeout/3); if the instance crashes, Redis frees it
     * after this time.
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(@Value("${redisson.address}") String address,
                                         @Value("${stock.lock.ttl:10s}") Duration lockTtl) {
        Config config = new Config();
        config.setLockWatchdogTimeout(lockTtl.toMillis());
        config.useSingleServer().setAddress(address);
        return Redisson.create(config);
    }
}
