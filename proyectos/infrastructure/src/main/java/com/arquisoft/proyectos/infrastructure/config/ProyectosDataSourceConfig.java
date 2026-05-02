package com.arquisoft.proyectos.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Configuración de DataSource para el bounded context {@code proyectos}.
 *
 * <p>Solo declara el bean DataSource. La configuración de JPA, Flyway y
 * TransactionManager se añadirá cuando el contexto tenga entidades implementadas.
 */
@Configuration
public class ProyectosDataSourceConfig {

    @Value("${datasource.proyectos.url:jdbc:postgresql://localhost:5432/proyectos_grado}")
    private String url;

    @Value("${datasource.proyectos.username:arquisoft_user}")
    private String username;

    @Value("${datasource.proyectos.password:arquisoft123}")
    private String password;

    @Value("${datasource.proyectos.hikari.maximum-pool-size:10}")
    private int maxPoolSize;

    @Value("${datasource.proyectos.hikari.minimum-idle:2}")
    private int minIdle;

    @Value("${datasource.proyectos.hikari.connection-timeout:20000}")
    private long connectionTimeout;

    @Bean(name = "proyectosDataSource")
    public DataSource proyectosDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);
        config.setConnectionTimeout(connectionTimeout);
        config.setPoolName("HikariPool-Proyectos");
        config.setDriverClassName("org.postgresql.Driver");
        return new HikariDataSource(config);
    }
}
