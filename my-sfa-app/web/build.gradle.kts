plugins {
    id("org.springframework.boot")
}

// Only create bootJar, not plain jar for the main application
tasks.jar {
    enabled = false
    archiveClassifier = ""
}

dependencies {
    implementation(project(":core"))
    implementation(project(":security"))
    implementation(project(":infra"))
    
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    
    // Vaadin UI framework - commented out as this is primarily a REST API application
    // implementation("com.vaadin:vaadin-spring-boot-starter:24.8.3")
    
    // PostgreSQL driver
    runtimeOnly("org.postgresql:postgresql")
    
    // H2 for testing
    runtimeOnly("com.h2database:h2")
    
    // OpenAPI/Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
    
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
}