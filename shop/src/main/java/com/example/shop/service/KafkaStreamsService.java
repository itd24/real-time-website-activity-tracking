package com.example.shop.service;

import io.apicurio.registry.serde.SerdeConfig;
import io.apicurio.registry.serde.jsonschema.*;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.shop.model.ProductScore;
import com.example.shop.model.TrackingEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class KafkaStreamsService {

    private final Properties streamsConfig;

    public KafkaStreamsService(@Qualifier("kafkaStreamsProperties") Properties streamsConfig) {
        this.streamsConfig = streamsConfig;
        initializeStream();
    }

    private void initializeStream() {
        StreamsBuilder builder = new StreamsBuilder();

        // Configure Serde for JSON using Apicurio's Serde
        JsonSchemaSerde<TrackingEvent> trackingEventSerde = new JsonSchemaSerde<>();
        trackingEventSerde.configure(serdeConfig(), false); // false for value serde

        // Step 1: Read from "product-interactions" topic
        KStream<String, TrackingEvent> trackingEventStream = builder.stream(
            "product-interactions",
            Consumed.with(Serdes.String(), trackingEventSerde)
        );

        // Step 2: Process and aggregate data
        trackingEventStream
            .groupBy((key, event) -> event.getProductId())
            .aggregate(
                () -> 0, // Initializer
                (key, event, aggregate) -> aggregate + event.getQuantity() * event.getWeight(),
                Materialized.with(Serdes.String(), Serdes.Integer())
            )
            .toStream()


            .peek((key, aggregate) -> {
                System.out.println("Aggregated value before toStream: key: "+key+", value: " + aggregate); // Log the aggregated Integer value
            })

            .mapValues(aggregate -> {
                return new ProductScore(aggregate);
            })
            .to("product-scores", Produced.with(Serdes.String(), createProductScoreSerde())); // Explicitly define ProductScore type

        // Build and start the stream
        KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfig);
        streams.start();

        // Graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    private JsonSchemaSerde<ProductScore> createProductScoreSerde() {
        JsonSchemaSerde<ProductScore> productScoreSerde = new JsonSchemaSerde<>();
        productScoreSerde.configure(serdeConfig(), false); // configure with the schema registry URL
        return productScoreSerde;
    }

    private Map<String, Object> serdeConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(SerdeConfig.REGISTRY_URL, "http://apicurio-registry:8080/");
        return config;
    }
}
