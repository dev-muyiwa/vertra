plugins {
	`java-library`
}


dependencies {
	api(project(":domain"))
	api(project(":application"))

	implementation(project(":adapters:persistence"))

	implementation("org.springframework:spring-context")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

description = "Vertra's audit adapter - JPA implementation for audit logs"
