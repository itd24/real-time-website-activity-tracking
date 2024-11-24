package com.example.shop.service.configuration;

import com.example.shop.model.TrackingEvent;
import io.confluent.kafka.streams.serdes.json.KafkaJsonSchemaSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.context.annotation.Configuration;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@EnableKafkaStreams
public class KafkaStreamsConfig {

    // Store aggregated results in a HashMap
    private final Map<String, Double> aggregationStore = new HashMap<>();

    @Bean
    public KafkaStreams kafkaStreams(StreamsBuilder streamsBuilder) {
        // JSON SerDe for TrackingEvent
        KafkaJsonSchemaSerde<TrackingEvent> jsonSerde = new KafkaJsonSchemaSerde<>();
        jsonSerde.configure(streamsConfig(), false);

        System.out.println("---------------------------staaaaaarted---------------");

        // Stream processing logic
        KStream<String, TrackingEvent> productStream = streamsBuilder.stream(
                "product-interactions", Consumed.with(Serdes.String(), jsonSerde)
        );

        KTable<String, Double> aggregatedStream = productStream
            .groupBy((key, event) -> event.getProductId(), Grouped.with(Serdes.String(), jsonSerde))
            .aggregate(
                () -> 0.0, // Initial value
                (productId, event, currentAggregate) -> currentAggregate + (event.getWeight() * event.getQuantity()), // Aggregation logic
                Materialized.with(Serdes.String(), Serdes.Double())
            );

            productStream.peek((key, event) -> {
                System.out.println("Key: " + key + ", Event: " + event);
            });

        // Write aggregated data to another topic
        aggregatedStream.toStream().to("aggregated-product-data", Produced.with(Serdes.String(), Serdes.Double()));

        // Update the in-memory HashMap for real-time access
        aggregatedStream.toStream().foreach((productId, total) -> aggregationStore.put(productId, total));

        return new KafkaStreams(streamsBuilder.build(), propertiesConfig());
    }

    @Bean
    public Map<String, Object> streamsConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "product-interaction-aggregation");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put("schema.registry.url", "http://apicurio-registry:8080/apis/registry/v2");
        return props;
    }

    @Bean
    public Properties propertiesConfig() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "product-interaction-aggregation");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put("schema.registry.url", "http://apicurio-registry:8080/apis/registry/v2");
        return props;
    }

    public Map<String, Double> getAggregationStore() {
        return aggregationStore;
    }
}
