# CH 3. 일정 관리 앱 만들기 Develope

# 목차
1. 프로젝트 개요
2. 기술 스택
3. 수행한 단계
4. 기능 목록
5. 프로젝트 규칙
6. 클래스 구조
7. 실행 방법(Run)
8. 3 Layer Architecture 기반 주요 클래스·역할 설명
9. CRUD API 구현
10. API 명세서
11. Postman 실행 결과
12. ERD

---

## 1. 프로젝트 개요


---

## 9. CRUD API 구현

| 구분        | 기능                | Method | URL                                      |  성공 | 실패                     |
|-----------|-------------------|--------|------------------------------------------|----:|------------------------|
| Auth      | 회원가입              | POST   | `/signup`                                | 201 | 400, 409, 500          |
| Auth      | 로그인               | POST   | `/login`                                 | 200 | 400, 403, 404, 500     |
| User      | 유저 전체 조회(공개)      | GET    | `/users`                                 | 200 | 500                    |
| User      | 유저 단건 조회(공개)      | GET    | `/users/{userId}`                        | 200 | 404, 500               |
| User      | 유저 수정(로그인+본인)     | PATCH  | `/users/{userId}`                        | 200 | 400, 401, 403, 404, 500 |
| User      | 유저 삭제(로그인+본인)     | DELETE | `/users/{userId}`                        | 204 | 401, 403, 404, 500     |
| Schedule  | 유저별 일정 생성(로그인+본인) | POST   | `/users/{userId}/schedules`              | 201 | 400, 401, 403, 404, 500 |
| Schedule  | 유저별 일정 전체 조회(공개)  | GET    | `/users/{userId}/schedules`              | 200 | 404, 500               |
| Schedule  | 일정 단건 조회(공개)      | GET    | `/users/{userId}/schedules/{scheduleId}` | 200 | 404, 500               |
| Schedule  | 일정 수정(로그인+본인)     | PUT    | `/users/{userId}/schedules/{scheduleId}` | 200 | 400, 401, 403, 404, 500 |
| Schedule  | 일정 삭제(로그인+본인)     | DELETE | `/users/{userId}/schedules/{scheduleId}` | 204 | 401, 403, 404, 500     |
| Comment  |  댓글 생성(로그인 필요)  | POST   |  `/schedules/{scheduleId}/comments`   | 201 | 400, 401, 404, 500     |
| Comment  |  댓글 전체 조회(공개)  | GET   | `/schedules/{scheduleId}/comments`   | 200 | 500    |




<br/>

<details>
<summary>공통 사항</summary>
<div markdown="1">

## 공통 사항
### Base URL
- `http://localhost:8080`

### Content-Type
- Request: `application/json`
- Response: `application/json`

### 인증 방식 (Cookie/Session)
- 로그인 성공 시 서버가 세션을 생성하고, 응답에 `Set-Cookie: JSESSIONID=...`가 포함됩니다.
- 이후 요청은 JSESSIONID 쿠키를 포함해야 로그인 상태로 인식됩니다.
- 생성/수정/삭제 API는 로그인 상태 + 본인만 가능하도록 제한되어 있습니다.

### Validation / Error Handling
- 요청 DTO 유효성 검증 실패 시 `400 Bad Request`
- 존재하지 않는 리소스 접근 시 `404 Not Found`
- 권한 없음 / 비밀번호 불일치 시 `403 Forbidden`
- 제약 위반(예: 이메일 중복) 시 `409 Conflict`
- 로그인 필요 시 `401 Unauthorized`
- 예기치 못한 서버 오류 시 `500 Internal Server Error`

`
{
  "status": 400,
  "error": "Bad Request",
  "message": "요청 값이 올바르지 않습니다.",
  "path": "/signup",
  "timestamp": "2026-01-10T12:34:56.789",
  "fieldErrors": [
    {
      "field": "email",
      "rejectedValue": "",
      "reason": "이메일(email)은 필수입니다."
    }
  ]
}
`

</div>
</details>

## 10. API 명세서
### User / Auth API 명세서
<details>
<summary>User / Auth API 명세서</summary>
<div markdown="1">

## 1) 회원가입 (Signup)
### Request
- Method: `POST`
- URL: `/signup`
- Body
    ```
    {
       "name": "홍길동",
       "email": "hong@test.com",
       "password": "12345678"
    }
    ```
### Validation
- `name`: 필수
- `email`: 필수, 이메일 형식
- `password`: 필수, 8자 이상  


### Response
#### ✅ Success
- Status: `201 Created`
- Body
    ```
    {
       "id": 1, 
       "name": "홍길동",
       "email": "hong@test.com",
       "createdAt": "2026-01-10T12:00:00", 
       "modifiedAt": "2026-01-10T12:00:00"
    }
    ```
<br/>

#### ❌ Fail
- `400 Bad Request` : 필수값 누락/형식 오류/길이 제한 위반
- `409 Conflict` : 이메일 중복
- `500 Internal Server Error` : 서버 오류

---

## 2) 로그인 (Login)
### Request
- Method: `GET`
- URL: `/login`
- Body
```
{
  "email": "hong@test.com",
  "password": "12345678"
}
```

### Validation
- `email`: 필수, 이메일 형식
- `password`: 필수, 8자 이상

### Response
#### ✅ Success
- Status: `200 OK`
- 세션 생성 + Set-Cookie: `JSESSIONID=...` 반환
- Body
    ```
    {
       "id": 1, 
       "email": "hong@test.com"
    }
    ```
  <br/>
#### ❌ Fail
- `400 Bad Request` : DTO 검증 실패
- `404 Not Found` : 이메일이 존재하지 않음
- `403 Forbidden` : 비밀번호 불일치(또는 이메일/비밀번호 불일치)
- `500 Internal Server Error` : 서버 오류

---

## 3) 유저 전체 조회
### Request
- Method: `GET`
- URL: `/users`

### Response
#### ✅ Success
- Status: `200 OK`
- Body
    ```
    [
      {
        "id": 1,
        "name": "홍길동",
        "email": "hong@test.com",
        "createdAt": "2026-01-10T12:00:00",
        "modifiedAt": "2026-01-10T12:00:00"
      }
    ]
    ```
<br/>

#### ❌ Fail
- `500 Internal Server Error` : 서버 오류
---

## 4) 유저 단건 조회
### Request
- Method: `GET`
- URL: `/users/{userId}`

### Response
#### ✅ Success
- Status: `200 OK`
- Body
    ```
    {
       "id": 1, 
       "name": "홍길동",
       "email": "hong@test.com",
       "createdAt": "2026-01-10T12:00:00",
       "modifiedAt": "2026-01-10T12:00:00"
    }
    ```

<br/>

#### ❌ Fail
- `404 Not Found` : 해당 유저 없음
- `500 Internal Server Error` : 서버 오류

---

## 5) 유저 수정 (본인만)
### Request
- Method: `PATCH`
- URL: `/users/{userId}`
- Auth: 세션 로그인 필요
- Body
    ```
    {
       "name": "수정된이름",
       "email": "new@test.com"
    }
    ```
<br/>

### Validation
- `name`: 필수
- `email`: 필수, 이메일 형식

<br/>

### Response
#### ✅ Success
- Status: `200 OK`
- Body
    ```
    {
       "id": 1, 
       "name": "수정된이름",
       "email": "new@test.com",
       "createdAt": "2026-01-10T12:00:00",
       "modifiedAt": "2026-01-10T12:00:00"
    }
    ```

<br/>

#### ❌ Fail
- `400 Bad Request` : DTO 검증 실패
- `401 Unauthorized` : 로그인 필요
- `403 Forbidden` : 본인만 수정 가능
- `404 Not Found` : 대상 유저 없음
- `500 Internal Server Error` : 서버 오류


---

## 6) 유저 삭제 (본인만)
### Request
- Method: `DELETE`
- URL: `/users/{userId}`
- Auth: 세션 로그인 필요

<br/>

### Response
#### ✅ Success
- Status: `204 No Content`

<br/>

#### ❌ Fail
- `401 Unauthorized` : 로그인 필요
- `403 Forbidden` : 본인만 삭제 가능
- `404 Not Found` : 대상 유저 없음
- `500 Internal Server Error` : 서버 오류

</div>
</details>

---

### 일정(Schedule) API 명세서
<details>
<summary>일정(Schedule) API 명세서</summary>
<div markdown="1">

## 1) 유저별 일정 생성 (본인만)
### Request
- Method: `POST`
- URL: `/users/{userId}/schedules`
- Auth: 세션 로그인 필요
- Body
    ```
    {
       "title": "오늘할일",
       "content": "운동하기"
    }
    ```
### Validation
- `title`: 필수, 최대 10자
- `content`: 선택  

### Response
#### ✅ Success
- Status: `201 Created`
- Body
    ```
    {
       "id": 1,
       "title": "오늘할일",
       "content": "운동하기",
       "createdAt": "2026-01-10T12:00:00",
       "modifiedAt": "2026-01-10T12:00:00"
    }
    ```

#### ❌ Fail
- `400 Bad Request` : DTO 검증 실패
- `401 Unauthorized` : 로그인 필요
- `403 Forbidden` : 본인 일정만 생성 가능
- `404 Not Found` : 대상 유저 없음
- `500 Internal Server Error` : 서버 오류

---

## 2) 유저별 일정 전체 조회
### Request
- Method: `GET`
- URL: `/users/{userId}/schedules`

### Response
#### ✅ Success
- Status: `200 OK`
- Body
    ```
    [
      {
        "id": 1,
        "title": "오늘할일",
        "content": "운동하기",
        "createdAt": "2026-01-10T12:00:00",
        "modifiedAt": "2026-01-10T12:00:00"
      }
    ]
    ```

#### ❌ Fail
- `404 Not Found` : 대상 유저 없음
- `500 Internal Server Error` : 서버 오류

---

## 3) 일정 단건 조회
### Request
- Method: `GET`
- URL: `/users/{userId}/schedules/{scheduleId}`

### Response
#### ✅ Success
- Status: `200 OK`
- Body
    ```
    {
       "id": 1,
       "title": "오늘할일",
       "content": "운동하기",
       "createdAt": "2026-01-10T12:00:00",
       "modifiedAt": "2026-01-10T12:00:00"
    }
    ```

#### ❌ Fail
- `404 Not Found` : 일정 없음
- `500 Internal Server Error` : 서버 오류

---

## 4) 일정 수정 (본인만)
### Request
- Method: `PUT`
- URL: `/users/{userId}/schedules/{scheduleId}`
- Auth: 세션 로그인 필요
- Body
    ```
    {
       "title": "수정제목",
       "content": "수정내용"
    }
    ```
### Validation
- `title`: 필수, 최대 10자
- `content`: 선택

### Response
#### ✅ Success
- Status: `200 OK`
- Body
    ```
    {
       "id": 1,
       "title": "수정제목",
       "content": "수정내용",
       "createdAt": "2026-01-10T12:00:00",
       "modifiedAt": "2026-01-10T12:00:00"
    }
    ```

#### ❌ Fail
- `400 Bad Request` : DTO 검증 실패
- `401 Unauthorized` : 로그인 필요
- `403 Forbidden` : 본인 일정만 수정 가능
- `404 Not Found` : 일정 없음
- `500 Internal Server Error` : 서버 오류

---

## 5) 일정 삭제 (본인만)
### Request
- Method: `DELETE`
- URL: `/users/{userId}/schedules/{scheduleId}`
- Auth: 세션 로그인 필요

### Response
#### ✅ Success
- Status: `204 No Content`

#### ❌ Fail
- `401 Unauthorized` : 로그인 필요
- `403 Forbidden` : 본인 일정만 삭제 가능
- `404 Not Found` : 일정 없음
- `500 Internal Server Error` : 서버 오류

</div>
</details>

---

### 댓글(Comment) API 명세서
<details>
<summary>댓글(Comment) API 명세서</summary>
<div markdown="1">

## 1) 댓글 생성 (로그인 필요)
### Request
- Method: `POST`
- URL: `/schedules/{scheduleId}/comments`
- Auth: 세션 로그인 필요
- Body
    ```
    {
       "content": "댓글 내용입니다."
    }
    ```
### Validation
- `content`: 필수

### Response
#### ✅ Success
- Status: `201 Created`
- Body
    ```
    {
       "id": 1,
       "content": "댓글 내용입니다.",
       "createdAt": "2026-01-10T12:00:00",
       "modifiedAt": "2026-01-10T12:00:00"
    }
    ```

#### ❌ Fail
- `400 Bad Request` : DTO 검증 실패
- `401 Unauthorized` : 로그인 필요
- `404 Not Found` : scheduleId에 해당하는 일정이 없음 / 로그인 유저가 존재하지 않음
- `500 Internal Server Error` : 서버 오류

---

## 2) 댓글 전체 조회 (공개)
### Request
- Method: `GET`
- URL: `/schedules/{scheduleId}/comments`

### Response
#### ✅ Success
- Status: `200 OK`
- Body
    ```
    [
      {
        "id": 3,
        "content": "가장 최근 댓글",
        "createdAt": "2026-01-10T12:00:00",
        "modifiedAt": "2026-01-10T12:00:00"
      },
      {
        "id": 1,
        "content": "이전 댓글",
        "createdAt": "2026-01-10T12:00:00",
        "modifiedAt": "2026-01-10T12:00:00"
      }
    ]
    ```
- 정렬: createdAt 기준 내림차순
(CommentRepository.findByScheduleIdOrderByCreatedAtDesc)

#### ❌ Fail
- `500 Internal Server Error` : 서버 오류

<br/>

</div>
</details>


---

## 11. Postman 실행 결과
<details>
<summary>Postman 실행 결과</summary>
<div markdown="1">


</div>
</details>

---

## 12. ERD

### 1) users (유저)

| 컬럼명         | 타입         | 제약 | 설명                       |
|-------------|------------|---|--------------------------|
| id          | LONG       | PK, AUTO_INCREMENT | 유저 고유 ID                 |
| name        | VARCHAR(4) | NOT NULL | 유저 이름 (최대 4자)            |
| email       | VARCHAR    | NOT NULL | 유저 이메일 (이메일 형식 검증)       |
| password    | VARCHAR    | NOT NULL | 비밀번호 (최소 8자, 응답 제외, 검증용) |
| created_at  | TIMESTAMP  | (Auditing) | 생성 시각 (수정 불가)            |
| modified_at | TIMESTAMP  | (Auditing) | 수정 시각                    |


---

### 2) schedules (일정)  ※ FK 사용

| 컬럼명         | 타입          | 제약                 | 설명              |
|-------------|-------------|--------------------|-----------------|
| id          | LONG        | PK, AUTO_INCREMENT | 일정 고유 ID        |
| user_id     | LONG        | FK, NOT NULL       | 유저 고유 ID        |
| title       | VARCHAR(10) | NOT NULL        | 일정 제목 (최대 10자)  |
| content     | TEXT        |                    | 일정 내용 (선택)      |
| created_at  | TIMESTAMP   | (Auditing)      | 생성 시각 (수정 불가)   |
| modified_at | TIMESTAMP   | (Auditing)      | 수정 시각           |

---

### 3) comments (댓글)  ※ FK 사용

| 컬럼명        | 타입 | 제약                 | 설명 |
|------------|---|--------------------|---|
| id         | LONG | PK, AUTO_INCREMENT | 댓글 고유 ID |
| user_id    | LONG        | FK, NOT NULL       | 유저 고유 ID      |
| schedule_id | LONG | FK, NOT NULL       | 일정 고유 ID |
| content    | TEXT | NOT NULL           | 댓글 내용 |
| created_at | TIMESTAMP  | (Auditing)         | 생성 시각 (수정 불가)  |
| modified_at | TIMESTAMP  | (Auditing)         | 수정 시각 |

---

### ERD 다이어그램
![img.png](images/erd/CH3%20숙련%20Spring%20일정관리앱%20(1).png)

---