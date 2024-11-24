package com.example.shop.service;

import com.example.shop.service.configuration.KafkaStreamsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AggregationService {

    private final KafkaStreamsConfig kafkaStreamsConfig;

    @Autowired
    public AggregationService(KafkaStreamsConfig kafkaStreamsConfig) {
        this.kafkaStreamsConfig = kafkaStreamsConfig;
    }

    // This method returns the aggregated data
    public Map<String, Double> getAggregatedData() {
        return kafkaStreamsConfig.getAggregationStore();
    }
}
