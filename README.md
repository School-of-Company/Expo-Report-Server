# Expo Report Server

박람회 보고서 서버 초기 프로젝트입니다.

## 개발 환경

- JDK 21, Kotlin 2.3.21, Spring Boot 4.1.1, Gradle 9.7.1
- PostgreSQL 17.6, Docker Compose v2
- Springdoc 3.1.1, Apache POI 5.5.1, Kotest 5.9.1

원본 Spring Boot 4 및 인접 Expo 서비스와 호환되도록 Springdoc은 3.1.1,
ktlint 플러그인은 14.2.0, Spotless는 8.10.2를 사용합니다.
[Springdoc 공식 호환표](https://springdoc.org/#what-is-the-compatibility-matrix-of-springdoc-openapi-with-spring-boot)
기준으로 Boot 4는 Springdoc 3 계열을 사용합니다.
ktlint/Spotless 블록은 Expo Application 서비스와 동일합니다.
JPA 플러그인(noArg 포함), allOpen, kapt을 적용했습니다. 별도 annotation processor는 아직 없습니다.

## 로컬 실행

```bash
cp .env.example .env
# .env의 로컬 계정과 포트를 필요에 맞게 수정
chmod 600 .env
docker compose up -d --wait
./gradlew bootRun
```

기본 서버 주소는 `http://localhost:8083`, DB 포트는 `35432`입니다.
로컬 서버와 DB는 루프백 주소에만 바인딩됩니다.
Swagger는 `/swagger-ui/index.html`, OpenAPI JSON은 `/v3/api-docs`입니다.
Spring Security가 적용되어 있으므로 `.env`의 `LOCAL_SECURITY_USER`와
`LOCAL_SECURITY_PASSWORD`로 로그인하세요. 예제 계정은 로컬 전용입니다.
설정은 `application.yaml` 하나로 관리하고 환경별 값은 환경 변수로 지정합니다.
Eureka 등록은 기본적으로 비활성화합니다.
Flyway를 사용하며 스키마 변경은 `src/main/resources/db/migration`에 추가합니다.
아직 엔티티와 마이그레이션은 없습니다. JPA는 `ddl-auto: validate`로 실행합니다.
운영 환경은 별도 DB·보안·서비스 디스커버리 설정이 필요합니다.

```bash
# CI와 같은 검증 (PostgreSQL이 실행 중이어야 함)
./gradlew build ktlintCheck spotlessCheck --no-daemon
# 포맷 수정
./gradlew spotlessApply
# DB 중지 (데이터 볼륨 유지)
docker compose down
```

## GitHub

`.github/workflows/ci.yml`은 main/develop push, 모든 PR, 수동 실행에서
PostgreSQL 기동 후 빌드·테스트·포맷을 검사하고 테스트 보고서를 저장합니다.
별도 GitHub Secrets는 필요하지 않습니다.
이슈/PR 템플릿은 [Gwangju-talent-festival-Server-V2](https://github.com/School-of-Company/Gwangju-talent-festival-Server-V2/tree/main/.github)에서 가져왔습니다.
이슈 템플릿의 자동 라벨인 `버그`, `할일`을 GitHub 저장소에 등록했습니다.
