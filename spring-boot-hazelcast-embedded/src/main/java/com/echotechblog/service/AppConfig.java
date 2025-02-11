package com.echotechblog.service;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.eureka.one.EurekaOneDiscoveryStrategyFactory;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import com.netflix.discovery.EurekaClient;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.echotechblog.service")
@EnableCaching
public class AppConfig {
    @Bean
    public CacheManager cacheManager(EurekaClient eurekaClient) {
        EurekaOneDiscoveryStrategyFactory.setEurekaClient(eurekaClient);
        Config config = new Config();
        config.addMapConfig(new MapConfig().setName("testEmbeddedCache"));
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(false);

        var eurekaConfig = config.getNetworkConfig().getJoin().getEurekaConfig();
        eurekaConfig.setEnabled(true)
                .setProperty("self-registration", "true")
                .setProperty("namespace", "embedded-hazelcast")
                .setProperty("use-metadata-for-host-and-port", "true")
                .setProperty("skip-eureka-registration-verification", "true");
        /* the above property is used because according to official documentation
         * When first node starts, it takes some time to do self-registration with Eureka Server.
         * Until Eureka data is updated it make no sense to verify registration.
         */
        return new HazelcastCacheManager(Hazelcast.newHazelcastInstance(config));
    }
}

//        config.setProperty("hazelcast.max.join.seconds", "20");
//        config.setProperty("hazelcast.discovery.initial.min.cluster.size", "2");
// Hazelcast performance and join settings
//        config.setProperty("hazelcast.max.join.seconds", "10");
//        config.setProperty("hazelcast.heartbeat.interval.seconds", "1");
//        config.setProperty("hazelcast.max.no.heartbeat.seconds", "5");
//        config.setProperty("hazelcast.operation.call.timeout.millis", "3000");
//        config.setProperty("hazelcast.partition.migration.min.delay.seconds", "1");
//        config.setProperty("hazelcast.partition.migration.interval.seconds", "1");

