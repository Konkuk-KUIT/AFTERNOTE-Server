# API 엔드포인트 수정안 - 타임레터 & 수신자

> **작성 기준**: 현재 코드베이스 (`TimeLetterController`, `ReceivedController`, `ReceiverAuthController`) 기반
> **Swagger**: https://afternote.kro.kr/swagger-ui/index.html#
> **담당자**: 조영탁 (타임레터 & 미디어 담당)

---

## 목차

1. [타임레터 (Time-Letters) API](#1-타임레터-time-letters-api)
2. [수신자 등록 (Received) API](#2-수신자-등록-received-api)
3. [수신자 인증 (Receiver Auth) API](#3-수신자-인증-receiver-auth-api)
4. [S3 파일 업로드 API](#4-s3-파일-업로드-api)
5. [공통 오류 코드](#5-공통-오류-코드)
6. [엔티티 관계도](#6-엔티티-관계도)

---

## 1. 타임레터 (Time-Letters) API

> **Base Path**: `/time-letters`
> **인증**: Bearer JWT Token 필수 (`@UserId`)
> **Swagger Tag**: `📬 TimeLetter API`

### 1-1. 엔드포인트 목록

| 상태 | Method | Endpoint | 설명 |
|:---:|:---:|---|---|
| ✅ | `GET` | `/time-letters` | 정식 등록된(SCHEDULED) 타임레터 전체 조회 |
| ✅ | `GET` | `/time-letters/{timeLetterId}` | 타임레터 단일 조회 |
| ✅ | `POST` | `/time-letters` | 타임레터 등록 (DRAFT/SCHEDULED) |
| ✅ | `PATCH` | `/time-letters/{timeLetterId}` | 타임레터 수정 |
| ✅ | `POST` | `/time-letters/delete` | 타임레터 단일/다건 삭제 |
| ✅ | `GET` | `/time-letters/temporary` | 임시저장(DRAFT) 전체 조회 |
| ✅ | `DELETE` | `/time-letters/temporary` | 임시저장 전체 삭제 |

---

### 1-2. 상세 명세

#### `GET /time-letters` — 타임레터 전체 조회

정식 등록된(SCHEDULED 상태) 타임레터 전체를 조회합니다. 미디어 및 수신자 ID 목록을 포함합니다.

**Response** `200 OK`
```json
{
  "isSuccess": true,
  "code": 200,
  "message": "요청에 성공하였습니다.",
  "result": {
    "timeLetters": [
      {
        "id": 1,
        "title": "미래의 나에게",
        "content": "1년 후의 나에게 보내는 편지...",
        "sendAt": "2025-12-31T23:59:59",
        "status": "SCHEDULED",
        "mediaList": [
          {
            "id": 1,
            "mediaType": "IMAGE",
            "mediaUrl": "https://s3.presigned-url..."
          }
        ],
        "receiverIds": [1, 2],
        "createdAt": "2025-06-01T10:00:00",
        "updatedAt": "2025-06-01T10:00:00"
      }
    ],
    "totalCount": 1
  }
}
```

---

#### `GET /time-letters/{timeLetterId}` — 타임레터 단일 조회

특정 타임레터를 조회합니다. 미디어 URL은 S3 Presigned GET URL로 변환됩니다.

**Path Parameter**
| 이름 | 타입 | 필수 | 설명 |
|---|---|:---:|---|
| `timeLetterId` | Long | O | 타임레터 ID |

**Response** `200 OK`
```json
{
  "isSuccess": true,
  "code": 200,
  "message": "요청에 성공하였습니다.",
  "result": {
    "id": 1,
    "title": "미래의 나에게",
    "content": "1년 후의 나에게 보내는 편지...",
    "sendAt": "2025-12-31T23:59:59",
    "status": "SCHEDULED",
    "mediaList": [
      {
        "id": 1,
        "mediaType": "IMAGE",
        "mediaUrl": "https://s3.presigned-url..."
      }
    ],
    "receiverIds": [1, 2, 3],
    "createdAt": "2025-06-01T10:00:00",
    "updatedAt": "2025-06-01T10:00:00"
  }
}
```

**Error**
| Code | 상황 |
|:---:|---|
| 420 | 타임레터를 찾을 수 없거나 본인의 타임레터가 아닌 경우 |

---

#### `POST /time-letters` — 타임레터 등록

새 타임레터를 등록합니다. `status`가 `SCHEDULED`이면 제목, 내용, 발송일시, 수신자가 필수입니다.

**Request Body**
```json
{
  "title": "미래의 나에게",
  "content": "1년 후의 나에게 보내는 편지...",
  "sendAt": "2025-12-31T23:59:59",
  "status": "SCHEDULED",
  "mediaList": [
    {
      "mediaType": "IMAGE",
      "mediaUrl": "https://bucket.s3.region.amazonaws.com/timeletters/xxx.jpg"
    }
  ],
  "receiverIds": [1, 2, 3],
  "deliveredAt": "2025-12-31T23:59:59"
}
```

| 필드 | 타입 | 필수 | 설명 |
|---|---|:---:|---|
| `title` | String | △ | 제목 (SCHEDULED일 때 필수) |
| `content` | String | △ | 내용 (SCHEDULED일 때 필수) |
| `sendAt` | LocalDateTime | △ | 발송 예정 시간 (SCHEDULED일 때 필수, 현재시간 이후) |
| `status` | Enum | **O** | `DRAFT` (임시저장) 또는 `SCHEDULED` (정식등록) |
| `mediaList` | Array | X | 첨부 미디어 목록 |
| `mediaList[].mediaType` | Enum | O* | `IMAGE`, `VIDEO`, `AUDIO`, `DOCUMENT` |
| `mediaList[].mediaUrl` | String | O* | S3에 업로드된 파일의 영구 URL |
| `receiverIds` | Array[Long] | △ | 수신자 ID 목록 (SCHEDULED일 때 필수) |
| `deliveredAt` | LocalDateTime | X | 배달 예정 시간 (미입력 시 sendAt 사용) |

> △ = SCHEDULED 상태일 때 필수, DRAFT일 때 선택

**Response** `200 OK` — `TimeLetterResponse` (단일 조회와 동일 형식)

**비즈니스 로직**:
- `DRAFT`: 모든 필드 선택 사항. 임시저장 용도.
- `SCHEDULED`: title, content, sendAt, receiverIds 필수. sendAt은 미래 시점이어야 함.
- 미디어는 Presigned URL로 S3에 미리 업로드 후, 해당 `fileUrl`을 `mediaUrl`로 전달.
- 수신자 등록 시 본인이 등록한 수신자인지 소유권 검증.

**Error**
| Code | 상황 |
|:---:|---|
| 424 | SCHEDULED 상태인데 필수 필드(title/content/sendAt) 누락 |
| 425 | sendAt이 현재 시간보다 이전 |
| 468 | receiverIds에 존재하지 않는 수신자 포함 |
| 475 | SCHEDULED인데 receiverIds 누락/빈 배열 |

---

#### `PATCH /time-letters/{timeLetterId}` — 타임레터 수정

타임레터를 수정합니다. SENT 상태의 타임레터는 수정 불가합니다. 미전달 필드는 기존 값을 유지합니다.

**Request Body**
```json
{
  "title": "수정된 제목",
  "content": "수정된 내용",
  "sendAt": "2026-06-30T23:59:59",
  "status": "SCHEDULED",
  "mediaList": [
    {
      "mediaType": "VIDEO",
      "mediaUrl": "https://bucket.s3.region.amazonaws.com/timeletters/yyy.mp4"
    }
  ]
}
```

| 필드 | 타입 | 필수 | 설명 |
|---|---|:---:|---|
| `title` | String | X | 수정할 제목 |
| `content` | String | X | 수정할 내용 |
| `sendAt` | LocalDateTime | X | 수정할 발송 예정 시간 |
| `status` | Enum | X | 변경할 상태 (DRAFT ↔ SCHEDULED) |
| `mediaList` | Array | X | 미디어 목록 (전달 시 기존 미디어 전체 교체) |

**비즈니스 로직**:
- `mediaList` 전달 시: 기존 미디어 전체 삭제 후 새로 저장 (교체 방식)
- `mediaList` 미전달 시: 기존 미디어 유지
- SCHEDULED 상태(변경 후 기준)이면 title, content, sendAt 유효성 재검증
- **수신자 수정은 이 API에서 불가** → `POST /api/received/time-letters`로 별도 관리

**Error**
| Code | 상황 |
|:---:|---|
| 420 | 타임레터를 찾을 수 없음 |
| 422 | 이미 발송된(SENT) 타임레터 수정 시도 |
| 424 | SCHEDULED 상태인데 필수 필드 누락 |
| 425 | sendAt이 현재 시간보다 이전 |

---

#### `POST /time-letters/delete` — 타임레터 삭제

타임레터를 단일/다건 삭제합니다. SENT 상태는 삭제 불가합니다.

> **주의**: HTTP Method가 `DELETE`가 아닌 `POST`입니다 (Request Body 필요).

**Request Body**
```json
{
  "timeLetterIds": [1, 2, 3]
}
```

| 필드 | 타입 | 필수 | 설명 |
|---|---|:---:|---|
| `timeLetterIds` | Array[Long] | **O** | 삭제할 타임레터 ID 목록 |

**Response** `200 OK`
```json
{
  "isSuccess": true,
  "code": 200,
  "message": "요청에 성공하였습니다.",
  "result": null
}
```

**비즈니스 로직**:
- 요청한 ID 중 본인의 것이 아니거나 존재하지 않는 것이 있으면 에러
- 연관 미디어도 함께 삭제
- SENT 상태인 항목이 하나라도 포함되면 전체 요청 거부

**Error**
| Code | 상황 |
|:---:|---|
| 420 | 존재하지 않거나 본인의 타임레터가 아닌 ID 포함 |
| 422 | SENT 상태의 타임레터 포함 |

---

#### `GET /time-letters/temporary` — 임시저장 전체 조회

DRAFT 상태의 타임레터 전체를 조회합니다. 응답 형식은 전체 조회와 동일합니다.

---

#### `DELETE /time-letters/temporary` — 임시저장 전체 삭제

DRAFT 상태의 타임레터를 모두 삭제합니다. 연관 미디어도 함께 삭제됩니다.

**Response** `200 OK`
```json
{
  "isSuccess": true,
  "code": 200,
  "message": "요청에 성공하였습니다.",
  "result": null
}
```

---

### 1-3. 타임레터 상태 플로우

```
  ┌──────────┐     정식등록     ┌────────────┐     sendAt 도달     ┌──────────┐
  │  DRAFT   │ ──────────────→ │ SCHEDULED  │ ──────────────────→ │   SENT   │
  │ (임시저장) │ ←────────────  │ (발송 대기) │      (스케줄러)      │ (발송완료) │
  └──────────┘   상태 변경 가능   └────────────┘                     └──────────┘
       │                              │                                  │
       │  수정/삭제 가능               │  수정/삭제 가능                   │ 수정/삭제 불가
       └──────────────────────────────┘                                  │
                                                                   ✖ Immutable
```

### 1-4. 미디어 타입

| Enum 값 | 설명 | 허용 확장자 |
|---|---|---|
| `IMAGE` | 이미지 | jpg, jpeg, png, gif, webp, heic |
| `VIDEO` | 영상 | mp4, mov |
| `AUDIO` | 음성 | mp3, m4a, wav |
| `DOCUMENT` | 문서 | pdf |

---

## 2. 수신자 등록 (Received) API

> **Base Path**: `/api/received`
> **인증**: Bearer JWT Token 필수 (`@UserId`)
> **Swagger Tag**: `Received API`
> **역할**: 이미 생성된 타임레터/마인드레코드에 수신자를 추가 등록

### 2-1. 엔드포인트 목록

| 상태 | Method | Endpoint | 설명 |
|:---:|:---:|---|---|
| ✅ | `POST` | `/api/received/time-letters` | 타임레터에 수신자 등록 |
| ✅ | `POST` | `/api/received/mind-records` | 마인드레코드에 수신자 등록 |

---

### 2-2. 상세 명세

#### `POST /api/received/time-letters` — 타임레터 수신자 등록

기존 타임레터에 수신자를 추가 등록합니다.

**Request Body**
```json
{
  "timeLetterID": 1,
  "receiverIds": [1, 2, 3],
  "deliveredAt": "2025-12-31T23:59:59"
}
```

| 필드 | 타입 | 필수 | 설명 |
|---|---|:---:|---|
| `timeLetterID` | Long | **O** | 대상 타임레터 ID |
| `receiverIds` | Array[Long] | **O** | 등록할 수신자 ID 목록 |
| `deliveredAt` | LocalDateTime | X | 배달 예정 시간 (미입력 시 타임레터 sendAt 사용) |

**Response** `200 OK`
```json
{
  "isSuccess": true,
  "code": 200,
  "message": "요청에 성공하였습니다.",
  "result": [10, 11, 12]
}
```

> `result`는 생성된 `TimeLetterReceiver` ID 목록입니다.

**비즈니스 로직**:
- 본인의 타임레터인지 소유권 검증
- 수신자가 본인이 등록한 수신자인지 검증
- `deliveredAt`이 null이면 타임레터의 `sendAt`을 배달 시간으로 사용

**Error**
| Code | 상황 |
|:---:|---|
| 420 | 타임레터를 찾을 수 없거나 본인의 것이 아님 |
| 468 | 수신자를 찾을 수 없음 |
| 999 | 본인이 등록한 수신자가 아님 (권한 부족) |

---

#### `POST /api/received/mind-records` — 마인드레코드 수신자 등록

기존 마인드레코드에 수신자를 추가 등록합니다.

**Request Body**
```json
{
  "mindRecordId": 1,
  "receiverIds": [1, 2, 3]
}
```

| 필드 | 타입 | 필수 | 설명 |
|---|---|:---:|---|
| `mindRecordId` | Long | **O** | 대상 마인드레코드 ID |
| `receiverIds` | Array[Long] | **O** | 등록할 수신자 ID 목록 |

**Response** `200 OK`
```json
{
  "isSuccess": true,
  "code": 200,
  "message": "요청에 성공하였습니다.",
  "result": [20, 21, 22]
}
```

> `result`는 생성된 `MindRecordReceiver` ID 목록입니다.

**Error**
| Code | 상황 |
|:---:|---|
| 440 | 마인드레코드를 찾을 수 없음 |
| 468 | 수신자를 찾을 수 없음 |
| 999 | 권한 부족 (본인의 마인드레코드/수신자가 아님) |

---

## 3. 수신자 인증 (Receiver Auth) API

> **Base Path**: `/api/receiver-auth`
> **인증**: `X-Auth-Code` 헤더 (UUID) — JWT 불필요
> **Swagger Tag**: `Receiver Auth API`
> **역할**: 비회원(유족)이 UUID 인증번호로 콘텐츠를 조회

### 3-1. 엔드포인트 목록

| 상태 | Method | Endpoint | 설명 |
|:---:|:---:|---|---|
| ✅ | `POST` | `/api/receiver-auth/verify` | 인증번호 검증 |
| ✅ | `GET` | `/api/receiver-auth/time-letters` | 수신 타임레터 목록 조회 |
| ✅ | `GET` | `/api/receiver-auth/time-letters/{timeLetterReceiverId}` | 수신 타임레터 상세 조회 |
| ✅ | `GET` | `/api/receiver-auth/after-notes` | 수신 애프터노트 목록 조회 |
| ✅ | `GET` | `/api/receiver-auth/after-notes/{afternoteId}` | 수신 애프터노트 상세 조회 |
| ✅ | `GET` | `/api/receiver-auth/mind-records` | 수신 마인드레코드 목록 조회 |
| ✅ | `GET` | `/api/receiver-auth/mind-records/{mindRecordId}` | 수신 마인드레코드 상세 조회 |
| ✅ | `GET` | `/api/receiver-auth/message` | 발신자 메시지 조회 |
| ✅ | `POST` | `/api/receiver-auth/presigned-url` | 파일 업로드용 Presigned URL 생성 |
| ✅ | `POST` | `/api/receiver-auth/delivery-verification` | 사망확인 서류 제출 |
| ✅ | `GET` | `/api/receiver-auth/delivery-verification/status` | 인증 상태 조회 |

---

### 3-2. 인증 방식

```
모든 요청에 헤더 포함 (verify 제외):
  X-Auth-Code: 550e8400-e29b-41d4-a716-446655440000

형식: UUID v4 (소문자 hex, 8-4-4-4-12)
```

---

### 3-3. 상세 명세

#### `POST /api/receiver-auth/verify` — 인증번호 검증

수신자 인증번호(UUID)를 검증하고 수신자/발신자 기본 정보를 반환합니다.

**Request Body**
```json
{
  "authCode": "550e8400-e29b-41d4-a716-446655440000"
}
```

| 필드 | 타입 | 필수 | 설명 |
|---|---|:---:|---|
| `authCode` | String (UUID) | **O** | 수신자 인증번호 |

**Response** `200 OK`
```json
{
  "isSuccess": true,
  "code": 200,
  "message": "요청에 성공하였습니다.",
  "result": {
    "receiverId": 1,
    "receiverName": "김지은",
    "senderName": "김철수",
    "relation": "딸"
  }
}
```

**비즈니스 로직**:
- UUID 형식 검증 (정규식 매칭)
- 인증번호로 수신자 조회
- 이 단계에서는 **전달 조건(Delivery Condition)을 검증하지 않음** — 본인 확인 목적만

**Error**
| Code | 상황 |
|:---:|---|
| 496 | 유효하지 않은 인증번호 (형식 오류 또는 미존재) |

---

#### `GET /api/receiver-auth/time-letters` — 수신 타임레터 목록 조회

인증번호로 전달된 타임레터 목록을 조회합니다.

**Header**: `X-Auth-Code: {UUID}`

**Response** `200 OK`
```json
{
  "isSuccess": true,
  "code": 200,
  "message": "요청에 성공하였습니다.",
  "result": {
    "timeLetters": [
      {
        "id": 1,
        "timeLetterReceiverId": 10,
        "title": "미래의 나에게",
        "content": "1년 후의 나에게...",
        "sendAt": "2025-12-31T23:59:59",
        "status": "SCHEDULED",
        "senderName": "김철수",
        "deliveredAt": "2025-12-31T23:59:59",
        "createdAt": "2025-06-01T10:00:00",
        "mediaList": [
          {
            "id": 1,
            "mediaType": "IMAGE",
            "mediaUrl": "https://s3.presigned-url..."
          }
        ],
        "isRead": false
      }
    ],
    "totalCount": 1
  }
}
```

**비즈니스 로직 — sendAt 기반 콘텐츠 보호**:
- `sendAt`이 아직 **지나지 않은** 타임레터: `title`, `content`, `senderName`, `createdAt`, `mediaList`, `isRead`가 모두 **null/빈 배열**로 반환
- `sendAt`이 **지난** 타임레터: 전체 콘텐츠 노출
- 전달 조건(Delivery Condition)이 충족되지 않으면 **609 에러**

```
sendAt 이전:  { "id": 1, "title": null, "content": null, "sendAt": "...", ... }
sendAt 이후:  { "id": 1, "title": "미래의 나에게", "content": "...", ... }
```

**Error**
| Code | 상황 |
|:---:|---|
| 496 | 유효하지 않은 인증번호 |
| 609 | 전달 조건 미충족 |

---

#### `GET /api/receiver-auth/time-letters/{timeLetterReceiverId}` — 수신 타임레터 상세 조회

수신한 특정 타임레터를 상세 조회합니다. **읽음 처리가 자동으로 수행됩니다.**

**Path Parameter**
| 이름 | 타입 | 필수 | 설명 |
|---|---|:---:|---|
| `timeLetterReceiverId` | Long | O | **수신 타임레터 ID** (타임레터 ID가 아님!) |

> **주의**: 목록 조회에서 반환되는 `timeLetterReceiverId`를 사용해야 합니다. `id`(타임레터 원본 ID)가 아닙니다.

**비즈니스 로직**:
- sendAt이 지난 경우에만 읽음 처리 (`readAt` 갱신)
- 이미 읽은 경우 중복 갱신하지 않음 (멱등성)
- sendAt 이전에는 콘텐츠 숨김 (목록 조회와 동일)

**Error**
| Code | 상황 |
|:---:|---|
| 420 | 해당 수신 타임레터를 찾을 수 없음 |
| 496 | 유효하지 않은 인증번호 |
| 609 | 전달 조건 미충족 |

---

#### `GET /api/receiver-auth/after-notes` — 수신 애프터노트 목록 조회

**Header**: `X-Auth-Code: {UUID}`

**Response** `200 OK`
```json
{
  "isSuccess": true,
  "code": 200,
  "message": "요청에 성공하였습니다.",
  "result": {
    "afternotes": [
      {
        "id": 1,
        "title": "내 아들에게",
        "category": "GALLERY",
        "leaveMessage": "사랑하는 아들에게...",
        "senderId": 5,
        "senderName": "김철수",
        "createdAt": "2025-06-01T10:00:00"
      }
    ],
    "totalCount": 1
  }
}
```

---

#### `GET /api/receiver-auth/after-notes/{afternoteId}` — 수신 애프터노트 상세 조회

카테고리에 따라 응답 구조가 달라집니다.

**SOCIAL 카테고리 응답**:
```json
{
  "result": {
    "id": 1,
    "category": "SOCIAL",
    "title": "SNS 계정 정보",
    "actions": ["인스타 삭제", "페이스북 추모 전환"],
    "leaveMessage": "...",
    "senderName": "김철수",
    "createdAt": "2025-06-01T10:00:00",
    "playlist": null
  }
}
```

**PLAYLIST 카테고리 응답**:
```json
{
  "result": {
    "id": 2,
    "category": "PLAYLIST",
    "title": "내가 좋아하던 노래들",
    "actions": null,
    "leaveMessage": null,
    "senderName": "김철수",
    "createdAt": "2025-06-01T10:00:00",
    "playlist": {
      "atmosphere": "잔잔하고 따뜻한",
      "songs": [
        {
          "title": "봄날",
          "artist": "BTS",
          "coverUrl": "https://..."
        }
      ],
      "memorialVideo": {
        "videoUrl": "https://...",
        "thumbnailUrl": "https://..."
      }
    }
  }
}
```

---

#### `GET /api/receiver-auth/mind-records` — 수신 마인드레코드 목록 조회

**Response** `200 OK`
```json
{
  "isSuccess": true,
  "code": 200,
  "message": "요청에 성공하였습니다.",
  "result": {
    "mindRecords": [
      {
        "id": 1,
        "type": "DIARY",
        "title": "오늘의 일기",
        "recordDate": "2025-06-15",
        "isDraft": false,
        "senderName": "김철수",
        "createdAt": "2025-06-15T22:30:00"
      }
    ],
    "totalCount": 1
  }
}
```

---

#### `GET /api/receiver-auth/mind-records/{mindRecordId}` — 수신 마인드레코드 상세 조회

타입에 따라 응답에 추가 필드가 포함됩니다.

| 타입 | 추가 필드 |
|---|---|
| `DIARY` | (기본 필드만) |
| `DAILY_QUESTION` | `questionId`, `questionContent` |
| `DEEP_THOUGHT` | `category` |

**Response** `200 OK` (DAILY_QUESTION 예시)
```json
{
  "result": {
    "id": 1,
    "type": "DAILY_QUESTION",
    "title": "오늘의 답변",
    "recordDate": "2025-06-15",
    "content": "나에게 가장 소중한 사람은...",
    "questionId": 42,
    "questionContent": "가장 소중한 사람은 누구인가요?",
    "category": null,
    "senderName": "김철수",
    "createdAt": "2025-06-15T22:30:00",
    "imageList": [
      {
        "id": 1,
        "imageUrl": "https://s3.presigned-url..."
      }
    ]
  }
}
```

---

#### `GET /api/receiver-auth/message` — 발신자 메시지 조회

수신자에게 발신자가 남긴 개인 메시지를 조회합니다.

**Response** `200 OK`
```json
{
  "result": {
    "senderName": "김철수",
    "message": "사랑하는 딸에게, 언제나 행복하길..."
  }
}
```

---

#### `POST /api/receiver-auth/presigned-url` — 파일 업로드용 Presigned URL

수신자가 사망확인 서류를 업로드하기 위한 S3 Presigned URL을 생성합니다.

**Request Body**
```json
{
  "extension": "pdf"
}
```

| 필드 | 타입 | 필수 | 설명 |
|---|---|:---:|---|
| `extension` | String | **O** | 파일 확장자 (점 없이) |

> **허용 확장자**: jpg, jpeg, png, gif, webp, heic, pdf

**Response** `200 OK`
```json
{
  "result": {
    "presignedUrl": "https://bucket.s3.region.amazonaws.com/documents/...?X-Amz-...",
    "fileUrl": "https://bucket.s3.region.amazonaws.com/documents/uuid.pdf",
    "contentType": "application/pdf"
  }
}
```

**사용법**:
1. 이 API로 `presignedUrl`과 `fileUrl`을 받음
2. `presignedUrl`로 PUT 요청으로 파일 직접 업로드 (10분 이내)
3. `fileUrl`을 사망확인 서류 제출 API에 사용

---

#### `POST /api/receiver-auth/delivery-verification` — 사망확인 서류 제출

전달 조건이 `DEATH_CERTIFICATE`인 경우 수신자가 인증 서류를 제출합니다.

**Request Body**
```json
{
  "deathCertificateUrl": "https://bucket.s3.region.amazonaws.com/documents/death-cert.pdf",
  "familyRelationCertificateUrl": "https://bucket.s3.region.amazonaws.com/documents/family-cert.pdf"
}
```

| 필드 | 타입 | 필수 | 설명 |
|---|---|:---:|---|
| `deathCertificateUrl` | String | **O** | 사망진단서 S3 URL |
| `familyRelationCertificateUrl` | String | **O** | 가족관계증명서 S3 URL |

**Response** `200 OK`
```json
{
  "result": {
    "id": 1,
    "status": "PENDING",
    "deathCertificateUrl": "https://...",
    "familyRelationCertificateUrl": "https://...",
    "adminNote": null,
    "createdAt": "2025-07-01T15:00:00"
  }
}
```

**비즈니스 로직**:
- 제출 후 `PENDING` 상태로 생성
- 관리자가 확인 후 `APPROVED` 또는 `REJECTED` 처리
- 이미 PENDING 상태의 요청이 있으면 **608 에러**

**인증 상태 플로우**:
```
     제출           관리자 승인          콘텐츠 열람 가능
  ──────→  PENDING  ──────────→  APPROVED  ──────────→  ✅
                    ──────────→  REJECTED  ──────────→  재제출 가능
```

**Error**
| Code | 상황 |
|:---:|---|
| 496 | 유효하지 않은 인증번호 |
| 606 | 전달 조건 타입 불일치 (DEATH_CERTIFICATE가 아님) |
| 608 | 이미 대기 중인 인증 요청 존재 |

---

#### `GET /api/receiver-auth/delivery-verification/status` — 인증 상태 조회

마지막으로 제출한 인증 요청의 상태를 조회합니다.

**Response** `200 OK`
```json
{
  "result": {
    "id": 1,
    "status": "APPROVED",
    "deathCertificateUrl": "https://...",
    "familyRelationCertificateUrl": "https://...",
    "adminNote": "확인 완료",
    "createdAt": "2025-07-01T15:00:00"
  }
}
```

| status | 의미 |
|---|---|
| `PENDING` | 관리자 검토 대기 중 |
| `APPROVED` | 승인됨 — 콘텐츠 열람 가능 |
| `REJECTED` | 거부됨 — 재제출 필요 |

**Error**
| Code | 상황 |
|:---:|---|
| 496 | 유효하지 않은 인증번호 |
| 602 | 제출한 인증 요청 없음 |

---

## 4. S3 파일 업로드 API

> **Base Path**: `/files`
> **인증**: Bearer JWT Token 필수
> **Swagger Tag**: 생략 (공통)

### `POST /files/presigned-url` — Presigned URL 생성

회원 사용자가 파일을 S3에 업로드하기 위한 Presigned URL을 생성합니다.

**Request Body**
```json
{
  "directory": "timeletters",
  "extension": "jpg"
}
```

| 필드 | 타입 | 필수 | 설명 |
|---|---|:---:|---|
| `directory` | String | **O** | 업로드 디렉토리 |
| `extension` | String | **O** | 파일 확장자 (점 없이) |

**허용 디렉토리**:

| 디렉토리 | 용도 |
|---|---|
| `profiles` | 프로필 이미지 |
| `timeletters` | 타임레터 미디어 |
| `afternotes` | 애프터노트 미디어 |
| `mindrecords` | 마인드레코드 이미지 |
| `documents` | 사망확인 서류 등 |

**Response** `200 OK`
```json
{
  "result": {
    "presignedUrl": "https://bucket.s3.region.amazonaws.com/timeletters/uuid.jpg?X-Amz-...",
    "fileUrl": "https://bucket.s3.region.amazonaws.com/timeletters/uuid.jpg",
    "contentType": "image/jpeg"
  }
}
```

**프론트엔드 업로드 플로우**:
```
1. POST /files/presigned-url  →  presignedUrl + fileUrl 수신
2. PUT {presignedUrl}          →  파일 바이너리 직접 업로드 (Content-Type 헤더 포함, 10분 이내)
3. POST /time-letters          →  fileUrl을 mediaUrl로 전달
```

**Error**
| Code | 상황 |
|:---:|---|
| 493 | Presigned URL 생성 실패 (서버 오류) |
| 494 | 허용되지 않는 파일 확장자 |
| 495 | 허용되지 않는 디렉토리 |

---

## 5. 공통 오류 코드

### 5-1. 공통 응답 형식

```json
{
  "isSuccess": false,
  "code": 420,
  "message": "타임레터를 찾을 수 없습니다."
}
```

### 5-2. 타임레터 관련 (420~429)

| Code | HTTP Status | Message |
|:---:|:---:|---|
| 420 | 404 | 타임레터를 찾을 수 없습니다. |
| 421 | 403 | 해당 타임레터에 대한 권한이 없습니다. |
| 422 | 400 | 이미 발송된 타임레터는 수정/삭제할 수 없습니다. |
| 423 | 400 | 유효하지 않은 상태 변경입니다. |
| 424 | 400 | 정식 등록 시 제목, 내용, 발송일시는 필수입니다. |
| 425 | 400 | 발송일시는 현재 시간 이후여야 합니다. |

### 5-3. 수신자 관련 (448, 468, 475, 496, 600~)

| Code | HTTP Status | Message |
|:---:|:---:|---|
| 448 | 403 | 해당 수신인에 대한 접근 권한이 없습니다. |
| 468 | 404 | 수신자를 찾을 수 없습니다. |
| 475 | 400 | 수신자(receivers)는 최소 1명 이상 필요합니다. |
| 496 | 404 | 유효하지 않은 인증번호입니다. |
| 602 | 404 | 인증 요청을 찾을 수 없습니다. |
| 604 | 400 | 이미 처리된 인증 요청입니다. |
| 606 | 400 | 설정된 전달 조건과 요청이 일치하지 않습니다. |
| 607 | 400 | 전달 조건 요청이 올바르지 않습니다. |
| 608 | 409 | 이미 대기 중인 인증 요청이 존재합니다. |
| 609 | 403 | 아직 전달 조건이 충족되지 않았습니다. |

### 5-4. S3/파일 관련 (493~495)

| Code | HTTP Status | Message |
|:---:|:---:|---|
| 493 | 500 | Presigned URL 생성에 실패했습니다. |
| 494 | 400 | 허용되지 않는 파일 확장자입니다. |
| 495 | 400 | 허용되지 않는 디렉토리입니다. |

---

## 6. 엔티티 관계도

```
┌─────────┐     1:N     ┌──────────────┐     1:N     ┌──────────────────┐
│  User   │────────────→│  TimeLetter  │────────────→│ TimeLetterMedia  │
│         │             │              │             │                  │
│ - email │             │ - title      │             │ - mediaType      │
│ - name  │             │ - content    │             │ - mediaUrl       │
│ - phone │             │ - sendAt     │             └──────────────────┘
│         │             │ - status     │
│         │  1:N        │  (DRAFT/     │     1:N     ┌────────────────────┐
│         │────────→    │   SCHEDULED/ │────────────→│TimeLetterReceiver  │
│         │ Receiver    │   SENT)      │             │                    │
│         │             └──────────────┘             │ - deliveredAt      │
│         │                                          │ - readAt           │
└─────────┘                                          └────────┬───────────┘
     │ 1:N                                                    │ N:1
     ▼                                                        ▼
┌──────────────┐                                    ┌──────────────┐
│  Receiver    │◄───────────────────────────────────│  (FK)        │
│              │                                    └──────────────┘
│ - name       │
│ - relation   │     1:N    ┌───────────────────────┐
│ - phone      │───────────→│ DeliveryVerification  │
│ - email      │            │                       │
│ - message    │            │ - deathCertificateUrl  │
│ - authCode   │            │ - familyRelationUrl    │
│  (UUID)      │            │ - status               │
│ - sortOrder  │            │  (PENDING/APPROVED/    │
└──────────────┘            │   REJECTED)            │
                            └───────────────────────┘
```

### 전달 조건 (Delivery Condition)

| 조건 타입 | 설명 | 콘텐츠 열람 조건 |
|---|---|---|
| `NONE` | 조건 없음 | 즉시 열람 가능 |
| `DEATH_CERTIFICATE` | 사망진단서 제출 | 관리자 승인 후 열람 |
| `INACTIVITY` | 일정 기간 미접속 | 시스템 자동 판단 |
| `SPECIFIC_DATE` | 특정 날짜 도래 | 해당 날짜 이후 열람 |
