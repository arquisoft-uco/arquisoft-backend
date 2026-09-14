package com.arquisoft.proyectos.infrastructure.config;

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
        basePackages = "com.arquisoft.proyectos.infrastructure",
        entityManagerFactoryRef = "proyectosEntityManagerFactory",
        transactionManagerRef = "proyectosTransactionManager",
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
public class ProyectosDataSourceConfig {

    @Value("${datasource.proyectos.url}")
    private String url;

    @Value("${datasource.proyectos.username}")
    private String username;

    @Value("${datasource.proyectos.password}")
    private String password;

    @Value("${datasource.proyectos.hikari.maximum-pool-size}")
    private int maxPoolSize;

    @Value("${datasource.proyectos.hikari.minimum-idle}")
    private int minIdle;

    @Value("${datasource.proyectos.hikari.connection-timeout}")
    private long connectionTimeout;

    @Bean(name = "proyectosDataSource")
    public DataSource proyectosDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);
        config.setConnectionTimeout(connectionTimeout);
        config.setPoolName("HikariPool-Proyectos");
        config.setDriverClassName("org.postgresql.Driver");
        return new HikariDataSource(config);
    }

    @Bean(name = "proyectosEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean proyectosEntityManagerFactory(
            @Qualifier("proyectosDataSource") DataSource dataSource,
            @Qualifier("proyectosFlyway") Flyway proyectosFlyway) {

        var em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.arquisoft.proyectos.infrastructure");
        em.setPersistenceUnitName("proyectos");

        var vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        em.setJpaPropertyMap(PropiedadesJpa.porDefecto());

        return em;
    }

    @Bean(name = "proyectosTransactionManager")
    public PlatformTransactionManager proyectosTransactionManager(
            @Qualifier("proyectosEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean(name = "proyectosFlyway", initMethod = "migrate")
    public Flyway proyectosFlyway(@Qualifier("proyectosDataSource") DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/proyectos")
                .baselineOnMigrate(false)
                .load();
    }
}
