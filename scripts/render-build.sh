#!/bin/bash

# Render build script for SalesForge
echo "Starting SalesForge build for Render..."

# Navigate to the Spring Boot application directory
cd my-sfa-app

# Make gradlew executable
chmod +x gradlew

# Clean and build the application (skip tests for faster build)
echo "Building Spring Boot application..."
./gradlew clean build -x test --no-daemon

# Check if build was successful
if [ $? -eq 0 ]; then
    echo "Build successful!"
    echo "JAR file location:"
    find web/build/libs -name "web-*.jar" -type f
else
    echo "Build failed!"
    exit 1
fi