package com.example.shop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;

import com.example.shop.service.KafkaProducerService;
import com.example.shop.service.KafkaConsumerService;
import com.example.shop.model.TrackingEvent;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserBehaviorController {

    private final KafkaProducerService kafkaProducerService;
    private final KafkaConsumerService kafkaConsumerService;

    public UserBehaviorController(KafkaProducerService kafkaProducerService, KafkaConsumerService kafkaConsumerService) {
        this.kafkaProducerService = kafkaProducerService;
        this.kafkaConsumerService = kafkaConsumerService;
    }

    @Operation(summary = "Track user behavior on the website",
               description = "Receives pre-parsed tracking data and sends it to Kafka.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event successfully tracked"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload")
    })
    @PostMapping("/track")
    public ResponseEntity<Void> trackUserBehavior(@RequestBody TrackingEvent trackingEvent) {

        if (trackingEvent.getProductId() == null || trackingEvent.getWeight() == null) {
            return ResponseEntity.badRequest().build();
        }

        for(int i=0;i<trackingEvent.getQuantity();i++){
            kafkaProducerService.sendEvent("product-interaction", trackingEvent);
        }

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get top 5 products", description = "Gets the top 5 products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Top products returned"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/top-products")
    public ResponseEntity<List<Map.Entry<String, Integer>>> GetTopProducts() {
        try {
            List<Map.Entry<String, Integer>> topProducts = kafkaConsumerService.getTopProducts(5, null);
            return ResponseEntity.ok(topProducts);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
