package com.arquisoft.mapas_ruta.infrastructure.config;

import com.arquisoft.shared.jpa.config.PropiedadesJpa;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
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
        basePackages = "com.arquisoft.mapas_ruta.infrastructure",
        entityManagerFactoryRef = "mapasRutaEntityManagerFactory",
        transactionManagerRef = "mapasRutaTransactionManager",
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
public class MapasRutaDataSourceConfig {

    @Value("${datasource.mapas_ruta.url}")
    private String url;

    @Value("${datasource.mapas_ruta.username}")
    private String username;

    @Value("${datasource.mapas_ruta.password}")
    private String password;

    @Value("${datasource.mapas_ruta.hikari.maximum-pool-size}")
    private int maxPoolSize;

    @Value("${datasource.mapas_ruta.hikari.minimum-idle}")
    private int minIdle;

    @Value("${datasource.mapas_ruta.hikari.connection-timeout}")
    private long connectionTimeout;

    @Bean(name = "mapasRutaDataSource")
    public DataSource mapasRutaDataSource() {
        var config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);
        config.setConnectionTimeout(connectionTimeout);
        config.setPoolName("HikariPool-MapasRuta");
        config.setDriverClassName("org.postgresql.Driver");
        return new HikariDataSource(config);
    }

    @Bean(name = "mapasRutaEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean mapasRutaEntityManagerFactory(
            @Qualifier("mapasRutaDataSource") DataSource dataSource,
            @Qualifier("mapasRutaFlyway") Flyway mapasRutaFlyway) {
        var entityManager = new LocalContainerEntityManagerFactoryBean();
        entityManager.setDataSource(dataSource);
        entityManager.setPackagesToScan("com.arquisoft.mapas_ruta.infrastructure");
        entityManager.setPersistenceUnitName("mapas_ruta");
        entityManager.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManager.setJpaPropertyMap(PropiedadesJpa.porDefecto());
        return entityManager;
    }

    @Bean(name = "mapasRutaTransactionManager")
    public PlatformTransactionManager mapasRutaTransactionManager(
            @Qualifier("mapasRutaEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean(name = "mapasRutaFlyway", initMethod = "migrate")
    public Flyway mapasRutaFlyway(
            @Qualifier("mapasRutaDataSource") DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/mapas_ruta")
                .baselineOnMigrate(false)
                .load();
    }
}
