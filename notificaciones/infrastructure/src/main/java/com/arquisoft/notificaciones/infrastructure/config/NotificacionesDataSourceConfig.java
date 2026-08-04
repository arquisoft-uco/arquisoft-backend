package com.arquisoft.notificaciones.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * DataSource, EntityManagerFactory y Flyway propios del contexto notificaciones.
 *
 * <p>Ningun bean se marca {@code @Primary}: ese lugar lo ocupa {@code usuariosTransactionManager}
 * y duplicarlo rompe el arranque.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.arquisoft.notificaciones.infrastructure",
        entityManagerFactoryRef = "notificacionesEntityManagerFactory",
        transactionManagerRef = "notificacionesTransactionManager"
)
public class NotificacionesDataSourceConfig {

    @Value("${datasource.notificaciones.url}")
    private String url;

    @Value("${datasource.notificaciones.username}")
    private String username;

    @Value("${datasource.notificaciones.password}")
    private String password;

    @Value("${datasource.notificaciones.hikari.maximum-pool-size}")
    private int maxPoolSize;

    @Value("${datasource.notificaciones.hikari.minimum-idle}")
    private int minIdle;

    @Value("${datasource.notificaciones.hikari.connection-timeout}")
    private long connectionTimeout;

    @Bean(name = "notificacionesDataSource")
    public DataSource notificacionesDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);
        config.setConnectionTimeout(connectionTimeout);
        config.setPoolName("HikariPool-Notificaciones");
        config.setDriverClassName("org.postgresql.Driver");
        return new HikariDataSource(config);
    }

    @Bean(name = "notificacionesEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean notificacionesEntityManagerFactory(
            @Qualifier("notificacionesDataSource") DataSource dataSource,
            @Qualifier("notificacionesFlyway") Flyway notificacionesFlyway) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.arquisoft.notificaciones.infrastructure");
        em.setPersistenceUnitName("notificaciones");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "validate");
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.jdbc.batch_size", "25");
        properties.put("hibernate.show_sql", "false");
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean(name = "notificacionesTransactionManager")
    public PlatformTransactionManager notificacionesTransactionManager(
            @Qualifier("notificacionesEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean(name = "notificacionesFlyway", initMethod = "migrate")
    public Flyway notificacionesFlyway(@Qualifier("notificacionesDataSource") DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/notificaciones")
                .baselineOnMigrate(true)
                .load();
    }
}
