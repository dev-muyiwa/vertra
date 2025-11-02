plugins {
	java
}

dependencies {
	implementation(project(":domain"))

	implementation("org.springframework:spring-context:6.2.12")
	implementation("org.springframework:spring-tx:6.2.12")

	implementation("jakarta.validation:jakarta.validation-api:3.1.1")
	implementation("org.hibernate.validator:hibernate-validator:9.0.1.Final")

	implementation("com.fasterxml.jackson.core:jackson-databind:2.20.1")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.20.1")

	testImplementation("org.springframework:spring-test:6.2.12")
	testImplementation("org.junit.jupiter:junit-jupiter:6.0.0")
	testImplementation("org.mockito:mockito-core:5.20.0")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
