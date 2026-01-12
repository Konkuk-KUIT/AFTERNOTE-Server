# ========== [1단계: 요리하는 곳 (Builder)] ==========
# 여기서는 JDK(개발 도구)가 필요합니다.
FROM --platform=linux/amd64 amazoncorretto:17-alpine-jdk AS builder

WORKDIR /build

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x ./gradlew
RUN ./gradlew bootJar

FROM --platform=linux/amd64 amazoncorretto:17-alpine-jdk

WORKDIR /app

COPY --from=builder /build/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

EXPOSE 8080