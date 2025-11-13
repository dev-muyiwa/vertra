plugins {
	`java-library`
}

dependencies {
	api(project(":domain"))

	implementation("org.springframework:spring-context:6.2.12")
	implementation("org.springframework:spring-tx:6.2.12")

	testImplementation("org.springframework:spring-test:6.2.12")
	testImplementation(project(":domain"))
}

description = "Vertra Application - Use cases and business services"