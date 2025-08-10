package com.example.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;

import java.net.URI;

@Configuration
public class DatabaseConfig {

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    @Bean
    @Primary
    public DataSource dataSource() {
        if (databaseUrl != null && databaseUrl.startsWith("postgresql://")) {
            // Convert Render's DATABASE_URL format to JDBC URL
            try {
                URI uri = new URI(databaseUrl);
                
                // Handle port - use default PostgreSQL port if not specified
                int port = uri.getPort() == -1 ? 5432 : uri.getPort();
                
                String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + ":" + port + uri.getPath();
                String[] userInfo = uri.getUserInfo().split(":");
                String username = userInfo[0];
                String password = userInfo[1];
                
                System.out.println("Parsed DATABASE_URL: " + jdbcUrl);
                System.out.println("Username: " + username);
                
                return DataSourceBuilder.create()
                    .driverClassName("org.postgresql.Driver")
                    .url(jdbcUrl)
                    .username(username)
                    .password(password)
                    .build();
            } catch (Exception e) {
                System.err.println("Failed to parse DATABASE_URL: " + databaseUrl);
                throw new RuntimeException("Failed to parse DATABASE_URL: " + databaseUrl, e);
            }
        }
        
        // Fall back to default Spring Boot configuration
        return DataSourceBuilder.create().build();
    }
}