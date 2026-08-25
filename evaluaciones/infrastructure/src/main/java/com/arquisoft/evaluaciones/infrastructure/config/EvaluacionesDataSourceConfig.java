package com.arquisoft.evaluaciones.infrastructure.config;

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
        basePackages = "com.arquisoft.evaluaciones.infrastructure",
        entityManagerFactoryRef = "evaluacionesEntityManagerFactory",
        transactionManagerRef = "evaluacionesTransactionManager"
)
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
        var config = new HikariConfig();
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

    @Bean(name = "evaluacionesEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean evaluacionesEntityManagerFactory(
            @Qualifier("evaluacionesDataSource") DataSource dataSource,
            @Qualifier("evaluacionesFlyway") Flyway evaluacionesFlyway) {
        var entityManager = new LocalContainerEntityManagerFactoryBean();
        entityManager.setDataSource(dataSource);
        entityManager.setPackagesToScan(
                "com.arquisoft.evaluaciones.application",
                "com.arquisoft.evaluaciones.infrastructure");
        entityManager.setPersistenceUnitName("evaluaciones");
        entityManager.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManager.setJpaPropertyMap(PropiedadesJpa.porDefecto());
        return entityManager;
    }

    @Bean(name = "evaluacionesTransactionManager")
    public PlatformTransactionManager evaluacionesTransactionManager(
            @Qualifier("evaluacionesEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean(name = "evaluacionesFlyway", initMethod = "migrate")
    public Flyway evaluacionesFlyway(
            @Qualifier("evaluacionesDataSource") DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/evaluaciones")
                .baselineOnMigrate(true)
                .load();
    }
}
