package com.arquisoft.evaluaciones.infrastructure.asesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.asesor.command.secondaryadapter.entity.AsesorJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.secondaryadapter.entity.EvaluacionAsesorJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DataJpaTest
class AsesorContactoCommandOutputAdapterTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-11-19T10:00:00Z");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AsesorCommandRepository repository;

    private AsesorCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new AsesorCommandOutputAdapter(repository, mock(AppLogger.class));
    }

    @Test
    void debeDevolverElContactoDelAsesorDeLaEvaluacion_aunqueTengaBajaLogica() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var otro = UtilUUID.generarNuevoUUID();
        entityManager.persist(AsesorJpaEntity.builder().id(asesor).identificador("A1").nombre("Asesor Uno")
                .email("asesor@uco.edu.co").ocurridoEn(OCURRIDO_EN).eliminadoEn(OCURRIDO_EN).build());
        entityManager.persist(AsesorJpaEntity.builder().id(otro).identificador("A2").nombre("Otro")
                .email("otro@uco.edu.co").ocurridoEn(OCURRIDO_EN).build());
        entityManager.persist(EvaluacionAsesorJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).evaluacion(evaluacion).asesor(asesor).build());
        entityManager.persist(EvaluacionAsesorJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).evaluacion(UtilUUID.generarNuevoUUID()).asesor(otro).build());
        entityManager.flush();
        entityManager.clear();

        // Act
        var contacto = adapter.obtenerContactoPorEvaluacion(evaluacion);

        // Assert
        assertThat(contacto).hasValueSatisfying(encontrado -> {
            assertThat(encontrado.id()).isEqualTo(asesor);
            assertThat(encontrado.nombre()).isEqualTo("Asesor Uno");
            assertThat(encontrado.email()).isEqualTo("asesor@uco.edu.co");
        });
    }

    @Test
    void debeRetornarVacio_cuandoLaEvaluacionNoTieneAsesorAsignado() {
        // Act & Assert
        assertThat(adapter.obtenerContactoPorEvaluacion(UtilUUID.generarNuevoUUID())).isEmpty();
    }
}
