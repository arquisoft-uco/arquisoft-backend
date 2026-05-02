package com.arquisoft.artefactos.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Configuración de DataSource para el bounded context {@code artefactos}.
 *
 * <p>Solo declara el bean DataSource. La configuración de JPA, Flyway y
 * TransactionManager se añadirá cuando el contexto tenga entidades implementadas.
 */
@Configuration
public class ArtefactosDataSourceConfig {

    @Value("${datasource.artefactos.url:jdbc:postgresql://localhost:5432/artefactos}")
    private String url;

    @Value("${datasource.artefactos.username:arquisoft_user}")
    private String username;

    @Value("${datasource.artefactos.password:arquisoft123}")
    private String password;

    @Value("${datasource.artefactos.hikari.maximum-pool-size:10}")
    private int maxPoolSize;

    @Value("${datasource.artefactos.hikari.minimum-idle:2}")
    private int minIdle;

    @Value("${datasource.artefactos.hikari.connection-timeout:20000}")
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
