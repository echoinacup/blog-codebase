package com.echotechblog.service;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.eureka.one.EurekaOneDiscoveryStrategyFactory;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import com.netflix.discovery.AbstractDiscoveryClientOptionalArgs;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.Jersey3DiscoveryClientOptionalArgs;
import com.netflix.discovery.shared.transport.jersey.TransportClientFactories;
import com.netflix.discovery.shared.transport.jersey3.Jersey3TransportClientFactories;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
    @ConditionalOnMissingBean(AbstractDiscoveryClientOptionalArgs.class)
    public Jersey3DiscoveryClientOptionalArgs jersey3DiscoveryClientOptionalArgs() {
        return new Jersey3DiscoveryClientOptionalArgs();
    }

    @Bean
    @ConditionalOnMissingBean(TransportClientFactories.class)
    public Jersey3TransportClientFactories jersey3TransportClientFactories() {
        return new Jersey3TransportClientFactories();
    }

    @Bean
    public CacheManager cacheManager(EurekaClient eurekaClient) {
        EurekaOneDiscoveryStrategyFactory.setEurekaClient(eurekaClient);
        Config config = new Config();
        config.getNetworkConfig().setPort(8426);
        config.addMapConfig(new MapConfig().setName("testEmbeddedCache"));
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(false);
        config.getNetworkConfig().setPortAutoIncrement(true);

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

