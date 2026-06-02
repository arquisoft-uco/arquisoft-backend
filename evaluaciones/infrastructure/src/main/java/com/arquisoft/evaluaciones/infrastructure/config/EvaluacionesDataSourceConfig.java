package com.arquisoft.evaluaciones.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Configuración de DataSource para el bounded context {@code evaluaciones}.
 *
 * <p>Solo declara el bean DataSource. La configuración de JPA, Flyway y
 * TransactionManager se añadirá cuando el contexto tenga entidades implementadas.
 */
@Configuration
public class EvaluacionesDataSourceConfig {

    @Value("${datasource.evaluaciones.url}")
    private String url;

    @Value("${datasource.evaluaciones.username}")
    private String username;

    @Value("${datasource.evaluaciones.password}")
    private String password;

    @Value("${datasource.evaluaciones.hikari.maximum-pool-size}")
    private int maxPoolSize;

    @Value("${datasource.evaluaciones.hikari.minimum-idle}")
    private int minIdle;

    @Value("${datasource.evaluaciones.hikari.connection-timeout}")
    private long connectionTimeout;

    @Bean(name = "evaluacionesDataSource")
    public DataSource evaluacionesDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);
        config.setConnectionTimeout(connectionTimeout);
        config.setPoolName("HikariPool-Evaluaciones");
        config.setDriverClassName("org.postgresql.Driver");
        return new HikariDataSource(config);
    }
}
