package com.arquisoft.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * DataSource centralizado para Spring Modulith Event Publication Registry.
 *
 * <p>La base de datos {@code arquisoft_events} almacena la tabla {@code event_publication}
 * con todos los eventos de dominio de todos los bounded contexts. Centralizar aquí
 * permite:
 * <ul>
 *   <li>Visibilidad operacional unificada de todos los eventos pendientes o fallidos.</li>
 *   <li>Una sola tabla a monitorear en producción.</li>
 *   <li>Independencia del ciclo de vida de cada contexto (la tabla no "pertenece" a ninguno).</li>
 * </ul>
 *
 * <p><b>Atomicidad:</b> la BD {@code arquisoft_events} es independiente de las BDs de los
 * contextos ({@code usuarios}, {@code fichas_perfil}, etc.). Al ser DataSources separados,
 * el INSERT en {@code event_publication} y el {@code save} del aggregate NO son atómicos.
 * El riesgo práctico es mínimo: ambas operaciones se ejecutan contra el mismo servidor
 * PostgreSQL en el mismo hilo, y el INSERT es una operación simple. La garantía real de
 * Spring Modulith es: <i>una vez el evento está en {@code event_publication}, será enviado
 * a RabbitMQ</i>, incluso si el broker estaba caído al momento del commit.
 *
 * <p><b>Prerrequisito:</b> la base de datos {@code arquisoft_events} debe existir antes
 * de arrancar la aplicación. Se crea en {@code init-db.sql} (ejecutado por Docker al
 * inicializar el contenedor). Si el contenedor ya está corriendo, ejecutar:
 * <pre>
 *   docker exec -it arquisoft-postgres psql -U postgres \
 *     -c "CREATE DATABASE arquisoft_events OWNER arquisoft_user;"
 * </pre>
 */
@Configuration
public class ArquisoftEventsDataSourceConfig {

    @Value("${datasource.arquisoft-events.url}")
    private String url;

    @Value("${datasource.arquisoft-events.username}")
    private String username;

    @Value("${datasource.arquisoft-events.password}")
    private String password;

    @Value("${datasource.arquisoft-events.hikari.maximum-pool-size:5}")
    private int maxPoolSize;

    @Value("${datasource.arquisoft-events.hikari.minimum-idle:1}")
    private int minIdle;

    @Value("${datasource.arquisoft-events.hikari.connection-timeout:20000}")
    private long connectionTimeout;

    /**
     * DataSource primario para Spring Modulith.
     *
     * <p>{@code @Primary} permite que {@code JdbcEventPublicationAutoConfiguration}
     * lo resuelva sin necesidad de {@code @Qualifier}. El resto de los DataSources
     * del proyecto usan {@code @Qualifier} explícito y no se ven afectados.
     */
    @Bean("arquisoftEventsDataSource")
    @Primary
    public DataSource arquisoftEventsDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);
        config.setConnectionTimeout(connectionTimeout);
        config.setPoolName("HikariPool-ArquisoftEvents");
        config.setDriverClassName("org.postgresql.Driver");
        return new HikariDataSource(config);
    }

    /**
     * JdbcTemplate primario para Spring Modulith.
     *
     * <p>{@code JdbcEventPublicationAutoConfiguration} lo inyecta para construir
     * {@code JdbcEventPublicationRepository}. Al ser primario, no interfiere con los
     * JdbcTemplates (si los hubiera) de los contextos, que usarían {@code @Qualifier}.
     */
    @Bean("arquisoftEventsJdbcTemplate")
    @Primary
    public JdbcTemplate arquisoftEventsJdbcTemplate(
            @Qualifier("arquisoftEventsDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * Flyway dedicado para {@code arquisoft_events}.
     * Aplica la migración que crea la tabla {@code event_publication}.
     */
    @Bean(name = "arquisoftEventsFlyway", initMethod = "migrate")
    public Flyway arquisoftEventsFlyway(
            @Qualifier("arquisoftEventsDataSource") DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/arquisoft_events")
                .baselineOnMigrate(true)
                .load();
    }
}
