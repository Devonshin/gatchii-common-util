# gatchii-common-util

[![CI](https://github.com/Devonshin/gatchii-common-util/actions/workflows/ci.yml/badge.svg)](https://github.com/Devonshin/gatchii-common-util/actions/workflows/ci.yml)

A Kotlin JVM utility library with reusable components for:
- Cryptography helpers: RSA key pair utilities (PEM/JWK conversion, encrypt/decrypt), EC (secp256r1) key operations and ECDSA sign/verify.
- Security: BCrypt password hashing.
- File helpers: simple read/write with rotation on overwrite, path utilities.
- Date/time helpers: test-friendly clock utilities and readable duration formatting.
- Lightweight task scheduling primitives built on Kotlin coroutines (one-time and daily routine tasks with simple "leader" gating).

Published to GitHub Packages; built with Gradle Kotlin DSL.

## Stack
- Language: Kotlin 2.1.0 (JVM)
- JDK: 21
- Build tool: Gradle 8.10 (wrapper included)
- Plugins: kotlin-jvm, kotlin-serialization, kotlin-power-assert, idea, maven-publish
- Logging: SLF4J + Logback
- Test: JUnit 5, MockK, kotlinx-coroutines-test
- Crypto libs: BouncyCastle, jose4j, nimbus-jose-jwt, jbcrypt

## Requirements
- Java 21 (Temurin recommended)
- Git (to clone)
- No standalone installation of Gradle required — use the provided wrapper: `./gradlew`

## Installation / Using as a dependency
Artifacts are published to GitHub Packages under this repository.

Gradle Groovy (build.gradle)
```
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/Devonshin/gatchii-common-util")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation "com.gatchii:gatchii-common-util:0.0.8" // TODO: verify latest version
}
```

Gradle Kotlin DSL (build.gradle.kts)
```
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/Devonshin/gatchii-common-util")
        credentials {
            username = findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
            password = findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation("com.gatchii:gatchii-common-util:0.0.8") // TODO: verify latest version
}
```

You can also put credentials into your ~/.gradle/gradle.properties:
```
gpr.user=YOUR_GH_USERNAME
gpr.key=YOUR_GH_TOKEN   # Token must have read:packages scope
```

## Quick start examples

RSA (encrypt/decrypt, generate files automatically)
```kotlin
import com.gatchii.common.utils.RsaPairHandler

fun demoRsa() {
    val cipher = RsaPairHandler.encrypt("hello")
    val plain = RsaPairHandler.decrypt(cipher)
    println(plain)
}
```

EC (secp256r1) sign/verify
```kotlin
import com.gatchii.common.utils.ECKeyPairHandler

fun demoEc() {
    val kp = ECKeyPairHandler.generateKeyPair()
    val msg = "message"
    val sig = ECKeyPairHandler.sign(msg, kp.private)
    check(ECKeyPairHandler.verify(msg, kp.public, sig))
}
```

BCrypt
```kotlin
import com.gatchii.common.utils.BCryptPasswordEncoder

val encoder = BCryptPasswordEncoder()
val hashed = encoder.encode("secret")
check(encoder.matches("secret", hashed))
```

Routine task (runs a task daily at a given time; immediate run if already past)
```kotlin
import com.gatchii.common.tasks.RoutineTaskHandler
import com.gatchii.common.tasks.RoutineScheduleExpression

val handler = RoutineTaskHandler(
    taskName = "sampleTask",
    scheduleExpression = RoutineScheduleExpression(hour = 1, minute = 0, second = 0),
    task = { println("do work") }
)
com.gatchii.common.tasks.TaskLeadHandler.addTasks(handler)
com.gatchii.common.tasks.TaskLeadHandler.runTasks()
```

## Environment variables
These are read by the code or build during runtime/CI:
- GITHUB_USERNAME, GITHUB_TOKEN — used by Gradle publishing and CI to authenticate with GitHub Packages.
- secret.gatchii.rsa.path — directory containing RSA key files (defaults to `./gatchii_secret`).
- secret.gatchii.rsa.privateKey — private key (PEM, Base64-encoded body). Supports both `-----BEGIN RSA PRIVATE KEY-----` and `-----BEGIN PRIVATE KEY-----` headers; whitespace/newlines are ignored.
- secret.gatchii.rsa.publicKey — public key (PEM, Base64-encoded body). Supports both `-----BEGIN RSA PUBLIC KEY-----` and `-----BEGIN PUBLIC KEY-----` headers; whitespace/newlines are ignored.

When keys are not provided, a new RSA key pair is generated on startup and written to:
- gatchii_secret/rsa_private.pem
- gatchii_secret/rsa_public.pem

## Build, test, and publish
- Build: `./gradlew build`
- Run tests: `./gradlew test`
  - Tag-based filtering (JUnit 5): `./gradlew test -Djunit.jupiter.tags=unitTest`
  - Custom unit tests task: `./gradlew unitTest` (runs tests tagged with `@UnitTest`)
  - Specific class: `./gradlew test --tests "com.gatchii.common.utils.RsaPairGeneratorUnitTest"`
- Coverage report: `./gradlew jacocoTestReport` (HTML: `build/reports/jacoco/test/html/index.html`)
- Publish to GitHub Packages (requires env vars above): `./gradlew publish`

## Project structure
```
.
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── src
│   ├── main
│   │   ├── kotlin
│   │   │   ├── com/gatchii/common/utils/ (BCryptPasswordEncoder, DateUtil, ECKeyPairHandler, FileUtil, RsaKeyPairHandler)
│   │   │   └── com/gatchii/common/tasks/ (TaskLeadHandler, OnetimeTaskHandler, RoutineTaskHandler)
│   │   └── resources/
│   └── test
│       └── kotlin/ (JUnit 5 tests, tags: @UnitTest, @IntegrationTest)
├── local-doc/ (project notes: API_REFERENCE.md, PROJECT_INDEX.md)
└── .github/workflows/publish.yaml (CI build & publish)
```

## Continuous Integration / Delivery

This repository uses GitHub Actions CI to build, test, lint, and generate coverage reports on each push/PR.

- Workflow file: .github/workflows/ci.yml
- Steps: checkout → setup-java (JDK 21) → gradlew clean test jacocoTestReport detekt ktlintCheck
- Artifacts uploaded:
  - JUnit test report: build/reports/tests/test/
  - JaCoCo coverage report: build/reports/jacoco/test/
  - Detekt report: build/reports/detekt/
  - KtLint report: build/reports/ktlint/

CI status badge is shown at the top of this README. Click it to view latest run and download artifacts.
A GitHub Actions workflow builds on push to `main` and publishes on GitHub Release creation:
- Uses Temurin JDK 21
- Steps: checkout → gradle build → gradle publish (with GITHUB_USERNAME/TOKEN)
See `.github/workflows/publish.yaml` for details.

## Scripts / Gradle tasks
- build — compile and run tests
- test — run all tests
- unitTest — run only tests tagged with `@UnitTest`
- publish — publish Maven artifacts to GitHub Packages

## Notes
- There is a simple `Main.kt` that prints "Hello World!". This project is primarily a library; no application plugin is configured.
- Crypto utilities depend on BouncyCastle; ensure the provider is available (EC utilities add it on demand).

## License
TODO: Add a LICENSE file and state the license for this repository.

## Contributing
Contributions are welcome! Feel free to open issues or send pull requests.

## Contact
TODO: Add maintainer contact information or point to GitHub Issues.
