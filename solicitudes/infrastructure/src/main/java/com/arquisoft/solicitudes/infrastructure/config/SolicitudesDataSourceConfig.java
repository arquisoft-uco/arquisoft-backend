package com.arquisoft.solicitudes.infrastructure.config;

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

// Ningún bean se marca @Primary: ese lugar lo ocupa usuariosTransactionManager y duplicarlo
// rompe el arranque.
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.arquisoft.solicitudes.infrastructure",
        entityManagerFactoryRef = "solicitudesEntityManagerFactory",
        transactionManagerRef = "solicitudesTransactionManager"
)
public class SolicitudesDataSourceConfig {

    @Value("${datasource.solicitudes.url}")
    private String url;

    @Value("${datasource.solicitudes.username}")
    private String username;

    @Value("${datasource.solicitudes.password}")
    private String password;

    @Value("${datasource.solicitudes.hikari.maximum-pool-size}")
    private int maxPoolSize;

    @Value("${datasource.solicitudes.hikari.minimum-idle}")
    private int minIdle;

    @Value("${datasource.solicitudes.hikari.connection-timeout}")
    private long connectionTimeout;

    @Bean(name = "solicitudesDataSource")
    public DataSource solicitudesDataSource() {
        var config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);
        config.setConnectionTimeout(connectionTimeout);
        config.setPoolName("HikariPool-Solicitudes");
        config.setDriverClassName("org.postgresql.Driver");
        return new HikariDataSource(config);
    }

    @Bean(name = "solicitudesEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean solicitudesEntityManagerFactory(
            @Qualifier("solicitudesDataSource") DataSource dataSource,
            @Qualifier("solicitudesFlyway") Flyway solicitudesFlyway) {

        var em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.arquisoft.solicitudes.infrastructure");
        em.setPersistenceUnitName("solicitudes");

        var vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        em.setJpaPropertyMap(PropiedadesJpa.porDefecto());

        return em;
    }

    @Bean(name = "solicitudesTransactionManager")
    public PlatformTransactionManager solicitudesTransactionManager(
            @Qualifier("solicitudesEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean(name = "solicitudesFlyway", initMethod = "migrate")
    public Flyway solicitudesFlyway(@Qualifier("solicitudesDataSource") DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/solicitudes")
                .baselineOnMigrate(false)
                .load();
    }
}
