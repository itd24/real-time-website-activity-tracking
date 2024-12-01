package com.example.shop.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductScore {
    private int score;

    @JsonCreator
    public ProductScore(
        @JsonProperty("score") int score
    ) {
        this.score = score;
    }

    // Getters and setters
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "ProductScore{" +
                "score=" + score +
                '}';
    }
}
