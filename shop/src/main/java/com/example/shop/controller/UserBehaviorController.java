package com.example.shop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import com.example.shop.service.KafkaProducerService;
import com.example.shop.model.TrackingEvent;

@RestController
@RequestMapping("/api")
public class UserBehaviorController {

    private final KafkaProducerService kafkaProducerService;

    public UserBehaviorController(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
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
}
