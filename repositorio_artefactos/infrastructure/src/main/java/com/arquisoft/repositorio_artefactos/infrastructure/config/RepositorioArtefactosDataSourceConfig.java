package com.arquisoft.repositorio_artefactos.infrastructure.config;

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
        basePackages = "com.arquisoft.repositorio_artefactos.infrastructure",
        entityManagerFactoryRef = "repositorioArtefactosEntityManagerFactory",
        transactionManagerRef = "repositorioArtefactosTransactionManager",
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
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

    @Bean(name = "repositorioArtefactosEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean repositorioArtefactosEntityManagerFactory(
            @Qualifier("repositorioArtefactosDataSource") DataSource dataSource,
            @Qualifier("repositorioArtefactosFlyway") Flyway repositorioArtefactosFlyway) {

        var em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.arquisoft.repositorio_artefactos.infrastructure");
        em.setPersistenceUnitName("repositorioArtefactos");

        var vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        em.setJpaPropertyMap(PropiedadesJpa.porDefecto());

        return em;
    }

    @Bean(name = "repositorioArtefactosTransactionManager")
    public PlatformTransactionManager repositorioArtefactosTransactionManager(
            @Qualifier("repositorioArtefactosEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean(name = "repositorioArtefactosFlyway", initMethod = "migrate")
    public Flyway repositorioArtefactosFlyway(@Qualifier("repositorioArtefactosDataSource") DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/repositorio_artefactos")
                .baselineOnMigrate(false)
                .load();
    }
}
