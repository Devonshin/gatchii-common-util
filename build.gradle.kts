val mockkVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val jbcryptVersion: String by project
val kotlinCoroutines: String by project
//val ktorVersion: String by project

plugins {
    kotlin("jvm") version "2.1.0"
    //id("io.ktor.plugin") version "3.1.0"
    //id("io.ktor.plugin") version "2.3.13"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.0"
    id("idea")
    kotlin("plugin.power-assert") version "2.0.0"
    jacoco
    id("io.gitlab.arturbosch.detekt") version "1.23.6"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    `maven-publish`
}

group = "com.gatchii"
version = "0.0.8"

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Devonshin/gatchii-common-util")
            credentials {
                username = System.getenv("GITHUB_USERNAME")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutines")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    // https://mvnrepository.com/artifact/org.bouncycastle/bcprov-jdk18on
    implementation("org.bouncycastle:bcprov-jdk18on:1.79")
    // https://mvnrepository.com/artifact/org.bitbucket.b_c/jose4j
    implementation("org.bitbucket.b_c:jose4j:0.9.6")
    implementation("com.nimbusds:nimbus-jose-jwt:9.45")
    implementation("org.mindrot:jbcrypt:$jbcryptVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:$kotlinVersion")
    testImplementation("io.mockk:mockk:${mockkVersion}")
    testImplementation("org.assertj:assertj-core:3.11.1")
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinCoroutines")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events(org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
               org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
               org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED)
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStandardStreams = false
    }
}

jacoco {
    toolVersion = "0.8.11"
}

// 정적 분석: detekt 기본 설정 (별도 설정 파일 없이 기본 규칙 사용)
detekt {
    buildUponDefaultConfig = true
    allRules = false
    autoCorrect = false
    // Use default rules; custom config is disabled to avoid schema mismatches
    ignoreFailures = true
}

// 정적 분석: ktlint 기본 설정 (플러그인 기본값 사용)
ktlint {
    ignoreFailures.set(true)
    disabledRules.set(setOf("no-wildcard-imports","final-newline","max-line-length","filename","package-name"))
}

// 품질 태스크를 check 파이프라인에 연결
tasks.named("check") {
    dependsOn("detekt", "ktlintCheck")
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
    // 커버리지 제외 설정: 라이브러리 동작과 무관한 샘플 엔트리포인트 등 제외
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "com/gatchii/MainKt*.*"
                )
            }
        })
    )
}

tasks.register("unitTest", Test::class) {
    group = "verification"
    description = "Run unit tests annotated with @UnitTest"
    useJUnitPlatform {
        includeTags("unitTest")
    }
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}