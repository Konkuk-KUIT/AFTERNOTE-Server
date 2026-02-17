package com.example.afternote.domain.timeletter.service;

import com.example.afternote.domain.image.service.S3Service;
import com.example.afternote.domain.receiver.model.Receiver;
import com.example.afternote.domain.receiver.model.TimeLetterReceiver;
import com.example.afternote.domain.receiver.repository.TimeLetterReceiverRepository;
import com.example.afternote.domain.receiver.service.ReceivedService;
import com.example.afternote.domain.timeletter.dto.TimeLetterCreateRequest;
import com.example.afternote.domain.timeletter.dto.TimeLetterDeleteRequest;
import com.example.afternote.domain.timeletter.dto.TimeLetterListResponse;
import com.example.afternote.domain.timeletter.dto.TimeLetterResponse;
import com.example.afternote.domain.timeletter.dto.TimeLetterUpdateRequest;
import com.example.afternote.domain.timeletter.model.TimeLetter;
import com.example.afternote.domain.timeletter.model.TimeLetterStatus;
import com.example.afternote.domain.timeletter.repository.TimeLetterMediaRepository;
import com.example.afternote.domain.timeletter.repository.TimeLetterRepository;
import com.example.afternote.domain.user.model.User;
import com.example.afternote.domain.user.repository.UserRepository;
import com.example.afternote.global.exception.CustomException;
import com.example.afternote.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeLetterServiceTest {

    @InjectMocks
    private TimeLetterService timeLetterService;

    @Mock
    private TimeLetterRepository timeLetterRepository;

    @Mock
    private TimeLetterMediaRepository timeLetterMediaRepository;

    @Mock
    private TimeLetterReceiverRepository timeLetterReceiverRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReceivedService receivedService;

    @Mock
    private S3Service s3Service;

    private User testUser;
    private TimeLetter testTimeLetter;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@test.com")
                .password("password123!")
                .name("테스트유저")
                .build();
        ReflectionTestUtils.setField(testUser, "id", 1L);

        testTimeLetter = TimeLetter.builder()
                .user(testUser)
                .title("테스트 제목")
                .content("테스트 내용")
                .sendAt(LocalDateTime.now().plusDays(1))
                .status(TimeLetterStatus.SCHEDULED)
                .build();
        ReflectionTestUtils.setField(testTimeLetter, "id", 1L);
        ReflectionTestUtils.setField(testTimeLetter, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(testTimeLetter, "updatedAt", LocalDateTime.now());
    }

    @Test
    @DisplayName("DRAFT 상태로 타임레터 생성 성공")
    void createTimeLetter_DRAFT_Success() {
        // given
        TimeLetterCreateRequest request = mock(TimeLetterCreateRequest.class);
        given(request.getStatus()).willReturn(TimeLetterStatus.DRAFT);
        given(request.getTitle()).willReturn("임시저장 제목");
        given(request.getContent()).willReturn(null);
        given(request.getSendAt()).willReturn(null);
        given(request.getMediaList()).willReturn(null);

        TimeLetter draftTimeLetter = TimeLetter.builder()
                .user(testUser)
                .title("임시저장 제목")
                .content(null)
                .sendAt(null)
                .status(TimeLetterStatus.DRAFT)
                .build();
        ReflectionTestUtils.setField(draftTimeLetter, "id", 2L);
        ReflectionTestUtils.setField(draftTimeLetter, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(draftTimeLetter, "updatedAt", LocalDateTime.now());

        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
        given(timeLetterRepository.save(any(TimeLetter.class))).willReturn(draftTimeLetter);

        // when
        TimeLetterResponse response = timeLetterService.createTimeLetter(1L, request);

        // then
        assertThat(response.getStatus()).isEqualTo(TimeLetterStatus.DRAFT);
        assertThat(response.getTitle()).isEqualTo("임시저장 제목");
        verify(timeLetterRepository).save(any(TimeLetter.class));
    }

    @Test
    @DisplayName("SCHEDULED 상태로 타임레터 생성 성공")
    void createTimeLetter_SCHEDULED_Success() {
        // given
        LocalDateTime futureDate = LocalDateTime.now().plusDays(7);
        TimeLetterCreateRequest request = mock(TimeLetterCreateRequest.class);
        given(request.getStatus()).willReturn(TimeLetterStatus.SCHEDULED);
        given(request.getTitle()).willReturn("정식 등록 제목");
        given(request.getContent()).willReturn("정식 등록 내용");
        given(request.getSendAt()).willReturn(futureDate);
        given(request.getMediaList()).willReturn(null);

        TimeLetter scheduledTimeLetter = TimeLetter.builder()
                .user(testUser)
                .title("정식 등록 제목")
                .content("정식 등록 내용")
                .sendAt(futureDate)
                .status(TimeLetterStatus.SCHEDULED)
                .build();
        ReflectionTestUtils.setField(scheduledTimeLetter, "id", 3L);
        ReflectionTestUtils.setField(scheduledTimeLetter, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(scheduledTimeLetter, "updatedAt", LocalDateTime.now());

        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
        given(timeLetterRepository.save(any(TimeLetter.class))).willReturn(scheduledTimeLetter);

        // when
        TimeLetterResponse response = timeLetterService.createTimeLetter(1L, request);

        // then
        assertThat(response.getStatus()).isEqualTo(TimeLetterStatus.SCHEDULED);
        assertThat(response.getTitle()).isEqualTo("정식 등록 제목");
        assertThat(response.getContent()).isEqualTo("정식 등록 내용");
        verify(timeLetterRepository).save(any(TimeLetter.class));
    }

    @Test
    @DisplayName("SCHEDULED 상태 생성 시 필수값 누락으로 실패")
    void createTimeLetter_SCHEDULED_MissingFields_Fail() {
        // given
        TimeLetterCreateRequest request = mock(TimeLetterCreateRequest.class);
        given(request.getStatus()).willReturn(TimeLetterStatus.SCHEDULED);
        given(request.getTitle()).willReturn(null);
        given(request.getContent()).willReturn("내용만 있음");
        given(request.getSendAt()).willReturn(LocalDateTime.now().plusDays(1));

        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));

        // when & then
        assertThatThrownBy(() -> timeLetterService.createTimeLetter(1L, request))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customEx = (CustomException) ex;
                    assertThat(customEx.getErrorCode()).isEqualTo(ErrorCode.TIME_LETTER_REQUIRED_FIELDS);
                });
    }

    @Test
    @DisplayName("SCHEDULED 상태 생성 시 과거 날짜로 실패")
    void createTimeLetter_SCHEDULED_PastDate_Fail() {
        // given
        TimeLetterCreateRequest request = mock(TimeLetterCreateRequest.class);
        given(request.getStatus()).willReturn(TimeLetterStatus.SCHEDULED);
        given(request.getTitle()).willReturn("제목");
        given(request.getContent()).willReturn("내용");
        given(request.getSendAt()).willReturn(LocalDateTime.now().minusDays(1));

        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));

        // when & then
        assertThatThrownBy(() -> timeLetterService.createTimeLetter(1L, request))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customEx = (CustomException) ex;
                    assertThat(customEx.getErrorCode()).isEqualTo(ErrorCode.TIME_LETTER_INVALID_SEND_DATE);
                });
    }

    @Test
    @DisplayName("존재하지 않는 타임레터 조회 시 404 에러")
    void getTimeLetter_NotFound_Fail() {
        // given
        given(timeLetterRepository.findByIdAndUserId(999L, 1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> timeLetterService.getTimeLetter(1L, 999L))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customEx = (CustomException) ex;
                    assertThat(customEx.getErrorCode()).isEqualTo(ErrorCode.TIME_LETTER_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("SENT 상태 타임레터 수정 시 실패")
    void updateTimeLetter_SentStatus_Fail() {
        // given
        TimeLetter sentTimeLetter = TimeLetter.builder()
                .user(testUser)
                .title("발송된 편지")
                .content("내용")
                .sendAt(LocalDateTime.now().minusDays(1))
                .status(TimeLetterStatus.SENT)
                .build();
        ReflectionTestUtils.setField(sentTimeLetter, "id", 5L);
        ReflectionTestUtils.setField(sentTimeLetter, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(sentTimeLetter, "updatedAt", LocalDateTime.now());

        TimeLetterUpdateRequest request = mock(TimeLetterUpdateRequest.class);

        given(timeLetterRepository.findByIdAndUserId(5L, 1L)).willReturn(Optional.of(sentTimeLetter));

        // when & then
        assertThatThrownBy(() -> timeLetterService.updateTimeLetter(1L, 5L, request))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customEx = (CustomException) ex;
                    assertThat(customEx.getErrorCode()).isEqualTo(ErrorCode.TIME_LETTER_ALREADY_SENT);
                });
    }

    @Test
    @DisplayName("삭제 요청에 타인의 타임레터 포함 시 실패")
    void deleteTimeLetters_OtherUserIncluded_Fail() {
        // given
        TimeLetterDeleteRequest request = mock(TimeLetterDeleteRequest.class);
        given(request.getTimeLetterIds()).willReturn(List.of(1L, 2L, 3L));

        // 3개 요청했지만 2개만 조회됨 (1개는 다른 사용자 것)
        given(timeLetterRepository.findByIdInAndUserId(anyList(), anyLong()))
                .willReturn(List.of(testTimeLetter));

        // when & then
        assertThatThrownBy(() -> timeLetterService.deleteTimeLetters(1L, request))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customEx = (CustomException) ex;
                    assertThat(customEx.getErrorCode()).isEqualTo(ErrorCode.TIME_LETTER_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("타임레터 전체 조회 성공 (SCHEDULED만)")
    void getTimeLetters_Success() {
        // given
        List<TimeLetter> scheduledLetters = List.of(testTimeLetter);
        given(timeLetterRepository.findByUserIdAndStatusOrderByCreatedAtDesc(1L, TimeLetterStatus.SCHEDULED))
                .willReturn(scheduledLetters);
        given(timeLetterMediaRepository.findByTimeLetterIdIn(anyList())).willReturn(new ArrayList<>());

        // when
        TimeLetterListResponse response = timeLetterService.getTimeLetters(1L);

        // then
        assertThat(response.getTotalCount()).isEqualTo(1);
        assertThat(response.getTimeLetters().get(0).getStatus()).isEqualTo(TimeLetterStatus.SCHEDULED);
    }

    @Test
    @DisplayName("임시저장 전체 조회 성공 (DRAFT만)")
    void getTemporaryTimeLetters_Success() {
        // given
        TimeLetter draftLetter = TimeLetter.builder()
                .user(testUser)
                .title("임시")
                .status(TimeLetterStatus.DRAFT)
                .build();
        ReflectionTestUtils.setField(draftLetter, "id", 10L);
        ReflectionTestUtils.setField(draftLetter, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(draftLetter, "updatedAt", LocalDateTime.now());

        given(timeLetterRepository.findByUserIdAndStatusOrderByCreatedAtDesc(1L, TimeLetterStatus.DRAFT))
                .willReturn(List.of(draftLetter));
        given(timeLetterMediaRepository.findByTimeLetterIdIn(anyList())).willReturn(new ArrayList<>());

        // when
        TimeLetterListResponse response = timeLetterService.getTemporaryTimeLetters(1L);

        // then
        assertThat(response.getTotalCount()).isEqualTo(1);
        assertThat(response.getTimeLetters().get(0).getStatus()).isEqualTo(TimeLetterStatus.DRAFT);
    }

    @Test
    @DisplayName("타임레터 수정 성공")
    void updateTimeLetter_Success() {
        // given
        TimeLetterUpdateRequest request = mock(TimeLetterUpdateRequest.class);
        given(request.getTitle()).willReturn("수정된 제목");
        given(request.getContent()).willReturn(null);
        given(request.getSendAt()).willReturn(null);
        given(request.getStatus()).willReturn(null);
        given(request.getMediaList()).willReturn(null);

        given(timeLetterRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(testTimeLetter));
        given(timeLetterMediaRepository.findByTimeLetterId(1L)).willReturn(new ArrayList<>());

        // when
        TimeLetterResponse response = timeLetterService.updateTimeLetter(1L, 1L, request);

        // then
        assertThat(response.getTitle()).isEqualTo("수정된 제목");
    }
}
