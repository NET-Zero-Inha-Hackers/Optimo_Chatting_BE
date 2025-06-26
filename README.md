# Optimo_Chatting_BE

Optimo 프로젝트의 Chatting 조회/삭 서비스 Backend Server용 레포지토리입니다.

---
# BE Server 테스트 및 실행

## 환경

- JDK 17, SpringBoot 3.5, Java + Gradle

## Connected Infra

- MongoDB
    - 환경 변수
        - `MONGODB_URI` : MongoDB 연결 URI

## 실행 방법
```
# 이전 빌드 제거 및 빌드 명령어
./gradlew clean bootJar

# 빌드된 jar 파일 실행 명령어
java -jar ./build/libs/Optimo_User_BE-0.0.1-SNAPSHOT.jar

```
---