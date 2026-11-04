package com.arquisoft.proyectos.infrastructure.estadoproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.proyectos.application.estadoproyectogrado.command.secondaryport.entity.EstadoProyectoGradoEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.EstadoProyectoGradoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataJpaTest
class EstadoProyectoGradoCommandOutputAdapterTest {

    private static final String EN_PROCESO = "EN_PROCESO";
    private static final String ATRASADO = "ATRASADO";
    private static final String FINALIZADO = "FINALIZADO";

    @Autowired
    private EstadoProyectoGradoCommandRepository estadoProyectoGradoCommandRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private EstadoProyectoGradoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstadoProyectoGradoCommandOutputAdapter(estadoProyectoGradoCommandRepository, logger);
    }

    private EstadoProyectoGradoEntity estado(UUID proyectoGrado, String estadoProyecto, String fecha) {
        return new EstadoProyectoGradoEntity(UtilUUID.generarNuevoUUID(), proyectoGrado, estadoProyecto,
                Instant.parse(fecha));
    }

    @Test
    void debePersistirYLeerTodosLosCampos_cuandoSeRegistraElEstado() {
        // Arrange
        var registrado = estado(UtilUUID.generarNuevoUUID(), EN_PROCESO, "2026-10-01T10:00:00Z");

        // Act
        adapter.registrar(registrado);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(adapter.obtenerEstadoActual(registrado.proyectoGrado())).contains(registrado);
        verify(logger).debug(EstadoProyectoGradoKey.LOG_GUARDADO, registrado.id(), registrado.proyectoGrado());
    }

    @Test
    void debeDevolverElMasReciente_cuandoElProyectoTieneVariosEstados() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var vigente = estado(proyectoGrado, FINALIZADO, "2026-10-05T10:00:00Z");
        adapter.registrar(estado(proyectoGrado, ATRASADO, "2026-10-03T10:00:00Z"));
        adapter.registrar(vigente);
        adapter.registrar(estado(proyectoGrado, EN_PROCESO, "2026-10-01T10:00:00Z"));
        adapter.registrar(estado(UtilUUID.generarNuevoUUID(), EN_PROCESO, "2026-10-09T10:00:00Z"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var actual = adapter.obtenerEstadoActual(proyectoGrado);

        // Assert
        assertThat(actual).contains(vigente);
    }

    @Test
    void debeDevolverVacio_cuandoElProyectoNoTieneEstados() {
        // Arrange
        adapter.registrar(estado(UtilUUID.generarNuevoUUID(), EN_PROCESO, "2026-10-01T10:00:00Z"));
        entityManager.flush();

        // Act
        var actual = adapter.obtenerEstadoActual(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(actual).isEmpty();
    }
}
