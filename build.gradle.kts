plugins {
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.5"
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
    kotlin("plugin.jpa") version "1.9.24"
    kotlin("kapt") version "1.9.24"
}

group = "com.highV"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web") // Spring Boot 웹 애플리케이션 개발을 위한 기본 스타터
    implementation("org.springframework.boot:spring-boot-starter-data-jpa") // Spring Data JPA를 사용한 데이터베이스 접근을 위한 스타터
    implementation("org.springframework.boot:spring-boot-starter-jdbc") // JDBC를 사용한 데이터베이스 접근을 위한 스타터
    implementation("org.springframework.boot:spring-boot-starter-security") // Spring Security를 사용한 인증 및 인가를 위한 스타터
    implementation("org.springframework.boot:spring-boot-starter-validation") // Spring Boot의 유효성 검사를 위한 스타터

    // Jackson Module for Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin") // Jackson의 Kotlin 모듈로, Kotlin 객체를 JSON으로 변환하기 위해 사용

    // Kotlin Standard Library and Reflection
    implementation("org.jetbrains.kotlin:kotlin-reflect") // Kotlin 리플렉션 기능을 제공
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8") // Kotlin 표준 라이브러리

    // MySQL Connector
    implementation("com.mysql:mysql-connector-j") // MySQL 데이터베이스 연결을 위한 MySQL JDBC 드라이버

    // Spring Boot Configuration Processor
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor") // Spring Boot의 구성 프로세서를 위한 애노테이션 프로세서

    // Test Dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test") // Spring Boot 테스트 스타터
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5") // JUnit 5와 통합된 Kotlin 테스트 라이브러리
    testRuntimeOnly("org.junit.platform:junit-platform-launcher") // JUnit 플랫폼 런처
    testImplementation("io.mockk:mockk:1.13.9") // Kotlin용 모킹 라이브러리
    testImplementation("io.kotest:kotest-runner-junit5:5.7.2") // Kotest의 JUnit 5 러너
    testImplementation("io.kotest:kotest-assertions-core:5.7.2") // Kotest의 기본 어설션 라이브러리
}


kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

kapt {
    arguments {
        arg("querydsl.entityAccessors", "true")
    }
}