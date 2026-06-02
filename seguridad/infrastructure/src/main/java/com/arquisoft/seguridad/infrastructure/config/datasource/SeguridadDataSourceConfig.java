package com.arquisoft.seguridad.infrastructure.config.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuración de persistencia para el bounded context {@code seguridad}.
 *
 * <p>Registra DataSource, EntityManagerFactory, TransactionManager y Flyway
 * para la base de datos {@code usuarios}.
 *
 * <p>{@code seguridadTransactionManager} es declarado {@code @Primary} porque es el
 * único TransactionManager con JPA activo actualmente. Cuando otros contextos
 * implementen JPA, cada adaptador deberá especificar su propio
 * {@code transactionManager} explícito y se retirará {@code @Primary} de aquí.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.arquisoft.seguridad.infrastructure.adapter.out.persistence",
        entityManagerFactoryRef = "seguridadEntityManager",
        transactionManagerRef = "seguridadTransactionManager"
)
public class SeguridadDataSourceConfig {

    @Value("${datasource.seguridad.url}")
    private String url;

    @Value("${datasource.seguridad.username}")
    private String username;

    @Value("${datasource.seguridad.password}")
    private String password;

    @Value("${datasource.seguridad.hikari.maximum-pool-size}")
    private int maxPoolSize;

    @Value("${datasource.seguridad.hikari.minimum-idle}")
    private int minIdle;

    @Value("${datasource.seguridad.hikari.connection-timeout}")
    private long connectionTimeout;

    @Bean(name = "seguridadDataSource")
    public DataSource seguridadDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);
        config.setConnectionTimeout(connectionTimeout);
        config.setPoolName("HikariPool-Seguridad");
        config.setDriverClassName("org.postgresql.Driver");
        return new HikariDataSource(config);
    }

    @Bean(name = "seguridadEntityManager")
    public LocalContainerEntityManagerFactoryBean seguridadEntityManager(
            @Qualifier("seguridadDataSource") DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.arquisoft.seguridad.infrastructure.adapter.out.persistence");
        em.setPersistenceUnitName("seguridad");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.hbm2ddl.auto", "validate");
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.jdbc.batch_size", "25");
        properties.put("hibernate.show_sql", "false");
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Primary
    @Bean(name = "seguridadTransactionManager")
    public PlatformTransactionManager seguridadTransactionManager(
            @Qualifier("seguridadEntityManager") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean(name = "seguridadFlyway", initMethod = "migrate")
    public Flyway seguridadFlyway(@Qualifier("seguridadDataSource") DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/seguridad")
                .baselineOnMigrate(true)
                .load();
    }
}
