package com.echotechblog.service;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.eureka.one.EurekaOneDiscoveryStrategyFactory;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.echotechblog.service")
@EnableCaching
public class AppConfig {

    @Value("${server.port}")
    private int port;

    @Bean
    public CacheManager cacheManager(EurekaClient eurekaClient) {
        EurekaOneDiscoveryStrategyFactory.setEurekaClient(eurekaClient);
        Config config = new Config();
        config.addMapConfig(new MapConfig().setName("testEmbeddedCache"));
        config.getNetworkConfig().setPort(port + 1);
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        var eurekaConfig = config.getNetworkConfig().getJoin().getEurekaConfig();
        eurekaConfig.setEnabled(true)
                .setProperty("self-registration", "true")
                .setProperty("namespace", "embedded-hazelcast")
                .setProperty("use-metadata-for-host-and-port", "true")
                .setProperty("skip-eureka-registration-verification", "true");
        return new HazelcastCacheManager(Hazelcast.newHazelcastInstance(config));
    }
}