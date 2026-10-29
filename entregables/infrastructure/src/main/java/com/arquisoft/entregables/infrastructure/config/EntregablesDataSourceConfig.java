package com.arquisoft.entregables.infrastructure.config;

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
        basePackages = "com.arquisoft.entregables.infrastructure",
        entityManagerFactoryRef = "entregablesEntityManagerFactory",
        transactionManagerRef = "entregablesTransactionManager",
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
public class EntregablesDataSourceConfig {

    @Value("${datasource.entregables.url}")
    private String url;

    @Value("${datasource.entregables.username}")
    private String username;

    @Value("${datasource.entregables.password}")
    private String password;

    @Value("${datasource.entregables.hikari.maximum-pool-size}")
    private int maxPoolSize;

    @Value("${datasource.entregables.hikari.minimum-idle}")
    private int minIdle;

    @Value("${datasource.entregables.hikari.connection-timeout}")
    private long connectionTimeout;

    @Bean(name = "entregablesDataSource")
    public DataSource entregablesDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);
        config.setConnectionTimeout(connectionTimeout);
        config.setPoolName("HikariPool-Entregables");
        config.setDriverClassName("org.postgresql.Driver");
        return new HikariDataSource(config);
    }

    @Bean(name = "entregablesEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entregablesEntityManagerFactory(
            @Qualifier("entregablesDataSource") DataSource dataSource,
            @Qualifier("entregablesFlyway") Flyway entregablesFlyway) {

        var em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.arquisoft.entregables.infrastructure");
        em.setPersistenceUnitName("entregables");

        var vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        em.setJpaPropertyMap(PropiedadesJpa.porDefecto());

        return em;
    }

    @Bean(name = "entregablesTransactionManager")
    public PlatformTransactionManager entregablesTransactionManager(
            @Qualifier("entregablesEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean(name = "entregablesFlyway", initMethod = "migrate")
    public Flyway entregablesFlyway(@Qualifier("entregablesDataSource") DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/entregables")
                .baselineOnMigrate(false)
                .load();
    }
}
