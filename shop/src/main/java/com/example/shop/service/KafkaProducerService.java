package com.example.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.shop.model.TrackingEvent;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, TrackingEvent> kafkaTemplate;
    private static final String TOPIC = "product-interactions";

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, TrackingEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(String key, TrackingEvent message) {
        kafkaTemplate.send(TOPIC, key, message);
    }
}