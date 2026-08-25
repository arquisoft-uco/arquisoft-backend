package com.arquisoft.usuarios.infrastructure.config;

import com.arquisoft.shared.jpa.config.PropiedadesJpa;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
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

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.arquisoft.usuarios.infrastructure",
        entityManagerFactoryRef = "usuariosEntityManagerFactory",
        transactionManagerRef = "usuariosTransactionManager"
)
public class UsuariosDataSourceConfig {

    @Value("${datasource.usuarios.url}")
    private String url;

    @Value("${datasource.usuarios.username}")
    private String username;

    @Value("${datasource.usuarios.password}")
    private String password;

    @Value("${datasource.usuarios.hikari.maximum-pool-size}")
    private int maxPoolSize;

    @Value("${datasource.usuarios.hikari.minimum-idle}")
    private int minIdle;

    @Value("${datasource.usuarios.hikari.connection-timeout}")
    private long connectionTimeout;

    @Bean(name = "usuariosDataSource")
    public DataSource usuariosDataSource() {
        var config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);
        config.setConnectionTimeout(connectionTimeout);
        config.setPoolName("HikariPool-Usuarios");
        config.setDriverClassName("org.postgresql.Driver");
        return new HikariDataSource(config);
    }

    @Bean(name = "usuariosEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean usuariosEntityManagerFactory(
            @Qualifier("usuariosDataSource") DataSource dataSource,
            @Qualifier("usuariosFlyway") Flyway usuariosFlyway) {

        var em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.arquisoft.usuarios.infrastructure");
        em.setPersistenceUnitName("usuarios");

        var vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        em.setJpaPropertyMap(PropiedadesJpa.porDefecto());

        return em;
    }

    @Primary
    @Bean(name = "usuariosTransactionManager")
    public PlatformTransactionManager usuariosTransactionManager(
            @Qualifier("usuariosEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean(name = "usuariosFlyway", initMethod = "migrate")
    public Flyway usuariosFlyway(@Qualifier("usuariosDataSource") DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/usuarios")
                .baselineOnMigrate(false)
                .load();
    }
}
