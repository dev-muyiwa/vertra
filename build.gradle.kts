plugins {
    java
    id("org.jetbrains.kotlin.jvm") version "1.9.24" apply false
    id("org.springframework.boot") version "3.5.7" apply false
    id("io.spring.dependency-management") version "1.1.7"
}

allprojects {
    group = "com.vertra"
    version = "1.0.0"

    repositories {
        mavenCentral()
    }
}


subprojects {
    apply(plugin = "java")
    apply(plugin = "io.spring.dependency-management")

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
        withSourcesJar()
        withJavadocJar()
    }

    dependencies {
        dependencyManagement {
            imports {
                mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.7")
            }
        }
        compileOnly("org.projectlombok:lombok:1.18.42")
        annotationProcessor("org.projectlombok:lombok:1.18.42")

        implementation("org.slf4j:slf4j-api:2.0.17")

        testImplementation("org.junit.jupiter:junit-jupiter-api:6.0.1")
        testImplementation("org.mockito:mockito-core:5.20.0")
        testImplementation("org.mockito:mockito-junit-jupiter:5.20.0")
        testImplementation("org.assertj:assertj-core:3.27.6")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-parameters")
        options.isIncremental = true
        options.incrementalAfterFailure = true
    }

}
