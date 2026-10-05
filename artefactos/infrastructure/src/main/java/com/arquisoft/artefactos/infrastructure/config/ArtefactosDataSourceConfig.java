package com.arquisoft.artefactos.infrastructure.config;

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
        basePackages = "com.arquisoft.artefactos.infrastructure",
        entityManagerFactoryRef = "artefactosEntityManagerFactory",
        transactionManagerRef = "artefactosTransactionManager",
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
public class ArtefactosDataSourceConfig {

    @Value("${datasource.artefactos.url}")
    private String url;

    @Value("${datasource.artefactos.username}")
    private String username;

    @Value("${datasource.artefactos.password}")
    private String password;

    @Value("${datasource.artefactos.hikari.maximum-pool-size}")
    private int maxPoolSize;

    @Value("${datasource.artefactos.hikari.minimum-idle}")
    private int minIdle;

    @Value("${datasource.artefactos.hikari.connection-timeout}")
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

    @Bean(name = "artefactosEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean artefactosEntityManagerFactory(
            @Qualifier("artefactosDataSource") DataSource dataSource,
            @Qualifier("artefactosFlyway") Flyway artefactosFlyway) {

        var em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.arquisoft.artefactos.infrastructure");
        em.setPersistenceUnitName("artefactos");

        var vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        em.setJpaPropertyMap(PropiedadesJpa.porDefecto());

        return em;
    }

    @Bean(name = "artefactosTransactionManager")
    public PlatformTransactionManager artefactosTransactionManager(
            @Qualifier("artefactosEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean(name = "artefactosFlyway", initMethod = "migrate")
    public Flyway artefactosFlyway(@Qualifier("artefactosDataSource") DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/artefactos")
                .baselineOnMigrate(false)
                .load();
    }
}
