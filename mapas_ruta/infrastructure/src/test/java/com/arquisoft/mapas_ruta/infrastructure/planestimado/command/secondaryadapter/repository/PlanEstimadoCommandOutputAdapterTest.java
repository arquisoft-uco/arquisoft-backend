package com.arquisoft.mapas_ruta.infrastructure.planestimado.command.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.planestimado.command.secondaryport.entity.PlanEstimadoEntity;
import com.arquisoft.mapas_ruta.infrastructure.planestimado.command.secondaryadapter.entity.PlanEstimadoJpaEntity;
import com.arquisoft.mapas_ruta.infrastructure.tarea.command.secondaryadapter.entity.TareaJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@DataJpaTest
@Import(PlanEstimadoCommandOutputAdapter.class)
class PlanEstimadoCommandOutputAdapterTest {

    @Autowired
    private PlanEstimadoCommandOutputAdapter adapter;

    @Autowired
    private TestEntityManager entityManager;

    @MockitoBean
    private AppLogger logger;

    private UUID sembrarTarea(UUID mapaRuta) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(TareaJpaEntity.builder()
                .id(id)
                .mapaRutaId(mapaRuta)
                .descripcion("Tarea " + id)
                .asesorId(UtilUUID.generarNuevoUUID())
                .estadoItemId("NUEVA")
                .build());
        return id;
    }

    private void sembrarPlan(UUID tarea, int dias) {
        entityManager.persist(PlanEstimadoJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .tareaId(tarea)
                .tiempoEstimadoDias(dias)
                .build());
    }

    @Test
    void debePersistirElPlan_cuandoSeRegistra() {
        // Arrange
        var entity = new PlanEstimadoEntity(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 5, "Revisar");

        // Act
        adapter.registrar(entity);
        entityManager.flush();
        entityManager.clear();

        // Assert
        var guardado = entityManager.find(PlanEstimadoJpaEntity.class, entity.id());
        assertThat(guardado.getTareaId()).isEqualTo(entity.tarea());
        assertThat(guardado.getTiempoEstimadoDias()).isEqualTo(5);
        assertThat(guardado.getDescripcionEstimado()).isEqualTo("Revisar");
        verify(logger).debug(any(ClaveMensaje.class), eq(entity.id()));
    }

    @Test
    void debeGuardarNull_cuandoLaDescripcionEsVacia() {
        // Arrange
        var entity = new PlanEstimadoEntity(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 2, UtilTexto.VACIO);

        // Act
        adapter.registrar(entity);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(entityManager.find(PlanEstimadoJpaEntity.class, entity.id()).getDescripcionEstimado()).isNull();
    }

    @Test
    void debeIndicarExistencia_soloParaLaTareaConPlan() {
        // Arrange
        var mapaRuta = UtilUUID.generarNuevoUUID();
        var conPlan = sembrarTarea(mapaRuta);
        var sinPlan = sembrarTarea(mapaRuta);
        sembrarPlan(conPlan, 3);
        entityManager.flush();

        // Act & Assert
        assertThat(adapter.existePorTarea(conPlan)).isTrue();
        assertThat(adapter.existePorTarea(sinPlan)).isFalse();
    }

    @Test
    void debeSumarDias_deTodasLasTareasDelMismoMapaIgnorandoOtrosMapas() {
        // Arrange
        var mapaRuta = UtilUUID.generarNuevoUUID();
        var tareaA = sembrarTarea(mapaRuta);
        var tareaB = sembrarTarea(mapaRuta);
        var tareaSinPlan = sembrarTarea(mapaRuta);
        var tareaOtroMapa = sembrarTarea(UtilUUID.generarNuevoUUID());
        sembrarPlan(tareaA, 3);
        sembrarPlan(tareaB, 4);
        sembrarPlan(tareaOtroMapa, 100);
        entityManager.flush();

        // Act & Assert
        assertThat(adapter.sumarDiasEstimadosPorMapaRutaDeTarea(tareaSinPlan)).isEqualTo(7L);
        assertThat(adapter.sumarDiasEstimadosPorMapaRutaDeTarea(tareaA)).isEqualTo(7L);
        assertThat(adapter.sumarDiasEstimadosPorMapaRutaDeTarea(tareaOtroMapa)).isEqualTo(100L);
    }

    @Test
    void debeDevolverCero_cuandoElMapaNoTienePlanes() {
        // Arrange
        var tarea = sembrarTarea(UtilUUID.generarNuevoUUID());
        entityManager.flush();

        // Act & Assert
        assertThat(adapter.sumarDiasEstimadosPorMapaRutaDeTarea(tarea)).isZero();
    }
}
