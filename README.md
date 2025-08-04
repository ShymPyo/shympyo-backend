# account-service

사용자 인증 및 계정 관리 서비스입니다.  
JWT 기반 인증, 회원가입, 로그인, 토큰 재발급, 로그아웃 기능을 제공합니다.

## ✅ 주요 기능

- 회원가입 (Sign up)
- 로그인 (Login) + Access/Refresh Token 발급
- Access Token 재발급 (Reissue)
- 로그아웃 (Logout)
- 사용자 정보 조회 (`/api/users/me`)

## 🛠️ 기술 스택

- Java 17
- Spring Boot 3.5.4
- Spring Security
- JWT (jjwt)
- JPA (Hibernate)
- H2 (개발용)
- Gradle

## ⚙️ 실행 방법

```bash
# 빌드
./gradlew build

# 실행
java -jar build/libs/account-service-0.0.1-SNAPSHOT.jar
