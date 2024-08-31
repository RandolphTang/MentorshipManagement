package com.example.securityOAuth.config;

import com.mentorship.shared.events.UserCreatedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {


//    @Value("${spring.kafka.bootstrap-servers}")
//    private String bootstrapServers;
//
//    @Value("${spring.kafka.properties.security.protocol}")
//    private String securityProtocol;
//
//    @Value("${spring.kafka.properties.sasl.mechanism}")
//    private String saslMechanism;
//
//    @Value("${spring.kafka.properties.sasl.jaas.config}")
//    private String jaasConfig;

    @Bean
    public KafkaTemplate<Long, UserCreatedEvent> kafkaUserCreatedTemplate() {
        return new KafkaTemplate<>(userCreatedProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, Long> kafkaUserDeletionTemplate() {
        return new KafkaTemplate<>(userDeletionProducerFactory());
    }


    @Bean
    public NewTopic userTopic() {
        return TopicBuilder.name("user-created")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic userDeletionTopic() {
        return TopicBuilder.name("user-deletion")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public ProducerFactory<Long, UserCreatedEvent> userCreatedProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public ProducerFactory<String, Long> userDeletionProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }
//
//    @Bean
//    public ProducerFactory<Long, UserCreatedEvent> userCreatedProducerFactory() {
//        return new DefaultKafkaProducerFactory<>(getCommonConfigs());
//    }
//
//    @Bean
//    public ProducerFactory<String, Long> userDeletionProducerFactory() {
//        return new DefaultKafkaProducerFactory<>(getCommonConfigs());
//    }
//
//    private Map<String, Object> getCommonConfigs() {
//        Map<String, Object> configProps = new HashMap<>();
//        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//        configProps.put("security.protocol", securityProtocol);
//        configProps.put("sasl.mechanism", saslMechanism);
//        configProps.put("sasl.jaas.config", jaasConfig);
//        return configProps;
//    }


}
