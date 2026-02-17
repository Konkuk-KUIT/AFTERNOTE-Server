package com.example.afternote.domain.receiver.controller;

import com.example.afternote.domain.receiver.dto.*;
import com.example.afternote.domain.receiver.service.ReceivedService;
import com.example.afternote.global.common.ApiResponse;
import com.example.afternote.global.resolver.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Received API", description = "수신자 등록 API")
@RestController
@RequestMapping("/api/received")
@RequiredArgsConstructor
public class ReceivedController {

    private final ReceivedService receivedService;

    @Operation(
            summary = "타임레터 수신자 등록",
            description = "타임레터에 수신자를 등록합니다. 여러 수신자를 한 번에 등록할 수 있습니다."
    )
    @PostMapping("/time-letters")
    public ApiResponse<List<Long>> createTimeLetterReceivers(
            @Parameter(hidden = true) @UserId Long userId,
            @Valid @RequestBody CreateTimeLetterReceiverRequest request
    ) {
        return ApiResponse.success(receivedService.createTimeLetterReceivers(userId, request));
    }

    @Operation(
            summary = "마인드레코드 수신자 등록",
            description = "마인드레코드에 수신자를 등록합니다. 여러 수신자를 한 번에 등록할 수 있습니다."
    )
    @PostMapping("/mind-records")
    public ApiResponse<List<Long>> createMindRecordReceivers(
            @Parameter(hidden = true) @UserId Long userId,
            @Valid @RequestBody CreateMindRecordReceiverRequest request
    ) {
        return ApiResponse.success(receivedService.createMindRecordReceivers(userId, request));
    }
}
