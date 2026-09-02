package com.arquisoft.notificaciones.infrastructure.config;

import com.arquisoft.shared.jpa.config.PropiedadesJpa;
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
        var config = new HikariConfig();
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

        var em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.arquisoft.notificaciones.infrastructure");
        em.setPersistenceUnitName("notificaciones");

        var vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        em.setJpaPropertyMap(PropiedadesJpa.porDefecto());

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
                .baselineOnMigrate(false)
                .load();
    }
}
