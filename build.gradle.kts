plugins {
    java
    id("org.jetbrains.kotlin.jvm") version "1.9.24" apply false
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.vertra"
version = "1.0.0"
description = "vertra"


subprojects {
    apply(plugin = "java")
    apply(plugin = "io.spring.dependency-management")

    if (file("build.gradle.kts").exists()) {
        apply(plugin = "org.jetbrains.kotlin.jvm")
    }

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
        withSourcesJar()
        withJavadocJar()
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        dependencyManagement {
            imports {
                mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.7")
            }
        }
        compileOnly("org.projectlombok:lombok:1.18.42")
        annotationProcessor("org.projectlombok:lombok:1.18.42")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

}
