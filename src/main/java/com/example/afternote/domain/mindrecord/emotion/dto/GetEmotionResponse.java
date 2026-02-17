package com.example.afternote.domain.mindrecord.emotion.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record GetEmotionResponse(
        List<EmotionStat> emotions,
        String summary
) {

    public record EmotionStat(
            String keyword,
            Double percentage
    ) {}


}