val mockkVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val jbcryptVersion: String by project
//val ktorVersion: String by project

plugins {
    kotlin("jvm") version "2.1.0"
    id("io.ktor.plugin") version "3.1.0"
    //id("io.ktor.plugin") version "2.3.13"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.0"
    id("idea")
    kotlin("plugin.power-assert") version "2.0.0"
}

group = "com.gatchii.common-util"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-host-common-jvm")
    implementation("io.ktor:ktor-server-status-pages-jvm")
    implementation("io.ktor:ktor-server-caching-headers-jvm")
    implementation("io.ktor:ktor-server-compression-jvm")
    implementation("com.ucasoft.ktor:ktor-simple-cache-jvm:0.4.3")
    implementation("com.ucasoft.ktor:ktor-simple-redis-cache-jvm:0.4.3")
    implementation("io.ktor:ktor-server-call-logging-jvm")
    implementation("io.ktor:ktor-server-call-id-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    //implementation("io.ktor:ktor-server-routing-jvm:$ktorVersion")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.ktor:ktor-server-config-yaml")
    // https://mvnrepository.com/artifact/org.bouncycastle/bcprov-jdk18on
    implementation("org.bouncycastle:bcprov-jdk18on:1.79")
    // https://mvnrepository.com/artifact/org.bitbucket.b_c/jose4j
    implementation("org.bitbucket.b_c:jose4j:0.9.6")
    implementation("com.nimbusds:nimbus-jose-jwt:9.45")
    implementation("org.mindrot:jbcrypt:$jbcryptVersion")
    testImplementation("io.ktor:ktor-server-test-host-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:$kotlinVersion")
    testImplementation("io.mockk:mockk:${mockkVersion}")
    testImplementation("org.assertj:assertj-core:3.11.1")
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
}

tasks.test {
    useJUnitPlatform()
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