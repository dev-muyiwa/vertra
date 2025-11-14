plugins {
    `java-library`
    id("org.liquibase.gradle") version "2.2.0"
}

liquibase {
    activities.register("main") {
        arguments = mapOf(
            "changelogFile" to "adapters/persistence/src/main/resources/db/changelog/db.changelog-master.xml",
            "url" to (project.findProperty("liquibaseUrl") as String?
                ?: System.getenv("DB_URL")
                ?: "jdbc:postgresql://localhost:5432/vertra-db"),
            "username" to (project.findProperty("liquibaseUsername") as String?
                ?: System.getenv("DB_USERNAME")
                ?: "postgres"),
            "password" to (project.findProperty("liquibasePassword") as String?
                ?: System.getenv("DB_PASSWORD")
                ?: "password"),
            "driver" to "org.postgresql.Driver",
            "defaultSchemaName" to "public",
            "liquibaseSchemaName" to "public",
            "contexts" to (project.findProperty("liquibaseContexts") as String?
                ?: System.getenv("LIQUIBASE_CONTEXTS")
                ?: "default")
        )
    }
}

dependencies {
    api(project(":domain"))
    api(project(":application"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("org.liquibase:liquibase-core:5.0.1")

    liquibaseRuntime("org.liquibase:liquibase-core:5.0.1")
    liquibaseRuntime("org.postgresql:postgresql")
    liquibaseRuntime("info.picocli:picocli:4.7.5")
    liquibaseRuntime("org.yaml:snakeyaml:2.2")

    runtimeOnly("org.postgresql:postgresql")

    implementation("io.hypersistence:hypersistence-utils-hibernate-63:3.11.0")

    implementation("org.mapstruct:mapstruct:1.6.3")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("com.h2database:h2")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
}

tasks.register("dbUpdate") {
    group = "liquibase"
    description = "Apply all pending database changes"
    dependsOn("update")
}

tasks.register("dbRollback") {
    group = "liquibase"
    description = "Rollback last N changesets"
    dependsOn("rollback")
}

tasks.register("dbStatus") {
    group = "liquibase"
    description = "Show pending database changes"
    dependsOn("status")
}

tasks.register("dbValidate") {
    group = "liquibase"
    description = "Validate changelog"
    dependsOn("validate")
}

tasks.register("dbHistory") {
    group = "liquibase"
    description = "Show migration history"
    dependsOn("history")
}

description = "Vertra Persistence Adapter - JPA repositories and entities"
