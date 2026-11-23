package com.arquisoft.config.outbox;

import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.modulith.events.core.EventSerializer;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ContextAwareEventPublicationRepositoryTest {

    private static final String CREAR_TABLA = "CREATE TABLE event_publication ("
            + "id UUID PRIMARY KEY, listener_id VARCHAR(512) NOT NULL, event_type VARCHAR(512) NOT NULL, "
            + "serialized_event VARCHAR(4000) NOT NULL, publication_date TIMESTAMP NOT NULL, "
            + "completion_date TIMESTAMP, status VARCHAR(20), completion_attempts INT, "
            + "last_resubmission_date TIMESTAMP)";
    private static final String INSERTAR = "INSERT INTO event_publication "
            + "(id, listener_id, event_type, serialized_event, publication_date, status, completion_attempts) "
            + "VALUES (?, 'listener', 'tipo', '{}', ?, 'OPEN', 0)";

    private EmbeddedDatabase baseDatos;
    private JdbcTemplate jdbc;
    private ContextAwareEventPublicationRepository repositorio;

    @BeforeEach
    void setUp() {
        baseDatos = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .generateUniqueName(true)
                .build();
        jdbc = new JdbcTemplate(baseDatos);
        jdbc.execute(CREAR_TABLA);
        repositorio = new ContextAwareEventPublicationRepository(
                Map.of("outbox", baseDatos), Map.of(), mock(EventSerializer.class), new MockEnvironment(),
                mock(AppLogger.class));
        repositorio.afterSingletonsInstantiated();
    }

    @AfterEach
    void tearDown() {
        baseDatos.shutdown();
    }

    private UUID insertar() {
        var id = UUID.randomUUID();
        jdbc.update(INSERTAR, id, Timestamp.from(Instant.now()));
        return id;
    }

    private List<UUID> idsRestantes() {
        return jdbc.queryForList("SELECT id FROM event_publication", UUID.class);
    }

    @Test
    void debeEliminarSoloLasPublicacionesIndicadas_cuandoSeDanVariosIdentificadores() {
        // Arrange
        var primera = insertar();
        var segunda = insertar();
        var conservada = insertar();

        // Act
        repositorio.deletePublications(List.of(primera, segunda));

        // Assert
        assertThat(idsRestantes()).containsExactly(conservada);
    }

    @Test
    void debeEliminarUnaSolaPublicacion_cuandoSeDaUnIdentificador() {
        // Arrange
        var eliminada = insertar();
        var conservada = insertar();

        // Act
        repositorio.deletePublications(List.of(eliminada));

        // Assert
        assertThat(idsRestantes()).containsExactly(conservada);
    }

    @Test
    void noDebeEliminarNada_cuandoLaListaEstaVacia() {
        // Arrange
        var conservada = insertar();

        // Act
        repositorio.deletePublications(List.of());

        // Assert
        assertThat(idsRestantes()).containsExactly(conservada);
    }
}
