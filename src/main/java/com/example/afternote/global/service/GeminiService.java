package com.example.afternote.global.service;

import com.example.afternote.domain.mindrecord.diary.model.Diary;
import com.example.afternote.domain.mindrecord.diary.repository.DiaryRepository;
import com.example.afternote.domain.mindrecord.emotion.model.Emotion;
import com.example.afternote.domain.mindrecord.emotion.repository.EmotionRepository;
import com.example.afternote.domain.mindrecord.event.MindRecordCreatedEvent;
import com.example.afternote.domain.mindrecord.model.MindRecord;
import com.example.afternote.domain.mindrecord.model.MindRecordType;
import com.example.afternote.domain.mindrecord.question.model.DailyQuestionAnswer;
import com.example.afternote.domain.mindrecord.question.repository.DailyQuestionAnswerRepository;
import com.example.afternote.domain.mindrecord.repository.MindRecordRepository;
import com.example.afternote.domain.mindrecord.thought.model.DeepThought;
import com.example.afternote.domain.mindrecord.thought.repository.DeepThoughtRepository;
import com.example.afternote.domain.user.repository.UserRepository;
import com.example.afternote.global.exception.CustomException;
import com.example.afternote.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import com.example.afternote.domain.mindrecord.emotion.service.EmotionCacheService;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiService {

    private final ChatModel chatModel;
    private final MindRecordRepository mindRecordRepository;
    private final DiaryRepository diaryRepository;
    private final DeepThoughtRepository deepThoughtRepository;
    private final DailyQuestionAnswerRepository dailyQuestionAnswerRepository;
    private final EmotionRepository emotionRepository;
    private final UserRepository userRepository;
    private final EmotionCacheService emotionCacheService;

    /**
     * MindRecord의 내용을 분석하여 감정 키워드를 추출합니다.
     * API 실패 시 기본 감정값을 반환합니다.
     * @param mindRecord 감정을 분석할 MindRecord
     * @return 감정 키워드 (예: "행복", "우울", "불안" 등)
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMindRecordCreated(MindRecordCreatedEvent event) {
        MindRecord mindRecord = mindRecordRepository.findById(event.getMindRecordId())
                .orElseThrow(() -> new CustomException(ErrorCode.MIND_RECORD_NOT_FOUND));
        analyzeEmotion(mindRecord);
    }

    private void analyzeEmotion(MindRecord mindRecord) {
        try {
            // MindRecord 타입에 따라 content 추출
            String content = extractContentFromMindRecord(mindRecord);

            // 감정 분석 프롬프트
            String instruction = String.format(
                    "다음 텍스트를 분석하여, 작성자의 마음이나 상황을 가장 잘 나타내는 '핵심 키워드' 3개를 선정해주세요.\n" +
                            "단순한 감정뿐만 아니라, 글의 제재나 주제가 되는 단어도 포함됩니다.\n\n" +
                            "[키워드 예시]\n" +
                            "- 감정: 행복, 그리움, 외로움, 감사, 열정, 불안\n" +
                            "- 대상/주제: 가족, 친구, 여행, 도전, 휴식, 성공, 꿈\n\n" +
                            "텍스트: %s\n\n" +
                            "답변 형식: 다른 미사여구 없이, 오직 키워드 3개를 띄어쓰기로 구분하여 한 줄로 나열해주세요.\n" +
                            "(예시: 친구 여행 도전)",
                content
            );
            String answer = chatModel.call(new Prompt(instruction)).getResult().getOutput().getText().trim();
            log.debug("제미니 성공!!!: {}",answer);
            saveEmotion(mindRecord, answer);
        } catch (Exception e) {
            log.error("Gemini API 호출 실패, 기본값 반환: {}", e.getMessage());
        }
    }

    /**
     * MindRecord 타입에 따라 실제 content를 추출합니다.
     */
    private String extractContentFromMindRecord(MindRecord mindRecord) {
        MindRecordType type = mindRecord.getType();
        
        return switch (type) {
            case DIARY -> {
                Diary diary = diaryRepository.findByMindRecord(mindRecord)
                        .orElseThrow(() -> new CustomException(ErrorCode.MIND_RECORD_NOT_FOUND));
                yield diary.getContent();
            }
            case DEEP_THOUGHT -> {
                DeepThought deepThought = deepThoughtRepository.findByMindRecord(mindRecord)
                        .orElseThrow(() -> new CustomException(ErrorCode.MIND_RECORD_NOT_FOUND));
                yield deepThought.getContent();
            }
            case DAILY_QUESTION -> {
                DailyQuestionAnswer answer = dailyQuestionAnswerRepository.findByMindRecord(mindRecord)
                        .orElseThrow(() -> new CustomException(ErrorCode.MIND_RECORD_NOT_FOUND));
                yield answer.getContent();
            }
        };
    }

    private void saveEmotion(MindRecord mindRecord, String keyword) {
        Long userId = mindRecord.getUser().getId();
        Long mindRecordId = mindRecord.getId();
        Emotion emotion = emotionRepository.findByUserIdAndMindRecordId(userId, mindRecordId)
                .orElseGet(() -> {
                    Emotion newEmotion = new Emotion();
                    newEmotion.setUser(mindRecord.getUser());
                    newEmotion.setMindRecord(mindRecord);
                    return newEmotion;
                });
        emotion.setKeyword(keyword);
        emotionRepository.save(emotion);
    }
    @Transactional
    public String summaryEmotion(Long userId){
        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        // 1) Redis에서 먼저 조회
        String cachedSummary = emotionCacheService.getEmotionSummaryText(userId);
        if (cachedSummary != null && !cachedSummary.isEmpty()) {
            return cachedSummary;
        }
        
        // 2) Redis에 없으면 LLM으로 생성
        String keywordsString = emotionRepository.findByUserId(userId).stream()
                .map(Emotion::getKeyword)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));

        String instruction = String.format(
                "사용자의 이번 주 일기에서 가장 많이 등장한 핵심 키워드는 [%s]입니다.\n" +
                        "이 키워드들을 조합하여 사용자의 한 주를 아우르는 통찰력 있는 '한 문장'을 작성해주세요.\n\n" +
                        "[제약 조건]\n" +
                        "1. 말투: 따뜻하고 부드러운 존댓말 (예: '~마음이 엿보입니다', '~시간이었군요')\n" +
                        "2. 문장은 자연스럽게 연결하고, 모든 키워드를 다 쓸 필요는 없음 (가장 중요한 2개 정도 활용)\n" +
                        "3. 길이: 20자 이내\n\n" +
                        "결과만 출력하세요.",
                keywordsString
        );

        String answer = "";
        try {
            answer = chatModel.call(new Prompt(instruction)).getResult().getOutput().getText().trim();
            log.debug("제미니 요약 성공!!!: {}",answer);
        } catch (Exception e) {
            log.error("Gemini API 호출 실패, 기본값 반환: {}", e.getMessage());
            throw new CustomException(ErrorCode.GEMINI_FAILED);
        }
        
        // 3) 생성한 요약을 Redis에 저장 (1일)
        if (!answer.isEmpty()) {
            emotionCacheService.saveSummaryText(userId, answer);
        }
        
        return answer;
    }
}