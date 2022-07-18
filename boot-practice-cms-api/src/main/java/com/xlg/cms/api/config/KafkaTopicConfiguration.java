package com.xlg.cms.api.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaTopicConfiguration {

    /**
     * 创建 KafkaAmin，可以自动检测集群中是否存在topic，不存在则创建
     */
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> props = new HashMap<>();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,  System.getProperty("spring.kafka.bootstrap-servers"));
        return new KafkaAdmin(props);
    }
}