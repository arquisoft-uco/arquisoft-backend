package com.arquisoft.fichas.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Configuración de DataSource para el bounded context {@code fichas}.
 *
 * <p>Solo declara el bean DataSource. La configuración de JPA, Flyway y
 * TransactionManager se añadirá cuando el contexto tenga entidades implementadas.
 */
@Configuration
public class FichasDataSourceConfig {

    @Value("${datasource.fichas.url:jdbc:postgresql://localhost:5432/fichas_perfil}")
    private String url;

    @Value("${datasource.fichas.username:arquisoft_user}")
    private String username;

    @Value("${datasource.fichas.password:arquisoft123}")
    private String password;

    @Value("${datasource.fichas.hikari.maximum-pool-size:10}")
    private int maxPoolSize;

    @Value("${datasource.fichas.hikari.minimum-idle:2}")
    private int minIdle;

    @Value("${datasource.fichas.hikari.connection-timeout:20000}")
    private long connectionTimeout;

    @Bean(name = "fichasDataSource")
    public DataSource fichasDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);
        config.setConnectionTimeout(connectionTimeout);
        config.setPoolName("HikariPool-Fichas");
        config.setDriverClassName("org.postgresql.Driver");
        return new HikariDataSource(config);
    }
}
