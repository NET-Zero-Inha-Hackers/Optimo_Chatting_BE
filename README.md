# Optimo_Chatting_BE

Optimo 프로젝트의 Chatting 조회/삭 서비스 Backend Server용 레포지토리입니다.

---

# API 명세서
- 이 문서는 채팅 서비스의 REST API 엔드포인트를 설명합니다. 
- 모든 요청은 인증이 필요하며 Authorization 헤더에 Bearer 토큰을 포함해야 합니다.

## 1. 채팅 목록 조회

**URL:**  
`GET /api/chattings`

### 요청

| 헤더             | 설명            | 예시                     |
|------------------|----------------|--------------------------|
| Authorization    | 액세스 토큰    | `Bearer eyJhbGciOiJIUzI1Ni...` |

### 응답

| 상태 코드         | 설명          | 예시/메시지                            |
|------------------|---------------|-----------------------------------|
| 200 OK           | 성공          | 아래 응답예시 참조(전체 스키마에서 chatList만 빠짐) |
| 401 Unauthorized | 인증 실패     | `Authentication failed: 토큰 만료`    |
| 400 Bad Request  | 헤더/토큰 오류 | `Invalid request: 헤더 형식이 잘못되었습니다` |

#### 응답 예시
```
[
    {
        chattingId: "string",
        ownerId: "string",
        title: "string",
        description: "string",
        createdAt: timestamp,
        modifiedAt: timestamp
    },
    {
        chattingId: "string",
        ownerId: "string",
        title: "string",
        description: "string",
        createdAt: timestamp,
        modifiedAt: timestamp
    }
]
```


---

## 2. 채팅 상세 조회

**URL:**  
`GET /api/chattings/{id}`

### 요청

| 헤더             | 설명            | 예시                     |
|------------------|----------------|--------------------------|
| Authorization    | 액세스 토큰    | `Bearer eyJhbGciOiJIUzI1Ni...` |
| id (경로 변수)   | 채팅 고유 ID   | `chatting_1`             |

### 응답

| 상태 코드         | 설명          | 예시/메시지      |
|------------------|---------------|-------------|
| 200 OK           | 성공          | 아래 응답 예시 참조 |
| 401 Unauthorized | 인증 실패     | `Authentication failed: 유효하지 않은 토큰` |
| 403 Forbidden    | 권한 없음     | `Forbidden: 해당 채팅에 접근 권한이 없습니다` |
| 404 Not Found    | 채팅 없음     | `Chatting not found: chatting_999` |
| 400 Bad Request  | 헤더/토큰 오류 | `Invalid authorization header: 토큰이 누락되었습니다` |

#### 응답 예시
```
{
	chattingId: "string",
	ownerId: "string",
	title: "string",
	description: "string",
	chatList: [
		{
			sender: "string", // USER, AI
			text: "string",
			timestamp: number,
			model: "string" // 빈 문자열 가능
		},
		{
			sender: "string", // USER, AI
			text: "string",
			timestamp: number,
			model: "string" // 빈 문자열 가능
		}
	],
	createdAt: timestamp,
	modifiedAt: timestamp
}
```


---

## 3. 채팅 삭제

**URL:**  
`DELETE /api/chattings/{id}`

### 요청

| 헤더             | 설명            | 예시                     |
|------------------|----------------|--------------------------|
| Authorization    | 액세스 토큰    | `Bearer eyJhbGciOiJIUzI1Ni...` |
| id (경로 변수)   | 채팅 고유 ID   | `chatting_1`             |

### 응답

| 상태 코드         | 설명          | 예시/메시지 |
|------------------|---------------|-------------|
| 204 No Content   | 성공          | (빈 응답)   |
| 401 Unauthorized | 인증 실패     | `인증 실패: 토큰이 만료되었습니다` |
| 403 Forbidden    | 권한 없음     | `삭제 권한이 없습니다: 다른 사용자의 채팅` |
| 400 Bad Request  | 채팅 없음     | `채팅이 존재하지 않습니다: chatting_999` |
| 500 Internal Server Error | 서버 오류 | `서버 내부 오류: 데이터베이스 연결 실패` |

---

## 4. 공통 에러 케이스

| 상태 코드 | 유형         | 예시 메시지                       |
|-----------|--------------|-----------------------------------|
| 401       | 인증 실패    | `Authentication failed: 토큰 서명 불일치` |
| 403       | 권한 없음    | `삭제 권한이 없습니다: 소유자만 삭제 가능` |
| 400       | 요청 오류    | `Invalid request: 헤더 형식 오류` |
| 404       | 리소스 없음  | `Chatting not found: chatting_999` |
| 500       | 서버 오류    | `서버 내부 오류: 예기치 않은 예외 발생` |

---

## 5. 요청/응답 규칙

1. **헤더 필수:**  
   모든 요청에 `Authorization` 헤더 포함
2. **응답 형식:**
    - **성공:** `application/json` (200/204)
    - **에러:** `text/plain` (400/401/403/404/500)
3. **예외 처리:**  
   서비스 계층에서 발생한 예외를 HTTP 상태 코드로 명확히 매핑

> **참고:**
> - **인증 vs 권한:**  
    >   `401`은 토큰 유효성 문제, `403`은 권한 부족 문제를 구분합니다.
> - **에러 메시지:**  
    >   한국어로 반환되며, 특정 에러만 영어 메시지 사용 (예: `Authentication failed`).