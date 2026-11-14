package com.arquisoft.entregables.infrastructure.asesorproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.entregables.infrastructure.asesorproyectogrado.command.secondaryadapter.entity.AsesorProyectoGradoJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(AsesorProyectoGradoAccesoQueryOutputAdapter.class)
class AsesorProyectoGradoAccesoQueryOutputAdapterTest {

    private static final Instant T0 = Instant.parse("2026-11-01T10:00:00Z");
    private static final Instant T1 = Instant.parse("2026-11-02T10:00:00Z");

    @Autowired
    private AsesorProyectoGradoAccesoQueryOutputAdapter adapter;

    @Autowired
    private TestEntityManager entityManager;

    private void sembrar(UUID asesor, UUID proyecto, Instant ocurridoEn, Instant finalizadoEn) {
        entityManager.persist(AsesorProyectoGradoJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).asesorId(asesor).proyectoGradoId(proyecto)
                .ocurridoEn(ocurridoEn).finalizadoEn(finalizadoEn).build());
    }

    private void confirmar() {
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void debeRetornarElAsesorDelVinculoMasReciente_cuandoElProyectoCambioDeAsesor() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var anterior = UtilUUID.generarNuevoUUID();
        var nuevo = UtilUUID.generarNuevoUUID();
        sembrar(anterior, proyecto, T0, T1);
        sembrar(nuevo, proyecto, T1, null);
        sembrar(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), T1, null);
        confirmar();

        // Act
        var vigente = adapter.obtenerAsesorVigentePorProyectoGrado(proyecto);

        // Assert
        assertThat(vigente).contains(nuevo);
    }

    @Test
    void debeRetornarVacio_cuandoElVinculoMasRecienteEstaFinalizadoOElProyectoNoTieneVinculos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        sembrar(UtilUUID.generarNuevoUUID(), proyecto, T0, null);
        sembrar(UtilUUID.generarNuevoUUID(), proyecto, T1, T1);
        confirmar();

        // Act & Assert
        assertThat(adapter.obtenerAsesorVigentePorProyectoGrado(proyecto)).isEmpty();
        assertThat(adapter.obtenerAsesorVigentePorProyectoGrado(UtilUUID.generarNuevoUUID())).isEmpty();
    }
}
