package com.example.afternote.domain.afternote.controller;

import com.example.afternote.domain.afternote.dto.AfternoteCreateRequest;
import com.example.afternote.domain.afternote.dto.AfternoteCreateResponse;
import com.example.afternote.domain.afternote.dto.AfternotePageResponse;
import com.example.afternote.domain.afternote.dto.AfternotedetailResponse;
import com.example.afternote.domain.afternote.model.AfternoteCategoryType;
import com.example.afternote.domain.afternote.service.AfternoteService;
import com.example.afternote.global.common.ApiResponse;
import com.example.afternote.global.resolver.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Afternote API", description = "afternote 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/afternotes")
public class AfternoteController {

    private final AfternoteService afternoteService;

    @Operation(
            summary = "애프터노트 목록 조회 API",
            description = "애프터노트 목록을 가져옵니다. param으로 category와 page, size를 보내주시면 됩니다."
    )
    @GetMapping
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

    @Operation(
            summary = "애프터노트 상세 목록 조회 API",
            description = "애프터노트 상세목록을 가져옵니다. path variable로 afternote_id를 보내주시면 됩니다."
    )
    @GetMapping("/{afternoteId}")
    public ApiResponse<AfternotedetailResponse> getDetailAfternote(
            @Parameter(hidden = true) @UserId Long userId,
            @Parameter(description = "afternote_id를 입력해주세요!", example = "1")
            @PathVariable Long afternoteId
    ) {
         AfternotedetailResponse afternotedetailResponse = afternoteService.getDetailAfternote(userId, afternoteId);
        return ApiResponse.success(afternotedetailResponse);
    }
    @Operation(
            summary = "애프터노트 생성 API",
            description = "새로운 애프터노트를 생성합니다. 카테고리에 따라 다른 필드를 전달해야 합니다."
    )
    @PostMapping
    public ApiResponse<AfternoteCreateResponse> createAfternote(
            @Parameter(hidden = true) @UserId Long userId,
            @Valid @RequestBody AfternoteCreateRequest request
    ) {
        AfternoteCreateResponse response = afternoteService.createAfternote(userId, request);
        return ApiResponse.success(response);
    }

    @Operation(
            summary = "애프터노트 수정 API",
            description = "기존 애프터노트를 수정합니다."
    )
    @PatchMapping("/{afternoteId}")
    public ApiResponse<AfternoteCreateResponse> updateAfternote(
            @Parameter(hidden = true) @UserId Long userId,
            @Parameter(description = "애프터노트 ID", example = "10")
            @PathVariable Long afternoteId,
            @Valid @RequestBody AfternoteCreateRequest request
    ) {
        AfternoteCreateResponse response = afternoteService.updateAfternote(userId, afternoteId, request);
        return ApiResponse.success(response);
    }

    @Operation(
            summary = "애프터노트 삭제 API",
            description = "특정 애프터노트를 삭제합니다."
    )
    @DeleteMapping("/{afternoteId}")
    public ApiResponse<Void> deleteAfternote(
            @Parameter(hidden = true) @UserId Long userId,
            @Parameter(description = "애프터노트 ID", example = "10")
            @PathVariable Long afternoteId
    ) {
        afternoteService.deleteAfternote(userId, afternoteId);
        return ApiResponse.success(null);
    }
}

