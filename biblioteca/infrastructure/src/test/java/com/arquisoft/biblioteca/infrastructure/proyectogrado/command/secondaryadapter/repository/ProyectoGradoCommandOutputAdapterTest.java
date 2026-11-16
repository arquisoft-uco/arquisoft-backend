package com.arquisoft.biblioteca.infrastructure.proyectogrado.command.secondaryadapter.repository;

import com.arquisoft.biblioteca.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.ProyectoGradoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DataJpaTest
class ProyectoGradoCommandOutputAdapterTest {

    @Autowired
    private ProyectoGradoCommandRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private ProyectoGradoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ProyectoGradoCommandOutputAdapter(repository, logger);
    }

    @Test
    void debeInsertarYLuegoActualizarElEstadoCompleto_cuandoSeGuardaDosVeces() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var inicial = new ProyectoGradoEntity(id, "Titulo", Instant.parse("2026-11-15T10:00:00Z"));
        var actualizado = new ProyectoGradoEntity(id, "Nuevo", Instant.parse("2026-11-16T10:00:00Z"));

        // Act
        adapter.guardar(inicial);
        entityManager.flush();
        entityManager.clear();
        var trasInsertar = adapter.obtenerPorId(id);
        adapter.guardar(actualizado);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(trasInsertar).hasValue(inicial);
        assertThat(adapter.obtenerPorId(id)).hasValue(actualizado);
        assertThat(adapter.obtenerPorId(UtilUUID.generarNuevoUUID())).isEmpty();
        assertThat(repository.count()).isEqualTo(1);
        verify(logger, times(2)).debug(ProyectoGradoKey.LOG_GUARDADO, id);
    }
}
