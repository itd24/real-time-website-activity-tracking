package com.example.shop.service;

import io.apicurio.registry.serde.SerdeConfig;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Service;

import com.example.shop.model.TrackingEvent;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class KafkaStreamAnalysisService {

    private final Properties streamsConfig;

    public KafkaStreamAnalysisService(@Qualifier("kafkaStreamAnalysisProperties") Properties streamsConfig) {
        this.streamsConfig = streamsConfig;
        initializeStream();
    }

    private void initializeStream() {
        StreamsBuilder builder = new StreamsBuilder();

        // Define Serde for POJO
        JsonSerde<TrackingEvent> pastProductInteractionSerde = new JsonSerde<>(TrackingEvent.class);
        pastProductInteractionSerde.configure(serdeConfig(), false);

        // Read past-product-interactions
        KStream<String, TrackingEvent> interactionStream = builder.stream(
            "past-product-interactions",
            Consumed.with(Serdes.String(), pastProductInteractionSerde)
        );

        // Windowed aggregation: compare product popularity over time periods
        KTable<Windowed<String>, Long> interactionCounts = interactionStream
            .groupBy((key, value) -> value.getProductId(), Grouped.with(Serdes.String(), pastProductInteractionSerde))
            .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofDays(30))) // Tumbling window of 1 month
            .count(Materialized.with(Serdes.String(), Serdes.Long()));

        // Print results
        interactionCounts.toStream().foreach((key, count) -> {
            System.out.printf("Product: %s, Count: %d, Start: %s, End: %s%n",
                key.key(),
                count,
                key.window().startTime(),
                key.window().endTime()
            );
        });

        KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfig);
        streams.start();

        // Graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    private Map<String, Object> serdeConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(SerdeConfig.REGISTRY_URL, "http://apicurio-registry:8080/");
        return config;
    }
}
