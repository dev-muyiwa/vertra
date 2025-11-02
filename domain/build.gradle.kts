plugins {
	java
}

dependencies {
	implementation("org.jetbrains:annotations:24.1.0")

	testImplementation("org.springframework:spring-test:6.2.12")
	testImplementation("org.junit.jupiter:junit-jupiter:6.0.0")
	testImplementation("org.mockito:mockito-core:5.20.0")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}