package org.example.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ZookeeperClientConfigProperties.class)
public class ZooKeeperClientConfig {

    /**
     * multi-parameters to build Zookeeper connection
     *
     * @return client
     */
    @Bean(name = "zookeeperClient")
    public CuratorFramework createWithOptions(ZookeeperClientConfigProperties properties) {
        ExponentialBackoffRetry backoffRetry = new ExponentialBackoffRetry(properties.getBaseSleepTimeMs(), properties.getMaxRetries());
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(properties.getConnectString())
                .retryPolicy(backoffRetry)
                .sessionTimeoutMs(properties.getSessionTimeoutMs())
                .connectionTimeoutMs(properties.getConnectionTimeoutMs())
                .build();
        client.start();
        return client;
    }

}