# CH 3. 일정 관리 앱 만들기 Develop

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
13. 3 Layer Architecture & Annotation 정리
14. 트러블슈팅 TIL

---

## 1. 프로젝트 개요
Spring Boot 기반의 **일정 관리 API**를 구현한 프로젝트입니다.

- **User(유저) / Auth(인증)**: 회원가입, 로그인(세션 기반), 유저 CRUD(수정/삭제는 로그인+본인만)
- **Schedule(일정)**: 유저별 일정 CRUD(생성/수정/삭제는 로그인+본인만), 조회는 공개
- **Comment(댓글)**: 일정에 댓글 작성/조회
    - 댓글 작성: **로그인 필요** (본인 일정이 아니어도 작성 가능)
    - 댓글 조회: **공개**
- **Validation + 예외 처리**: 요청 DTO 검증 및 커스텀 예외를 `@RestControllerAdvice`로 공통 처리
- **비밀번호 암호화**: BCrypt 기반 `PasswordEncoder`를 직접 구현하여 회원가입/로그인에 적용
- **페이지네이션**: Spring Data JPA의 `Pageable`, `Page`를 활용한 일정 페이징 조회 기능 구현

---

## 2. 기술 스택
- **Language**: Java 17
- **Framework**: Spring Boot (Spring MVC)
- **Data**: Spring Data JPA (Hibernate)
- **Validation**: Jakarta Validation (`@Valid`, `@NotBlank`, `@Size`, `@Email`)
- **DB**: MySQL
- **Build Tool**: Gradle
- **Boilerplate**: Lombok
- **Password Hashing**: `at.favre.lib:bcrypt` (BCrypt)
- **Auth**: Cookie/Session (`HttpSession`, `@SessionAttribute`)
- **Error Handling**: `@RestControllerAdvice` + 커스텀 예외 + 공통 에러 응답(`ErrorResponse`)
- **Test/Client**: Postman


---

## 3. 수행한 단계

`필수 기능`
- [Lv 0] : API 명세 및 ERD 작성
- [Lv 1] : 일정 CRUD
- [Lv 2] : 유저 CRUD
- [Lv 3] : 회원가입
- [Lv 4] : 로그인(인증)

`도전 기능`
- [Lv 5] : 다양한 예외처리
- [Lv 6] : 비밀번호 암호화
- [Lv 7] : 댓글 CRUD
- [Lv 8] : 일정 페이징 조회

---

## 4. 기능 목록

### Auth / User
- 회원가입 (이메일 중복 체크, 비밀번호 암호화 저장)
- 로그인 (세션 생성, 쿠키 기반 인증)
- 유저 전체 조회 (공개)
- 유저 단건 조회 (공개)
- 유저 수정 (로그인 + 본인만)
- 유저 삭제 (로그인 + 본인만)

### Schedule
- 유저별 일정 생성 (로그인 + 본인만)
- 유저별 일정 전체 조회 (공개)
- 일정 단건 조회 (공개)
- 일정 수정 (로그인 + 본인만)
- 일정 삭제 (로그인 + 본인만)
- 일정 페이징 조회 (Pageable/Page, 수정일 기준 내림차순)

### Comment
- 댓글 생성 (로그인 필요, **본인 일정이 아니어도 가능**)
- 댓글 전체 조회 (공개, 생성일 기준 내림차순)

---

## 5. 프로젝트 규칙

- **세션 인증**
    - 로그인 성공 시 세션에 로그인 사용자 정보를 저장하고, 이후 요청은 쿠키(`JSESSIONID`)로 인증합니다.
- **권한 정책**
    - 유저 수정/삭제: 로그인 + 본인만
    - 일정 생성/수정/삭제: 로그인 + 본인만
    - 댓글 생성: 로그인 필요(일정 소유자 여부와 무관)
    - 조회 API: 기본적으로 공개(필요 시 로그인만으로 변경 가능)
- **Validation**
    - DTO 필수값/형식/길이 제한은 Jakarta Validation으로 처리합니다.
- **예외 처리**
    - 커스텀 예외(401/403/404/409) + Validation(400) + 예기치 못한 오류(500)를 전역 예외 처리로 응답합니다.
- **비밀번호**
    - 회원가입 시 BCrypt로 해시하여 저장하고, 로그인 시 `matches()`로 검증합니다.
- **Auditing**
    - `createdAt`은 생성 시각, `modifiedAt`은 수정 시각으로 관리합니다.

---

## 6. 📂 클래스 구조

<details>
<summary>열기</summary>
<div markdown="1">

```
ScheduleManagementSystem
 ├─ README.md 
 ├─ images
 │    ├─ erd  
 │    └─ postman 
 └─ src
  └─ main
      ├─ resources
      │   └─ application.properties
      └─ java
          └─ com.example.schedulemanagementsystem
              │
              ├─ ScheduleManagementSystemApplication
              │    
              ├─ common
              │    ├─ error 
              │    │   ├─ ErrorResponse
              │    │   └─ GlobalExceptionHandler
              │    └─ exception 
              │        ├─ ConflictException
              │        ├─ ForbiddenException 
              │        ├─ NotFoundException
              │        └─ UnauthorizedException
              │ 
              ├─ config
              │    └─ PasswordEncoder
              │
              ├─ user
              │    ├─ controller 
              │    │   └─ UserController
              │    ├─ dto
              │    │   ├─ GetUserResponse
              │    │   ├─ LoginRequest
              │    │   ├─ LoginResponse
              │    │   ├─ SessionUser
              │    │   ├─ SignupRequest 
              │    │   ├─ SignupResponse
              │    │   ├─ UpdateUserRequest
              │    │   └─ UpdateUserResponse
              │    ├─ entity
              │    │   ├─ BaseEntity
              │    │   └─ User
              │    ├─ controller 
              │    │   └─ UserRepository
              │    └─ service 
              │        └─ UserService
              │
              ├─ schedule
              │    ├─ controller 
              │    │   └─ ScheduleController
              │    ├─ dto
              │    │   ├─ CreateScheduleRequest
              │    │   ├─ CreateScheduleResponse
              │    │   ├─ GetScheduleResponse
              │    │   ├─ UpdateScheduleRequset
              │    │   ├─ UpdateScheduleResponse 
              │    │   └─ SchedulePageResponse
              │    ├─ entity
              │    │   ├─ BaseEntity
              │    │   └─ Schedule
              │    ├─ controller 
              │    │   └─ ScheduleRepository
              │    └─ service 
              │        └─ ScheduleService
              │
              └─ comment
                   ├─ controller 
                   │   └─ CommentController
                   ├─ dto
                   │   ├─ CreateCommentRequest
                   │   ├─ CreateCommentResponse
                   │   └─ GetCommentResponse
                   ├─ entity
                   │   ├─ BaseEntity
                   │   └─ Comment
                   ├─ controller 
                   │   └─ CommentRepository
                   └─ service 
                       └─ CommentService
```

</div>
</details>

---

## 7. 실행 방법(Run)

### 7.1 사전 준비
- MySQL 실행
- DB 생성 : `sql CREATE DATABASE ScheduleManagement;`

### 7.2 application.properties 설정
`src/main/resources/application.properties`에 DB 접속 정보 설정
<details>
<summary>코드 붙여넣기</summary>
<div markdown="1">


    spring.datasource.url=jdbc:mysql://localhost:3307/ScheduleManagement
    spring.datasource.username=root
    spring.datasource.password=12345678
    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
    spring.jpa.hibernate.ddl-auto=create
    spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
    spring.jpa.show-sql=true
    spring.jpa.properties.hibernate.format_sql=true

- 포트번호를 3307로 설정한 이유
    - 개발 당시, 로컬에서 3306은 먼저 사용되고 있다고 충돌이 뜨기 때문에 변경해 사용했음.

</div>
</details>

> 참고
> - `ddl-auto=create`는 실행할 때마다 테이블이 다시 생성됩니다.
> - 데이터를 유지하려면 `update`로 변경해서 사용합니다.

### 7.3 build.gradle 설정
`implementation 'at.favre.lib:bcrypt:0.10.2'` 의존성 추가

---

## 8. 3 Layer Architecture 기반 주요 클래스·역할 설명

### Controller Layer (요청/응답, 라우팅)
- `UserController`
    - 회원가입(`/signup`), 로그인(`/login`), 유저 조회/수정/삭제 API 제공
    - 로그인 필요한 API는 `@SessionAttribute`로 세션 유저를 받아 검증

- `ScheduleController`
    - 일정 CRUD 및 일정 페이징 조회 API 제공
    - 생성/수정/삭제는 로그인 + 본인만 가능하도록 제어

- `CommentController`
    - 댓글 생성(로그인 필요), 댓글 목록 조회(공개) API 제공
    - 댓글 작성자는 URL 파라미터가 아니라 **세션 로그인 유저**로 결정

- `GlobalExceptionHandler` (`@RestControllerAdvice`)
    - Validation 실패(`400`) 및 커스텀 예외(`401/403/404/409`)를 공통 포맷으로 변환하여 응답

### Service Layer (비즈니스 로직)
- `UserService`
    - 회원가입 시 이메일 중복 체크 및 비밀번호 해시 저장
    - 로그인 시 비밀번호 `matches()` 검증
    - 유저 수정/삭제 시 “본인 여부” 검증

- `ScheduleService`
    - 일정 CRUD 비즈니스 로직 처리 및 “본인 일정 여부” 검증
    - `Pageable`, `Page` 기반 일정 페이징 조회 제공

- `CommentService`
    - 댓글 생성 시 로그인 유저/일정 존재 여부 검증 후 저장
    - 일정 기준 댓글 목록 조회(내림차순 정렬)

### Repository Layer (DB 접근)
- `UserRepository`, `ScheduleRepository`, `CommentRepository`
    - Spring Data JPA 기반 CRUD 및 조건 조회 메서드 제공
    - 페이징 조회는 `Pageable`을 파라미터로 받아 `Page<T>` 형태로 반환


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

### 로그인
<details>
<summary>로그인</summary>
<div markdown="1">

### Success
`200 OK`
![login_200.png](images/postman/login_200.png)

### Fail
`400 Bad Request`
![login_400.png](images/postman/login_400.png)

`403 Forbidden`
![login_403.png](images/postman/login_403.png)

`404 Not Found`
![login_404.png](images/postman/login_404.png)

`500 Internal Server Error`
![login_500.png](images/postman/login_500.png)

</div>
</details>

---

### 유저 API
<details>
<summary>유저 API</summary>
<div markdown="1">

### 회원가입, 유저 생성(POST)
<details>
<summary>회원가입, 유저 생성(POST)</summary>
<div markdown="1">

### Success
`201 Created`
![img.png](images/postman/user_signup_201.png)
![login_session.png](images/postman/login_session.png)

### Fail
`400 Bad Request`
![user_signup_400.png](images/postman/user_signup_400.png)

`409 Conflict`
![user_signup_409.png](images/postman/user_signup_409.png)

`500 Internal Server Error`
![user_signup_500.png](images/postman/user_signup_500.png)

</div>
</details>

### 유저 전체 조회(GET)
<details>
<summary>유저 전체 조회(GET)</summary>
<div markdown="1">

### Success
`200 OK`
![user_getAll_200.png](images/postman/user_getAll_200.png)

### Fail
`500 Internal Server Error`
![user_getAll_500.png](images/postman/user_getAll_500.png)

</div>
</details>

### 유저 단건 조회(GET)
<details>
<summary>유저 단건 조회(GET)</summary>
<div markdown="1">

### Success
`200 OK`
![login_200.png](images/postman/login_200.png)

### Fail
`404 Not Found`
![user_getOne_404.png](images/postman/user_getOne_404.png)

`500 Internal Server Error`
![user_getOne_500.png](images/postman/user_getOne_500.png)

</div>
</details>

### 유저 수정(PATCH)
<details>
<summary>유저 수정(PATCH)</summary>
<div markdown="1">

### Success
`200 OK`
![user_update_200.png](images/postman/user_update_200.png)

### Fail
`400 Bad Request`
![user_update_400.png](images/postman/user_update_400.png)

`401 Unauthorized`
![user_update_401.png](images/postman/user_update_401.png)

`403 Forbidden`
![user_update_403.png](images/postman/user_update_403.png)

`404 Not Found`
⚠️ 수정 필요
- 유저 수정 시, userId가 존재하지 않음에도 본인 외 userId라면 무조건 403 "본인만 수정할 수 있습니다." 출력

`500 Internal Server Error`
![user_update_500.png](images/postman/user_update_500.png)\

</div>
</details>

### 유저 삭제(DELETE)
<details>
<summary>유저 삭제(DELETE)</summary>
<div markdown="1">

### Success
`204 No Content`
![user_del_204.png](images/postman/user_del_204.png)

### Fail
`401 Unauthorized`
![user_del_401.png](images/postman/user_del_401.png)

`403 Forbidden`
![user_del_403.png](images/postman/user_del_403.png)

`404 Not Found`
⚠️ 수정 필요
- 유저 삭제 시, userId가 존재하지 않음에도 본인 외 userId라면 무조건 403 "본인만 삭제할 수 있습니다." 출력

`500 Internal Server Error`
![user_del_500.png](images/postman/user_del_500.png)

</div>
</details>


</div>
</details>

---

### 일정 API
<details>
<summary>일정 API</summary>
<div markdown="1">


### 일정 생성(POST)
<details>
<summary>일정 생성(POST)</summary>
<div markdown="1">

### Success
`201 Created`
![schedule_create_201.png](images/postman/schedule_create_201.png)

### Fail
`400 Bad Request`
![schedule_create_400.png](images/postman/schedule_create_400.png)

`401 Unauthorized`
![schedule_create_401.png](images/postman/schedule_create_401.png)

`403 Forbidden`
![schedule_create_403.png](images/postman/schedule_create_403.png)

`404 Not Found`
![schedule_create_404.png](images/postman/schedule_create_404.png)

`500 Internal Server Error`
![schedule_create_500.png](images/postman/schedule_create_500.png)

</div>
</details>

### 일정 전체 조회(GET)
<details>
<summary>일정 전체 조회(GET)</summary>
<div markdown="1">

### Success
`200 OK`

### Fail
`404 Not Found`

`500 Internal Server Error`

</div>
</details>

### 일정 단건 조회(GET)
<details>
<summary>일정 단건 조회(GET)</summary>
<div markdown="1">

### Success
`200 OK`
![schedule_getAll_200.png](images/postman/schedule_getAll_200.png)

### Fail
`404 Not Found`
![schedule_getAll_404.png](images/postman/schedule_getAll_404.png)

`500 Internal Server Error`
![schedule_getAll_500.png](images/postman/schedule_getAll_500.png)

</div>
</details>

### 일정 수정(PUT)
<details>
<summary>일정 수정(PUT)</summary>
<div markdown="1">

### Success
`200 OK`
![schedule_update_200.png](images/postman/schedule_update_200.png)

### Fail
`400 Bad Request`
![schedule_update_400.png](images/postman/schedule_update_400.png)

`401 Unauthorized`
![schedule_create_401.png](images/postman/schedule_create_401.png)

`403 Forbidden`
![schedule_update_403.png](images/postman/schedule_update_403.png)

`404 Not Found`
![schedule_update_404.png](images/postman/schedule_update_404.png)

`500 Internal Server Error`
![schedule_update_500.png](images/postman/schedule_update_500.png)

</div>
</details>

### 일정 삭제(DELETE)
<details>
<summary>일정 삭제(DELETE)</summary>
<div markdown="1">

### Success
`204 No Content`
![schedule_del-204.png](images/postman/schedule_del_204.png)

### Fail

`401 Unauthorized`
![schedule_del_401.png](images/postman/schedule_del_401.png)

`403 Forbidden`
![schedule_del_403.png](images/postman/schedule_del_403.png)

`404 Not Found`
![schedule_del_404.png](images/postman/schedule_del_404.png)

`500 Internal Server Error`
![schedule_del_500.png](images/postman/schedule_del_500.png)

</div>
</details>


</div>
</details>

---

### 댓글 API
<details>
<summary>댓글 API</summary>
<div markdown="1">

### 댓글 생성(POST)
<details>
<summary>댓글 생성(POST)</summary>
<div markdown="1">

### Success
`201 Created`
![comment_create_201.png](images/postman/comment_create_201.png)

### Fail
`400 Bad Request`
![comment_create_400.png](images/postman/comment_create_400.png)

`401 Unauthorized`
![comment_create_401.png](images/postman/comment_create_401.png)

`404 Not Found`
![comment_create_404.png](images/postman/comment_create_404.png)

`500 Internal Server Error`
![comment_create_500.png](images/postman/comment_create_500.png)

</div>
</details>

### 댓글 전체 조회(GET)
<details>
<summary>댓글 전체 조회(GET)</summary>
<div markdown="1">

</div>
</details>

### Success
`200 OK`
![comment_getAll_200.png](images/postman/comment_getAll_200.png)

### Fail
`500 Internal Server Error`
![comment_getAll_500.png](images/postman/comment_getAll_500.png)

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

## 13. 3 Layer Architecture & Annotation 정리
https://velog.io/@dlql6717/TIL-3-Layer-Architecture-Spring-요청-어노테이션-정리


---

## 14. 트러블슈팅 TIL
https://velog.io/@dlql6717/TIL-CH-CH3-일정-관리-앱-만들기-트러블슈팅


