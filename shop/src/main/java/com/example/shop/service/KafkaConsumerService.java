package com.example.shop.service;

import java.io.Console;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.example.shop.model.ProductScore;

@Service
public class KafkaConsumerService {

    private final Map<String, Integer> productScores = new HashMap<>();
 
    @KafkaListener(topics = "product-scores", containerFactory = "kafkaListenerContainerFactory")
    public void consumeEvent(ConsumerRecord<String, ProductScore> record) {
        System.out.println("Something was consuuuuuumeeeeeed");        
        String productId = record.key();
        ProductScore productScore = record.value();

        System.out.println("hmmmm "+productId+", "+productScore);
        
        if(productScores.get(productId) == null || productScores.get(productId) < productScore.getScore())
            productScores.put(productId, productScore.getScore());
        System.out.println(productScores);
    }

    public Map<String, Integer> getAllProductScores() {
        return new HashMap<>(productScores);
    }

    // Method to get the top N products
    public List<Map.Entry<String, Integer>> getTopProducts(int topN, String productIdToExclude) {
        return productScores.entrySet()
                .stream()
                .filter(entry -> productIdToExclude == null || !entry.getKey().equals(productIdToExclude)) 
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue())) 
                .limit(topN) 
                .collect(Collectors.toList());
    }

    public Integer getProductScore(String productId) {
        return productScores.get(productId);
    }
}
