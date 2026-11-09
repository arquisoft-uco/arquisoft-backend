package com.arquisoft.proyectos.infrastructure.entregableproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.proyectos.application.entregableproyectogrado.command.secondaryport.entity.EntregableProyectoGradoEntity;
import com.arquisoft.proyectos.infrastructure.entregableproyectogrado.command.secondaryadapter.entity.EntregableProyectoGradoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.EntregableProyectoGradoKey;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DataJpaTest
class EntregableProyectoGradoCommandOutputAdapterTest {

    @Autowired
    private EntregableProyectoGradoCommandRepository entregableProyectoGradoCommandRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private EntregableProyectoGradoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EntregableProyectoGradoCommandOutputAdapter(entregableProyectoGradoCommandRepository, logger);
    }

    private EntregableProyectoGradoEntity entity(UUID id, UUID proyectoGrado, int version, String estado) {
        return new EntregableProyectoGradoEntity(id, proyectoGrado, version, estado,
                Instant.parse("2026-11-01T10:00:00Z"));
    }

    private void sembrar(UUID proyectoGrado, int version, String estado) {
        entityManager.persistAndFlush(EntregableProyectoGradoJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).proyectoGradoId(proyectoGrado).versionEntregable(version)
                .estadoEntregableId(estado).ocurridoEn(Instant.parse("2026-11-01T10:00:00Z")).build());
    }

    @Test
    void debeInsertarYLuegoActualizarElMismoId_cuandoSeGuardaDosVeces() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();

        // Act
        adapter.guardar(entity(id, proyectoGrado, 1, "DISPONIBLE_EVALUACION"));
        entityManager.flush();
        entityManager.clear();
        adapter.guardar(entity(id, proyectoGrado, 1, "CERRADO"));
        entityManager.flush();
        entityManager.clear();

        // Assert
        var resultado = adapter.obtenerPorId(id);
        assertThat(resultado).map(EntregableProyectoGradoEntity::estadoEntregable).contains("CERRADO");
        assertThat(entregableProyectoGradoCommandRepository.count()).isEqualTo(1);
        verify(logger, times(2))
                .debug(EntregableProyectoGradoKey.LOG_GUARDADO, id, proyectoGrado);
    }

    @Test
    void debeRetornarLaMayorVersionSinMezclarProyectos_cuandoHayVariosEntregables() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        sembrar(proyectoGrado, 1, "CANCELADO");
        sembrar(proyectoGrado, 3, "CERRADO");
        sembrar(proyectoGrado, 2, "CANCELADO");
        sembrar(UtilUUID.generarNuevoUUID(), 9, "DISPONIBLE_EVALUACION");

        // Act
        var resultado = adapter.obtenerUltimoPorProyectoGrado(proyectoGrado);

        // Assert
        assertThat(resultado).hasValueSatisfying(ultimo -> {
            assertThat(ultimo.version()).isEqualTo(3);
            assertThat(ultimo.estadoEntregable()).isEqualTo("CERRADO");
            assertThat(ultimo.proyectoGrado()).isEqualTo(proyectoGrado);
        });
    }

    @Test
    void debeRetornarVacio_cuandoNoHayEntregablesNiExisteElId() {
        // Act & Assert
        assertThat(adapter.obtenerUltimoPorProyectoGrado(UtilUUID.generarNuevoUUID())).isEmpty();
        assertThat(adapter.obtenerPorId(UtilUUID.generarNuevoUUID())).isEmpty();
    }
}
