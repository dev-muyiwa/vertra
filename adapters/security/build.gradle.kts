plugins {
    `java-library`
}


dependencies {
    api(project(":domain"))
    api(project(":application"))

    implementation("org.jetbrains:annotations:24.1.0")

    implementation("org.springframework.security:spring-security-web")
    implementation("org.springframework.security:spring-security-config")
    implementation("org.springframework.security:spring-security-core")

    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-web")
    implementation("org.springframework.boot:spring-boot-autoconfigure")

    implementation("org.springframework.security:spring-security-crypto")

    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")

    implementation("io.jsonwebtoken:jjwt-api:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.13.0")

    // Jackson for OAuth token parsing
    implementation("com.fasterxml.jackson.core:jackson-databind")

    implementation("de.mkammerer:argon2-jvm:2.12")

    implementation("org.bouncycastle:bcprov-jdk18on:1.82")
    implementation("org.bouncycastle:bcpkix-jdk18on:1.82")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

description = "Vertra's security adapter - JWT, encryption, authentication"
