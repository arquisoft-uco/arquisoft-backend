package com.arquisoft.fichas.infrastructure.config;

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
        basePackages = "com.arquisoft.fichas.infrastructure",
        entityManagerFactoryRef = "fichasEntityManagerFactory",
        transactionManagerRef = "fichasTransactionManager"
)
public class FichasDataSourceConfig {

    @Value("${datasource.fichas.url}")
    private String url;

    @Value("${datasource.fichas.username}")
    private String username;

    @Value("${datasource.fichas.password}")
    private String password;

    @Value("${datasource.fichas.hikari.maximum-pool-size}")
    private int maxPoolSize;

    @Value("${datasource.fichas.hikari.minimum-idle}")
    private int minIdle;

    @Value("${datasource.fichas.hikari.connection-timeout}")
    private long connectionTimeout;

    @Bean(name = "fichasDataSource")
    public DataSource fichasDataSource() {
        var config = new HikariConfig();
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

    @Bean(name = "fichasEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean fichasEntityManagerFactory(
            @Qualifier("fichasDataSource") DataSource dataSource,
            @Qualifier("fichasFlyway") Flyway fichasFlyway) {

        var em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.arquisoft.fichas.infrastructure");
        em.setPersistenceUnitName("fichas");

        var vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        em.setJpaPropertyMap(PropiedadesJpa.porDefecto());

        return em;
    }

    @Bean(name = "fichasTransactionManager")
    public PlatformTransactionManager fichasTransactionManager(
            @Qualifier("fichasEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean(name = "fichasFlyway", initMethod = "migrate")
    public Flyway fichasFlyway(@Qualifier("fichasDataSource") DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/fichas")
                .baselineOnMigrate(false)
                .load();
    }
}
