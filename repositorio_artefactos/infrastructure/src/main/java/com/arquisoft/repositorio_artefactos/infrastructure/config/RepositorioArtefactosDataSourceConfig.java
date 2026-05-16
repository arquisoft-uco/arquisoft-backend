package com.arquisoft.repositorio_artefactos.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Configuración de DataSource para el bounded context {@code repositorio_artefactos}.
 *
 * <p>Solo declara el bean DataSource. La configuración de JPA, Flyway y
 * TransactionManager se añadirá cuando el contexto tenga entidades implementadas.
 */
@Configuration
public class RepositorioArtefactosDataSourceConfig {

    @Value("${datasource.repositorio_artefactos.url}")
    private String url;

    @Value("${datasource.repositorio_artefactos.username}")
    private String username;

    @Value("${datasource.repositorio_artefactos.password}")
    private String password;

    @Value("${datasource.repositorio_artefactos.hikari.maximum-pool-size}")
    private int maxPoolSize;

    @Value("${datasource.repositorio_artefactos.hikari.minimum-idle}")
    private int minIdle;

    @Value("${datasource.repositorio_artefactos.hikari.connection-timeout}")
    private long connectionTimeout;

    @Bean(name = "repositorioArtefactosDataSource")
    public DataSource repositorioArtefactosDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);
        config.setConnectionTimeout(connectionTimeout);
        config.setPoolName("HikariPool-RepositorioArtefactos");
        config.setDriverClassName("org.postgresql.Driver");
        return new HikariDataSource(config);
    }
}
