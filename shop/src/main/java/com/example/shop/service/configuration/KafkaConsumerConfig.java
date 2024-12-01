package com.example.shop.service.configuration;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import io.confluent.kafka.serializers.KafkaJsonDeserializer;
import com.example.shop.model.ProductScore;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Bean
    public DefaultKafkaConsumerFactory<String, ProductScore> consumerFactory() {

        Random rand = new Random();

        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaJsonDeserializer.class);
        configProps.put("value.class.name", ProductScore.class);
        //configProps.put(KafkaJsonDeserializer., ProductScore.class);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "product-scores-consumer-group-"+rand.nextInt(1000));
        configProps.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://apicurio-registry:8080/apis/ccompat/v7");
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // Start from the beginning
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProductScore> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ProductScore> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
