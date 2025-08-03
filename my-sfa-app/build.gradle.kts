import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension

plugins {
    java
    id("org.springframework.boot") version "3.3.5" apply false
    id("io.spring.dependency-management") version "1.1.6" apply false
}

allprojects {
    group = "com.example"
    version = "0.0.1-SNAPSHOT"
    
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    
    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }
    
    configure<DependencyManagementExtension> {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:3.3.5")
            mavenBom("org.testcontainers:testcontainers-bom:1.19.3")
        }
    }
    
    dependencies {
        // Common dependencies across all modules
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")
        
        // Testing
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.testcontainers:junit-jupiter")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }
    
    tasks.withType<Test> {
        useJUnitPlatform()
    }
}