package com.arquisoft.artefactos.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class ArtefactosDataSourceConfig {

    @Value("${datasource.artefactos.url}")
    private String url;

    @Value("${datasource.artefactos.username}")
    private String username;

    @Value("${datasource.artefactos.password}")
    private String password;

    @Value("${datasource.artefactos.hikari.maximum-pool-size}")
    private int maxPoolSize;

    @Value("${datasource.artefactos.hikari.minimum-idle}")
    private int minIdle;

    @Value("${datasource.artefactos.hikari.connection-timeout}")
    private long connectionTimeout;

    @Bean(name = "artefactosDataSource")
    public DataSource artefactosDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);
        config.setConnectionTimeout(connectionTimeout);
        config.setPoolName("HikariPool-Artefactos");
        config.setDriverClassName("org.postgresql.Driver");
        return new HikariDataSource(config);
    }
}
