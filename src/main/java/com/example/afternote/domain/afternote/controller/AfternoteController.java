package com.example.afternote.domain.afternote.controller;

import com.example.afternote.domain.afternote.dto.AfternotePageResponse;
import com.example.afternote.domain.afternote.model.AfternoteCategoryType;
import com.example.afternote.domain.afternote.service.AfternoteService;
import com.example.afternote.global.common.ApiResponse;
import com.example.afternote.global.resolver.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Afternote API", description = "afternote 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/afternotes")
public class AfternoteController {

    private final AfternoteService afternoteService;

    @Operation(
            summary = "모든 afternote 목록 API",
            description = "모든 afternote 목록을 가져옵니다. param으로 category와 page, size 를 보내주시면됩니다."
    )
    @GetMapping()
    public ApiResponse<AfternotePageResponse> getAfternotes(
            @Parameter(hidden = true) @UserId Long userId,
            @Parameter(description = "카테고리 필터 (SOCIAL, GALLERY, PLAYLIST)", example = "SOCIAL")
            @RequestParam(required = false) AfternoteCategoryType category,
            
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "페이지 사이즈", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        AfternotePageResponse response = afternoteService.getAfternotes(userId, category, page, size);
        return ApiResponse.success(response);
    }
}

