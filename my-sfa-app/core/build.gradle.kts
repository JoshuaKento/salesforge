// Disable Spring Boot JAR generation for library modules
tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
    archiveClassifier = ""
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("jakarta.persistence:jakarta.persistence-api")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    
    // For entity auditing
    implementation("org.springframework.data:spring-data-commons")
}