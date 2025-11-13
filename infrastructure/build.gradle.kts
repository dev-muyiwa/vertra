plugins {
    java
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
}


dependencies {
    implementation(project(":domain"))
    implementation(project(":application"))
    implementation(project(":adapters:persistence"))
    implementation(project(":adapters:web"))
    implementation(project(":adapters:security"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("io.micrometer:micrometer-registry-prometheus:1.12.0")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

//    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(project(":domain"))
    testImplementation(project(":application"))
}

tasks.bootJar {
    archiveFileName.set("vertra.jar")
    mainClass.set("com.vertra.VertraApplication")
}

tasks.jar {
    enabled = false
}

springBoot {
    buildInfo {
        properties {
            time = null
        }
    }
}

description = "Vertra Infrastructure - Main Spring Boot application"
